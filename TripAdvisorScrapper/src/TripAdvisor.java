import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
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
    private String filePath;
    private String city;
    private CustomFileHandler fh;
    private WebDriver driver;
    private XPathUtils xp;
    private String geo;
    private int REST_PER_PAGE = 30;
    private int REV_PER_PAGE = 10;
    private List<Restaurant> restaurants = new ArrayList<Restaurant>();
    private String firstRestaurantName = "";
    private int SAVE_AFTER_REVIEWS = 1000;
    private List<Integer> reviewsPerStar;
    private int curReviews = 0;

    private void seleniumRetrievingPart(){
        xp.clickWT("//button[@aria-label='OK']");
        xp.clickWT("//a[contains(.,'Restaurantes')]");
        xp.typeWT("//div[@data-test-attribute='typeahead-QuickLink_RESTAURANTS_geopicker']//input[@placeholder='Aonde você quer ir?']", city);
        xp.sleepS(2.5);
        geo = xp.attr("(//div[@id='typeahead_results']/a)[1]","href").split("-")[1].replaceAll("^g", "");
        driver.quit();
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

    private void initialSettings(String city){
        this.city = city;//Set the city in the class
        filePath = "TripAdvisorData/" + normalizedCityName(city);
        fh = new CustomFileHandler();//Initializing the FileHandler
        fh.createDirectories(filePath);//Creating search directory
    }

    private boolean setRestaurantVars(Document restaurantDoc,int iter){
        Element mainParams = restaurantDoc.select("a.bHGqj.Cj.b").first();//Get the element that has important parameters
        
        String name = mainParams.text().split(String.valueOf(iter)+".")[1].trim();//Get the name of the restaurant
        if(name == firstRestaurantName) return false;//If the name is the same as the first one, we can stop iterating

        String url = "https://www.tripadvisor.com.br" + mainParams.attr("href");//Get the url of the restaurant
        
        double avgRating = Double.parseDouble(restaurantDoc.select("svg.RWYkj.d.H0").first().attr("aria-label").split(" ")[0].replace(",", "."));
        
        int ratingNo = Integer.parseInt(restaurantDoc.select("span.NoCoR").first().text().split(" ")[0].replace(".","").trim());
        
        Elements tagsAndPrices = restaurantDoc.select("div.bhDlF.bPJHV.eQXRG").get(0).children();
        
        List<String> tags = Arrays.asList(tagsAndPrices.first().text().split(","));
        for(String tag : tags) tag = tag.trim();

        String price = tagsAndPrices.get(1).text().trim();    

        int minPriceTag;
        int maxPriceTag;
        if(price.contains("-")){
            minPriceTag = price.split("-")[0].trim().length();
            maxPriceTag = price.split("-")[1].trim().length();
        }
        else{
            minPriceTag = price.length();
            maxPriceTag = minPriceTag;
        }
        restaurants.add(new Restaurant(filePath,name,url,avgRating,ratingNo,tags,minPriceTag,maxPriceTag));
        return true;
    }

    private void setRatingsStatistics(Document restaurantDoc,Restaurant restaurant){
        Elements reviewsPerStarElems = restaurantDoc.select("span.row_num.is-shown-at-tablet");
        reviewsPerStar = new ArrayList<>();
        for(Element elem : reviewsPerStarElems){
            reviewsPerStar.add(Integer.parseInt(elem.text().replace(".","").trim()));
        }
        restaurant.setReviewsPerStar(reviewsPerStar);
    }


    private boolean getReviewsData(Restaurant restaurant,String url,int iter){
        Document restaurantDoc = null;
        try{restaurantDoc = Jsoup.connect(url).followRedirects(true).get();}
        catch(Exception e){System.out.println("Error: " + e.getMessage());}
        if(!restaurantDoc.location().contains("-Reviews-or") && iter != 0) return false;
        if(iter == 0) setRatingsStatistics(restaurantDoc,restaurant);
        Elements reviews = restaurantDoc.select("div.review-container");
        System.out.println("Reviews Size: " + reviews.size());
        for(Element review : reviews){
            Document reviewDoc = Jsoup.parse(review.html());
            String username = reviewDoc.select("div.info_text.pointer_cursor").first().text().trim();
            int userReviewsNo = Integer.parseInt(reviewDoc.select("span.badgeText").first().text().trim().split(" ")[0]);
            int rating = Integer.parseInt(reviewDoc.select("span.ui_bubble_rating").first().attr("class").split(" ")[1].split("_")[1])/10;
            String title = reviewDoc.select("div.quote").first().text().trim();
            String reviewText = reviewDoc.select("p.partial_entry").first().text().trim();
            String date = reviewDoc.select("div.prw_rup.prw_reviews_stay_date_hsx").first().text().trim().split("visita:")[1].trim();
            String likesStr = reviewDoc.select("span.helpful_text").first().text();
            int likes = likesStr.matches("[0-9]+") ? Integer.parseInt(likesStr) : 0;
            Review newReview = new Review(username,userReviewsNo,rating,title,reviewText,date,likes);
            restaurant.addReview(newReview);
            curReviews += 1;
        }
        return true;
    }
        

    private void setReviews(Restaurant restaurant){
        String url = restaurant.getUrl();
        String firstPartUrl = url.split("Reviews-")[0] + "Reviews";
        String lastPartUrl = "-" + url.split("Reviews-")[1];
        int iter = 1;
        if(!getReviewsData(restaurant,url,0)) return;
        while(true){
            if(!getReviewsData(restaurant,firstPartUrl + "-or" +String.valueOf(REV_PER_PAGE*iter) +lastPartUrl,iter)) break;
            iter++;
            System.out.println("cur("+String.valueOf(curReviews)+");getRev("+String.valueOf(restaurant.getReviews().size())+")");
            if(restaurant.getReviewsSize() == SAVE_AFTER_REVIEWS) restaurant.writeReviews();
        }
        restaurant.writeReviews();
        System.out.println("Restaurant " + restaurant.getName() + " finished 1000 reviews");
    }


    private void retrieveRestaurants(){
        String baseUrl = "https://www.tripadvisor.com.br/RestaurantSearch?Action=PAGE&ajax=1&availSearchEnabled=true&sortOrder=popularity&geo="+geo+"&itags=10591&o=a";
        String url;
        String restaurantStr = "";
        Document restaurantsDoc = null;
        Document restaurantDoc = null;
        Element restaurantElement = null;
        int iter = 0;
        boolean endRestaurants = false;
        System.out.println("Starting to retrieve restaurants");
        while(!endRestaurants){
            url = baseUrl+String.valueOf(REST_PER_PAGE*iter);
            try{restaurantsDoc = Jsoup.connect(url).followRedirects(true).get();}
            catch(Exception e){System.out.println("Error: " + e.getMessage());}


            for(int i=1;i<=REST_PER_PAGE;i++){
                try{
                    restaurantStr = "[data-test=\""+String.valueOf(i)+"_list_item\"]";
                    restaurantElement = restaurantsDoc.select(restaurantStr).first(); 
                }
                catch(Exception e){
                    endRestaurants = true;
                    break;
                }
                restaurantDoc = Jsoup.parse(restaurantElement.html());

                if(!setRestaurantVars(restaurantDoc,i)){
                    endRestaurants = true;
                    break;
                }
                if(iter == 0 && i == 1) firstRestaurantName = restaurants.get(0).getName();
                setReviews(restaurants.get(i-1));
            }

            iter++;
        }
    }

    public TripAdvisor(String city) {
        initialSettings(city);//Initializing the class first params
        manageWindow("--start-maximized");//Managing selenium driver
        seleniumRetrievingPart();//Retrieving data just achievable with selenium
        retrieveRestaurants();//Retrieving data faster with jsoup
    }
}