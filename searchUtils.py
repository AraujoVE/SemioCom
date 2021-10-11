import pandas as pd
import re
import numpy as np

def joinFiles(mainPath,baseName,rangeFiles,extension,max):
    toWriteFile = open(mainPath+baseName+extension,"w")
    for i in range(rangeFiles):
        if(i > max):
            break
        with open(mainPath+baseName+"_"+str(i)+"_"+str(rangeFiles)+extension,'r') as f:
            r = f.read() if i == 0 else f.read().split("\n",1)[1]
            toWriteFile.write(r)
    toWriteFile.close()

def filterStrList(df,strMatrix,colId,subSep="",makeToAnyPos=False):
    if strMatrix == None or len(strMatrix) == 0:
        return df
    preDelimiter = ".*" + subSep if makeToAnyPos or subSep != "" else subSep
    postDelimiter = subSep + ".*" if makeToAnyPos or subSep != "" else subSep

    allDfs = []
    for strVec in strMatrix:
        strList = [strVec] if isinstance(strVec,str) else strVec
        if subSep == "":
            for strIt in range(len(strList)):   strList[strIt] = re.compile(strList[strIt])
            regmatch = np.vectorize(lambda x: ([(bool(r.match(x))) for r in strList].count(False) == 0))            
        else:
            for strIt in range(len(strList)):   strList[strIt] = re.compile(preDelimiter+strList[strIt]+postDelimiter)
            regmatch = np.vectorize(lambda x: ([(bool(r.match(subSep+x+subSep))) for r in strList].count(False) == 0))

        rightValuesBool = regmatch(df.iloc[:,colId])
        rightValuesIndex = [index for index in range(len(rightValuesBool)) if rightValuesBool[index]]

        allDfs.append(df.iloc[rightValuesIndex,:])
    
    filteredDf = pd.concat(allDfs,axis=0).drop_duplicates().reset_index(drop=True)
    return filteredDf


def minMaxValues(numberStr):
    numberPair = [i.strip() for i in numberStr.split(',')]
    operators = [np.equal,np.equal]
    if(len(numberPair) == 1):
        numberPair[0] = float(numberPair[0])
        numberPair.append(numberPair[0])
    else:
        if len(numberPair[0]) == 0:
            operators[0] = np.greater
            numberPair[0] = -1*float("inf")
        else:
            operators[0] = np.greater if numberPair[0][0] == '(' else np.greater_equal
            numberPair[0] = float(numberPair[0].replace("(","").replace("[",""))


        if len(numberPair[1]) == 0:
            operators[1] = np.less
            numberPair[1] = float("inf")
        else:
            operators[1] = np.less if numberPair[1][-1] == ')' else np.less_equal
            numberPair[1] = float(numberPair[1].replace(")","").replace("]",""))
    print("numberPair")
    print(numberPair)
    print("operators")
    print(operators)

    return numberPair, operators

def filterNumberRangeList(df,numberRangVec,colId):
    if numberRangVec == None or len(numberRangVec) == 0:
        return df

    allDfs = []
    for numberRangeItem in numberRangVec:
        numberRange,operators = minMaxValues(numberRangeItem)
        
        #numberBetween = np.vectorize(lambda x: bool(operators[0](x,numberRange[0]) and operators[1](x,numberRange[1])))
        numberBetween = np.vectorize(lambda x: bool(operators[0](x,numberRange[0]) and operators[1](x,numberRange[1])))
        
        rightValuesBool = numberBetween(df.iloc[:,colId])
        rightValuesIndex = [index for index in range(len(rightValuesBool)) if rightValuesBool[index]]

        allDfs.append(df.iloc[rightValuesIndex,:])

    filteredDf =  pd.concat(allDfs,axis=0).drop_duplicates().reset_index(drop=True)

    return filteredDf
