from email.mime import base
from random import randint

medias = {"True":"filter:media", "False":"-filter:media"}
replies = {"True":"filter:replies","False":"-filter:replies"}


def isLeapYear(year):
    if year % 4 == 0:
        if year % 100 == 0:
            if year % 400 == 0:
                return True
            else:
                return False
        else:
            return True
    else:
        return False



def getThirtyMinutessInYears(firstYear, lastYear):
    monthsSize = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    years = [i for i in range(firstYear, lastYear+1)]
    thirtyMinutess = []
    for year in years:
        if isLeapYear(year):
            monthsSize[1] = 29
        else:
            monthsSize[1] = 28
        for month in range(len(monthsSize)):
            for day in range(monthsSize[month]):
                for hour in range(24):
                    for thirtyMinutes in range(2):
                        initMins = f"{thirtyMinutes*3}0:00"
                        endMins = f"{(thirtyMinutes*3)+2}9:59"
                        baseThirtyMinutes = f"{year}-{(month+1):02d}-{(day+1):02d}T{(hour+1):02d}:"
                        thirtyMinutess.append(f"since_time:{baseThirtyMinutes}{initMins} until_time:{baseThirtyMinutes}{endMins}")
    return thirtyMinutess


def getThirtyMinutess(firstYear, lastYear):
    thirtyMinutess = getThirtyMinutessInYears(firstYear, lastYear)
    noOfHours = 72
    choosenThirtyMinutes = []
    for i in range(noOfHours*2):
        randomThirtyMinutes = randint(0, len(thirtyMinutess)-1)
        choosenThirtyMinutes.append(thirtyMinutess[randomThirtyMinutes])
        thirtyMinutess.pop(randomThirtyMinutes)

    return choosenThirtyMinutes

def writeThirtyMinutes(fileName, firstYear, lastYear, baseText):
    with open(fileName, 'a') as f:
        for thirtyMinutes in getThirtyMinutess(firstYear, lastYear):
            for media in medias:
                for reply in replies:
                    f.write(f"{medias[media]} {replies[reply]} {baseText} lang:pt {thirtyMinutes}\n")

writeThirtyMinutes("randSearchs.txt", 2016, 2021,'bolsonaro')