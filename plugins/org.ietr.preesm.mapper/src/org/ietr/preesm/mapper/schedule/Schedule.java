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
  private List<ScheduleEntry>       scheduleEntries      = null;
  @JsonProperty("preScheduleEntries")
  private List<Object>              preScheduleEntries   = null;
  @JsonProperty("bufferEntities")
  private List<BufferEntity>        bufferEntities       = null;
  @JsonProperty("taskEntities")
  private List<TaskEntity>          taskEntities         = null;
  @JsonProperty("processingUnits")
  private List<ProcessingUnit>      processingUnits      = null;
  @JsonProperty("period")
  private Integer                   period;
  @JsonProperty("intervals")
  private Integer                   intervals;
  @JsonProperty("step")
  private Integer                   step;
  @JsonProperty("applicationName")
  private String                    applicationName;
  @JsonProperty("runID")
  private String                    runID;
  @JsonProperty("architecture")
  private Architecture              architecture;
  @JsonProperty("delay")
  private Double                    delay;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("scheduleEntries")
  public List<ScheduleEntry> getScheduleEntries() {
    return this.scheduleEntries;
  }

  @JsonProperty("scheduleEntries")
  public void setScheduleEntries(final List<ScheduleEntry> scheduleEntries) {
    this.scheduleEntries = scheduleEntries;
  }

  @JsonProperty("preScheduleEntries")
  public List<Object> getPreScheduleEntries() {
    return this.preScheduleEntries;
  }

  @JsonProperty("preScheduleEntries")
  public void setPreScheduleEntries(final List<Object> preScheduleEntries) {
    this.preScheduleEntries = preScheduleEntries;
  }

  @JsonProperty("bufferEntities")
  public List<BufferEntity> getBufferEntities() {
    return this.bufferEntities;
  }

  @JsonProperty("bufferEntities")
  public void setBufferEntities(final List<BufferEntity> bufferEntities) {
    this.bufferEntities = bufferEntities;
  }

  @JsonProperty("taskEntities")
  public List<TaskEntity> getTaskEntities() {
    return this.taskEntities;
  }

  @JsonProperty("taskEntities")
  public void setTaskEntities(final List<TaskEntity> taskEntities) {
    this.taskEntities = taskEntities;
  }

  @JsonProperty("processingUnits")
  public List<ProcessingUnit> getProcessingUnits() {
    return this.processingUnits;
  }

  @JsonProperty("processingUnits")
  public void setProcessingUnits(final List<ProcessingUnit> processingUnits) {
    this.processingUnits = processingUnits;
  }

  @JsonProperty("period")
  public Integer getPeriod() {
    return this.period;
  }

  @JsonProperty("period")
  public void setPeriod(final Integer period) {
    this.period = period;
  }

  @JsonProperty("intervals")
  public Integer getIntervals() {
    return this.intervals;
  }

  @JsonProperty("intervals")
  public void setIntervals(final Integer intervals) {
    this.intervals = intervals;
  }

  @JsonProperty("step")
  public Integer getStep() {
    return this.step;
  }

  @JsonProperty("step")
  public void setStep(final Integer step) {
    this.step = step;
  }

  @JsonProperty("applicationName")
  public String getApplicationName() {
    return this.applicationName;
  }

  @JsonProperty("applicationName")
  public void setApplicationName(final String applicationName) {
    this.applicationName = applicationName;
  }

  @JsonProperty("runID")
  public String getRunID() {
    return this.runID;
  }

  @JsonProperty("runID")
  public void setRunID(final String runID) {
    this.runID = runID;
  }

  @JsonProperty("architecture")
  public Architecture getArchitecture() {
    return this.architecture;
  }

  @JsonProperty("architecture")
  public void setArchitecture(final Architecture architecture) {
    this.architecture = architecture;
  }

  @JsonProperty("delay")
  public Double getDelay() {
    return this.delay;
  }

  @JsonProperty("delay")
  public void setDelay(final Double delay) {
    this.delay = delay;
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
