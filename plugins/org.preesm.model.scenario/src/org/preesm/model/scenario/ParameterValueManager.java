/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
import java.util.Map;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;

/**
 * Manager class for parameters, storing values given by the user to parameters.
 *
 * @author cguy
 */
public class ParameterValueManager {

  private final Map<Parameter, String> parameterValueMap = new LinkedHashMap<>();

  private final PreesmScenario preesmScenario;

  /**
   * Instantiates a new parameter value manager.
   */
  public ParameterValueManager(final PreesmScenario preesmScenario) {
    this.preesmScenario = preesmScenario;
  }

  /**
   * Gets the parameter values.
   *
   * @return the parameter values
   */
  public Map<Parameter, String> getParameterValues() {
    return this.parameterValueMap;
  }

  /**
   * Adds the independent parameter value.
   *
   * @param parameter
   *          the parameter
   * @param value
   *          the value
   */
  public void addParameterValue(final Parameter parameter, final String value) {
    this.parameterValueMap.put(parameter, value);
  }

  /**
   * Update with.
   *
   * @param graph
   *          the graph
   */
  public void updateWith(final PiGraph graph) {
    getParameterValues().clear();
    if (graph != null) {
      for (final Parameter p : graph.getAllParameters()) {
        if (!p.isConfigurationInterface()) {
          addParameterValue(p, p.getExpression().getExpressionAsString());
        }
      }
    }
  }
}
