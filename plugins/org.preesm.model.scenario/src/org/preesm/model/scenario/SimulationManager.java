/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.model.scenario;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.preesm.model.scenario.types.DataType;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.route.Route;

/**
 * Handles simulation parameters.
 *
 * @author mpelcat
 */
public class SimulationManager {

  /** Names of the main operator and com node. */
  private ComponentInstance mainComNode;

  /** The main operator name. */
  private ComponentInstance mainOperator;

  /**
   * Average transfer size sizes in base unit (usually byte). This size is used while calculating the routing table. The
   * routes between operators are static and will be optimized for the given data size.
   */
  private long averageDataSize = Route.averageTransfer;

  /** Names of the data types with their size. */
  private final Map<String /* type name */, DataType> dataTypes;

  /** Operators able to execute special vertices. */
  private final Set<ComponentInstance> specialVertexOperators;

  /** Number of executions of the top graph when simulating PiGraphs. */
  private int numberOfTopExecutions = 1;

  private final PreesmScenario preesmScenario;

  /**
   * Instantiates a new simulation manager.
   */
  public SimulationManager(final PreesmScenario preesmScenario) {
    super();
    this.preesmScenario = preesmScenario;

    this.dataTypes = new LinkedHashMap<>();
    this.specialVertexOperators = new LinkedHashSet<>();
  }

  /**
   * Gets the main com node name.
   *
   * @return the main com node name
   */
  public ComponentInstance getMainComNode() {
    return this.mainComNode;
  }

  /**
   * Sets the main com node name.
   *
   * @param mainComNode
   *          the new main com node name
   */
  public void setMainComNode(final ComponentInstance mainComNode) {
    this.mainComNode = mainComNode;
  }

  /**
   * Gets the main operator name.
   *
   * @return the main operator name
   */
  public ComponentInstance getMainOperator() {
    return this.mainOperator;
  }

  /**
   * Sets the main operator name.
   *
   * @param mainOperator
   *          the new main operator name
   */
  public void setMainOperator(final ComponentInstance mainOperator) {
    this.mainOperator = mainOperator;
  }

  /**
   * Gets the data types.
   *
   * @return the data types
   */
  public Map<String, DataType> getDataTypes() {
    return this.dataTypes;
  }

  /**
   * Gets the data type.
   *
   * @param name
   *          the name
   * @return the data type
   */
  public DataType getDataType(final String name) {
    return this.dataTypes.get(name);
  }

  /**
   * Gets the data type size or default.
   *
   * @param name
   *          the name
   * @return the data type size or default
   */
  public long getDataTypeSizeOrDefault(final String name) {
    if (this.dataTypes.get(name) == null) {
      return DataType.DEFAUTL_DATA_TYPE_SIZE;
    } else {
      return this.dataTypes.get(name).getSize();
    }
  }

  /**
   * Put data type.
   *
   * @param dataType
   *          the data type
   */
  public void putDataType(final DataType dataType) {
    this.dataTypes.put(dataType.getTypeName(), dataType);
  }

  /**
   * Removes the data type.
   *
   * @param dataTypeName
   *          the data type name
   */
  public void removeDataType(final String dataTypeName) {
    this.dataTypes.remove(dataTypeName);
  }

  /**
   * Sets the average data size.
   *
   * @param size
   *          the new average data size
   */
  public void setAverageDataSize(final long size) {
    this.averageDataSize = size;
  }

  /**
   * Gets the average data size.
   *
   * @return the average data size
   */
  public long getAverageDataSize() {
    return this.averageDataSize;
  }

  /**
   * Gets the special vertex operator ids.
   *
   * @return the special vertex operator ids
   */
  public Set<ComponentInstance> getSpecialVertexOperators() {
    return this.specialVertexOperators;
  }

  /**
   * Adds the special vertex operator id.
   *
   * @param opId
   *          the op id
   */
  public void addSpecialVertexOperator(final ComponentInstance opId) {
    if (!hasSpecialVertexOperator(opId)) {
      this.specialVertexOperators.add(opId);
    }
  }

  /**
   * Removes the special vertex operator id.
   *
   * @param id
   *          the id
   */
  public void removeSpecialVertexOperator(final ComponentInstance id) {
    this.specialVertexOperators.remove(id);
  }

  /**
   * Checks for special vertex operator id.
   *
   * @param id
   *          the id
   * @return true, if successful
   */
  public boolean hasSpecialVertexOperator(final ComponentInstance id) {
    return this.specialVertexOperators.contains(id);
  }

  /**
   * Gets the number of top executions.
   *
   * @return the number of top executions
   */
  public int getNumberOfTopExecutions() {
    if (this.numberOfTopExecutions < 1) {
      setNumberOfTopExecutions(1);
    }
    return this.numberOfTopExecutions;
  }

  /**
   * Sets the number of top executions.
   *
   * @param numberOfTopExecutions
   *          the new number of top executions
   */
  public void setNumberOfTopExecutions(final int numberOfTopExecutions) {
    this.numberOfTopExecutions = numberOfTopExecutions;
  }

}
