var s = require("./basics.js")
const fs = require('fs');
var secret = JSON.parse(fs.readFileSync('secret.json', 'utf8')); 


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

async function waitWhileLoading(driver){
    let keepWaiting = true;
    while(keepWaiting){
        await s.wait(0.1);
        try{
            await s.clickXPath(driver,["//div[@role='progressbar']"]);
        }
        catch(e){
            keepWaiting = false;
        }
    }
}


async function scrollToTheEnd(driver){
    await waitWhileLoading(driver);
    let lastHeight = await driver.executeScript("return document.body.scrollHeight");
    let newHeight;
    while(true) {
        await driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        await waitWhileLoading(driver);
        newHeight = await driver.executeScript("return document.body.scrollHeight");
        if(newHeight == lastHeight) break;
        lastHeight = newHeight;
    }
}


async function searchWithKeywords(driver,keywords){
    await s.goToPage(driver,"https://twitter.com/search?q="+keywords);
    await scrollToTheEnd(driver);
}


async function main(){
    const driver = await s.getDriver();
    await s.goToPage(driver,"https://twitter.com/");
    await s.maximizeWindow(driver);
    await loginOnTwitter(driver);
    await searchWithKeywords(driver,"bandolim");
}

main();
