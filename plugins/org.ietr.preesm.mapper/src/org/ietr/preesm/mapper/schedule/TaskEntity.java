package org.ietr.preesm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anmorvan
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "task", "delay", "repeat" })
public class TaskEntity {

  @JsonProperty("task")
  private String                    task;
  @JsonProperty("delay")
  private Double                    delay;
  @JsonProperty("repeat")
  private Integer                   repeat;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("task")
  public String getTask() {
    return this.task;
  }

  @JsonProperty("task")
  public void setTask(final String task) {
    this.task = task;
  }

  @JsonProperty("delay")
  public Double getDelay() {
    return this.delay;
  }

  @JsonProperty("delay")
  public void setDelay(final Double delay) {
    this.delay = delay;
  }

  @JsonProperty("repeat")
  public Integer getRepeat() {
    return this.repeat;
  }

  @JsonProperty("repeat")
  public void setRepeat(final Integer repeat) {
    this.repeat = repeat;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(final String name, final Object value) {
    this.additionalProperties.put(name, value);
  }

}
