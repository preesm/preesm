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
import java.util.Map;

/**
 *
 * @author anmorvan
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "producer", "consumer", "initValue", "initValueN" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class BufferEntity {

  @JsonProperty("producer")
  private String producer;
  @JsonProperty("consumer")
  private String consumer;
  @JsonProperty("initValue")
  private Double initValue;
  // same as initValue
  @JsonProperty("initValueN")
  private Double                    initValueN;
  @JsonIgnore
  private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

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

}
