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
@JsonPropertyOrder({ "taskName", "singleRateInstanceNumber", "start", "startN", "end", "endN", "core",
    "processingUnitName", "graphIteration" })
public class ScheduleEntry {

  @JsonProperty("taskName")
  private String              taskName;
  @JsonProperty("singleRateInstanceNumber")
  private Integer             singleRateInstanceNumber;
  @JsonProperty("start")
  private Integer             start;
  @JsonProperty("startN")
  private Integer             startN;
  @JsonProperty("end")
  private Integer             end;
  @JsonProperty("endN")
  private Integer             endN;
  @JsonProperty("core")
  private Integer             core;
  @JsonProperty("processingUnitName")
  private String              processingUnitName;
  @JsonProperty("graphIteration")
  private Integer             graphIteration;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("taskName")
  public String getTaskName() {
    return taskName;
  }

  @JsonProperty("taskName")
  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  @JsonProperty("singleRateInstanceNumber")
  public Integer getSingleRateInstanceNumber() {
    return singleRateInstanceNumber;
  }

  @JsonProperty("singleRateInstanceNumber")
  public void setSingleRateInstanceNumber(Integer singleRateInstanceNumber) {
    this.singleRateInstanceNumber = singleRateInstanceNumber;
  }

  @JsonProperty("start")
  public Integer getStart() {
    return start;
  }

  @JsonProperty("start")
  public void setStart(Integer start) {
    this.start = start;
  }

  @JsonProperty("startN")
  public Integer getStartN() {
    return startN;
  }

  @JsonProperty("startN")
  public void setStartN(Integer startN) {
    this.startN = startN;
  }

  @JsonProperty("end")
  public Integer getEnd() {
    return end;
  }

  @JsonProperty("end")
  public void setEnd(Integer end) {
    this.end = end;
  }

  @JsonProperty("endN")
  public Integer getEndN() {
    return endN;
  }

  @JsonProperty("endN")
  public void setEndN(Integer endN) {
    this.endN = endN;
  }

  @JsonProperty("core")
  public Integer getCore() {
    return core;
  }

  @JsonProperty("core")
  public void setCore(Integer core) {
    this.core = core;
  }

  @JsonProperty("processingUnitName")
  public String getProcessingUnitName() {
    return processingUnitName;
  }

  @JsonProperty("processingUnitName")
  public void setProcessingUnitName(String processingUnitName) {
    this.processingUnitName = processingUnitName;
  }

  @JsonProperty("graphIteration")
  public Integer getGraphIteration() {
    return graphIteration;
  }

  @JsonProperty("graphIteration")
  public void setGraphIteration(Integer graphIteration) {
    this.graphIteration = graphIteration;
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
