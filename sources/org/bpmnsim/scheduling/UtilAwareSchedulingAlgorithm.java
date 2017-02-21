/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.bpmnsim.scheduling;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.util.MathUtil;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;
import org.workflowsim.scheduling.BaseSchedulingAlgorithm;

import java.util.Iterator;
import java.util.List;

public class UtilAwareSchedulingAlgorithm extends BaseSchedulingAlgorithm {



    /**
     * The main function
     */
    @Override
    public void run() {


        for (Iterator it = getCloudletList().iterator(); it.hasNext();) {
            Cloudlet cloudlet = (Cloudlet) it.next();
            boolean stillHasVm = false;
            for (Iterator itc = getVmList().iterator(); itc.hasNext();) {
                CondorVM vm = (CondorVM) itc.next();

                Double currentMips = MathUtil.sum(vm.getCloudletScheduler().getCurrentMipsShare());
                if((vm.getMips()-currentMips) > 0.0){
                    cloudlet.setVmId(vm.getId());
                    getScheduledList().add(cloudlet);
                    stillHasVm = true;
                    break;
                }
            }
            //no vm available 
            if (!stillHasVm) {
                break;
            }
        }
    }

}
