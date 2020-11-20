package io.temporal.samples.dsl;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

public class SampleActivities {
  @ActivityInterface(namePrefix = "SampleActivities1")
  public interface SampleActivities1 {
    @ActivityMethod
    String getInfo();
  }

  @ActivityInterface(namePrefix = "SampleActivity2")
  public interface SampleActivities2 {
    String getInfo();
  }

  @ActivityInterface(namePrefix = "SampleActivity3")
  public interface SampleActivities3 {
    String getInfo();
  }

  @ActivityInterface(namePrefix = "SampleActivity4")
  public interface SampleActivities4 {
    String getInfo();
  }

  @ActivityInterface(namePrefix = "SampleActivity5")
  public interface SampleActivities5 {
    String getInfo();
  }

  public static class SampleActivitiesImpl1 implements SampleActivities1 {
    @Override
    public String getInfo() {
      ActivityExecutionContext context = Activity.getExecutionContext();
      // info = context.getInfo();
      // info.getActivityId();

      long test = 0;
      /*
      while (true) {
        try {
          test++;
          context.heartbeat(test);
          sleep(1);
        } catch (ActivityWorkerShutdownException ex) {
          System.out.println(ex.getMessage());
          throw Activity.wrap(ex);
        }
      }

       */
      String name = Activity.getExecutionContext().getInfo().getActivityType();
      return "Result_" + name;
    }

    private void sleep(int seconds) {
      try {
        Thread.sleep(seconds * 1000);
      } catch (InterruptedException ee) {
        // Empty
      }
    }
  }

  public static class SampleActivitiesImpl2 implements SampleActivities2 {
    @Override
    public String getInfo() {
      String name = Activity.getExecutionContext().getInfo().getActivityType();
      return "Result_" + name;
    }
  }

  public static class SampleActivitiesImpl3 implements SampleActivities3 {
    @Override
    public String getInfo() {

      String name = Activity.getExecutionContext().getInfo().getActivityType();
      return "Result_" + name;
    }
  }

  public static class SampleActivitiesImpl4 implements SampleActivities4 {
    @Override
    public String getInfo() {
      String name = Activity.getExecutionContext().getInfo().getActivityType();
      return "Result_" + name;
    }
  }

  public static class SampleActivitiesImpl5 implements SampleActivities5 {
    @Override
    public String getInfo() {
      String name = Activity.getExecutionContext().getInfo().getActivityType();
      return "Result_" + name;
    }
  }
}
