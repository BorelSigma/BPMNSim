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
public class ParallelMergeGateway extends ParallelGateway {


    private Set<Integer> waitingTokens = new HashSet<>();

    public ParallelMergeGateway(String name) {
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

    public void processMerge(SimEvent ev) {
        Token token = (Token) ev.getData();
        if (isAlreadyWaiting(token)) {
            waitingTokens.remove(token.getId());
            processTokenArrived(ev);
        } else {
            waitingTokens.add(token.getId());
        }
    }

    private boolean isAlreadyWaiting(Token t) {
        return waitingTokens.contains(t.getId());
    }

}
