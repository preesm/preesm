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
  private String              task;
  @JsonProperty("delay")
  private Double              delay;
  @JsonProperty("repeat")
  private Integer             repeat;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("task")
  public String getTask() {
    return task;
  }

  @JsonProperty("task")
  public void setTask(String task) {
    this.task = task;
  }

  @JsonProperty("delay")
  public Double getDelay() {
    return delay;
  }

  @JsonProperty("delay")
  public void setDelay(Double delay) {
    this.delay = delay;
  }

  @JsonProperty("repeat")
  public Integer getRepeat() {
    return repeat;
  }

  @JsonProperty("repeat")
  public void setRepeat(Integer repeat) {
    this.repeat = repeat;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}
