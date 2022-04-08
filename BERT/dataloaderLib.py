import numpy as np
import torch
from sklearn.model_selection import StratifiedShuffleSplit
from torch.utils.data import TensorDataset, DataLoader, SequentialSampler
from torchsample.samplers import StratifiedSampler 
import pandas as pd

#Get sentence and labels from .csv file
def getSentenceAndLabels(dataPath):
    baseData = pd.read_csv(dataPath,delimiter="|")
    sentences = baseData["sentence"]
    labels = np.array(baseData["appreciative"])
    return sentences,labels

#Tokenize the text
def tokenizeText(sentences,tokenizer):
    inputValues = []
    attentionMasks = []
    for sentence in sentences:
        encodedDict = tokenizer.encode_plus(
            sentence,                     # Sentence to encode.
            add_special_tokens = True,    # Add '[CLS]' and '[SEP]'
            max_length = 512,             # Pad & truncate all sentences.
            pad_to_max_length = True,     # Do padding
            return_attention_mask = True, # Construct attn. masks.
            return_tensors = 'pt',        # Return pytorch tensors.
        )
        inputValues.append(np.array(encodedDict['input_ids'])) #Get the input token IDs.
        attentionMasks.append(np.array(encodedDict['attention_mask'])) #Get the attention mask.

    #Convert to numpy arrays
    inputValues = np.array(inputValues)
    attentionMasks = np.array(attentionMasks)
    return inputValues,attentionMasks

#Split the data into train and validation in a stratified way
def stratifiedSplit(inputValues,attentionMasks,labels,valPercentage):
    sss = StratifiedShuffleSplit(n_splits=1, test_size=valPercentage, random_state=0) #Set stratified split
    sss.get_n_splits(inputValues, labels) #Make the split with inputValues in respect to labels classes

    setTensors = lambda list,index: torch.cat([torch.tensor(value) for value in list[index]], dim = 0)

    for train_index, test_index in sss.split(inputValues, labels):
        #Setting values for train and validation
        trainValues = setTensors(inputValues,train_index)
        validationValues = setTensors(inputValues, test_index)

        #Setting attention masks for train and validation
        trainAttentionMasks = setTensors(attentionMasks, train_index)
        validationAttentionMasks = setTensors(attentionMasks, test_index)
        
        #Setting labels for train and validation
        trainLabels = torch.tensor(labels[train_index])
        validationLabels = torch.tensor(labels[test_index])

    #Defining the dataloaders
    return trainValues, trainAttentionMasks, trainLabels, validationValues, validationAttentionMasks, validationLabels


#Setting train Dataloader
def trainDataloader(trainDataset,trainLabels,batchSize):
    trainDataloader = DataLoader(
        trainDataset, #Training samples
        sampler = StratifiedSampler(class_vector=trainLabels,batch_size=batchSize), #Select batches stratified randomly
        batch_size = batchSize #Trains with this batch size.
    )
    return trainDataloader

#Setting validation Dataloader
def valDataloader(validationDataset,batchSize):
    validationDataloader = DataLoader(
        validationDataset, #Validation samples.
        sampler = SequentialSampler(validationDataset), #Pull out batches sequentially.
        batch_size = batchSize #Evaluate with this batch size.
    )
    return validationDataloader

#Generate (training and validation) or (prediction) Dataloader(s)
def dataloaderGeneration(dataPath,tokenizer,valPercentage,batchSize):
    
    sentences, labels = getSentenceAndLabels(dataPath)
    
    inputValues,attentionMasks = tokenizeText(sentences,tokenizer)
    
    trainValues, trainAttentionMasks, trainLabels, validationValues, validationAttentionMasks, validationLabels = stratifiedSplit(inputValues,attentionMasks,labels,valPercentage)
    
    trainDataset = TensorDataset(trainValues, trainAttentionMasks, trainLabels)
    validationDataset = TensorDataset(validationValues, validationAttentionMasks, validationLabels)
    
    if valPercentage != 1.0: #If there isn't just validation, generate the train and validation Dataloaders
        trainingDataloader = trainDataloader(trainDataset,trainLabels,batchSize)
        validationDataloader = valDataloader(validationDataset, batchSize)
        return trainingDataloader, validationDataloader

    predDataloader = valDataloader(validationDataset, batchSize) #Otherwise, generate just the prediction Dataloader
    return predDataloader

