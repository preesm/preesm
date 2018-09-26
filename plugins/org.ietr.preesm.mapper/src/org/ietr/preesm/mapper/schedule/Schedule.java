package org.ietr.preesm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author anmorvan
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "scheduleEntries", "preScheduleEntries", "bufferEntities", "taskEntities", "processingUnits",
    "period", "intervals", "step", "applicationName", "runID", "architecture", "delay" })
public class Schedule {

  @JsonProperty("scheduleEntries")
  private List<ScheduleEntry>  scheduleEntries      = null;
  @JsonProperty("preScheduleEntries")
  private List<Object>         preScheduleEntries   = null;
  @JsonProperty("bufferEntities")
  private List<BufferEntity>   bufferEntities       = null;
  @JsonProperty("taskEntities")
  private List<TaskEntity>     taskEntities         = null;
  @JsonProperty("processingUnits")
  private List<ProcessingUnit> processingUnits      = null;
  @JsonProperty("period")
  private Integer              period;
  @JsonProperty("intervals")
  private Integer              intervals;
  @JsonProperty("step")
  private Integer              step;
  @JsonProperty("applicationName")
  private String               applicationName;
  @JsonProperty("runID")
  private String               runID;
  @JsonProperty("architecture")
  private Architecture         architecture;
  @JsonProperty("delay")
  private Double               delay;
  @JsonIgnore
  private Map<String, Object>  additionalProperties = new HashMap<String, Object>();

  @JsonProperty("scheduleEntries")
  public List<ScheduleEntry> getScheduleEntries() {
    return scheduleEntries;
  }

  @JsonProperty("scheduleEntries")
  public void setScheduleEntries(List<ScheduleEntry> scheduleEntries) {
    this.scheduleEntries = scheduleEntries;
  }

  @JsonProperty("preScheduleEntries")
  public List<Object> getPreScheduleEntries() {
    return preScheduleEntries;
  }

  @JsonProperty("preScheduleEntries")
  public void setPreScheduleEntries(List<Object> preScheduleEntries) {
    this.preScheduleEntries = preScheduleEntries;
  }

  @JsonProperty("bufferEntities")
  public List<BufferEntity> getBufferEntities() {
    return bufferEntities;
  }

  @JsonProperty("bufferEntities")
  public void setBufferEntities(List<BufferEntity> bufferEntities) {
    this.bufferEntities = bufferEntities;
  }

  @JsonProperty("taskEntities")
  public List<TaskEntity> getTaskEntities() {
    return taskEntities;
  }

  @JsonProperty("taskEntities")
  public void setTaskEntities(List<TaskEntity> taskEntities) {
    this.taskEntities = taskEntities;
  }

  @JsonProperty("processingUnits")
  public List<ProcessingUnit> getProcessingUnits() {
    return processingUnits;
  }

  @JsonProperty("processingUnits")
  public void setProcessingUnits(List<ProcessingUnit> processingUnits) {
    this.processingUnits = processingUnits;
  }

  @JsonProperty("period")
  public Integer getPeriod() {
    return period;
  }

  @JsonProperty("period")
  public void setPeriod(Integer period) {
    this.period = period;
  }

  @JsonProperty("intervals")
  public Integer getIntervals() {
    return intervals;
  }

  @JsonProperty("intervals")
  public void setIntervals(Integer intervals) {
    this.intervals = intervals;
  }

  @JsonProperty("step")
  public Integer getStep() {
    return step;
  }

  @JsonProperty("step")
  public void setStep(Integer step) {
    this.step = step;
  }

  @JsonProperty("applicationName")
  public String getApplicationName() {
    return applicationName;
  }

  @JsonProperty("applicationName")
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  @JsonProperty("runID")
  public String getRunID() {
    return runID;
  }

  @JsonProperty("runID")
  public void setRunID(String runID) {
    this.runID = runID;
  }

  @JsonProperty("architecture")
  public Architecture getArchitecture() {
    return architecture;
  }

  @JsonProperty("architecture")
  public void setArchitecture(Architecture architecture) {
    this.architecture = architecture;
  }

  @JsonProperty("delay")
  public Double getDelay() {
    return delay;
  }

  @JsonProperty("delay")
  public void setDelay(Double delay) {
    this.delay = delay;
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
