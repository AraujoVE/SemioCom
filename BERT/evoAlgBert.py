from EvolutionaryAlgorithm.evoAlgIter import EvoAlgIter
from BertExecution import bertExecution

from random import random
import json

def auxFunc(fixedParams,variableParams): return random()

evoAlgIter = EvoAlgIter(auxFunc,"./evoAlgParam.json") 
#evoAlgIter = EvoAlgIter(bertExecution,"./evoAlgParam.json") 
bestParams = evoAlgIter.run()

batchSize = bestParams["params"][-1]
learningRates = [el for el in bestParams["params"][:-1] if int(el) != -1]
epochs = len(learningRates) - 1
bestParamsObj = {
    "batchSize" : batchSize,
    "learningRates" : learningRates,
    "epochs" : epochs,
    "prediction" : bestParams["value"]
}

with open('bestParams.json', 'w') as outfile:
    json.dump(bestParamsObj, outfile)