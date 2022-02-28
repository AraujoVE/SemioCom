import java.util.HashMap;

public class TweetParams {
    private String url;
    private String user;
    private String id;
    private String text;
    private String date;
    private String hasMedia;
    private String isReply;
    HashMap<String,String> dictWords = new HashMap<String,String>();
    
    public TweetParams(){
        dictWords.put("<LINK>", "link");
        dictWords.put("<MEDIA>", "media");
    }
    public TweetParams(String url, String text, String date, String hasMedia, String isReply) {
        this();
        this.url = url;
        user = url.split("/")[1];
        id = url.split("/")[3];
        this.text = text;
        this.date = date;
        this.hasMedia = hasMedia;
        this.isReply = isReply;
    }
    
    public String getUrl() {
        return url;
    }
    public String getUser() {
        return user;
    }
    public String getId() {
        return id;
    }
    public void setUrl(String url) {
        this.url = "https://twitter.com" + url;
        user = url.split("/")[1];
        id = url.split("/")[3];
    }
    
    public String getText() {
        return text;
    }
    public void setText(String text) {
        String auxText = text.trim().replace("|"," ").replaceAll("\\s+", "|").replace("|", " ");
        for(String replaceWord : dictWords.keySet()){
            auxText = auxText.replace(replaceWord, dictWords.get(replaceWord));
        }
        this.text = auxText;
    }
    
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getIsReply() {
        return isReply;
    }
    public void setIsReply(String isReply) {
        this.isReply = isReply;
    }

    public String getHasMedia() {
        return hasMedia;
    }
    public void setHasMedia(String hasMedia) {
        this.hasMedia = hasMedia;
    }

    public String toString() {
        return url + "|" + user + "|" + id + "|" + date + "|" + text + "|" + hasMedia + "|" + isReply;
    }
}