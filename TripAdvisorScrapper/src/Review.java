public class Review {
    String username;
    int userReviewsNo;
    int rating;
    String title;
    String review;
    String date;
    int likes;

    public Review(String username, int userReviewsNo, int rating, String title, String review, String date, int likes) {
        this.username = username;
        this.userReviewsNo = userReviewsNo;
        this.rating = rating;
        this.title = title;
        this.review = review;
        this.date = date;
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
    public String getDate(){
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
    public void setDate(String date){
        this.date = date;
    }
    public void setLikes(int likes){
        this.likes = likes;
    }

    public String toFileStr(){
        return username + ":::" + userReviewsNo + ":::" + rating + ":::" + title.replaceAll("\\s+", ":.:").replace(":.:"," ") + ":::" + review.replaceAll("\\s+", ":.:").replace(":.:"," ") + ":::" + date + ":::" + likes + "\n";
    }

}
