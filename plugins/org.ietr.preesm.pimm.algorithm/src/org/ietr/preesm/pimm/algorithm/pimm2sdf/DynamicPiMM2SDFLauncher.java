/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.pimm.algorithm.pimm2sdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor.DynamicPiMM2SDFVisitor;

// TODO: Auto-generated Javadoc
/**
 * The Class DynamicPiMM2SDFLauncher.
 */
public class DynamicPiMM2SDFLauncher {

  /** The scenario. */
  private final PreesmScenario scenario;

  /** The graph. */
  private final PiGraph graph;

  /**
   * Instantiates a new dynamic pi MM 2 SDF launcher.
   *
   * @param scenario
   *          the scenario
   * @param graph
   *          the graph
   */
  public DynamicPiMM2SDFLauncher(final PreesmScenario scenario, final PiGraph graph) {
    this.scenario = scenario;
    this.graph = graph;
  }

  /**
   * Launch.
   *
   * @return the sets the
   */
  public Set<SDFGraph> launch() {
    final Set<SDFGraph> result = new HashSet<>();

    // Get all the available values for all the parameters
    final Map<String, List<Integer>> parametersValues = getParametersValues();
    // Get the values for Parameters directly contained by graph (top-level
    // parameters), if any
    final Map<String, List<Integer>> outerParametersValues = new HashMap<>();
    // The number of time we need to execute, and thus visit graph
    final int nbExecutions = this.scenario.getSimulationManager().getNumberOfTopExecutions();

    for (final Parameter param : this.graph.getParameters()) {
      final List<Integer> pValues = parametersValues.get(param.getName());
      if (pValues != null) {
        outerParametersValues.put(param.getName(), pValues);
      }
    }

    // Visitor creating the SDFGraphs
    DynamicPiMM2SDFVisitor visitor;
    PiGraphExecution execution;
    // Values for the parameters for one execution
    Map<String, List<Integer>> currentValues;
    for (int i = 0; i < nbExecutions; i++) {
      // Values for one execution are parametersValues except for
      // top-level Parameters, for which we select only one value for a
      // given execution
      currentValues = parametersValues;
      for (final String s : outerParametersValues.keySet()) {
        // Value selection
        final List<Integer> availableValues = outerParametersValues.get(s);
        final int nbValues = availableValues.size();
        if (nbValues > 0) {
          final ArrayList<Integer> value = new ArrayList<>();
          value.add(availableValues.get(i % nbValues));
          currentValues.put(s, new ArrayList<>(value));
        }
      }

      execution = new PiGraphExecution(this.graph, currentValues, "_" + i, i);
      visitor = new DynamicPiMM2SDFVisitor(execution);
      this.graph.accept(visitor);

      final SDFGraph sdf = visitor.getResult();

      result.add(sdf);
    }

    return result;
  }

  /**
   * Gets the parameters values.
   *
   * @return the parameters values
   */
  private Map<String, List<Integer>> getParametersValues() {
    final Map<String, List<Integer>> result = new HashMap<>();

    for (final ParameterValue paramValue : this.scenario.getParameterValueManager().getParameterValues()) {
      switch (paramValue.getType()) {
        case ACTOR_DEPENDENT:
          result.put(paramValue.getName(), new ArrayList<>(paramValue.getValues()));
          break;
        case INDEPENDENT:
          final List<Integer> values = new ArrayList<>();
          final int value = Integer.parseInt(paramValue.getValue());
          values.add(value);
          result.put(paramValue.getName(), values);
          break;
        default:
          break;
      }
    }

    return result;
  }

}
