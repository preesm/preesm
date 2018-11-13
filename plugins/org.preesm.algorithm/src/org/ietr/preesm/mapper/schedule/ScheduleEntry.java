/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
  private String                    taskName;
  @JsonProperty("singleRateInstanceNumber")
  private Integer                   singleRateInstanceNumber;
  @JsonProperty("start")
  private Integer                   start;
  @JsonProperty("startN")
  private Integer                   startN;
  @JsonProperty("end")
  private Integer                   end;
  @JsonProperty("endN")
  private Integer                   endN;
  @JsonProperty("core")
  private Integer                   core;
  @JsonProperty("processingUnitName")
  private String                    processingUnitName;
  @JsonProperty("graphIteration")
  private Integer                   graphIteration;
  @JsonProperty("topologicalStart")
  private Integer                   topologicalStart;
  @JsonProperty("topologicalEnd")
  private Integer                   topologicalEnd;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("taskName")
  public String getTaskName() {
    return this.taskName;
  }

  @JsonProperty("taskName")
  public void setTaskName(final String taskName) {
    this.taskName = taskName;
  }

  @JsonProperty("singleRateInstanceNumber")
  public Integer getSingleRateInstanceNumber() {
    return this.singleRateInstanceNumber;
  }

  @JsonProperty("singleRateInstanceNumber")
  public void setSingleRateInstanceNumber(final Integer singleRateInstanceNumber) {
    this.singleRateInstanceNumber = singleRateInstanceNumber;
  }

  @JsonProperty("start")
  public Integer getStart() {
    return this.start;
  }

  @JsonProperty("start")
  public void setStart(final Integer start) {
    this.start = start;
  }

  @JsonProperty("startN")
  public Integer getStartN() {
    return this.startN;
  }

  @JsonProperty("startN")
  public void setStartN(final Integer startN) {
    this.startN = startN;
  }

  @JsonProperty("end")
  public Integer getEnd() {
    return this.end;
  }

  @JsonProperty("end")
  public void setEnd(final Integer end) {
    this.end = end;
  }

  @JsonProperty("endN")
  public Integer getEndN() {
    return this.endN;
  }

  @JsonProperty("endN")
  public void setEndN(final Integer endN) {
    this.endN = endN;
  }

  @JsonProperty("core")
  public Integer getCore() {
    return this.core;
  }

  @JsonProperty("core")
  public void setCore(final Integer core) {
    this.core = core;
  }

  @JsonProperty("processingUnitName")
  public String getProcessingUnitName() {
    return this.processingUnitName;
  }

  @JsonProperty("processingUnitName")
  public void setProcessingUnitName(final String processingUnitName) {
    this.processingUnitName = processingUnitName;
  }

  @JsonProperty("graphIteration")
  public Integer getGraphIteration() {
    return this.graphIteration;
  }

  @JsonProperty("graphIteration")
  public void setGraphIteration(final Integer graphIteration) {
    this.graphIteration = graphIteration;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(final String name, final Object value) {
    this.additionalProperties.put(name, value);
  }

  public Integer getTopologicalEnd() {
    return topologicalEnd;
  }

  public void setTopologicalEnd(Integer topologicalEnd) {
    this.topologicalEnd = topologicalEnd;
  }

  public Integer getTopologicalStart() {
    return topologicalStart;
  }

  public void setTopologicalStart(Integer topologicalStart) {
    this.topologicalStart = topologicalStart;
  }

}
