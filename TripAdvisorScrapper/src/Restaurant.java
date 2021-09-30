import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    String filePath;
    String name;
    String url;
    double avgRating;
    int ratingNo;
    List<String> tags = new ArrayList<>();
    int minPriceTag;
    int maxPriceTag;
    int avgPriceTag;
    List<Integer> reviewsPerStar = new ArrayList<Integer>();
    List<Review> reviews = new ArrayList<Review>();
    boolean headingWritten = false;
    CustomFileHandler fh;

    public Restaurant(String baseFilePath,String name, String url, double avgRating, int ratingNo, List<String> tags, int minPriceTag, int maxPriceTag, List<Integer> reviewsPerStar,List<Review> reviews) {
        filePath = baseFilePath+"/"+name+".txt";
        fh = new CustomFileHandler();
        fh.createFile(filePath);
        this.name = name;
        this.url = url;
        this.avgRating = avgRating;
        this.ratingNo = ratingNo;
        this.tags = tags;
        this.minPriceTag = minPriceTag;
        this.maxPriceTag = maxPriceTag;
        avgPriceTag = (minPriceTag + maxPriceTag) / 2;
        this.reviews = reviews;
        this.reviewsPerStar = reviewsPerStar;
    }

    public Restaurant(String baseFilePath,String name, String url, double avgRating, int ratingNo, List<String> tags, int minPriceTag, int maxPriceTag, List<Integer> reviewsPerStar) {
        this.filePath = baseFilePath+"/"+name+".txt";
        fh = new CustomFileHandler();
        fh.createFile(filePath);
        this.name = name;
        this.url = url;
        this.avgRating = avgRating;
        this.ratingNo = ratingNo;
        this.tags = tags;
        this.minPriceTag = minPriceTag;
        this.maxPriceTag = maxPriceTag;
        avgPriceTag = (minPriceTag + maxPriceTag) / 2;
        this.reviewsPerStar = reviewsPerStar;

    }

    public Restaurant(String baseFilePath,String name, String url, double avgRating, int ratingNo, List<String> tags, int minPriceTag, int maxPriceTag) {
        this.filePath = baseFilePath+"/"+name+".txt";
        fh = new CustomFileHandler();
        fh.createFile(filePath);
        this.name = name;
        this.url = url;
        this.avgRating = avgRating;
        this.ratingNo = ratingNo;
        this.tags = tags;
        this.minPriceTag = minPriceTag;
        this.maxPriceTag = maxPriceTag;
        avgPriceTag = (minPriceTag + maxPriceTag) / 2;
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
    public int getAvgPriceTag(){
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

    private String toFileStr(){
        String text = name + ":::" + url + ":::" + avgRating + ":::" + ratingNo + ":::" + String.join("::", tags) + ":::" + minPriceTag + ":::" + maxPriceTag + ":::" + avgPriceTag + ":";
        for(int i = 0; i < reviewsPerStar.size(); i++){
            text += "::" + reviewsPerStar.get(i);
        }
        text += "\n";
        return text;
    }

    public void writeReviews(){
        if(!headingWritten){
            fh.appendFile(filePath, toFileStr());
            headingWritten = true;
        }
        for(Review review : reviews){
            fh.appendFile(filePath,review.toFileStr());
        }
    }
}
