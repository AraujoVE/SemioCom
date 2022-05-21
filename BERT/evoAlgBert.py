from EvolutionaryAlgorithm.evoAlgIter import EvoAlgIter

from random import random
import json

def auxFunc(fixedParams,variableParams): return random()

evoAlgIter = EvoAlgIter(auxFunc,"./evoAlgParam.json") 
bestParams = evoAlgIter.run()

batchSize = bestParams[-1]
learningRates = [el for el in bestParams[:-1] if int(el) != -1]
epochs = len(learningRates) - 1
bestParamsObj = {
    "batchSize" : batchSize,
    "learningRates" : learningRates,
    "epochs" : epochs
}

with open('bestParams.json', 'w') as outfile:
    json.dump(bestParamsObj, outfile)