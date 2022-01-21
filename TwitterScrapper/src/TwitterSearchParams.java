import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class TwitterSearchParams {
    private String searchText;
    private String originalEncodedStr;
    private String encodedStr;
    CustomFileHandler fh = new CustomFileHandler();

    public TwitterSearchParams(String path) {
        setSearchText(path);
        encodedStr = searchText;
        encodeString();
        originalEncodedStr = encodedStr;
    }
    public TwitterSearchParams(){}

    private String dateToSec(String dateText,String timeZone){
        LocalDateTime ldt = LocalDateTime.parse(dateText);
        ZoneId z = ZoneId.of(timeZone);
        ZonedDateTime zdt = ldt.atZone( z ) ; 
        Instant instant = zdt.toInstant();
        long epochSecond = instant.getEpochSecond();
        return String.valueOf(epochSecond);
    }

    private void setSearchText(String path) {
        System.out.println("Reading search text from file: " + path);
        List<String> fileLines = fh.readFileLines(path);
        String searchText = Pattern.compile("(?<=(since|until)_time:)(.+?)(?=$| )").matcher(fileLines.get(0)).replaceAll(x -> dateToSec(x.group(),fileLines.get(1)));
        
        this.searchText = searchText;
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

    public String getOriginalEncodedStr() {
        return originalEncodedStr;
    }

    public void setEncodeIdString(String url){
        encodedStr = searchText.replaceAll("until_time:.+?(?=$| )", "max_id:" + url);
        encodeString();
    }

    public String getTwitterURL(){
        return "https://twitter.com/search?q=" +  encodedStr  + "&src=typed_query&f=live";
    }
}
