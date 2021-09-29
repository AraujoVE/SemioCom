
public class App {
    public static void main(String[] args) throws Exception {
        CustomFileHandler fh = new CustomFileHandler();
        fh.createDirectories("./TwitterData");
        String[] texts = new String[] {"bolsonaro"};
        TwitterSearchParams params = new TwitterSearchParams(texts,"2020-09-23", "2020-09-24","pt");
        new TweetsIds(params);
    }
}






