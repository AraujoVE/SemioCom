import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TweetData {/*
    private TwitterSearchParams keyword;
    private WebDriver driver;
    private CustomFileHandler fh;
    private List<String> filePaths;
    private List<String> tweetIds;
    private List<TweetParams> tweetParams = new ArrayList<TweetParams>();
    private XPathUtils xp;
    private String newEndName;


    public String tryTweetData(String tweetId,String dataType){
        String dataVal;
        try{
            dataVal = xp.attr("//a[@href='"+tweetId+"/"+dataType+"']","innerText").split(" ")[0];
        }
        catch(Exception e){
            dataVal = "0";
        }
        return dataVal;
    }

    public void writeTweets(){
        String path = "./TwitterData/" + keyword.getEncodedStr() +  "/tweets/" + newEndName + ".csv";
        String content = "";
        for(TweetParams tp : tweetParams){
            content += tp.toString() + "\n";
        }
        System.out.println(content);
        System.out.println(path);
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
    }

    public void getTweetsData(String filePath){  
        List<String> splittedFilePath = Arrays.asList(filePath.split("/"));
        newEndName = splittedFilePath.get(splittedFilePath.size()-1);     
        List<String> splittedId;
        String tweetId;
        tweetIds = fh.readFileLines(filePath);
        String _id;
        String at;
        String text;
        String date;
        String likes;
        String retweets;
        String retweetsWithComments;
        List<TweetParams> tweetParams2;
        boolean toContinue;
        boolean continueWhile;
        int iter = 0;
        for(String id : tweetIds){
            splittedId = Arrays.asList(id.split("/"));
            tweetId = id.split("twitter.com")[1];
            driver.get(id);
            _id = tweetId;
            at = splittedId.get(splittedId.size()-3);
            continueWhile = true;
            toContinue = true;
            while(continueWhile){
                continueWhile = false;
                try{
                    xp.elem("//input");
                }
                catch(Exception e){
                    continueWhile = true;
                }
                if(!continueWhile) {
                    toContinue = false;
                    break;
                }
                continueWhile = false;
                toContinue = true;
                try{
                    xp.elem("//div[contains(.,'Tentar novamente')]");
                }
                catch(Exception e){
                    continueWhile = true;
                    toContinue = false;
                }
            }
            if(toContinue) continue;
            text = xp.attr("(//div[@class='css-1dbjc4n r-1s2bzr4'])[1]","innerText");
            date = xp.attr("//a[@href='"+tweetId+"']", "innerText");
            likes = tryTweetData(tweetId,"likes");
            retweets = tryTweetData(tweetId,"retweets");
            retweetsWithComments = tryTweetData(tweetId,"retweets/with_comments");
            //tweetParams.add(new TweetParams(_id,at,text,date,likes,retweets,retweetsWithComments));
            iter++;
            System.out.println("Iter pos =>"+String.valueOf(iter));
        }
        writeTweets();
        tweetParams.clear();
        tweetIds.clear();
    }

    private void manageWindow(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        fh = new CustomFileHandler();
        filePaths = fh.getFilePaths("TwitterData/"+keyword.getEncodedStr()+"/tweetIds");
        xp = new XPathUtils(driver);
    }

    public TweetData(TwitterSearchParams keyword) {
        this.keyword = keyword;
        manageWindow();
        for(String filePath : filePaths) {
            System.out.println("Reading file: "+filePath);
            getTweetsData(filePath);
        }
    }
*/}
