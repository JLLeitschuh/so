/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.so.bpmn.infrastructure.bpmn.subprocess;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import java.util.List;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.onap.so.bpmn.common.BuildingBlockExecution;
import org.onap.so.bpmn.BaseBPMNTest;

public class VNFUnlockActivityTest extends BaseBPMNTest {
    @Test
    public void sunnyDayVNFUnlockActivity_Test() throws InterruptedException {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("VNFUnlockActivity", variables);
        assertThat(pi).isNotNull();
        processExternalTasks(pi, "TaskUnlock");
        assertThat(pi).isStarted().hasPassedInOrder("VNFUnlockActivity_Start", "TaskPreProcessActivity", "TaskUnlock",
                "VNFUnlockActivity_End");
        assertThat(pi).isEnded();
    }

    @Test
    public void rainyDayVNFUnlockActivity_Test() throws Exception {
        doThrow(new BpmnError("7000", "TESTING ERRORS")).when(appcOrchestratorPreProcessor)
                .buildAppcTaskRequest(any(BuildingBlockExecution.class), any(String.class));
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("VNFUnlockActivity", variables);
        assertThat(pi).isNotNull().isStarted().hasPassedInOrder("VNFUnlockActivity_Start", "TaskPreProcessActivity")
                .hasNotPassed("TaskUnlock", "VNFUnlockActivity_End");
    }

}
