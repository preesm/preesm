package org.ietr.preesm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
class ScheduleEntry {
  @JsonProperty(required = true)
  String actorName;

  @JsonProperty(required = true)
  int singleRateInstanceNumber;

  @JsonProperty(required = true)
  int start;

  @JsonProperty(required = false)
  int startN;

  @JsonProperty(required = true)
  int end;

  @JsonProperty(required = false)
  int endN;

  @JsonProperty(required = false)
  int core;

  @JsonProperty(required = true)
  String processingElementName;

  @JsonProperty(required = false)
  int graphIteration;

}