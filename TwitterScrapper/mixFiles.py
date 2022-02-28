from random import shuffle


files = ["bolsonaro","lula"]
joinedFileName = "lulonaro"
tweetsPerFile = 25000
basePath = "./joinedData/"
lines = []
error = False
for file in files:
    with open(basePath+file+".txt", "r") as f:
        auxLine = [fileLine.strip("\n") for fileLine in f.readlines()]
        del auxLine[0]
        if len(auxLine) < tweetsPerFile:
            print("ERROR QTTY OF TWEETS")
            error = True
            break
        shuffle(auxLine)
        lines += auxLine[:tweetsPerFile]

if not error:
    lines = list(set(lines))
    shuffle(lines)
    lines = [line+"\n" for line in lines]
    with open(basePath+joinedFileName+".txt", "w") as f:
        f.write("url|user|id|date|text|isReply\n")
        f.writelines(lines)