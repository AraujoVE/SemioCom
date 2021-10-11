import pandas as pd
import re
import numpy as np
from searchUtils import *
import os


def filterRestaurants(restaurantsPath,savePath,sep='|',subSep=';',name=None,url=None,score=None,reviewsNo=None,foodTags=None,prices=None,address=None,neighborhood=None,excelente=None,muitoBom=None,razoavel=None,ruim=None,horrivel=None,returnNone=False):
    if returnNone:
        return None
    df = pd.read_csv(restaurantsPath,sep=sep)

    df = filterStrList(df,name,0)
    df = filterStrList(df,url,1)
    df = filterNumberRangeList(df,score,2)
    df = filterNumberRangeList(df,reviewsNo,3)
    df = filterStrList(df,foodTags,4,subSep=subSep)
    df = filterNumberRangeList(df,prices,7)
    df = filterStrList(df,address,8)
    df = filterStrList(df,neighborhood,9)
    df = filterNumberRangeList(df,excelente,10)
    df = filterNumberRangeList(df,muitoBom,11)
    df = filterNumberRangeList(df,razoavel,12)
    df = filterNumberRangeList(df,ruim,13)
    df = filterNumberRangeList(df,horrivel,14)

    print(df)

    df.to_csv(savePath,sep=sep,index=False)

    return [[i] for i in df.iloc[:,0].tolist()]



def filterReviews(reviewsPath,savePath,sep='|',restaurant=None,username=None,reviewsNo=None,score=None,title=None,text=None,reviewDate=None,visitDate=None,reviewLikes=None):
    filteredReviews = [reviewsPath+"/"+file for file in os.listdir(reviewsPath) if file.split(".")[-1] == 'csv' and not re.match(r"restaurantsList.*",file)]
    allDfs = []
    for reviewIt in range(len(filteredReviews)):
        reviewPath = filteredReviews[reviewIt]
        df = pd.read_csv(reviewPath,sep=sep)

        df = filterStrList(df,restaurant,0)
        df = filterStrList(df,username,1)
        df = filterNumberRangeList(df,reviewsNo,2)
        df = filterNumberRangeList(df,score,3)
        df = filterStrList(df,title,4)
        df = filterStrList(df,text,5)
        df = filterNumberRangeList(df,reviewDate,6)
        df = filterNumberRangeList(df,visitDate,7)
        df = filterNumberRangeList(df,reviewLikes,8)

        if reviewIt == 0:
            df.to_csv(savePath,sep=sep,index=False)
        else:
            df.to_csv(savePath,sep=sep,index=False,mode="a",header=False)



basePath = "./TripAdvisorScrapper/TripAdvisorData/Sao_Paulo/"
filePathBase = "restaurantsList"
iters = 10
max = 1

extension = ".csv"
restaurantsPath = basePath+filePathBase+extension
savePathRest = "./restaurantLog.csv"
savePathRev = "./reviewsLog.csv"

joinFiles(basePath,filePathBase,iters,extension,max)
chosenRests = filterRestaurants(restaurantsPath,savePathRest,returnNone=True)
filterReviews(basePath[:-1],savePathRev,restaurant=chosenRests,text=[[".*Mais$"]])