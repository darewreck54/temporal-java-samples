/*
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.uber.cadence.samples.fileprocessing;

import com.uber.cadence.workflow.WorkflowMethod;

/**
 * Contract for file processing workflow
 */
public interface FileProcessingWorkflow {

    final class Arguments {
        String sourceBucketName;
        String sourceFilename;
        String targetBucketName;
        String targetFilename;

        public String getSourceBucketName() {
            return sourceBucketName;
        }

        public void setSourceBucketName(String sourceBucketName) {
            this.sourceBucketName = sourceBucketName;
        }

        public String getSourceFilename() {
            return sourceFilename;
        }

        public void setSourceFilename(String sourceFilename) {
            this.sourceFilename = sourceFilename;
        }

        public String getTargetBucketName() {
            return targetBucketName;
        }

        public void setTargetBucketName(String targetBucketName) {
            this.targetBucketName = targetBucketName;
        }

        public String getTargetFilename() {
            return targetFilename;
        }

        public void setTargetFilename(String targetFilename) {
            this.targetFilename = targetFilename;
        }
    }
    /**
     * Uses a structure as arguments, to make addition of new arguments a backwards compatible change.
     */
    @WorkflowMethod(taskList = FileProcessingStarter.WORKFLOW_TASK_LIST,
            executionStartToCloseTimeoutSeconds = 300)
    void processFile(Arguments args);
}
