
public class App {
    public static void main(String[] args) throws Exception {
        CustomFileHandler fh = new CustomFileHandler();
        fh.createDirectories("./TwitterData");
        TwitterSearchParams params = new TwitterSearchParams("./searchParams/searchText.txt");
        new TweetsIds(params);
    }
}