import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javafx.util.Pair;

public class XPathUtils {
    private WebDriver driver;
    private static int DELAY_TIME = 50;
    private static int DELAY_TYPING_TIME = 50;
    
    public XPathUtils(WebDriver driver) {
        this.driver = driver;
    }

    public void type(String xpath, String text) {
        // Selenium type
        WebElement element = elem(xpath);
        //Type in element the text
        for(char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
            try {
                Thread.sleep(DELAY_TYPING_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
    public static void type(WebDriver driver,String xpath, String text) {
        // Selenium type
        WebElement element = elem(driver,xpath);
        //Type in element the text
        for(char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
            try {
                Thread.sleep(DELAY_TYPING_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
    public void typeWT(String xpath, String text) {
        // Selenium type
        WebElement element = elemWT(xpath);
        //Type in element the text
        for(char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
            try {
                Thread.sleep(DELAY_TYPING_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
    public static void typeWT(WebDriver driver,String xpath, String text) {
        // Selenium type
        WebElement element = elemWT(driver,xpath);
        //Type in element the text
        for(char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
            try {
                Thread.sleep(DELAY_TYPING_TIME);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void sleepS(double seconds) {
        try {
            Thread.sleep(Math.round(seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public void click(String xpath) {
        driver.findElement(By.xpath(xpath)).click();
    }
    public static void click(WebDriver driver,String xpath) {
        driver.findElement(By.xpath(xpath)).click();
    }
    public void clickWT(String xpath) {
        boolean contWhile = true;
        while(contWhile){
            contWhile = false;
            try{
                click(xpath);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return;
    }
    public static void clickWT(WebDriver driver,String xpath) {
        boolean contWhile = true;
        while(contWhile){
            contWhile = false;
            try{
                click(driver,xpath);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return;
    }


    public WebElement elem(String xpath) {
        return driver.findElement(By.xpath(xpath));
    }
    public static WebElement elem(WebDriver driver,String xpath) {
        return driver.findElement(By.xpath(xpath));
    }
    public WebElement elemWT(String xpath) {
        boolean contWhile = true;
        WebElement elem = null;
        while(contWhile){
            contWhile = false;
            try{
                elem = elem(xpath);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return elem;
    }
    public static WebElement elemWT(WebDriver driver,String xpath) {
        boolean contWhile = true;
        WebElement elem = null;
        while(contWhile){
            contWhile = false;
            try{
                elem = elem(driver,xpath);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return elem;
    }


    public List<WebElement> elems(String xpath) {
        return driver.findElements(By.xpath(xpath));
    }
    public static List<WebElement> elems(WebDriver driver,String xpath) {
        return driver.findElements(By.xpath(xpath));
    }
    public List<WebElement> elemsWT(String xpath) {
        boolean contWhile = true;
        List<WebElement> elems = null;
        while(contWhile){
            contWhile = false;
            try{
                elems = elems(xpath);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if(elems.size() == 0) contWhile = true;
        }
        return elems;
    }
    public static List<WebElement> elemsWT(WebDriver driver,String xpath) {
        boolean contWhile = true;
        List<WebElement> elems = null;
        while(contWhile){
            contWhile = false;
            try{
                elems = elems(driver,xpath);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if(elems.size() == 0) contWhile = true;
        }
        return elems;
    }


    public String attr(String xpath, String attribute) {
        return elem(xpath).getAttribute(attribute);
    }
    public static String attr(WebDriver driver,String xpath, String attribute) {
        return elem(driver, xpath).getAttribute(attribute);
    }
    public String attrWT(String xpath,String attribute) {
        boolean contWhile = true;
        String attr = "";
        while(contWhile){
            contWhile = false;
            try{
                attr = attr(xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return attr;
    }
    public static String attrWT(WebDriver driver,String xpath,String attribute) {
        boolean contWhile = true;
        String attr = "";
        while(contWhile){
            contWhile = false;
            try{
                attr = attr(driver,xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return attr;
    }

    
    public Pair<String,WebElement> attrElem(String xpath, String attribute) {
        WebElement elem = elem(xpath);
        String attr = elem.getAttribute(attribute);
        return new Pair<String,WebElement>(attr,elem);
    }
    public static Pair<String,WebElement> attrElem(WebDriver driver,String xpath, String attribute) {
        WebElement elem = elem(driver,xpath);
        String attr = elem.getAttribute(attribute);
        return new Pair<String,WebElement>(attr,elem);
    }
    public Pair<String,WebElement> attrElemWT(String xpath,String attribute) {
        boolean contWhile = true;
        Pair<String,WebElement> attrElem = null;
        while(contWhile){
            contWhile = false;
            try{
                attrElem = attrElem(xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return attrElem;
    }
    public static Pair<String,WebElement> attrElemWT(WebDriver driver,String xpath,String attribute) {
        boolean contWhile = true;
        Pair<String,WebElement> attrElem = null;
        while(contWhile){
            contWhile = false;
            try{
                attrElem = attrElem(driver,xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return attrElem;
    }


    public List<String> attrs(String xpath, String attribute) {
        List<WebElement> elems = elems(xpath);
        List<String> attrs = new java.util.ArrayList<String>();
        for(WebElement elem : elems) attrs.add(elem.getAttribute(attribute));
        return attrs;
    }
    public static List<String> attrs(WebDriver driver,String xpath, String attribute) {
        List<WebElement> elems = elems(driver,xpath);
        List<String> attrs = new java.util.ArrayList<String>();
        for(WebElement elem : elems) attrs.add(elem.getAttribute(attribute));
        return attrs;
    }
    public List<String> attrsWT(String xpath,String attribute) {
        boolean contWhile = true;
        List<String> attrs = null;
        while(contWhile){
            contWhile = false;
            try{
                attrs = attrs(xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if(attrs.size() == 0) contWhile = true;
        }
        return attrs;
    }
    public static List<String> attrsWT(WebDriver driver,String xpath,String attribute) {
        boolean contWhile = true;
        List<String> attrs = null;
        while(contWhile){
            contWhile = false;
            try{
                attrs = attrs(driver,xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if(attrs.size() == 0) contWhile = true;
        }
        return attrs;
    }



    public Pair<List<String>,List<WebElement>> attrsElems(String xpath, String attribute) {
        List<WebElement> elems = elems(xpath);
        List<String> attrs = new java.util.ArrayList<String>();
        for(WebElement elem : elems) attrs.add(elem.getAttribute(attribute));
        return new Pair<List<String>,List<WebElement>>(attrs,elems);
    }
    public static Pair<List<String>,List<WebElement>> attrsElems(WebDriver driver,String xpath, String attribute) {
        List<WebElement> elems = elems(driver,xpath);
        List<String> attrs = new java.util.ArrayList<String>();
        for(WebElement elem : elems) attrs.add(elem.getAttribute(attribute));
        return new Pair<List<String>,List<WebElement>>(attrs,elems);
    }
    public Pair<List<String>,List<WebElement>> attrsElemsWT(String xpath,String attribute) {
        boolean contWhile = true;
        Pair<List<String>,List<WebElement>> attrsElems = null;
        while(contWhile){
            contWhile = false;
            try{
                attrsElems = attrsElems(xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if(attrsElems == null || attrsElems.getValue().size() == 0) contWhile = true;
        }
        return attrsElems;
    }
    public static Pair<List<String>,List<WebElement>> attrsElemsWT(WebDriver driver,String xpath,String attribute) {
        boolean contWhile = true;
        Pair<List<String>,List<WebElement>> attrsElems = null;
        while(contWhile){
            contWhile = false;
            try{
                attrsElems = attrsElems(driver,xpath,attribute);
            }
            catch (Exception e){
                contWhile = true;
                e.printStackTrace();
                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if(attrsElems == null || attrsElems.getValue().size() == 0) contWhile = true;
        }
        return attrsElems;
    }
}
