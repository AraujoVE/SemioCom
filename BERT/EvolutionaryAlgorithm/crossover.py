from typing import Any, Callable, List, Type
import numpy.typing as npt
import numpy as np
import random

class Crossover:

    def __init__(self, population: Type) -> None:
        self.popClass : Type = population
        self.execFunc : Callable = getattr(self, self.popClass.crossoverFuncName)
        self.arrayFunc : Callable = getattr(self, self.popClass.crossoverArrayFuncName)

    def crossover(self) -> None:
        self.arrayFunc()


    def meanValue(self,parentals : npt.NDArray) -> npt.NDArray:
        return parentals.mean(axis = 0)    

    def randomValue(self,parentals : npt.NDArray) -> npt.NDArray:
        parentalsPerGene : npt.NDArray = np.random.randint(low=0,high=np.shape(parentals)[0],size=np.shape(parentals)[1])
        return np.choose(parentalsPerGene,parentals)


    def masked_0(self) -> None:
        parentals : npt.NDArray = self.popClass.pop[self.popClass.globalVars.data["parentalsIndex"]]
        parentalsMaskedValuesList : npt.NDArray = np.count_nonzero(parentals.mask == True, axis = 1)
        
        parentalsMaskAvg : float = np.average(parentalsMaskedValuesList)
        masksNo : int = round(parentalsMaskAvg) - random.getrandbits(1) if parentalsMaskAvg % 1 == 0.5 else round(parentalsMaskAvg)
        
        offspring : npt.NDArray = self.execFunc(parentals)
        if masksNo != 0: offspring.mask[-masksNo:] = True
        
        self.popClass.offsprings.append(offspring)

    def nonmasked_0(self) -> None:
        parentals : npt.NDArray = self.popClass.pop[self.popClass.globalVars.data["parentalsIndex"]]
        self.popClass.offsprings.append(self.execFunc(parentals))
