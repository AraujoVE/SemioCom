var s = require("./basics.js")
const fs = require('fs');
var secret = JSON.parse(fs.readFileSync('secret.json', 'utf8')); 
const {Builder, By, Key, util, WebElement} = require("selenium-webdriver")

let initTime;
let endTime;
let tweets = {};
let localTweets = 0;
let lastIdFirst10Digits = 100000000000;
let tweetIds = [];
const MAX_TWEETS = 1000;
const MAX_TWEET_IDS = 1 * MAX_TWEETS;
let lastTweetIdVal = 0;

function msToTime(s) {
    var ms = s % 1000;
    s = (s - ms) / 1000;
    var secs = s % 60;
    s = (s - secs) / 60;
    var mins = s % 60;
    var hrs = (s - mins) / 60;

    return hrs + ':' + mins + ':' + secs + '.' + ms;
}


async function ensurePageIsLoaded(driver){
    await s.xPathWhileTrue(driver,s.clickXPath,["//div"]); // To ensure page is loaded
}

async function popUpLogin(driver){
    await s.xPathWhileTrue(driver,s.typeSlowly,[secret[Object.keys(secret)[0]]["username"],"//input[@name='username']"]) // digita o username do usuário
    await s.xPathWhileTrue(driver,s.clickXPath,["//div[@role='button' and contains(.,'Avançar')]"]) // clica para avançar
    await s.xPathWhileTrue(driver,s.typeSlowly,[secret[Object.keys(secret)[0]]["password"],"//input[@name='password']"]) // digita a senha do usuário    
}

async function noPopUpLogin(driver){
    await s.xPathWhileTrue(driver,s.typeSlowly,[secret[Object.keys(secret)[0]]["username"],"//input[@name='session[username_or_email]']"]) // digita o username do usuário    
    await s.xPathWhileTrue(driver,s.typeSlowly,[secret[Object.keys(secret)[0]]["username"],"//input[@name='session[password]']"]) // digita o username do usuário    
}

async function loginSecondPart(driver){
    let popUp = true;
    await ensurePageIsLoaded(driver)
    try{
        await s.clickXPath(driver,["//a[@href='/login']"]); // clicar no link: 'Celular, e-mail ou nome de usuário'
    }
    catch(e){
        popUp = false;
    }
    if(popUp) await popUpLogin(driver);
    else await noPopUpLogin(driver);
    await s.xPathWhileTrue(driver,s.clickXPath,["//div[@role='button' and contains(.,'Entrar')]"]) // clica para entrar
}




async function loginOnTwitter(driver){
    await ensurePageIsLoaded(driver)
    try{
        await s.clickXPath(driver,["//span[contains(.,'Entre') and @role='button']"]);
    }
    catch(e){}
    await loginSecondPart(driver);
}
/*
async function getTweets(driver,fixedKeyword){
    let texts = await s.getElemsAttrByXPath(driver,["//div[@class='css-901oao r-18jsvk2 r-37j5jr r-a023e6 r-16dba41 r-rjixqe r-bcqeeo r-bnwqim r-qvutc0']","innerText"]);
    let responseRetweetsAndLikes = await s.getElemsAttrByXPath(driver,["//span[@class='css-901oao css-16my406 r-poiln3 r-n6v787 r-1cwl3u0 r-1k6nrdp r-1e081e0 r-qvutc0']","innerText"]);
    let ids = await s.getElemsAttrByXPath(driver,["//a[./time]","href"]);
    let times = await s.getElemsAttrByXPath(driver,["//a/time","datetime"]);
    let splittedId;    
    let splittedIdLen;
    let curId;
    console.log("Entry:\n\tTexts: ",texts.length,"\n\tResponses, retweets and likes: ",responseRetweetsAndLikes.length/3,"\n\tIds: ",ids.length,"\n\tTimes: ",times.length);
    for(let i = 0; i < texts.length; i++){
        console.log("pre-split: ",ids[i]);
        splittedId = ids[i].split("/");
        splittedIdLen = splittedId.length;
        curId = parseInt(splittedId[splittedIdLen-1].slice(0,11));
        console.log(curId);
        if(curId > lastIdFirst10Digits) continue;
        lastIdFirst10Digits = curId;

        tweets[curId] = {
            "user": "@"+splittedId[splittedIdLen-3],
            "time": times[i],
            "text": texts[i],
            "responses": responseRetweetsAndLikes[3*i] === "" ? '0' : responseRetweetsAndLikes[3*i],
            "retweets": responseRetweetsAndLikes[3*i+1] === "" ? '0' : responseRetweetsAndLikes[3*i+1],
            "likes": responseRetweetsAndLikes[3*i+2] === "" ? '0' : responseRetweetsAndLikes[3*i+2]
        }
        localTweets++;
        if(localTweets % 1000 === 0) await saveTweets(fixedKeyword); 
    }
}
*/

async function retrieveTweetIds(driver,fixedKeyword){
    let all = await s.xPathWhileTrue(driver,s.getElemsAndAttrByXPath,["//a[./time]","href"]);
    let elems = all[0],ids = all[1];
    let curId;
    let splittedId = ids[ids.length-1].split("/");
    let nextLastId = parseInt(splittedId[splittedId.length-1].slice(0,11));
    for(let i = ids.length - 1; i >= 0; i--){
        splittedId = ids[i].split("/");
        curId = parseInt(splittedId[splittedId.length-1].slice(0,11));

        if(curId < lastIdFirst10Digits) tweetIds.push(ids[i]);
        else break;
    }
    if(nextLastId < lastIdFirst10Digits) lastIdFirst10Digits = nextLastId;
    let tweetIdsLen = tweetIds.length;
    console.log("Tweets: ",tweetIdsLen,"\n");
    if(tweetIdsLen >= MAX_TWEET_IDS){
        fs.writeFileSync("./tweets/"+fixedKeyword+"/tweetIds/"+lastTweetIdVal.toString()+"-"+ (tweetIdsLen+lastTweetIdVal).toString() + ".json",tweetIds.join("\n"));
        console.log("Saved tweetIds: "+lastTweetIdVal.toString()+"-"+ (tweetIdsLen+lastTweetIdVal).toString() + ".json");
        lastTweetIdVal = tweetIdsLen + 1;
        tweetIds = [];
        endTime = new Date().getTime();
        console.log("Time: ",msToTime(endTime-initTime));
    }

    await driver.executeScript("arguments[0].scrollIntoView(true);",elems[elems.length-1]);

    return;
}



async function scrollToTheEnd(driver,fixedKeyword){
    while(true){
        await s.wait(0.1);
        try{
            await s.clickXPath(driver,["//div[@class='css-1dbjc4n r-c66ptq']"]);
        }
        catch(e){
            break;
        }
    }
    
    let continueScrolling = true;
    initTime = new Date().getTime();
    while(continueScrolling){
        await s.wait(0.05);
        continueScrolling = false;
        try{
            await s.clickXPath(driver,["//div[@class='css-1dbjc4n r-c66ptq']"]);
        }
        catch(e){
            await retrieveTweetIds(driver,fixedKeyword);
            //press pageDown key with selenium webdriver
            //await driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            continueScrolling = true;
            continue;
        }
        continueScrolling = true;
        try{
            await s.clickXPath(driver,["//div[@role='button' and contains(.,'Tentar novamente')]"]);
        }
        catch(e){
            continueScrolling = false;
        }

        if(continueScrolling) continue;

        for(let i = 0; i < 100; i++){
            await s.wait(0.01);
            try{
                await s.clickXPath(driver,["//div[@class='css-1dbjc4n r-c66ptq']"]);
            }
            catch(e){
                continueScrolling = true;
                break;
            }
        }
    }

    return;
}

async function saveTweets(fixedKeyword){
    let ks = localTweets/1000;
    let fileName = "tweets/" + fixedKeyword + "__" + (ks-1).toString() + "k-" + ks.toString() + "k.json";
    fs.writeFileSync(fileName, JSON.stringify(tweets));
    tweets = {};
    return;
}



async function fixKeyword(keyword){
    return encodeURIComponent(keyword.text.join(" ") + " since:" +keyword.since + " until:" + keyword.until + " lang:" + keyword.lang);
}

async function searchWithKeywords(driver,keyword){
    let fixedKeyword = await fixKeyword(keyword);
    if(!fs.existsSync("./tweets/"+fixedKeyword)){
        fs.mkdirSync("./tweets/"+fixedKeyword);
        if(!fs.existsSync("./tweets/"+fixedKeyword+"/tweetIds")) fs.mkdirSync("./tweets/"+fixedKeyword+"/tweetIds");
        if(!fs.existsSync("./tweets/"+fixedKeyword+"/tweetIds")) fs.mkdirSync("./tweets/"+fixedKeyword+"/tweets");
    }
    await s.goToPage(driver,"https://twitter.com/search?q="+fixedKeyword+"&src=typed_query&f=live");
    await s.xPathWhileTrue(driver,s.clickXPath,["//div"]);
    //let initTime = new Date().getTime();
    await scrollToTheEnd(driver,fixedKeyword);
    //let endTime = new Date().getTime();
    //console.log("Tempo de execução: "+msToTime(endTime-initTime));
    await s.wait(10000);
}


async function main(){
    let keyword = {
        "text": ["bolsonaro"],
        "since": "2020-09-23",
        "until": "2020-09-24",
        "lang": "pt"
    }
    const driver = await s.getDriver();
    await s.goToPage(driver,"https://twitter.com/");
    await s.maximizeWindow(driver);
    //await loginOnTwitter(driver);
    await searchWithKeywords(driver,keyword);
}

main();
