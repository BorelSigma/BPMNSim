package org.bpmnsim.entities;

import org.bpmnsim.MessageBroker;
import org.bpmnsim.Token;
import org.bpmnsim.utils.BpmnSimTags;
import org.bpmnsim.utils.FileSize;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.HashMap;

/**
 * Created by BERGHL on 04.02.17.
 */
public class Task extends SimEntity implements BpmnElement {

    private final double DELAY = 0.0;

    private UtilizationModel utilizationModel;

    private MessageBroker messageBroker;

    private HashMap<Integer, Token> currentTokens = new HashMap<>();


    private Long length;

    private HashMap<Integer, Integer> runningTasks = new HashMap<>();


    public BpmnElement getSuccessor() {
        return successor;
    }

    public void setSuccessor(BpmnElement successor) {
        this.successor = successor;
    }

    private BpmnElement successor;


    public Task(String name, MessageBroker broker, Long length) {
        super(name);
        this.length = length;
        setMessageBroker(broker);
        utilizationModel = new UtilizationModelFull();
    }

    public Task(String name, MessageBroker broker, Long length, UtilizationModel utilizationModel){
        this(name, broker, length);
        this.utilizationModel = utilizationModel;
    }


    @Override
    public void startEntity() {

    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case BpmnSimTags.TOKEN_ARRIVED:
                processTokenArrived(ev);
                break;
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(ev);

        }
    }

    private void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        Integer tokenId = runningTasks.get(cloudlet.getCloudletId());
        Token token = currentTokens.get(tokenId);
        send(successor.getId(), DELAY, BpmnSimTags.TOKEN_ARRIVED, processToken(token, cloudlet));
        currentTokens.remove(tokenId);
        runningTasks.remove(cloudlet.getCloudletId());
    }


    private Token processToken(Token token, Cloudlet cloudlet) {
        token.addHistoryEntry(getName() + "Start", cloudlet.getExecStartTime());
        token.addHistoryEntry(getName() + "Finish", cloudlet.getFinishTime());
        return token;
    }

    private void processTokenArrived(SimEvent ev) {
        Token token = (Token) ev.getData();
        currentTokens.put(token.getId(), token);
        Cloudlet cloudlet = new Cloudlet(token.hashCode()+getId(), getFinalLength(token.getFileSize()), 1, 300, 300, this.utilizationModel, this.utilizationModel, this.utilizationModel);
        runningTasks.put(cloudlet.getCloudletId(), token.getId());
        send(messageBroker.getId(), DELAY, BpmnSimTags.SUBMIT_TASK, cloudlet);
    }


    private Long getFinalLength(FileSize fileSize){
        long length = this.length;
        switch (fileSize){
            case LARGE:
                length = length * 3;
                break;
            case MEDIUM:
                length = length * 2;
                break;
            case SMALL:
                length = length * 1;
                break;
            default:

        }
        return length;
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
