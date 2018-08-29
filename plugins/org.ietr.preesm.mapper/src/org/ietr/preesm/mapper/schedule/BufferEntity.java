package org.ietr.preesm.mapper.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
class BufferEntity {
  @JsonProperty(required = true)
  String producer;
  @JsonProperty(required = true)
  String consumer;

  @JsonProperty(required = true)
  double initValue;
  @JsonProperty(required = true)
  double initValueN;
}
