package org.bpmnsim.entities;

import org.bpmnsim.Token;
import org.bpmnsim.utils.BpmnSimTags;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by BERGHL on 05.02.17.
 */
public class XORMergeGateway extends XORGateway {


    private BpmnElement successor;

    public void setSuccessor(BpmnElement elem) {
        successor = elem;
    }


    public XORMergeGateway(String name) {
        super(name);
    }

    @Override
    public void processEvent(SimEvent ev) {

        switch (ev.getTag()) {
            case BpmnSimTags.TOKEN_ARRIVED:
                processMerge(ev);

                break;
        }

    }

    private Set<Integer> merged = new HashSet<>();

    private void processMerge(SimEvent ev) {
        Token token = (Token) ev.getData();
        if(merged.contains(token.getId())) Log.printLine("Token " + token.getId() + " not processed at merge gateway because clone already arrived");
        if(successor != null){
            merged.add(token.getId());
            send(successor.getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, token);
        }else{
            processTokenArrived(ev);
        }
    }
}
