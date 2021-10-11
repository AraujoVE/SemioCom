import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;	
public class TweetsIds {
    private WebDriver driver;
    private TwitterSearchParams keyword;
    private XPathUtils xp;
    private int lastIdFirst9Digits = 1000000000;
    private int MAX_TWEETS = 1000;
    private int lastTweetIdCount = 0;
    private int MAX_NO_NEW_TWEETS = 1000;
    private int noNewTweets = 0;
    private CustomFileHandler fh;
    private List<TweetParams> tweets = new ArrayList<>();


    private void writeTweets(){
        //Structuring tweet path
        String path = "./TwitterData/" + keyword.getEncodedStr() +  "/" + Integer.toString(lastTweetIdCount) + "-" + Integer.toString(lastTweetIdCount+tweets.size())  + ".csv";
        //Joining content texts
        String content = "";
        for(TweetParams tp : tweets){
            content += tp.toString() + "\n";
        }
        //Writing to file
        fh.writeFile(path, content);
        //Updating tweet count
        lastTweetIdCount += tweets.size() + 1;
        //Clearing tweets
        tweets.clear();

    }


    private void setTweetParams(Document articleDoc,Element timeElem,String id){
        List<String> tweetDatasStr = new ArrayList<>(); //TweetDatas will be used to get the tweet replies, retweets and likes texts as strings 
        TweetParams tp = new TweetParams(); //Initializing the tweet params
        
        tp.setId(id); //Setting the id
        tp.setAt(id.split("/")[1]); //Setting the at of the tweet
        tp.setText(articleDoc.select("div.css-901oao.r-18jsvk2.r-37j5jr.r-a023e6.r-16dba41.r-rjixqe.r-bcqeeo.r-bnwqim.r-qvutc0").first().text()); //Setting the text of the tweet
        Elements tweetDatas = articleDoc.select("div.css-1dbjc4n.r-xoduu5.r-1udh08x");//Getting tweet replies, likes and retweets
        for(int j=0;j<3;j++) tweetDatasStr.add(tweetDatas.get(j).text());//Loading the texts of each data type to the corresponding list
        tp.setReplies(tweetDatasStr.get(0).matches("[0-9]+") ? tweetDatasStr.get(0) : "0"); //Setting the replies
        tp.setRetweets(tweetDatasStr.get(1).matches("[0-9]+") ? tweetDatasStr.get(1) : "0"); //Setting the retweets
        tp.setLikes(tweetDatasStr.get(2).matches("[0-9]+") ? tweetDatasStr.get(2) : "0"); //Setting the likes
        tp.setDate(timeElem.attr("datetime")); //Setting the date

        tweets.add(tp); //Adding the tweet params to the list of tweets
    }


    private void retrieveIds() {
        List<WebElement> elems = xp.elemsWT("//time"); // To, in the end, find the element to move on (time.size()-1)
        int nextLastId = 0;
        Document doc;
        doc = Jsoup.parse(xp.attr("/html","innerHTML")); //Get the page html
        List<Document> articleDocs = new ArrayList<>(); //List of articles as docs
        for(Element a : doc.select("article")) articleDocs.add(Jsoup.parse(a.toString())); //Iterate through articles and add to list fo docs
        int integerId;//Current tweet id

        for(int i = articleDocs.size() - 1; i>=0 ;i--){ //Iterate through articles in reverse order
            
            Element timeElem = articleDocs.get(i).select("time").first();//get time element
            String curId = timeElem.parent().attr("href");//get the id by getting the href attribute of the time element parent
            integerId = Integer.parseInt((curId.split("/")[curId.split("/").length - 1].substring(0,9))); //get the id by parsing the last 9 digits of the id last splited by '/' part
            if(i == articleDocs.size()-1) nextLastId = integerId; //If we are on the last article, set the nextLastId to the current id
            
            //If the integerId is older - its id is smaller than the last id - we can add it to the list
            if(integerId < lastIdFirst9Digits) setTweetParams(articleDocs.get(i), timeElem, curId);
            else break; //Else, we can stop iterating
        }

        if(nextLastId < lastIdFirst9Digits){ //If we had a older tweet id found, we can update the last id
            lastIdFirst9Digits = nextLastId; //Updating the last id
            noNewTweets = 0; //Resetting the noNewTweets counter
        }
        else{//Else, we increase the noNewTweets counter and wait for some time
            noNewTweets++;
            xp.sleepS(0.06);
        }
        int tweetsLen = tweets.size(); //Getting the length of the tweets list
        System.out.println("TweetsLen: " + tweetsLen); //Printing the length of the tweets list

        //Remover dps de mostrar
        if(tweetsLen > 0){
            for(int j = 0;j<tweetsLen;j++){
                System.out.println(tweets.get(j).toString()); //Printing the tweets
            }
            xp.sleepS(100000);
        }
        //Remover dps de mostrar^

        if(tweetsLen >= MAX_TWEETS) writeTweets(); //If the tweetsLen reached a maximum value, the tweets are written in a file

        //We than try to go to the last 'time' element to scroll the page
        try{
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",elems.get(elems.size()-1));
        }
        catch(Exception e){}

        return;
    }

    private void getTweetIds() {
        boolean continueLoop = true;
        //Waiting until the last element is not reachble anymore
        while(continueLoop){
            try{
                xp.elem("//div[@class='css-1dbjc4n r-c66ptq']");
            }
            catch(Exception e){
                continueLoop = false;
            }
        }

        boolean continueScrolling = true;
        //While until there are no more tweets to get
        while(continueScrolling){
            try{
                //Try to click the button to try again
                xp.click("//div[@role='button' and contains(.,'Tentar novamente')]");
            }
            catch(Exception e){
                //If not possible, retrieve existent tweets
                retrieveIds();
            }
            // If there are no new tweets for MAX_NO_NEW_TWEETS consectutive times, stop scrolling
            if(noNewTweets >= MAX_NO_NEW_TWEETS) continueScrolling = false;
        }
    }


    private void reduceScreenZoom(double reductTimes){
        xp.elemWT("//div");//Waiting for the page to load
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom = '"+String.valueOf(reductTimes/100)+"'"); //Changing zoom to specified value      
    }

    private void manageWindow(String ... strOptions) {
        ChromeOptions options = new ChromeOptions();//Creating Chrome driver options
        for(String option : strOptions) options.addArguments(option);//Adding options to the driver
        driver = new ChromeDriver(options);//Creating Chrome driver with given options
        driver.get(keyword.getTwitterURL());//Go to choosen page
        xp = new XPathUtils(driver);//Initializing xPathUtils helper
        reduceScreenZoom(50);//Reduce the screen zoom
    }

    private void initialSettings(TwitterSearchParams keyword){
        this.keyword = keyword;//Set the keyword in the class
        fh = new CustomFileHandler();//Initializing the FileHandler
        fh.createDirectories("./TwitterData/"+keyword.getEncodedStr());//Creating search directory
    }

    public TweetsIds(TwitterSearchParams keyword) {
        initialSettings(keyword);//Initializing the class first params
        manageWindow("--start-maximized");//Managing selenium driver
        getTweetIds(); //Getting tweet ids
    }
}