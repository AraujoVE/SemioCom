import matplotlib.pyplot as plt
from sklearn.metrics import matthews_corrcoef
import numpy as np
import seaborn as sns

# Create a barplot showing the MCC score for each batch of test samples.
def plotData(matthews_set):
    ax = sns.barplot(x=list(range(len(matthews_set))), y=matthews_set, ci=None)

    plt.title('MCC Score per Batch')
    plt.ylabel('MCC Score (-1 to +1)')
    plt.xlabel('Batch #')

    plt.show()

#Get the MCC score for each batch of test samples.
def matthewsCoef(predictions,trueLabels):
    matthews_set = []

    print('Calculating Matthews Corr. Coef. for each batch...')

    #Evaluate each test batch using Matthew's correlation coefficient
    for i in range(len(trueLabels)):
        pred_labels_i = np.argmax(predictions[i], axis=1).flatten() #Convert to 1-D max array

        #Calculate and store the coef for this batch.  
        matthews = matthews_corrcoef(trueLabels[i], pred_labels_i)                
        matthews_set.append(matthews)

    plotData(matthews_set)

#Get the MCC final score
def fullMatthewsCoef(predictions,trueLabels):
    #Concatenate the predictions
    flat_predictions = np.concatenate(predictions, axis=0) #Combine the results across all batches. 

    #Get the max value of the list
    flat_predictions = np.argmax(flat_predictions, axis=1).flatten() #For each sample, pick the label (0 or 1) with the higher score.

    #Concatenate de true labels
    flat_true_labels = np.concatenate(trueLabels, axis=0) #Combine the correct labels for each batch into a single list.

    #Return the matthews coef
    return matthews_corrcoef(flat_true_labels, flat_predictions) #Calculate the MCC

