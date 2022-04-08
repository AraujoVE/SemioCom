import time
import datetime
import torch
import numpy as np


#Function to calculate the accuracy of our predictions vs labels
def flatAccuracy(preds, labels):
    pred_flat = np.argmax(preds, axis=1).flatten()
    labels_flat = labels.flatten()
    return np.sum(pred_flat == labels_flat) / len(labels_flat)


#Takes a time in seconds and returns a string hh:mm:ss
def formatTime(elapsed):
    elapsed_rounded = int(round((elapsed)))
    return str(datetime.timedelta(seconds=elapsed_rounded))

#For reps in reps print log
def progressLog(step,reps,trainDataloader,t0):
    if step % reps == 0 and not step == 0:
        elapsed = formatTime(time.time() - t0)        
        print('  Batch {:>5,}  of  {:>5,}.    Elapsed: {:}.'.format(step, len(trainDataloader), elapsed))

def printTrainTime(avg_train_loss,training_time,t0):
    trainingTime = formatTime(time.time() - t0)
    print("\n  Average training loss: {0:.2f}".format(avg_train_loss))
    print("  Training epcoh took: {:}".format(training_time))
    return trainingTime

#Set validation parameters
def valParams(total_eval_loss,total_eval_accuracy,validationDataloader,t0,epoch_i,avg_train_loss,training_time):
    #Report the final accuracy for this validation run.
    avg_val_accuracy = total_eval_accuracy / len(validationDataloader)
    print("  Accuracy: {0:.2f}".format(avg_val_accuracy))

    #Calculate the average loss over all of the batches.
    avg_val_loss = total_eval_loss / len(validationDataloader)
    
    #Measure how long the validation run took.
    validation_time = formatTime(time.time() - t0)
    print("  Validation Loss: {0:.2f}".format(avg_val_loss))
    print("  Validation took: {:}".format(validation_time))

    # Record all statistics from this epoch.
    return {
        'epoch': epoch_i + 1,
        'Training Loss': avg_train_loss,
        'Valid. Loss': avg_val_loss,
        'Valid. Accur.': avg_val_accuracy,
        'Training Time': training_time,
        'Validation Time': validation_time
    }

#Print traing data
def printTraining(epoch_i,epochs):
    print("")
    print('======== Epoch {:} / {:} ========'.format(epoch_i + 1, epochs))
    print('Training...')

    # Measure how long the training epoch takes.
    return time.time()

#Function to make a batch iteration training
def trainBatchIter(step,trainDataloader,t0,batch,device,model,optimizer,scheduler,total_train_loss):
    progressLog(step,40,trainDataloader,t0)

    inputIds, inputMask, labels = tuple(t.to(device) for t in batch) # Unpack the batch (id,mask and labels)

    model.zero_grad() #Clears any previously calculated gradients before performing a backward pass     

    loss, logits = model(
        inputIds, 
        token_type_ids=None, 
        attention_mask=inputMask, 
        labels=labels
    ) #Perform a forward pass

    total_train_loss += loss.item() # Accumulate the training loss over all of the batches to calculate the average loss at the end.

    loss.backward() #Perform a backward pass to calculate the gradients.

    torch.nn.utils.clip_grad_norm_(model.parameters(), 1.0) #Clip the norm of the gradients to 1.0. to prevent "exploding gradients"

    optimizer.step() #Update parameters and take a step using the computed gradient. The optimizer dictates the "update rule"

    scheduler.step()#Update the learning rate.

    return total_train_loss

#Function to make a batch iteration validation
def valBatchIter(batch,device,model,total_eval_loss,total_eval_accuracy):
    inputIds, inputMask, labels = tuple(t.to(device) for t in batch) # Unpack the batch (id,mask and labels)

    
    with torch.no_grad(): #Don't compute graph during the forward pass, since this is only needed for backprop        
        (loss, logits) = model(
            inputIds, 
            token_type_ids=None, 
            attention_mask=inputMask,
            labels=labels
        ) #Perform a forward pass
        
    total_eval_loss += loss.item() #Accumulate the validation loss.

    #Move logits and labels to CPU
    logits = logits.detach().cpu().numpy()
    label_ids = labels.to('cpu').numpy()

    total_eval_accuracy += flatAccuracy(logits, label_ids) #Calculate the accuracy for this batch of test sentences, and accumulate it over all batches.
    return total_eval_loss,total_eval_accuracy

#Function to make a batch iteration prediction
def predBatchIter(batch,device,model):
    inputIds, inputMask, labels = tuple(t.to(device) for t in batch) # Unpack the batch (id,mask and labels)
  
    with torch.no_grad():# Forward pass, calculate logit predictions
        outputs = model(
            inputIds, 
            token_type_ids=None, 
            attention_mask=inputMask
        )

    logits = outputs[0]

    #Move logits and labels to CPU
    logits = logits.detach().cpu().numpy()
    label_ids = labels.to('cpu').numpy()
  
    return logits,label_ids

