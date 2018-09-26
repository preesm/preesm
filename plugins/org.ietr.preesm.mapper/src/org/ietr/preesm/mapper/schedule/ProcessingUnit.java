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
  private String                    uName;
  @JsonProperty("cores")
  private Integer                   cores;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("UName")
  public String getUName() {
    return this.uName;
  }

  @JsonProperty("UName")
  public void setUName(final String uName) {
    this.uName = uName;
  }

  @JsonProperty("cores")
  public Integer getCores() {
    return this.cores;
  }

  @JsonProperty("cores")
  public void setCores(final Integer cores) {
    this.cores = cores;
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
