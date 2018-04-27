/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.pimm.algorithm.pimm2sdf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.ParameterValueManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor.StaticPiMM2SDFVisitor;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMM2SDFLauncher {

  /** The scenario. */
  private final PreesmScenario scenario;

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
  public StaticPiMM2SDFLauncher(final PreesmScenario scenario, final PiGraph graph) {
    this.scenario = scenario;
    this.graph = graph;
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   * @throws StaticPiMM2SDFException
   *           the static pi MM 2 SDF exception
   */
  public SDFGraph launch() throws StaticPiMM2SDFException {
    SDFGraph result;

    // Get all the available values for all the parameters
    final Map<Parameter, Integer> parametersValues = getParametersValues();

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
   * @throws StaticPiMM2SDFException
   *           the static pi MM 2 SDF exception
   */
  private Map<Parameter, Integer> getParametersValues() throws StaticPiMM2SDFException {
    final Map<Parameter, Integer> result = new LinkedHashMap<>();

    final ParameterValueManager parameterValueManager = this.scenario.getParameterValueManager();
    final Set<ParameterValue> parameterValues = parameterValueManager.getParameterValues();
    for (final ParameterValue paramValue : parameterValues) {
      switch (paramValue.getType()) {
        case ACTOR_DEPENDENT:
          throw new StaticPiMM2SDFException("Parameter " + paramValue.getName() + " is depends on a configuration actor. It is thus impossible to use the"
              + " Static PiMM 2 SDF transformation. Try instead the Dynamic PiMM 2 SDF"
              + " transformation (id: org.ietr.preesm.experiment.pimm2sdf.PiMM2SDFTask)");
        case INDEPENDENT:
          try {
            final int value = Integer.parseInt(paramValue.getValue());
            result.put(paramValue.getParameter(), value);
            break;
          } catch (final NumberFormatException e) {
            // The expression associated to the parameter is an
            // expression (and not an constant int value).
            // Leave it as it is, it will be solved later.
            break;
          }
        default:
          break;
      }
    }

    return result;
  }

  /**
   * The Class StaticPiMM2SDFException.
   */
  public class StaticPiMM2SDFException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8272147472427685537L;

    /**
     * Instantiates a new static pi MM 2 SDF exception.
     *
     * @param message
     *          the message
     */
    public StaticPiMM2SDFException(final String message) {
      super(message);
    }
  }

}
