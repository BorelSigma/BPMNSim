package org.bpmnsim.entities;

import org.bpmnsim.Token;
import org.bpmnsim.utils.BpmnSimTags;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by BERGHL on 04.02.17.
 */
public class ParallelGateway extends Gateway implements BpmnElement {


    private Set<BpmnElement> successors = new HashSet<>();

    public ParallelGateway(String name) {
        super(name);
    }

    public void addSuccessor(BpmnElement succ) {
        if (successors.size() < 2) {
            successors.add(succ);
        }
    }


    public ParallelGateway(String name, BpmnElement succ1, BpmnElement succ2) {
        this(name);
        successors.add(succ1);
        successors.add(succ2);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case BpmnSimTags.TOKEN_ARRIVED:
                processTokenArrived(ev);
                break;
        }
    }

    protected void processTokenArrived(SimEvent ev) {
        Token token = (Token) ev.getData();
        successors.forEach(elem -> send(elem.getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, token));
    }
}
