package io.temporal.samples.dsl;

import io.temporal.samples.dsl.models.DslWorkflow;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SimpleDSLWorkflow {
  @WorkflowMethod
  void execute(DslWorkflow workflow);

  @SignalMethod
  void callback(String activityName, String response);
}
