package org.bpmnsim;

import org.bpmnsim.evaluation.Evaluator;
import org.bpmnsim.utils.BpmnSimTags;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.workflowsim.CondorVM;
import org.workflowsim.Job;
import org.workflowsim.Task;
import org.workflowsim.WorkflowScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MessageBroker extends SimEntity {


    private Evaluator evaluator;


    public void setEvaluator(Evaluator e){
        evaluator = e;
    }

    public Evaluator getEvaluator(){
        return evaluator;
    }
    private List<Token> finishedTokens = new ArrayList<>();

    public TokenGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(TokenGenerator generator) {
        this.generator = generator;
    }

    public List<Token> getFinishedTokens(){
        return finishedTokens;
    }
    private WorkflowScheduler scheduler;

    private TokenGenerator generator;
    private HashMap<Integer, Integer> submitter = new HashMap<>();

    public int getNumberOfFinishedTokens(){
        return finishedTokens.size();
    }
    public MessageBroker(String name){

        super(name);
        try {
            scheduler = new WorkflowScheduler("scheduler");
            scheduler.setWorkflowEngineId(this.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFinishedToken(Token token){
        finishedTokens.add(token);
    }

    @Override
    public void startEntity() {
        Log.printLine(getName() + " is starting...");
        send(scheduler.getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()){
            case BpmnSimTags.SUBMIT_TASK:
                processTaskSubmit(ev);
                break;
            /**
             * Indicates that VMs are ready to use.
             */
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(ev);
                break;
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(ev);
                break;
            /**
             * Send from end event to start new token after last one finished.
             */
            case BpmnSimTags.RUN_TOKEN_GENERATION:
                processTokenGeneration();
        }

    }

    private void processTokenGeneration() {
        send(generator.getId(), 0.0, BpmnSimTags.RUN_TOKEN_GENERATION);
    }

    /**
     * VMs are ready to use. TokenGenerator should now start the first token.
     * @param ev
     */
    private void processCloudletSubmit(SimEvent ev) {
        send(generator.getId(), 0.0, BpmnSimTags.RUN_TOKEN_GENERATION);
    }

    /**
     * A token arrived at a Task which triggers a generation of a new cloudlet which should be submittet to the scheduler.
     * @param ev
     */
    private void processTaskSubmit(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        submitter.put(cloudlet.getCloudletId(), ev.getSource());

        send(getWorkflowScheduler().getId(), 0.0, CloudSimTags.CLOUDLET_SUBMIT, transform(cloudlet));
    }

    /**
     * A job is finished. Message is sent to the respective task to forward the token to next element.
     * @param ev
     */
    private void processCloudletReturn(SimEvent ev){
        Job cloudlet = (Job) ev.getData();
        send(submitter.get(cloudlet.getCloudletId()), 0.0, CloudSimTags.CLOUDLET_RETURN, cloudlet);
    }


    @Override
    public void shutdownEntity() {
        Log.printLine(this.getName() + " is shutting down.");
        finishedTokens.stream().forEach(t -> Log.printLine(t.toString()));
        Log.printLine("Total Simulation runtime: " + finishedTokens.get(finishedTokens.size()-1).getFinishTime());
    }


    public WorkflowScheduler getWorkflowScheduler(){return this.scheduler;}



    private List<Job> transform(Cloudlet cloudlet){
        Job job = new Job(cloudlet.getCloudletId(), cloudlet.getCloudletLength());
        job.setUserId(scheduler.getId());
        List<Job> list = new ArrayList<>();
        list.add(job);
        list.get(0).addTaskList(list);
        return list;
    }

}
