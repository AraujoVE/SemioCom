import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    private List<String> tweetIds = new ArrayList<>();
    private int lastTweetIdCount = 0;
    private int MAX_NO_NEW_TWEETS = 1000;
    private int noNewTweets = 0;
    private CustomFileHandler fh;
    private List<TweetParams> tweets = new ArrayList<>();
    Element lastElement;



    private void manageWindow() {
        driver = new ChromeDriver();
        driver.get(keyword.getTwitterURL());
        driver.manage().window().maximize();
        
        xp = new XPathUtils(driver);
    }

    public void clearTweetIds() {
        tweetIds.clear();
    }

    public void updateLastTweetIdCount() {
        lastTweetIdCount = tweetIds.size() + 1;
    }

    public String getKeywordEncStr() {
        return keyword.getEncodedStr();
    }

    public int getLastTweetIdCount() {
        return lastTweetIdCount;
    }

    public void setLastTweetIdCount(int newVal){
        lastTweetIdCount = newVal;
    }

    public List<String> getTweetIdsVal() {
        return tweetIds;
    }


    private void writeTweets(){
        String path = "./TwitterData/" + keyword.getEncodedStr() +  "/" + Integer.toString(lastTweetIdCount) + "-" + Integer.toString(lastTweetIdCount+tweets.size())  + ".csv";
        String content = "";
        for(TweetParams tp : tweets){
            content += tp.toString() + "\n";
        }
        fh.writeFile(path, content);
        lastTweetIdCount += tweets.size() + 1;
        tweets.clear();
    }



    private void retrieveIds() {
        List<WebElement> elems = xp.elemsWT("//time");
        int nextLastId = 0;
        String idPart;
        Elements tweetDatas;
        String html;

        Document doc;
        doc = Jsoup.parse(xp.attr("/html","innerHTML"));
        List<Document> articleDocs = new ArrayList<>();
        for(Element a : doc.select("article")) articleDocs.add(Jsoup.parse(a.toString()));
        int curId;

        for(int i = articleDocs.size() - 1; i>=0 ;i--){
            Element timeElem = articleDocs.get(i).select("time").first();
            String id = timeElem.parent().attr("href");
            curId = Integer.parseInt((id.split("/")[id.split("/").length - 1].substring(0,9)));
            if(i == articleDocs.size()-1) nextLastId = curId;

            if(curId < lastIdFirst9Digits){
                lastElement = timeElem;
                TweetParams tp = new TweetParams();
                tp.setId(id);
                tp.setAt(id.split("/")[1]);
                tp.setText(articleDocs.get(i).select("div.css-901oao.r-18jsvk2.r-37j5jr.r-a023e6.r-16dba41.r-rjixqe.r-bcqeeo.r-bnwqim.r-qvutc0").first().text());
                tweetDatas = articleDocs.get(i).select("div.css-1dbjc4n.r-xoduu5.r-1udh08x");
                List<String> tweetDatasStr = new ArrayList<>();
                for(int j=0;j<3;j++) tweetDatasStr.add(tweetDatas.get(j).text());
                tp.setReplies(tweetDatasStr.get(0).matches("[0-9]+") ? tweetDatasStr.get(0) : "0");
                tp.setRetweets(tweetDatasStr.get(1).matches("[0-9]+") ? tweetDatasStr.get(1) : "0");
                tp.setLikes(tweetDatasStr.get(2).matches("[0-9]+") ? tweetDatasStr.get(2) : "0");
                tp.setDate(timeElem.attr("datetime"));

                tweets.add(tp);

            }
            else break;
        }

        /*
        for(int i = ids.size() - 1; i >= 0; i--){
            splittedId = Arrays.asList(ids.get(i).split("/"));
            curId = Integer.parseInt(splittedId.get(splittedId.size()-1).substring(0,9));
            
            if(curId < lastIdFirst9Digits){
                tweetIds.add(ids.get(i));
                idPart = ids.get(i).split("twitter.com")[1];
                TweetParams tp = new TweetParams();
                html = xp.attr("//article[.//a[@href='"+idPart+"']]","innerHTML");
                doc = Jsoup.parse(html);
                tp.setId(idPart);
                tp.setAt(idPart.split("/")[1]);
                tp.setText(doc.select("div.css-901oao.r-18jsvk2.r-37j5jr.r-a023e6.r-16dba41.r-rjixqe.r-bcqeeo.r-bnwqim.r-qvutc0").first().text());
                tweetDatas = doc.select("div.css-1dbjc4n.r-xoduu5.r-1udh08x");
                for(int j=0;j<3;j++) tweetDatasStr.add(tweetDatas.get(j).text());
                tp.setReplies(tweetDatasStr.get(0).matches("[0-9]+") ? tweetDatasStr.get(0) : "0");
                tp.setRetweets(tweetDatasStr.get(1).matches("[0-9]+") ? tweetDatasStr.get(1) : "0");
                tp.setLikes(tweetDatasStr.get(2).matches("[0-9]+") ? tweetDatasStr.get(2) : "0");
                tp.setDate(doc.select("time").first().attr("datetime"));

                tweets.add(tp);
            }
            else break;
        }*/
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
            xp.sleepS(0.06);
        }
        int tweetsLen = tweets.size();   
        System.out.println("TweetsLen: " + tweetsLen);    

        if(tweetsLen >= MAX_TWEETS) writeTweets();

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
        fh = new CustomFileHandler(this);
        fh.createDirectories("./TwitterData/"+keyword.getEncodedStr());
        manageWindow();
        getTweetIds();
    }
}