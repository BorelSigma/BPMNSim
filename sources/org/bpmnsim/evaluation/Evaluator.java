package org.bpmnsim.evaluation;

import org.bpmnsim.MessageBroker;
import org.bpmnsim.Token;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.util.MathUtil;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by BERGHL on 11.02.17.
 */
public class Evaluator {


    private SlaParameter parameter;

    private MessageBroker messageBroker;

    public int getMaxActiveTokens() {
        return maxActiveTokens;
    }

    public void setMaxActiveTokens(int maxActiveTokens) {
        this.maxActiveTokens = maxActiveTokens;
    }

    private int maxActiveTokens;

    public Evaluator(SlaParameter parameter, MessageBroker m) {
        this.parameter = parameter;
        this.messageBroker = m;
        messageBroker.setEvaluator(this);
    }

    public void printMaxRuntime(){

    }

    public void printStatistics(){
        Log.printLine("[Evaluation] maximal number of active tokens: "+  maxActiveTokens);
        Log.printLine("[Evaluation] Runtime Violation: " + getSlaRuntimeViolations());
        printRuntimeStatistics();
    }

    private void printRuntimeStatistics(){
        List<Double> runtimes = messageBroker.getFinishedTokens().stream().map(t -> t.getTotalRuntime()).collect(Collectors.toList());
        Log.printLine("[Evaluation] Runtime Mean: " + MathUtil.mean(runtimes));
        Log.printLine("[Evaluation] Runtime Median: " + MathUtil.median(runtimes));
        Log.printLine("[Evaluation] Runtime standard deviation: " + MathUtil.stDev(runtimes));
    }

    private int getSlaRuntimeViolations(){
        int numberOfViolations = 0;
        Iterator iter = messageBroker.getFinishedTokens().iterator();
        while(iter.hasNext()){
            Token token = (Token) iter.next();
            if(token.getTotalRuntime() > parameter.getMaxRuntime()){
                numberOfViolations++;
            }
        }
        return numberOfViolations;
    }
}
