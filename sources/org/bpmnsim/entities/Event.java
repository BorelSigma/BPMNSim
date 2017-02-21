package org.bpmnsim.entities;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

/**
 * Created by BERGHL on 06.02.17.
 */
public abstract class Event extends SimEntity {


    /**
     * Creates a new entity.
     *
     * @param name the name to be associated with this entity
     */
    public Event(String name) {
        super(name);
    }

    @Override
    public void startEntity() {

    }

    @Override
    public void processEvent(SimEvent ev) {

    }

    @Override
    public void shutdownEntity() {

    }
}
