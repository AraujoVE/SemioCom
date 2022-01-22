from os import system

timeZone = "Brazil/East"
while(True):
    with open("./randSearchs/randSearchs.txt", 'r') as f:
        randSearchLines = f.readlines()
        if (len(randSearchLines) == 0) or (randSearchLines[-1].strip().strip("\n") == ""):
            break
    with open("./searchParams/searchText.txt", 'w') as f:
        f.write(randSearchLines[0].strip().strip("\n")+"\n"+timeZone)
    system("java -jar TwitterScrapper.jar")
    del randSearchLines[0]
    with open("./randSearchs/randSearchs.txt", 'w') as f:
        f.writelines(randSearchLines)