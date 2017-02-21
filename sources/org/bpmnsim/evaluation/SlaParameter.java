package org.bpmnsim.evaluation;

/**
 * Created by BERGHL on 11.02.17.
 */
public final class SlaParameter {
    private int maxRuntime;

    public int getMaxRuntime(){
        return maxRuntime;
    }
    public SlaParameter(int maxRuntime) {
        this.maxRuntime = maxRuntime;
    }
}
