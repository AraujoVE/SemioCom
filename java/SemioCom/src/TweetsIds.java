import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javafx.util.Pair;

public class TweetsIds {
    private WebDriver driver;
    private TwitterSearchParams keyword;
    private XPathUtils xp;
    private int lastIdFirst9Digits = 1000000000;
    private int MAX_TWEETS = 1000;
    private int MAX_TWEET_IDS = 10 * MAX_TWEETS;
    private List<String> tweetIds = new ArrayList<>();
    private int lastTweetIdCount = 0;
    private int MAX_NO_NEW_TWEETS = 1000;
    private int noNewTweets = 0;

    private void sleepS(double seconds) {
        try {
            Thread.sleep(Math.round(seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void manageWindow() {
        driver = new ChromeDriver();
        driver.get(keyword.getTwitterURL());
        driver.manage().window().maximize();
        xp = new XPathUtils(driver);
    }


    private void writeTweetIds(){
        String path = "./TwitterData/tweetIds/" + keyword.getEncodedStr() +  "_" + Integer.toString(lastTweetIdCount) + "-" + Integer.toString(lastTweetIdCount+tweetIds.size())  + ".json";
        String content = String.join("\n", tweetIds);
        File file = new File(path);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastTweetIdCount = tweetIds.size() + 1;
        tweetIds.clear();
    }

    private void retrieveIds() {
        Pair<List<String>,List<WebElement>> all = xp.attrsElemsWT("//a[./time]","href");
        List<String> ids = all.getKey();
        List<WebElement> elems = all.getValue();
        int curId;
        List<String> splittedId = Arrays.asList(ids.get(ids.size()-1).split("/"));
        int nextLastId = Integer.parseInt(splittedId.get(splittedId.size()-1).substring(0,9));

        for(int i = ids.size() - 1; i >= 0; i--){
            splittedId = Arrays.asList(ids.get(i).split("/"));
            curId = Integer.parseInt(splittedId.get(splittedId.size()-1).substring(0,9));
            
            if(curId < lastIdFirst9Digits){
                tweetIds.add(ids.get(i));
            }
            else break;
        }
        if(nextLastId < lastIdFirst9Digits){
            lastIdFirst9Digits = nextLastId;
            noNewTweets = 0;
        }
        else{
            try{
                xp.elem("//div[@class='css-1dbjc4n r-c66ptq']");
            }
            catch(Exception e){
                noNewTweets++;
            }
            sleepS(0.06);
        }
        int tweetIdsLen = tweetIds.size();   
        System.out.println("TweetIdsLen: " + tweetIdsLen);    

        if(tweetIdsLen >= MAX_TWEET_IDS) writeTweetIds();

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",elems.get(elems.size()-1));
    
        return;
    }

    private void getTweetIds() {
        boolean continueLoop = true;
        xp.elemWT("//div");
        while(continueLoop){
            try{
                xp.elem("//div[@class='css-1dbjc4n r-c66ptq']");
            }
            catch(Exception e){
                continueLoop = false;
            }
        }

        boolean continueScrolling = true;
        while(continueScrolling){
            try{
                xp.click("//div[@role='button' and contains(.,'Tentar novamente')]");
            }
            catch(Exception e){
                retrieveIds();
                continueScrolling = true;
                continue;
            }
            if(noNewTweets >= MAX_NO_NEW_TWEETS) continueScrolling = false;
        }
    }

    public TweetsIds(TwitterSearchParams keyword) {
        this.keyword = keyword;
        manageWindow();
        getTweetIds();
    }
}
