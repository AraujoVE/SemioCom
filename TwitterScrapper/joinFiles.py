from os import listdir, system
from os.path import isdir,join
from random import shuffle
import re

def standardizeLines(lines,pathName):
    standardizedLines = []
    for line in lines:
        auxLine = line.strip("\n").split("|")
        auxLine[4] = re.sub(r"(^|\s)(http[s]?://.*)($|\s)", r"\1<LINK>\3", auxLine[4])
        if auxLine[5] == "True":
            auxLine[4] += " <MEDIA>"
        del auxLine[5]
        testLine = re.sub(r"[a-zA-Z]*?:[a-zA-Z]*?", "", auxLine[4]).lower()
        testLine = re.sub(r"\s+", " ", auxLine[4])
        continueFor = False
        for token in pathName.split(" "):
            if token == "":
                continueFor = True
                break
            if not token in testLine:
                continueFor = True
                break 
        if continueFor:
            continue

        auxLine = "|".join(auxLine) + "\n"
        standardizedLines.append(auxLine)

    return standardizedLines


def joinTexts(pathName):
    searchs = [file for file in listdir("./TwitterData") if (isdir(join("./TwitterData", file)) and pathName in file)]
    #print(searchs)

    fileLines = []
    for searchDir in searchs:
        csvFiles = [file for file in listdir("TwitterData/" + searchDir) if file.split(".")[-1] == "csv"]
        for csvFile in csvFiles:
            with open("TwitterData/" + searchDir + "/" + csvFile, "r") as f:
                fileLines += [line.strip("\n").strip()+"\n" for line in f.readlines() if line.strip().strip("\n") != ""]
    fileLines = list(set(fileLines))
    standardizedLines = standardizeLines(fileLines,pathName)
    shuffle(fileLines)
    with open("./joinedData/"+pathName+"__original.txt", "w") as f:
        f.write("url|user|id|date|text|hasMedia|isReply\n")        
        f.writelines(fileLines)

    with open("./joinedData/"+pathName+".txt", "w") as f:
        f.write("url|user|id|date|text|isReply\n")        
        f.writelines(standardizedLines)

joinTexts("bolsonaro")