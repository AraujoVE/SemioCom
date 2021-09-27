import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static List<TweetsIds> tweetsIds = new ArrayList<TweetsIds>();

    private static void createDirectory(String path){
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdir();
        }
    }
    
    private static void createDirectories(){
        createDirectory("./TwitterData");
        createDirectory("./TwitterData/tweetIds");
        createDirectory("./TwitterData/tweets");
    }
    public static void main(String[] args) throws Exception {
        createDirectories();
        String[] texts = new String[] {"bolsonaro"};
        TwitterSearchParams params = new TwitterSearchParams(texts,"2020-09-23", "2020-09-24","pt");
        new TweetsIds(params);
    }
}






