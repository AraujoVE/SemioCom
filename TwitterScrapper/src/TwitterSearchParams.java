import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwitterSearchParams {
    private String[] texts;
    private String since;
    private String until;    
    private String lang;
    private String encodedStr;
    public TwitterSearchParams(String[] texts, String since, String until, String lang) {
        this.texts = texts;
        this.since = since;
        this.until = until;
        this.lang = lang;
        encodeString();
    }
    public TwitterSearchParams(){}
    public String[] getTexts() {
        return texts;
    }
    public String getSince() {
        return since;
    }
    public String getUntil() {
        return until;
    }
    public String getLang() {
        return lang;
    }
    public String encode(String s) {
        String encodedStr = "";
        try {
            encodedStr = URLEncoder.encode(s, "UTF-8");
            encodedStr = encodedStr.replaceAll("\\+", "%20");
            encodedStr = encodedStr.replaceAll("%21", "!");
            encodedStr = encodedStr.replaceAll("%27", "'");
            encodedStr = encodedStr.replaceAll("%28", "(");
            encodedStr = encodedStr.replaceAll("%29", ")");
            encodedStr = encodedStr.replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {}
        return encodedStr;
    }
    private void encodeString() {
        encodedStr = String.join(" ", texts) + " since:" + since + " until:" + until + " lang:" + lang;
        try {
            encodedStr = URLEncoder.encode(encodedStr, "UTF-8");
            encodedStr = encodedStr.replaceAll("\\+", "%20");
            encodedStr = encodedStr.replaceAll("%21", "!");
            encodedStr = encodedStr.replaceAll("%27", "'");
            encodedStr = encodedStr.replaceAll("%28", "(");
            encodedStr = encodedStr.replaceAll("%29", ")");
            encodedStr = encodedStr.replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {}
    }
    public String getEncodedStr() {
        return encodedStr;
    }

    public String getTwitterURL(){
        return "https://twitter.com/search?q=" +  encodedStr  + "&src=typed_query&f=live";
    }
}
