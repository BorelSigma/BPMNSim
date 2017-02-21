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
public class ORMergeGateway extends ORGateway {
    /**
     * Creates a new entity.
     *
     * @param name the name to be associated with this entity
     */
    public ORMergeGateway(String name) {
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

    public void setSuccessor(BpmnElement elem) {
        successor = elem;
    }

    private BpmnElement successor;
    private Set<Integer> merged = new HashSet<>();

    private void processMerge(SimEvent ev) {
        Token token = (Token) ev.getData();
        if (merged.contains(token.getId()))
        if (successor != null) {
            merged.add(token.getId());
            send(successor.getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, token);
        } else {
            processTokenArrived(ev);
        }
    }

}
