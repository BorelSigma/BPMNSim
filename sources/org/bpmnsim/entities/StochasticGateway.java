package org.bpmnsim.entities;

import org.cloudbus.cloudsim.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BERGHL on 05.02.17.
 */
public abstract class StochasticGateway extends Gateway{
    /**
     * Creates a new entity.
     *
     * @param name the name to be associated with this entity
     */
    public StochasticGateway(String name) {
        super(name);
    }

    protected List<PropabilityTuple> successors = new ArrayList<>();


    public void addSuccessor(BpmnElement succ, Double propability){
        if(propability > 1.0) Log.printLine("Stochastic Gateway accepts only probability number between 0 and 1");
        if(successors.size()<2){
            successors.add(new PropabilityTuple(succ, propability));
        }
    }

    protected class PropabilityTuple{
        private BpmnElement obj;
        private Double prob;
        public PropabilityTuple(BpmnElement obj, Double prob){
            this.obj = obj;
            this.prob = prob;
        }

        public BpmnElement getObj(){
            return obj;
        }
        public Double getProb(){
            return prob;
        }

    }
}
