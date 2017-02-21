package bpmsim;

import org.bpmnsim.BPMNSimulation;

/**
 * Created by BERGHL on 04.02.17.
 */
public class Example1 {



    ////////////////////////// STATIC METHODS ///////////////////////
    /**
     * Creates main() to run this example This example has only one datacenter
     * and one storage
     */
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();


        for(int x = 0; x<1; x++){

            BPMNSimulation simulation = new BPMNSimulation();

            simulation.run(1000);

        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Total runtime of simulation process in ms: "+ totalTime);

    }



}
