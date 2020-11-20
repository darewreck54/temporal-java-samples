package io.temporal.samples.dsl.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.CanceledFailure;
import io.temporal.failure.TemporalException;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.CancellationScope;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ActivityInvocation {
  public String name;
  public String method;
  public String[] arguments;
  public String result;
  public boolean supportCallback;

  @JsonCreator
  public ActivityInvocation(
      @JsonProperty("name") String name,
      @JsonProperty("method") String method,
      @JsonProperty("arguments") String[] arguments,
      @JsonProperty("result") String result,
      @JsonProperty("supportCallBack") boolean supportCallback) {
    this.name = name;
    this.arguments = arguments;
    this.result = result;
    this.method = method;
    this.supportCallback = supportCallback;
  }

  public Void execute(
      Map<String, String> bindings,
      Map<String, CancellationScope> map,
      Map<String, String> activitySignalResponseMap) {
    String[] args = makeInput(this.arguments, bindings);
    ActivityStub stub =
        Workflow.newUntypedActivityStub(
            ActivityOptions.newBuilder()
                .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .setTaskQueue(this.name)
                .build());

    CancellationScope scope = null;
    try {
      String methodNameDescriptor =
          this.name + this.method.substring(0, 1).toUpperCase() + this.method.substring(1);

      AtomicReference<Promise<String>> activityExecutePromiseAtomic = new AtomicReference<>();

      scope =
          Workflow.newCancellationScope(
              () -> {
                Promise<String> activityPromise =
                    stub.executeAsync(methodNameDescriptor, String.class, new Object[] {args});
                activityExecutePromiseAtomic.set(activityPromise);
              });

      map.put(this.name, scope);
      scope.run();

      Promise<String> activityExecutePromise = activityExecutePromiseAtomic.get();
      String activityResults = null;
      try {
        activityResults = activityExecutePromise.get();
      } catch (ActivityFailure e) {
        if ((e.getCause() instanceof CanceledFailure)) {
          System.out.println("scope successfully cancelled");
          if (scope.isCancelRequested()) {
            activityResults = activitySignalResponseMap.get(this.name);
          }

        } else {
          System.out.println("scope received a CanceledFailure");
          throw e;
        }
      } catch (TemporalException ex) {
        throw ex;
      }
      // execute activity which will be blocking
      System.out.println("Activity has completed");

      // Determine if the callback was triggered which will effectively cancel the activity
      if (!Strings.isNullOrEmpty(this.result)) {
        bindings.put(this.result, activityResults);
      }

      return null;
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.toString(), e.getMessage());
    } finally {
    }
  }

  private String[] makeInput(String[] arguments, Map<String, String> argsMap) {
    String[] args = new String[arguments.length];
    for (int i = 0; i < arguments.length; i++) {
      args[i] = argsMap.get(arguments[i]);
    }
    return args;
  }
}
