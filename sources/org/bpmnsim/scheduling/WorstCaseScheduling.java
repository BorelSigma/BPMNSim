package org.bpmnsim.scheduling;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.util.MathUtil;
import org.workflowsim.CondorVM;
import org.workflowsim.scheduling.BaseSchedulingAlgorithm;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by BERGHL on 11.02.17.
 */
public class WorstCaseScheduling extends BaseSchedulingAlgorithm {
    @Override
    public void run() throws Exception {
        List vmList = getVmList();
        int sizeVm = vmList.size();

        for (Iterator it = getCloudletList().iterator(); it.hasNext();) {
            Cloudlet cloudlet = (Cloudlet) it.next();
            int randomNum = ThreadLocalRandom.current().nextInt(0, sizeVm-1);
            CondorVM vm = (CondorVM) vmList.get(randomNum);
            cloudlet.setVmId(vm.getId());
            getScheduledList().add(cloudlet);
        }
    }
}
