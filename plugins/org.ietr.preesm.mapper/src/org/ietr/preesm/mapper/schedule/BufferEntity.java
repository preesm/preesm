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
@JsonPropertyOrder({ "producer", "consumer", "initValue", "initValueN" })
public class BufferEntity {

  @JsonProperty("producer")
  private String                    producer;
  @JsonProperty("consumer")
  private String                    consumer;
  @JsonProperty("initValue")
  private Double                    initValue;
  @JsonProperty("initValueN")
  private Double                    initValueN;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("producer")
  public String getProducer() {
    return this.producer;
  }

  @JsonProperty("producer")
  public void setProducer(final String producer) {
    this.producer = producer;
  }

  @JsonProperty("consumer")
  public String getConsumer() {
    return this.consumer;
  }

  @JsonProperty("consumer")
  public void setConsumer(final String consumer) {
    this.consumer = consumer;
  }

  @JsonProperty("initValue")
  public Double getInitValue() {
    return this.initValue;
  }

  @JsonProperty("initValue")
  public void setInitValue(final Double initValue) {
    this.initValue = initValue;
  }

  @JsonProperty("initValueN")
  public Double getInitValueN() {
    return this.initValueN;
  }

  @JsonProperty("initValueN")
  public void setInitValueN(final Double initValueN) {
    this.initValueN = initValueN;
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
