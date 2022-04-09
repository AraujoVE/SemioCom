#Importing Libraries
import torch

import numpy as np
import pandas as pd

from transformers import AutoModel, AutoTokenizer, BertForSequenceClassification, AdamW, get_linear_schedule_with_warmup

import random
import time

import coefLib as clib
import dataloaderLib as dlib
import gpuLib as glib
import trainingLib as tlib
import savingPreTrainedLib as spLib

applyValidation = True
validationPercentage = 0.2 if applyValidation else 0.0

#Defining tokenizer and model from BERTimbau
tokenizer = AutoTokenizer.from_pretrained('neuralmind/bert-large-portuguese-cased')
model = AutoModel.from_pretrained('neuralmind/bert-large-portuguese-cased')

device = glib.checkGPU() #Setting the GPU

#Including New Vocabulary
newTokens = list(pd.read_csv("extraVocab.csv")["vocab"])
tokenizer.add_tokens(newTokens)
model.resize_token_embeddings(len(tokenizer))

trainDataloader, validationDataloader = dlib.dataloaderGeneration("./baseDataFixed.csv",tokenizer,validationPercentage,32) #Setting the DataLoaders

model = BertForSequenceClassification.from_pretrained(
    "neuralmind/bert-large-portuguese-cased", #Use the 24-layer BERTimbau model, with a cased vocab.
    num_labels = 2, #The number of output labels--2 for binary classification. You can increase this for multi-class tasks.   
    output_attentions = False, #Whether the model returns attentions weights.
    output_hidden_states = False, #Whether the model returns all hidden-states.
)

optimizer = AdamW(model.parameters(),lr = 2e-5, eps = 1e-8 ) #Set the optimizer and its parameters

epochs = 4

total_steps = len(trainDataloader) * epochs #[number of batches] x [number of epochs]

scheduler = get_linear_schedule_with_warmup(optimizer, num_warmup_steps = 0, num_training_steps = total_steps) #Create the learning rate scheduler.

#Set the seed value all over the place to make this reproducible.
seed_val = 42
random.seed(seed_val)
np.random.seed(seed_val)
torch.manual_seed(seed_val)
torch.cuda.manual_seed_all(seed_val)

training_stats = []

total_t0 = time.time()

for epoch_i in range(0, epochs): #Execute for each epoch
    
    t0 = tlib.printTraining(epoch_i,epochs)

    total_train_loss = 0 #Reset the total loss for this epoch.

    model.train() #Put the model into training mode.

    #For each batch of training data...
    for step, batch in enumerate(trainDataloader):
        tlib.trainBatchIter(step,trainDataloader,t0,batch,device,model,optimizer,scheduler,total_train_loss)

    if applyValidation: #When training for real, ther's no need to run validation
        avg_train_loss = total_train_loss / len(trainDataloader) #Calculate the average loss over all of the batches.
        
        training_time = tlib.printTrainTime(avg_train_loss,t0) #Measure how long this epoch took.
            
        print("\nRunning Validation...")

        t0 = time.time()

        model.eval() #Put the model in evaluation mode

        total_eval_accuracy, total_eval_loss = [0,0]

        #Evaluate data for one epoch
        for batch in validationDataloader:
            total_eval_loss, total_eval_accuracy = tlib.valBatchIter(batch,device,model,total_eval_loss,total_eval_accuracy)
            
        #Set training stats params (epoch,Training Loss,Valid. Loss,Valid. Accur.,Training Time,Validation Time)
        training_stats.append(tlib.valParams(total_eval_loss,total_eval_accuracy,validationDataloader,t0,epoch_i,avg_train_loss,training_time))


print("\nTraining complete!\nTotal training took {:} (h:mm:ss)".format(tlib.formatTime(time.time()-total_t0)))
spLib.savePreTrained(model,tokenizer,training_stats)

