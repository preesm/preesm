/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2016)
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
package org.preesm.algorithm.pisdf.pimm2sdf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiGraphExecution;
import org.preesm.model.scenario.Scenario;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMM2SDFLauncher {

  /** The scenario. */
  private final Scenario scenario;

  /** The graph. */
  private final PiGraph graph;

  /**
   * Instantiates a new static pi MM 2 SDF launcher.
   *
   * @param scenario
   *          the scenario
   * @param graph
   *          the graph
   */
  public StaticPiMM2SDFLauncher(final Scenario scenario, final PiGraph graph) {
    this.scenario = scenario;
    this.graph = graph;
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   */
  public SDFGraph launch() {
    SDFGraph result;

    // Get all the available values for all the parameters
    final Map<Parameter, Long> parametersValues = getParametersValues();

    // Visitor creating the SDFGraph
    final PiGraphExecution execution = new PiGraphExecution(parametersValues);
    final StaticPiMM2SDFVisitor visitor = new StaticPiMM2SDFVisitor(execution);
    visitor.doSwitch(this.graph);

    result = visitor.getResult();
    return result;
  }

  /**
   * Gets the parameters values.
   *
   * @return the parameters values
   */
  private Map<Parameter, Long> getParametersValues() {
    final Map<Parameter, Long> result = new LinkedHashMap<>();

    final Set<Entry<Parameter, String>> parameterValues = this.scenario.getParameterValues().entrySet();
    for (final Entry<Parameter, String> paramValue : parameterValues) {
      if (!paramValue.getKey().isLocallyStatic()) {

        throw new PreesmRuntimeException("Parameter " + paramValue.getKey().getName()
            + " is depends on a configuration actor. It is thus impossible to use the"
            + " Static PiMM 2 SDF transformation. Try instead the Dynamic PiMM 2 SDF"
            + " transformation (id: org.ietr.preesm.experiment.pimm2sdf.PiMM2SDFTask)");
      } else {
        try {
          final long value = Long.parseLong(paramValue.getValue());
          result.put(paramValue.getKey(), value);
        } catch (final NumberFormatException e) {
          // The expression associated to the parameter is an
          // expression (and not an constant long value).
          // Leave it as it is, it will be solved later.
        }
      }
    }

    return result;
  }

}
