import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.exec.ExecuteException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;

public class Restaurant {
    String reviewsCsvHeading = "nome_do_restaurante|username_do_usuario|numero_de_reviews_feitas_pelo_usuario|nota_dada_pelo_usuario_ao_restaurante|titulo|texto|data_do_review|data_da_visita|numero_de_likes_recebidos_no_review\n";
    String filePath;
    String name;
    String url;
    String basePath;
    double avgRating;
    int ratingNo;
    List<String> tags = new ArrayList<>();
    int minPriceTag;
    int maxPriceTag;
    double avgPriceTag;
    List<Integer> reviewsPerStar = new ArrayList<Integer>();
    String address;
    String neighborhood;

    List<Review> reviews = new ArrayList<Review>();
    boolean headingWritten = false;
    CustomFileHandler fh;
    String minDate = "1 de janeiro de 1970";
    String maxDate = "30 de setembro de 2021";
    int minDateInt;
    int maxDateInt;
    private int REV_PER_PAGE = 10;
    private int SAVE_AFTER_REVIEWS = 1000;
    HashMap<String, Integer> dateToIntMap = new HashMap<String, Integer>();
    int TERMINAL_NUMBER;
    int parallelProgs;
    String logFilePath;
    XPathUtils xp;

    public Restaurant(XPathUtils xp,String baseFilePath,String name, String url, double avgRating, int ratingNo, List<String> tags, int minPriceTag, int maxPriceTag,HashMap<String, Integer> dateToIntMap,int TERMINAL_NUMBER,int parallelProgs) {
        this.xp = xp;
        basePath = baseFilePath;
        filePath = basePath+ "/" + name +".csv";
        fh = new CustomFileHandler();
        this.name = name.replace("|"," ").replaceAll("\\s+", ":.:.:.:").replace(":.:.:.:"," ").trim();
        System.out.println("Restaurant: " + name);
        this.url = url.replace("|"," ").replaceAll("\\s+", ":.:.:.:").replace(":.:.:.:"," ").trim();
        this.avgRating = avgRating;
        this.ratingNo = ratingNo;
        for(String tag: tags){
            this.tags.add(tag.replace("|"," ").replace(";"," ").replaceAll("\\s+", ":.:.:.:").replace(":.:.:.:"," "));
        }
        this.minPriceTag = minPriceTag;
        this.maxPriceTag = maxPriceTag;
        avgPriceTag = Double.valueOf(minPriceTag + maxPriceTag) / 2.0;
        this.dateToIntMap = dateToIntMap;
        maxDateInt = dateToInt(maxDate);
        minDateInt = dateToInt(minDate);
        this.TERMINAL_NUMBER = TERMINAL_NUMBER;
        this.parallelProgs = parallelProgs;
        logFilePath = basePath + "/restaurantsList_"+TERMINAL_NUMBER +"_" + parallelProgs +".csv";
        fh.writeFile(filePath,reviewsCsvHeading);
    }

    public Restaurant(){}

    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
    public double getAvgRating(){
        return avgRating;
    }
    public int getRatingNo(){
        return ratingNo;
    }
    public List<String> getTags(){
        return tags;
    }
    public int getMinPriceTag(){
        return minPriceTag;
    }
    public int getMaxPriceTag(){
        return maxPriceTag;
    }
    public double getAvgPriceTag(){
        return avgPriceTag;
    }
    public List<Integer> getReviewsPerStar(){
        return reviewsPerStar;
    }
    public List<Review> getReviews(){
        return reviews;
    }


    public void setName(String name){
        this.name = name;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setAvgRating(int avgRating){
        this.avgRating = avgRating;
    }
    public void setRatingNo(int ratingNo){
        this.ratingNo = ratingNo;
    }
    public void setTags(List<String> tags){
        this.tags = tags;
    }
    public void setMinPriceTag(int minPriceTag){
        this.minPriceTag = minPriceTag;
    }
    public void setMaxPriceTag(int maxPriceTag){
        this.maxPriceTag = maxPriceTag;
    }
    public void calcAvgPriceTag(int maxPriceTag,int minPriceTag){
        this.maxPriceTag = maxPriceTag;
        this.minPriceTag = minPriceTag;
        avgPriceTag = (minPriceTag + maxPriceTag) / 2;
    }
    public void calcAvgPriceTag(){
        avgPriceTag = (minPriceTag + maxPriceTag) / 2;
    }
    public void setReviewsPerStar(List<Integer> reviewsPerStar){
        this.reviewsPerStar = reviewsPerStar;
    }
    public void setReviews(List<Review> reviews){
        this.reviews = reviews;
    }

    public String toStringParams(){
        String finalString = "";
        finalString += "Name: " + name + "\n";
        finalString += "URL: " + url + "\n";
        finalString += "Average Rating: " + avgRating + "\n";
        finalString += "Number of Ratings: " + ratingNo + "\n";
        finalString += "Tags: " + String.join(",", tags) + "\n";
        finalString += "Min Price Tag: " + minPriceTag + "\n";
        finalString += "Max Price Tag: " + maxPriceTag + "\n";
        finalString += "Average Price Tag: " + avgPriceTag + "\n";

        return finalString;
    }

    public void addReview(Review review){
        reviews.add(review);
    }

    public int getReviewsSize(){
        return reviews.size();
    }

    public String toFileStr(){
        String text = name + "|" + url + "|" + avgRating + "|" + ratingNo + "|" + tags.get(0);
        
        for(int i = 1; i < tags.size(); i++){
            text += ";" + tags.get(i).trim();
        }
        
        text += "|" + minPriceTag + "|" + maxPriceTag + "|" + avgPriceTag + "|" + address + "|" + neighborhood;
        for(int i = 0; i < reviewsPerStar.size(); i++){
            text += "|" + reviewsPerStar.get(i);
        }
        text += "\n";
        return text;
    }

    public void writeReviews(){
        if(!headingWritten){
            fh.writeFile(filePath,reviewsCsvHeading);
            headingWritten = true;
        }
        for(Review review : reviews){
            fh.appendFile(filePath,review.toFileStr(name));
        }
    }

    private int dateToInt(String date){
        String[] dateParts = date.split(" ");
        int day = 0;
        int month = 0;
        int year = 0;
        if(dateParts.length == 5){
            day = Integer.parseInt(dateParts[0]);
            month = dateToIntMap.get(dateParts[2].trim());
            year = Integer.parseInt(dateParts[4]);
        }
        else if(dateParts.length == 3) {
            day = 0;
            month = dateToIntMap.get(dateParts[0].trim());
            year = Integer.parseInt(dateParts[2]);
        }

        return year*10000 + month*100 + day;
    }

    private void setRatingsStatistics(Document restaurantDoc){
        Elements reviewsPerStarElems = restaurantDoc.select("span.row_num.is-shown-at-tablet");
        reviewsPerStar = new ArrayList<>();
        for(Element elem : reviewsPerStarElems){
            reviewsPerStar.add(Integer.parseInt(elem.text().replace(".","").trim()));
        }
    }

    private void setAddress(Document restaurantDoc){
        try{
            address = restaurantDoc.select("[href=\"#MAPVIEW\"]").first().text();
        }
        catch(Exception e){
            System.out.println("url >>"+restaurantDoc.location());
        }
        address = restaurantDoc.select("[href=\"#MAPVIEW\"]").first().text();
        try{
            neighborhood = restaurantDoc.select("span.ui_icon.neighborhoods.bPFFU.QKJnF + span").first().child(0).text().trim();
        }
        catch(Exception e){
            neighborhood = "-1";
        }
    }


    private Document structurePageSelenium(String url){
        xp.goToPage(url);
        xp.elemWT("//div");
        List<WebElement> expandTextElements = xp.elems("//span[@class='taLnk ulBlueLinks' and contains(.,'Mais')]");
        for(WebElement elem : expandTextElements){
            try{
                elem.click();
            }
            catch(Exception e){}
        }
        while(xp.elems("//span[@class='taLnk ulBlueLinks' and contains(.,'Mais')]").size() > 0) xp.sleepS(0.05);
        return Jsoup.parse(xp.attr("/html","innerHTML")); //Get the page html
    }

    private boolean getReviewsData(String url,int iter){
        Document jsoupDoc;

        try{jsoupDoc = Jsoup.connect(url).get();}
        catch(Exception e){return true;}

        Document restaurantDoc = structurePageSelenium(url);

        if(!jsoupDoc.location().contains("-Reviews-or") && iter != 0) return false;
        if(iter == 0){
            setRatingsStatistics(restaurantDoc);
            setAddress(restaurantDoc);
        }
        Elements reviews = restaurantDoc.select("div.review-container");
        for(Element review : reviews){
            Document reviewDoc = Jsoup.parse(review.html());
            String username = "";
            try{
                username = reviewDoc.select("div.info_text.pointer_cursor").first().text().trim();
            }
            catch(Exception e){
                continue;
            }
            String userReviewsNoStr = "";
            int userReviewsNo;
            int rating = Integer.parseInt(reviewDoc.select("span.ui_bubble_rating").first().attr("class").split(" ")[1].split("_")[1])/10;
            String title = reviewDoc.select("div.quote").first().text().trim();
            String reviewText = reviewDoc.select("p.partial_entry").first().text().trim();
            String date = reviewDoc.select("span.ratingDate").first().attr("title").trim();
            int dateInt = dateToInt(date);
            if(dateInt > maxDateInt) continue;
            if(dateInt < minDateInt) return false;
            String likesStr = "";
            int likes;
            try{
                likesStr = reviewDoc.select("span.helpful_text").first().text();
                likes = likesStr.matches("[0-9]+") ? Integer.parseInt(likesStr.replace(".","")) : 0;
                userReviewsNo = Integer.parseInt(reviewDoc.select("span.badgeText").first().text().trim().split(" ")[0].replace(".", ""));
            }
            catch(Exception e){
                try{
                    System.out.println("Deu ruim 1");
                    likesStr = reviewDoc.select("span.ui_icon.pencil-paper + span.badgetext").first().text();
                    likes = likesStr.matches("[0-9]+") ? Integer.parseInt(likesStr.replace(".","")) : 0;
                    userReviewsNoStr = reviewDoc.select("span.ui_icon.thumbs-up + span.badgetext").first().text();                
                    userReviewsNo = userReviewsNoStr.matches("[0-9]+") ? Integer.parseInt(likesStr.replace(".","")) : 0;
                }
                catch(Exception e1){
                    try{
                        System.out.println("Deu ruim 2");
                        userReviewsNo = Integer.parseInt(reviewDoc.select("span.badgeText").first().text().trim().split(" ")[0].replace(".", ""));
                        likes = -1;
                    }
                    catch(Exception e2){
                        System.out.println("Deu ruim Total");
                        userReviewsNo = -1;
                        likes = -1;
                    }
                }
            }

            int visitedIn = 0;
            try{
                visitedIn = dateToInt(reviewDoc.select("div.prw_rup.prw_reviews_stay_date_hsx").first().text().split("visita:")[1].trim());
            }
            catch(Exception e){
                visitedIn = -1;
            }

            Review newReview = new Review(username,userReviewsNo,rating,title,reviewText,dateInt,visitedIn,likes);
            this.reviews.add(newReview);
        }
        return true;
    }

    public void setReviews(){
        String firstPartUrl = url.split("Reviews-")[0] + "Reviews";
        String lastPartUrl = "-" + url.split("Reviews-")[1];
        int iter = 0;

        fh.clearFile(filePath);

        if(getReviewsData(url,0)){
            iter++;
            while(true){
                if(!getReviewsData(firstPartUrl + "-or" +String.valueOf(REV_PER_PAGE*iter) +lastPartUrl,iter)) break;
                iter++;
                if(reviews.size() % SAVE_AFTER_REVIEWS == 0) writeReviews();
                System.out.println("Reviews: " + String.valueOf(iter*REV_PER_PAGE));
            }
        }
        writeReviews();
        fh.appendFile(logFilePath,toFileStr());
        System.out.println("Restaurant " + name + " finished its reviews");
    }
}
