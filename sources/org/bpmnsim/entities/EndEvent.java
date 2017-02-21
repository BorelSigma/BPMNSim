package org.bpmnsim.entities;

import org.bpmnsim.MessageBroker;
import org.bpmnsim.Token;
import org.bpmnsim.utils.BpmnSimTags;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by BERGHL on 04.02.17.
 */
public class EndEvent extends Event implements BpmnElement {



    public EndEvent(String name) {
        super(name);
    }

    private MessageBroker messageBroker;

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case BpmnSimTags.TOKEN_ARRIVED:
                processTokenArrived(ev);
                break;
        }
    }

    private void processTokenArrived(SimEvent ev) {
        Token token = (Token) ev.getData();
        token.setFinishTime(ev.eventTime());
        messageBroker.addFinishedToken(token);
    }

    @Override
    public void shutdownEntity() {
            }

    public MessageBroker getMessageBroker() {
        return messageBroker;
    }

    public void setMessageBroker(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }
}
