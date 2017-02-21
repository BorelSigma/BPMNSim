package org.bpmnsim.entities;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

/**
 * Created by BERGHL on 04.02.17.
 */
public abstract class Gateway extends SimEntity{


    /**
     * Creates a new entity.
     *
     * @param name the name to be associated with this entity
     */
    public Gateway(String name) {
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
