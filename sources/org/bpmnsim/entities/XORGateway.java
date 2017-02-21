package org.bpmnsim.entities;

import org.bpmnsim.Token;
import org.bpmnsim.utils.BpmnSimTags;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.*;


public class XORGateway extends StochasticGateway implements BpmnElement {


    public XORGateway (String name) {
        super(name);
    }

    @Override
    public void processEvent(SimEvent ev) {

        switch (ev.getTag()){
            case BpmnSimTags.TOKEN_ARRIVED:
                processTokenArrived(ev);
                break;
        }
    }

    protected void processTokenArrived(SimEvent ev){
        double rand = new Random().nextDouble();
        Token token = (Token) ev.getData();
        boolean isSent = false;
        Iterator<PropabilityTuple> iter = successors.iterator();
        while(iter.hasNext() && !isSent){
            PropabilityTuple tuple = iter.next();
            if(rand <= tuple.getProb()){
                isSent = true;
                send(tuple.getObj().getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, token);
            }
        }
        if(!isSent){
            send(successors.get(0).getObj().getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, token);
        }
    }

}
