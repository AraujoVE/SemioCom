public class TweetParams {
    private String id;
    private String at;
    private String text;
    private String date;
    private String likes;
    private String retweets;
    private String retweetsWithComments;
    private String replies;
    public TweetParams(){}
    public TweetParams(String id, String at, String text, String date, String likes, String retweets, String retweetsWithComments, String replies) {
        this.id = id;
        this.at = at;
        this.text = text;
        this.date = date;
        this.likes = likes;
        this.retweets = retweets;
        this.retweetsWithComments = retweetsWithComments;
        this.replies = replies;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAt() {
        return at;
    }
    public void setAt(String at) {
        this.at = at;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getLikes() {
        return likes;
    }
    public void setLikes(String likes) {
        this.likes = likes;
    }
    public String getRetweets() {
        return retweets;
    }
    public void setRetweets(String retweets) {
        this.retweets = retweets;
    }
    public String getRetweetsWithComments() {
        return retweetsWithComments;
    }
    public void setRetweetsWithComments(String retweetsWithComments) {
        this.retweetsWithComments = retweetsWithComments;
    }
    public String getReplies() {
        return replies;
    }
    public void setReplies(String replies) {
        this.replies = replies;
    }


    public String toString() {
        return id + "," + at  + "," + date + "," + likes + "," + retweets + "," + replies + "," + text.trim().replaceAll("\\s+", "_").replace("_", " ");
    }
}