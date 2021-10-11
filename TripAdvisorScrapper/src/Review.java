public class Review {
    String username;
    int userReviewsNo;
    int rating;
    String title;
    String review;
    int date;
    int likes;
    int visitedIn;

    public Review(String username, int userReviewsNo, int rating, String title, String review, int date,int visitedIn, int likes) {
        this.username = username.replace("|"," ").replaceAll("\\s+", ":.:.:.:").replace(":.:.:.:"," ").trim();
        this.userReviewsNo = userReviewsNo;
        this.rating = rating;
        this.title = title.replace("|"," ").replaceAll("\\s+", ":.:.:.:").replace(":.:.:.:"," ").trim();
        this.review = review.replace("|"," ").replaceAll("\\s+", ":.:.:.:").replace(":.:.:.:"," ").trim();
        this.date = date;
        this.visitedIn = visitedIn;
        this.likes = likes;
    }    


    public String getUsername(){
        return username;
    }
    public int getUserReviewsNo(){
        return userReviewsNo;
    }
    public int getRating(){
        return rating;
    }
    public String getTitle(){
        return title;
    }
    public String getReview(){
        return review;
    }
    public int getDate(){
        return date;
    }
    public int getLikes(){
        return likes;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setUserReviewsNo(int userReviewsNo){
        this.userReviewsNo = userReviewsNo;
    }
    public void setRating(int rating){
        this.rating = rating;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setReview(String review){
        this.review = review;
    }
    public void setDate(int date){
        this.date = date;
    }
    public void setLikes(int likes){
        this.likes = likes;
    }

    public String toFileStr(String name){
        return name + "|" + username + "|" + userReviewsNo + "|" + rating + "|" + title + "|" + review + "|" + date + "|" + visitedIn + "|" + likes + "\n";
    }

}
