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
@JsonPropertyOrder({ "UName", "cores" })
public class ProcessingUnit {

  @JsonProperty("UName")
  private String              uName;
  @JsonProperty("cores")
  private Integer             cores;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("UName")
  public String getUName() {
    return uName;
  }

  @JsonProperty("UName")
  public void setUName(String uName) {
    this.uName = uName;
  }

  @JsonProperty("cores")
  public Integer getCores() {
    return cores;
  }

  @JsonProperty("cores")
  public void setCores(Integer cores) {
    this.cores = cores;
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
