from torch import full
from EvolutionaryAlgorithm.preMutation import PreMutation
from EvolutionaryAlgorithm.preUpdatePop import PreUpdatePop
from EvolutionaryAlgorithm.population import Population
from EvolutionaryAlgorithm.selection import Selection
from EvolutionaryAlgorithm.sorting import Sorting
from EvolutionaryAlgorithm.fitness import Fitness

from EvolutionaryAlgorithm.globalVars import GlobalVars

from EvolutionaryAlgorithm.stopCondition import StopCondition

from typing import List, Type, Dict, Any, Callable

import numpy as np

import numpy.typing as npt

import json

class EvoAlgIter:

    def __init__(self,calcFitnessFunc : Callable,paramsPath : str) -> None:
        self.calcFitnessFunc = calcFitnessFunc
        self.globalVars : Type[GlobalVars] = GlobalVars()

        with open(paramsPath) as jfile: 
            self.params : Dict[str,Any] = json.load(jfile)
        
        self.fixedArguments = self.params["fitnessFunctionFixedArguments"]
        self.mainFuncName : str = self.params["mainFuncName"]
        self.mainFunc : Callable = getattr(self,self.mainFuncName)

        self.offspringSize : int = self.params["offsprings"]["size"]
        self.offspringParams : Dict[str,Any] = self.params["offsprings"]["params"]

        self.popSize : int = self.params["pop"]["generalParams"]["size"]

        popNames : List[str] = list(self.params["pop"].keys())
        popNames.remove("generalParams")
        self.pops : List[Population] = [Population(self.globalVars, name, self.params["popListOrder"].index(name) ,self.params["pop"][name],self.params["pop"]["generalParams"]) for name in popNames]
        self.pops = sorted(self.pops, key=lambda pop : pop.orderId)


        self.sortingParams : Dict[str,Any] = self.params["sorting"]["params"]
        self.sortingFuncName : str = self.params["sorting"]["funcName"]
        self.sortingFunc : Type[Sorting] = Sorting(self)

        self.selectionParams : Dict[str,Any] = self.params["selection"]["params"]
        self.selectionFuncName : str = self.params["selection"]["funcName"]
        self.selectionFunc : Type[Selection] = Selection(self)

        self.preMutationParams : Dict[str,Any] = self.params["preMutation"]["params"]
        self.preMutationFuncName : str = self.params["preMutation"]["funcName"]
        self.preMutationFunc : Type[PreMutation] = PreMutation(self)

        self.preUpdatePopParams : Dict[str,Any] = self.params["preUpdatePop"]["params"]
        self.preUpdatePopFuncName : str = self.params["preUpdatePop"]["funcName"]
        self.preUpdatePopFunc : Type[PreUpdatePop] = PreUpdatePop(self)

        self.stopConditionParams : Dict[str,Any] = self.params["stopCondition"]["params"]
        self.stopConditionFuncName : str = self.params["stopCondition"]["funcName"]
        self.stopConditionFunc : Type[StopCondition] = StopCondition(self)

        self.fitnessParams : Dict[str,Any] = self.params["fitness"]["params"]
        self.fitnessFuncName : str = self.params["fitness"]["funcName"]
        self.fitnessFunc : Type[Fitness] = Fitness(self)



    def run(self) -> Dict[str,Any]:
        return self.mainFunc()

    def mainEAFunc(self) -> Dict[str,Any]:
        for pop in self.pops: pop.initializePop()

        self.i : int = 0
        while not self.stopConditionFunc.stop():
            self.fitnessFunc.fit()
            self.sortingFunc.sort()
            for i in range(self.offspringSize):
                self.selectionFunc.select()
                for pop in self.pops: pop.crossover()

            self.preUpdatePopFunc.preUpdatePop()
            for pop in self.pops: pop.updatePop()
            
            self.preMutationFunc.preMutate()
            for pop in self.pops: pop.mutate()
            self.i += 1
        self.fitnessFunc.fit()

        return self.globalVars.data["bestIndividual"]
