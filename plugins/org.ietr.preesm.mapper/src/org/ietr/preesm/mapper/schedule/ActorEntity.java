package org.ietr.preesm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
class ActorEntity {
  @JsonProperty(required = true)
  String actor;
  @JsonProperty(required = true)
  double delay;
  @JsonProperty(required = true)
  int    repeat;
}
