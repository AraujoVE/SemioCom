import pandas as pd
import random
import emoji
import re

#Translate specified dicts and emoticons in text
def translatePatterns(text):
    replaceDict = {"<MEDIA>":"[MEDIA]","<LINK>":"[LINK]"}

    df = pd.DataFrame(text.split('\n'))[0].str.split('|',expand=True) #Create a DataFrame out of the text

    df[4] = df[4].str.replace(r'(:)(\S+?)(:)', r'\2', regex=True) #Replace string with the (:)(\S+?)(:) pattern
    
    df[4] = df[4].apply(lambda text : emoji.demojize(text)) #Convert emojis to text

    extraVocab =  ["[{}]".format(emot) for emot in re.findall(r'(?<=:)(\S+?)(?=:)','\n'.join(list(df[4])))] + list(replaceDict.values()) #Save used emoji names + replaceDict values names

    df[4] = df[4].str.replace(r'(:)(\S+?)(:)', r'[\2]', regex=True) #Fix emoji names to a pattern

    df[0] = df[df.columns[:]].apply(lambda x: '|'.join(x.dropna().astype(str)),axis=1) #Join columns

    text = '\n'.join(list(df[0])) #Join lines

    for initSymbol,endSymbol in replaceDict.items():
        text = text.replace(initSymbol,endSymbol) #Replace replace dict values

    with open("./extraVocab.csv",'w') as f:
        f.write('vocab\n'+'\n'.join(list(set(extraVocab)))) #Save extra vocab

    return text

#Change binary parts to 0 and 1    
def fixAppreciation(text):
    splittedText = text.split("|")
    splittedText[-1] = "1" if splittedText[-1].split(";")[0] == "Apreciativo" else "0"
    splittedText[-2] = "1" if splittedText[-2] == "True" else "0"
    return "|".join(splittedText)

#Set paths
inputPath = "./trainingDataOld.csv"
outputPath = "./trainingData.csv"
emoticonPath = "./emoticonDictionary.csv"

#Generate heading
heading = "link|user|id|date|sentence|response|appreciative".split("|")
headingCols = dict([tuple([i,heading[i]]) for i in range(len(heading))])

with open(inputPath,'r') as f: #Read and fix the data
    textLines = [fixAppreciation(line) for line in translatePatterns(f.read().strip('\n')).split("\n")]    

random.shuffle(textLines)
df = pd.DataFrame(textLines)[0].str.split('|',expand=True)
df.rename(columns=headingCols,inplace=True)

dfAppreciative = df.loc[df['appreciative'] == '1'].reset_index(drop=True)
dfNonAppreciative = df.loc[df['appreciative'] == '0'].reset_index(drop=True)

minAppreciativeLabel = int(min(len(dfAppreciative),len(dfNonAppreciative))*0.9)

df = pd.concat([dfAppreciative[:minAppreciativeLabel],dfNonAppreciative[:minAppreciativeLabel]]).reset_index(drop=True)
df = df.sample(frac=1).reset_index(drop=True)

df.to_csv(outputPath,sep='|',index=False)