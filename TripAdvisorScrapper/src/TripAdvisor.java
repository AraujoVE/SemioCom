import java.io.WriteAbortedException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class TripAdvisor {
    int TERMINAL_NUMBER;
    int iter;
    int parallelProgs;

    private String filePath;
    private String city;
    private String normalizedCity;
    private CustomFileHandler fh;
    private WebDriver driver;
    private XPathUtils xp;
    private String geo;
    private int REST_PER_PAGE = 30;
    private List<Restaurant> restaurants = new ArrayList<Restaurant>();
    String restaurantCsvHeading = "nome_do_restaurante|url_do_restaurante|nota_media_do_restaurante|numero_de_reviews_recebidos_pelo_restaurante|tag_1_do_tipo_de_restaurante;tag_2_do_tipo_de_restaurante;tag_N_do_tipo_de_restaurante|minimo_de_cifroes|maximo_de_cifroes|media_de_cifroes|endereco_completo|bairro|numero_de_reviews_excelente|numero_de_reviews_muito-bom|numero_de_reviews_razoavel|numero_de_reviews_ruim|numero_de_reviews_horrivel\n";
    int maxDateInt;
    int minDateInt;
    HashMap<String, Integer> dateToIntMap = new HashMap<String, Integer>();
    List<String> writtenRestaurants = new ArrayList<String>();

        
    private boolean setRestaurantVars(Document restaurantDoc,int iter){
        Element mainParams = restaurantDoc.select("a.bHGqj.Cj.b").first();//Get the element that has important parameters
        
        String restHref = mainParams.attr("href");
        String partialName = restHref.split("-Reviews-")[1].split("-"+normalizedCity)[0].trim();
        String restId = restHref.split("-Reviews-")[0].split("d")[1].trim();
        String name = partialName + "#" + restId;
        //if name in writtenRestaurants, break
        if(writtenRestaurants.contains(name)){
            System.out.println("\tRestaurant already written = "+name);
            return true;
        }
        writtenRestaurants.add(name);

        String url = "https://www.tripadvisor.com.br" + mainParams.attr("href");//Get the url of the restaurant
        
        double avgRating = Double.parseDouble(restaurantDoc.select("svg.RWYkj.d.H0").first().attr("aria-label").split(" ")[0].replace(",", "."));
        
        int ratingNo = Integer.parseInt(restaurantDoc.select("span.NoCoR").first().text().split(" ")[0].replace(".","").trim());
        
        Elements tagsAndPrices = restaurantDoc.select("div.bhDlF.bPJHV.eQXRG").get(0).children();
        List<String> tags = new ArrayList<String>();
        String price = "";
        if(tagsAndPrices.size() == 0){
            tags.add("-1");
            price = "";
        }
        else if(tagsAndPrices.size() == 1){
            if(tagsAndPrices.first().text().contains("$")){
                tags.add("-1");
                price = tagsAndPrices.first().text().trim();
            }
            else{
                tags = Arrays.asList(tagsAndPrices.first().text().split(","));
                price = "";
            }
        }
        else{
            tags = Arrays.asList(tagsAndPrices.first().text().split(","));
            price = tagsAndPrices.get(1).text().trim();
        }

        int minPriceTag;
        int maxPriceTag;
        if(price.contains("-")){
            minPriceTag = price.split("-")[0].trim().length();
            maxPriceTag = price.split("-")[1].trim().length();
        }
        else if(price.length() > 0){
            minPriceTag = price.length();
            maxPriceTag = minPriceTag;
        }
        else{
            minPriceTag = -1;
            maxPriceTag = -1;
        }
        Restaurant auxRest = new Restaurant(xp,filePath,name,url,avgRating,ratingNo,tags,minPriceTag,maxPriceTag,dateToIntMap,TERMINAL_NUMBER,parallelProgs);
        restaurants.add(auxRest);
        restaurants.get(restaurants.size()-1).setReviews();
        System.out.println(auxRest.toFileStr());
        return true;
    }

    private void retrieveRestaurants(){
        String baseUrl = "https://www.tripadvisor.com.br/RestaurantSearch?Action=PAGE&ajax=1&availSearchEnabled=true&sortOrder=popularity&geo="+geo+"&itags=10591&o=a";
        String url;
        String restaurantStr = "";
        Document restaurantsDoc = null;
        Document restaurantDoc = null;
        Element restaurantElement = null;
        boolean endRestaurants = false;
        System.out.println("Starting to retrieve restaurants");
        while(!endRestaurants){
            url = baseUrl+String.valueOf(REST_PER_PAGE*iter);
            System.out.println("Getting restaurants from " + url);
            try{restaurantsDoc = Jsoup.connect(url).followRedirects(true).get();}
            catch(Exception e){System.out.println("Error: " + e.getMessage());}

            System.out.println("Starting getting restaurants\n\n");
            long startTime = System.currentTimeMillis();
            long elapsedTime;
            for(int i=1;i<=REST_PER_PAGE;i++){
                int restId = i + REST_PER_PAGE*iter;
                System.out.println("Restaurant Id =>" + restId); 
                restaurantStr = "[data-test=\""+String.valueOf(restId)+"_list_item\"]";
                restaurantElement = restaurantsDoc.select(restaurantStr).first(); 
                if(restaurantElement == null) {
                    endRestaurants = true;
                    break;
                }
                restaurantDoc = Jsoup.parse(restaurantElement.html());

                if(!setRestaurantVars(restaurantDoc,restId)){
                    endRestaurants = true;
                    break;
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Elapsed Time: ");
                System.out.println(elapsedTime);
                System.out.println("\n\n");
            }

            iter+=parallelProgs;
        }
    }


    private void seleniumRetrievingPart(){
        xp.clickWT("//button[@aria-label='OK']");
        xp.clickWT("//a[contains(.,'Restaurantes')]");
        xp.typeWT("//div[@data-test-attribute='typeahead-QuickLink_RESTAURANTS_geopicker']//input[@placeholder='Aonde você quer ir?']", city);
        xp.sleepS(2.5);
        geo = xp.attr("(//div[@id='typeahead_results']/a)[1]","href").split("-")[1].replaceAll("^g", "");
    }

    private void manageWindow(String ... strOptions) {
        ChromeOptions options = new ChromeOptions();//Creating Chrome driver options
        for(String option : strOptions) options.addArguments(option);//Adding options to the driver
        driver = new ChromeDriver(options);//Creating Chrome driver with given options
        driver.get("https://www.tripadvisor.com.br");//Go to choosen page
        xp = new XPathUtils(driver);//Initializing xPathUtils helper
    }

    private String normalizedCityName(String city){
        String normalizedString = city.trim().toLowerCase();
        normalizedString = Normalizer.normalize(normalizedString, Normalizer.Form.NFD);        
        normalizedString = normalizedString.replaceAll("[^\\p{ASCII}]", "");
        normalizedString = normalizedString.replaceAll("\\s+", "_");
        normalizedString = Pattern.compile("(?<=(^|_))(\\w)").matcher(normalizedString).replaceAll(matche -> matche.group().toUpperCase());
        return normalizedString;
    }

    private void initialSettings(String city,int TERMINAL_NUMBER,int parallelProgs){

        dateToIntMap.put("janeiro", 1);
        dateToIntMap.put("fevereiro", 2);
        dateToIntMap.put("março", 3);
        dateToIntMap.put("abril", 4);
        dateToIntMap.put("maio", 5);
        dateToIntMap.put("junho", 6);
        dateToIntMap.put("julho", 7);
        dateToIntMap.put("agosto", 8);
        dateToIntMap.put("setembro", 9);
        dateToIntMap.put("outubro", 10);
        dateToIntMap.put("novembro", 11);
        dateToIntMap.put("dezembro", 12);
        
        this.city = city;//Set the city in the class
        this.TERMINAL_NUMBER = TERMINAL_NUMBER;//Set the number of terminals
        iter = TERMINAL_NUMBER;//Set the number of restaurants per page
        this.parallelProgs = parallelProgs;//Set the number of parallel programs
        normalizedCity = normalizedCityName(city);//Set the normalized city in the class
        filePath = "./TripAdvisorData/" + normalizedCity;
        fh = new CustomFileHandler();//Initializing the FileHandler
        fh.createDirectories(filePath);//Creating search directory
        String pathh = filePath + "/" + "restaurantsList_" + String.valueOf(TERMINAL_NUMBER)+"_"+ String.valueOf(parallelProgs) +".csv";
        
        if(fh.writeFile(pathh,restaurantCsvHeading) == 2){
            writtenRestaurants = fh.readFileLines(pathh);
            writtenRestaurants.remove(0);
            for(int i = 0;i< writtenRestaurants.size();i++){
                writtenRestaurants.set(i,writtenRestaurants.get(i).split("\\|")[0]);
            }
        }//Creating restaurants file
    }

    public TripAdvisor(String city,int TERMINAL_NUMBER,int parallelProgs) {
        initialSettings(city,TERMINAL_NUMBER,parallelProgs);//Initializing the class first params
        manageWindow("--headless");//Managing selenium driver
        seleniumRetrievingPart();//Retrieving data just achievable with selenium
        //geo = "303631";
        retrieveRestaurants();//Retrieving data faster with jsoup
    }
}