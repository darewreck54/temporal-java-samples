/*
 * Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.simpleworkflow.flow.examples.booking;

import com.amazonaws.services.simpleworkflow.flow.examples.common.ConfigHelper;
import com.uber.cadence.WorkflowService;
import com.uber.cadence.client.CadenceClient;
import com.uber.cadence.internal.StartWorkflowOptions;

public class WorkflowExecutionStarter {
    private static WorkflowService.Iface swfService;
    private static String domain;
    
    public static void main(String[] args) throws Exception {

    	// Load configuration
    	ConfigHelper configHelper = ConfigHelper.createConfig();
        
        // Create the client for Simple Workflow Service
        swfService = configHelper.createWorkflowClient();
        domain = configHelper.getDomain();
        
        // Start Workflow instance
        int requestId = Integer.parseInt(configHelper.getValueFromConfig(BookingConfigKeys.WORKFLOW_INPUT_REQUESTID_KEY));
        int customerId = Integer.parseInt(configHelper.getValueFromConfig(BookingConfigKeys.WORKFLOW_INPUT_CUSTOMERID_KEY));

        // Start Wrokflow Execution
        CadenceClient client = CadenceClient.newClient(swfService, domain, null);

        // Start Wrokflow Execution
        String taskList = configHelper.getValueFromConfig(BookingConfigKeys.WORKFLOW_WORKER_TASKLIST);
        StartWorkflowOptions options = new StartWorkflowOptions();
        options.setTaskList(taskList);
        options.setExecutionStartToCloseTimeoutSeconds(20);
        options.setTaskStartToCloseTimeoutSeconds(3);
        BookingWorkflow workflow = client.newWorkflowStub(BookingWorkflow.class, options);
        String activityTaskList = configHelper.getValueFromConfig(BookingConfigKeys.ACTIVITY_WORKER_TASKLIST);
        workflow.makeBooking(activityTaskList, requestId, customerId, true, true);
        System.exit(0);
    }    
}