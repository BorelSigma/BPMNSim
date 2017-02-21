package org.bpmnsim;

import org.bpmnsim.entities.*;
import org.bpmnsim.evaluation.Evaluator;
import org.bpmnsim.evaluation.SlaParameter;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by BERGHL on 14.02.17.
 */
public class BPMNSimulation {


    public BPMNSimulation() {

    }
    private BpmnElement createWorkflow(MessageBroker messageBroker){
        StartEvent start = new StartEvent("start1");
        EndEvent end = new EndEvent("end1");

        ParallelGateway parallelGateway = new ParallelGateway("AndGateway1");
        ParallelMergeGateway mergeGateway = new ParallelMergeGateway("AndMergeGateway1");


        XORGateway xorGateway = new XORGateway("XORGateway1");
        XORMergeGateway xorMergeGateway = new XORMergeGateway("XORMergeGateway");

        org.bpmnsim.entities.Task task1 = new org.bpmnsim.entities.Task("task1", messageBroker, (long)10000);
        org.bpmnsim.entities.Task task2 = new org.bpmnsim.entities.Task("task2", messageBroker, (long)12000);
        org.bpmnsim.entities.Task task3 = new org.bpmnsim.entities.Task("task3", messageBroker, (long)4000);
        org.bpmnsim.entities.Task task4 = new org.bpmnsim.entities.Task("task4", messageBroker, (long)11000);
        org.bpmnsim.entities.Task task5 = new org.bpmnsim.entities.Task("task5", messageBroker, (long)17000);
        org.bpmnsim.entities.Task task6 = new org.bpmnsim.entities.Task("task6", messageBroker, (long)8000);
        org.bpmnsim.entities.Task task7 = new org.bpmnsim.entities.Task("task7", messageBroker, (long)9000);



        start.setSuccessor(task1);
        task1.setSuccessor(parallelGateway);
        parallelGateway.addSuccessor(task2);
        task2.setSuccessor(task7);
        parallelGateway.addSuccessor(task3);
        task7.setSuccessor(mergeGateway);
        task3.setSuccessor(mergeGateway);
        mergeGateway.addSuccessor(task4);
        task4.setSuccessor(xorGateway);
        xorGateway.addSuccessor(task5, 0.4);
        xorGateway.addSuccessor(task6, 0.8);
        task5.setSuccessor(xorMergeGateway);
        task6.setSuccessor(xorMergeGateway);
        xorMergeGateway.setSuccessor(end);
        end.setMessageBroker(messageBroker);

        return start;
    }

    private WorkflowDatacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        for (int i = 1; i <= 1; i++) {
            List<Pe> peList1 = new ArrayList<>();
            int mips = 2000;
            // 3. Create PEs and add these into the list.
            //for a quad-core machine, a list of 4 PEs is required:
            peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
            peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
            peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
            peList1.add(new Pe(3, new PeProvisionerSimple(mips)));
            int hostId = i;
            int ram = 8192; //host memory (MB)
            long storage = 1000000; //host storage
            int bw = 10000;
            hostList.add(
                    new Host(
                            hostId,
                            new RamProvisionerSimple(ram),
                            new BwProvisionerSimple(bw),
                            storage,
                            peList1,
                            new VmSchedulerSpaceShared(peList1))); // This is our first machine
            //hostId++;
        }

        // 4. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<>();	//we are not adding SAN devices by now
        WorkflowDatacenter datacenter = null;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 5. Finally, we need to create a storage object.
        /**
         * The bandwidth within a data center in MB/s.
         */
        int maxTransferRate = 30;// the number comes from the futuregrid site, you can specify your bw

        try {
            // Here we set the bandwidth to be 15MB/s
            HarddriveStorage s1 = new HarddriveStorage(name, 1e12);
            s1.setMaxTransferRate(maxTransferRate);
            storageList.add(s1);
            datacenter = new WorkflowDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    private List<CondorVM> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<>();

        //VM Parameters
        long size = 1000; //image size (MB)
        int ram = 2048; //vm memory (MB)
        int mips = 2000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        CondorVM[] vm = new CondorVM[vms];
        for (int i = 0; i < vms; i++) {
            double ratio = 1.0;
            vm[i] = new CondorVM(i, userId, mips * ratio, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }
        return list;
    }



    public void run(int numberOfTokens){
        try {
            // First step: Initialize the WorkflowSim package.
            /**
             * However, the exact number of vms may not necessarily be vmNum If
             * the data center or the host doesn't have sufficient resources the
             * exact vmNum would be smaller than that. Take care.
             */
            int vmNum = 4;//number of vms;
            /** Since we are using MINMIN scheduling algorithm, the planning
             * algorithm should be INVALID such that the planner would not
             * override the result of the scheduler
             */
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.ROUNDROBIN;
            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.SHARED;

            SlaParameter parameter = new SlaParameter(200);

            /**
             * No overheads
             */
            OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);


            /**
             * Initialize static parameters
             */
            Parameters.init(vmNum, "", null,
                    null, op, null, sch_method, null,
                    null, 0);
            ReplicaCatalog.init(file_system);

            // before creating any entities.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, false);

            WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

            MessageBroker messageBroker = new MessageBroker("broker");
            Evaluator evaluator = new Evaluator(parameter, messageBroker);

            /**
             * Create a list of VMs.The userId of a vm is basically the id of
             * the scheduler that controls this vm.
             */
            List<CondorVM> vmlist0 = createVM(messageBroker.getWorkflowScheduler().getId(), Parameters.getVmNum());

            TokenGenerator generator = new TokenGenerator("TokenGenerator",
                    (StartEvent) createWorkflow(messageBroker),
                    numberOfTokens,
                    new ExponentialDistr(10),
                    messageBroker);
            messageBroker.setGenerator(generator);
            messageBroker.getWorkflowScheduler().bindSchedulerDatacenter(datacenter0.getId());
            messageBroker.getWorkflowScheduler().submitVmList(vmlist0);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();
            evaluator.printStatistics();

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }
}
