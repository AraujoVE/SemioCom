#Importing Libraries
from transformers import BertForSequenceClassification, BertTokenizer

import coefLib as clib
import dataloaderLib as dlib
import gpuLib as glib
import trainingLib as tlib

output_dir = './model_save/'
# Load a trained model and vocabulary that you have fine-tuned
model = BertForSequenceClassification.from_pretrained(output_dir)
tokenizer = BertTokenizer.from_pretrained(output_dir)

device = glib.checkGPU() #Setting the GPU


# Copy the model to the GPU.
model.to(device)


#Setting sentences and labels
predictionDataloader = dlib.dataloaderGeneration("./testDataFixed.csv",tokenizer,1.0,32)


print("\nPrediction on test set")

#Put model in evaluation mode
model.eval()

predictions , true_labels = [], []

#Predict 
for batch in predictionDataloader:
    logits, label_ids = tlib.predBatchIter(batch,device,model)
    predictions.append(logits)
    true_labels.append(label_ids)

print('    DONE.')

clib.matthewsCoef(predictions,true_labels)