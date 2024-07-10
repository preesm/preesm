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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author anmorvan
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "actorName", "singleRateInstanceNumber", "start", "startN", "end", "endN", "core",
    "processingUnitName", "graphIteration" })
public class ScheduleEntry {

  @JsonProperty("actorName")
  private String  actorName;
  @JsonProperty("singleRateInstanceNumber")
  private Integer singleRateInstanceNumber;
  // start in our units, if float in json file, will be converted automatically to long
  @JsonProperty("start")
  private Long start;
  // start in AOW units (subintervals of the while execution time)
  @JsonProperty("startN")
  private Integer startN;
  @JsonProperty("end")
  private Long    end;
  @JsonProperty("endN")
  private Integer endN;
  @JsonProperty("core")
  private Integer core;
  @JsonProperty("processingUnitName")
  private String  processingUnitName;
  @JsonProperty("graphIteration")
  private Integer graphIteration;
  // start in our units, taking into account delays, but not broadcasts apparently
  @JsonProperty("topologicalStart")
  private Long                      topologicalStart;
  @JsonProperty("topologicalEnd")
  private Long                      topologicalEnd;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

  // Name in Preesm
  private String firingName;

  public String getFiringName() {
    return firingName;
  }

  public void setFiringName(String firingName) {
    this.firingName = firingName;
  }

  @JsonProperty("actorName")
  public String getActorName() {
    return this.actorName;
  }

  @JsonProperty("actorName")
  public void setActorName(final String taskName) {
    this.actorName = taskName;
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
  public Long getStart() {
    return this.start;
  }

  @JsonProperty("start")
  public void setStart(final Long start) {
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
  public Long getEnd() {
    return this.end;
  }

  @JsonProperty("end")
  public void setEnd(final Long end) {
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

  @JsonProperty("topologicalEnd")
  public Long getTopologicalEnd() {
    return this.topologicalEnd;
  }

  @JsonProperty("topologicalEnd")
  public void setTopologicalEnd(final Long topologicalEnd) {
    this.topologicalEnd = topologicalEnd;
  }

  @JsonProperty("topologicalStart")
  public Long getTopologicalStart() {
    return this.topologicalStart;
  }

  @JsonProperty("topologicalStart")
  public void setTopologicalStart(final Long topologicalStart) {
    this.topologicalStart = topologicalStart;
  }

}
