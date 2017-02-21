package org.bpmnsim;

import org.bpmnsim.entities.BpmnElement;
import org.bpmnsim.entities.StartEvent;
import org.bpmnsim.utils.BpmnSimTags;
import org.bpmnsim.utils.FileSize;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.workflowsim.utils.DistributionGenerator;

/**
 * Created by BERGHL on 04.02.17.
 */
public class TokenGenerator extends SimEntity {

    private int tokenId = 0;
    private int NUMBER_OF_TOKENS;

    private MessageBroker messageBroker;

    StartEvent start;

    private ContinuousDistribution distribution;

    public TokenGenerator (String name, StartEvent start, int number_of_tokens, ContinuousDistribution dist, MessageBroker messageBroker){
        super(name);
        this.start = start;
        this.NUMBER_OF_TOKENS = number_of_tokens;
        this.distribution = dist;
        this.messageBroker = messageBroker;
    }
    @Override
    public void startEntity() {
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()){
            case BpmnSimTags.RUN_TOKEN_GENERATION:
                processTokenGeneration(ev);
        }

    }

    private int getTokensInSimulation(){
        return tokenId - messageBroker.getNumberOfFinishedTokens();
    }

    private void processTokenGeneration(SimEvent ev) {
        Token token = new Token(tokenId, FileSize.SMALL);
        token.setStartTime(ev.eventTime());
        tokenId++;
        Double sample = distribution.sample();
        if(tokenId <= NUMBER_OF_TOKENS){
            send(start.getId(), 0.0, BpmnSimTags.TOKEN_ARRIVED, token);
            send(getId(),sample , BpmnSimTags.RUN_TOKEN_GENERATION);
            Log.printLine("Token Generator got random number " + sample);
            Log.printLine("Currently are " + getTokensInSimulation() + " active");
            if(messageBroker.getEvaluator().getMaxActiveTokens()< getTokensInSimulation()){
                messageBroker.getEvaluator().setMaxActiveTokens(getTokensInSimulation());
            }
        }

    }

    @Override
    public void shutdownEntity() {
        Log.printLine(this.getName() + " is shutting down.");
    }
}
