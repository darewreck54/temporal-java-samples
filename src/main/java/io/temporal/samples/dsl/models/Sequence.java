package io.temporal.samples.dsl.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.temporal.workflow.CancellationScope;
import java.util.Map;

public class Sequence {
  public Statement elements[];

  @JsonCreator
  public Sequence(@JsonProperty("elements") Statement elements[]) {
    this.elements = elements;
  }

  public void execute(
      Map<String, String> bindings,
      Map<String, CancellationScope> map,
      Map<String, String> activitySignalResponseMap) {
    for (Statement s : elements) {
      s.execute(bindings, map, activitySignalResponseMap);
    }
  }
}
