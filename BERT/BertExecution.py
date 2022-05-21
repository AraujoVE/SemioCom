from BertDataclass import BertData_Initial, BertData_Hyperparameters, BertData_Fixed, BertData_Variable
from BertModel import BertModel

def bertExecution(fixedArguments,trainableParams):
    #Getting fixed arguments from dict param 'fixedArguments'
    validation = fixedArguments["validation"]
    modelPath = fixedArguments["modelPath"]
    tokenizerPath = fixedArguments["tokenizerPath"]
    inputDataPath = fixedArguments["inputDataPath"]
    extraVocabPath = fixedArguments["extraVocabPath"]
    preTrainedModelPath = fixedArguments["preTrainedModelPath"]
    outputDataPath = fixedArguments["outputDataPath"]

    testMode = False #Test mode definition

    #Getting variable params from trainableparams
    batchSize = int(trainableParams[-1])
    learningRates = [float(el) for el in trainableParams[:-1] if int(el) != -1]
    epochs = len(learningRates) - 1


    bertModelParams : BertData_Initial = BertData_Initial(
        testMode,
        validation,
        modelPath,
        tokenizerPath,
        inputDataPath,
        extraVocabPath,
        preTrainedModelPath,
        outputDataPath
    )


    bertHyperparams : BertData_Hyperparameters = BertData_Hyperparameters(learningRates,batchSize,epochs)

    bertModel : BertModel = BertModel(bertModelParams)
    bertModel.setHyperparameters(bertHyperparams)
    bertModel.train()