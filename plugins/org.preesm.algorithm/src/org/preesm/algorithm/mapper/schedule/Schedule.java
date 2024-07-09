/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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
  private Long                      period;
  @JsonProperty("intervals")
  private Long                      intervals;
  @JsonProperty("step")
  private Long                      step;
  @JsonProperty("applicationName")
  private String                    applicationName;
  @JsonProperty("runID")
  private String                    runID;
  @JsonProperty("architecture")
  private Architecture              architecture;
  @JsonProperty("delay")
  private Double                    delay;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

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
  public Long getPeriod() {
    return this.period;
  }

  @JsonProperty("period")
  public void setPeriod(final Long period) {
    this.period = period;
  }

  @JsonProperty("intervals")
  public Long getIntervals() {
    return this.intervals;
  }

  @JsonProperty("intervals")
  public void setIntervals(final Long intervals) {
    this.intervals = intervals;
  }

  @JsonProperty("step")
  public Long getStep() {
    return this.step;
  }

  @JsonProperty("step")
  public void setStep(final Long step) {
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

}
