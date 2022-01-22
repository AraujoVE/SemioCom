from email.mime import base
from random import randint
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

def getDecaminutesInYears(firstYear, lastYear):
    monthsSize = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    years = [i for i in range(firstYear, lastYear+1)]
    decaminutes = []
    for year in years:
        if isLeapYear(year):
            monthsSize[1] = 29
        else:
            monthsSize[1] = 28
        for month in range(len(monthsSize)):
            for day in range(monthsSize[month]):
                for hour in range(24):
                    for decaminute in range(6):
                        baseDecaminute = f"{year}-{(month+1):02d}-{(day+1):02d}T{(hour+1):02d}:{decaminute}"
                        decaminutes.append(f"since:{baseDecaminute}0:00 until:{baseDecaminute}9:59")
    return decaminutes


def getDecaminutes(firstYear, lastYear):
    decaminutes = getDecaminutesInYears(firstYear, lastYear)
    noOfHours = 60
    choosenDecaminutes = []
    for i in range(noOfHours*6):
        randomDecaminute = randint(0, len(decaminutes)-1)
        choosenDecaminutes.append(decaminutes[randomDecaminute])
        decaminutes.pop(randomDecaminute)

    return choosenDecaminutes

def writeDecaminutes(fileName, firstYear, lastYear, baseText):
    with open(fileName, 'w') as f:
        for decaminute in getDecaminutes(firstYear, lastYear):
            f.write(f"{baseText} {decaminute}\n")

writeDecaminutes("randSearchs.txt", 2016, 2021,"bolsonaro lang:pt")