package org.bpmnsim.entities;

import org.bpmnsim.Token;
import org.bpmnsim.utils.BpmnSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

/**
 * Created by BERGHL on 04.02.17.
 */
public class StartEvent extends Event implements BpmnElement {

    public StartEvent(String name) {

        super(name);
    }

    public BpmnElement getSuccessor() {
        return successor;
    }

    public void setSuccessor(BpmnElement successor) {
        this.successor = successor;
    }

    private BpmnElement successor;

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case BpmnSimTags.TOKEN_ARRIVED:
                processTokenArrived(ev);
                break;
        }
    }

    private void processTokenArrived(SimEvent ev) {
        send(getSuccessor().getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, ev.getData());
    }
}
