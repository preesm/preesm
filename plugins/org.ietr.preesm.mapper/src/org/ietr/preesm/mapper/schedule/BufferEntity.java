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
  private String              producer;
  @JsonProperty("consumer")
  private String              consumer;
  @JsonProperty("initValue")
  private Double              initValue;
  @JsonProperty("initValueN")
  private Double              initValueN;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("producer")
  public String getProducer() {
    return producer;
  }

  @JsonProperty("producer")
  public void setProducer(String producer) {
    this.producer = producer;
  }

  @JsonProperty("consumer")
  public String getConsumer() {
    return consumer;
  }

  @JsonProperty("consumer")
  public void setConsumer(String consumer) {
    this.consumer = consumer;
  }

  @JsonProperty("initValue")
  public Double getInitValue() {
    return initValue;
  }

  @JsonProperty("initValue")
  public void setInitValue(Double initValue) {
    this.initValue = initValue;
  }

  @JsonProperty("initValueN")
  public Double getInitValueN() {
    return initValueN;
  }

  @JsonProperty("initValueN")
  public void setInitValueN(Double initValueN) {
    this.initValueN = initValueN;
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
