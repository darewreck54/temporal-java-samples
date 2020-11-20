/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
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

package io.temporal.samples.dsl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.samples.dsl.models.DslWorkflow;
import io.temporal.samples.hello.HelloActivity;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/** Unit test for {@link HelloActivity}. Doesn't use an external Temporal service. */
public class DslActivityTest {

  private TestWorkflowEnvironment testEnv;
  private Worker worker;
  private WorkflowClient client;

  @Rule
  public TestWatcher watchman =
      new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
          if (testEnv != null) {
            System.err.println(testEnv.getDiagnostics());
            testEnv.close();
          }
        }
      };

  @Before
  public void setUp() {
    testEnv = TestWorkflowEnvironment.newInstance();
    worker = testEnv.newWorker("dsl");
    worker.registerWorkflowImplementationTypes(SimpleDSLWorkflowImpl.class);

    client = testEnv.getWorkflowClient();
  }

  @After
  public void tearDown() {
    testEnv.close();
  }

  @Test(timeout = 5000)
  public void testCallbackLogic() throws IOException {
    SampleActivities.SampleActivitiesImpl1 activities =
        new SampleActivities.SampleActivitiesImpl1();
    Worker newWorker = testEnv.newWorker("SampleActivities1");
    newWorker.registerActivitiesImplementations(activities);

    SampleActivities.SampleActivitiesImpl2 activities2 =
        new SampleActivities.SampleActivitiesImpl2();
    Worker newWorker2 = testEnv.newWorker("SampleActivities2");
    newWorker2.registerActivitiesImplementations(activities2);

    testEnv.start();

    // Get a workflow stub using the same task queue the worker uses.
    SimpleDSLWorkflow workflow =
        client.newWorkflowStub(
            SimpleDSLWorkflow.class,
            WorkflowOptions.newBuilder()
                .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                .setTaskQueue("dsl")
                .build());

    Path resourceDirectory = Paths.get("src", "test", "resources", "dsl", "workflow1.yaml");

    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ObjectMapper objectMapper =
        new ObjectMapper(new YAMLFactory()).registerModule(new ParameterNamesModule());
    objectMapper.enable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES);
    objectMapper.findAndRegisterModules();

    DslWorkflow dslWorkflow = objectMapper.readValue(new File(absolutePath), DslWorkflow.class);
    // workflow.execute(dslWorkflow);
    WorkflowClient.start(workflow::execute, dslWorkflow);

    testEnv.sleep(Duration.ofSeconds(30));
    workflow.callback("SampleActivities1", "hello");

    testEnv.sleep(Duration.ofSeconds(30));
    Assert.assertTrue(false);
    // trigger signal
    //  workflow.callback("SampleActivities1");

  }

  /*
  @Test
  public void testMockedActivity() throws IOException {
    worker = testEnv.newWorker("dsl");
    worker.registerWorkflowImplementationTypes(SimpleDSLWorkflowImpl.class);

    SampleActivities.SampleActivities1 activities = mock(SampleActivities.SampleActivities1.class);
    when(activities.getInfo()).thenThrow(ApplicationFailure.newNonRetryableFailure("test", "test"));
    Worker newWorker = testEnv.newWorker("SampleActivities1");
    newWorker.registerActivitiesImplementations(activities);

    testEnv.start();

    // Get a workflow stub using the same task queue the worker uses.
    SimpleDSLWorkflow workflow =
        client.newWorkflowStub(
            SimpleDSLWorkflow.class,
            WorkflowOptions.newBuilder()
                .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                .setTaskQueue("dsl")
                .build());

    Path resourceDirectory = Paths.get("src", "test", "resources", "dsl", "workflow1.yaml");

    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ObjectMapper objectMapper =
        new ObjectMapper(new YAMLFactory()).registerModule(new ParameterNamesModule());
    objectMapper.enable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES);
    objectMapper.findAndRegisterModules();

    DslWorkflow dslWorkflow = objectMapper.readValue(new File(absolutePath), DslWorkflow.class);
    workflow.execute(dslWorkflow);
  }

   */
}
