/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.preesm.algorithm.randomsdf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.core.scenario.ConstraintGroupManager;
import org.preesm.algorithm.core.scenario.PreesmScenario;
import org.preesm.algorithm.core.scenario.Timing;
import org.preesm.algorithm.generator.SDFRandomGraph;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.visitors.SDF4JException;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.WorkflowException;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This Workflow element is used to generate random SDF graphs The user can define the following options: - Number of
 * vertices - Minimum/maximum number of input/output per actor - Minimum/maximum input/output rates - Minimum/maximum
 * execution time
 *
 * <p>
 * The Workflow element must have a scenario, a sdf and an architecture as inputs and outputs a SDF and a Scenario
 * </p>
 *
 * <p>
 * This Workflow element is experimental and probably have many flaws. It should be used with great caution.
 * </p>
 *
 * @author kdesnos
 *
 */
public class RandomSDF extends AbstractTaskImplementation {

  /** The nb vertex. */
  private int nbVertex;

  /** The min in degree. */
  private int minInDegree;

  /** The max in degree. */
  private int maxInDegree;

  /** The min out degree. */
  private int minOutDegree;

  /** The max out degree. */
  private int maxOutDegree;

  /** The min rate. */
  private int minRate;

  /** The max rate. */
  private int maxRate;

  /** The min time. */
  private int minTime;

  /** The max time. */
  private int maxTime;
  // All prod./Cons. rate will be multiplied by the value.
  // This does not change the consistency, only make
  /** The rate multiplier. */
  // the production/consumption rates to be more significant
  private int rateMultiplier;

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    retrieveParameters(parameters);

    // Retrieve inputs
    final Map<String, Object> outputs = new LinkedHashMap<>();
    SDFGraph sdf = (SDFGraph) inputs.get("SDF");

    final Design architecture = (Design) inputs.get("architecture");
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

    // Creates a random SDF graph
    final SDFRandomGraph graphGenerator = new SDFRandomGraph();
    SDFGraph generatedGraph = null;
    try {
      generatedGraph = graphGenerator.createRandomGraph(this.nbVertex, this.minInDegree, this.maxInDegree,
          this.minOutDegree, this.maxOutDegree, this.minRate, this.maxRate, this.rateMultiplier);
    } catch (final SDF4JException ex) {
      throw new WorkflowException(ex.getMessage(), ex);
    }

    if (generatedGraph != null) {
      final Random generator = new Random();

      sdf = generatedGraph;

      // If the generated graph is not null, update
      // the scenario so that all task can be scheduled
      // on all operators, and all have the same runtime.
      final Map<String, Integer> verticesNames = new LinkedHashMap<>();
      for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
        final int time = generator.nextInt((this.maxTime - this.minTime) + 1) + this.minTime;
        verticesNames.put(vertex.getName(), time);
        vertex.setInfo(vertex.getName());
        vertex.setId(vertex.getName());
      }

      final ConstraintGroupManager constraint = scenario.getConstraintGroupManager();
      for (final ComponentInstance component : architecture.getComponentInstances()) {
        constraint.addConstraints(component.getInstanceName(), verticesNames.keySet());
        for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
          final Timing t = scenario.getTimingManager().addTiming(vertex.getName(),
              component.getComponent().getVlnv().getName());
          t.setTime(verticesNames.get(vertex.getName()));
        }
      }

    }

    outputs.put("scenario", scenario);
    outputs.put("SDF", sdf);
    return outputs;
  }

  /**
   * This method retrieve the parameters set by the user.
   *
   * @param parameters
   *          the list of parameters and their values
   */
  private void retrieveParameters(final Map<String, String> parameters) {
    String param = parameters.get("nbVertex");
    this.nbVertex = (param != null) ? Integer.decode(param) : 10;

    param = parameters.get("minInDegree");
    this.minInDegree = (param != null) ? Integer.decode(param) : 1;

    param = parameters.get("maxInDegree");
    this.maxInDegree = (param != null) ? Integer.decode(param) : 5;

    param = parameters.get("minOutDegree");
    this.minOutDegree = (param != null) ? Integer.decode(param) : 1;

    param = parameters.get("maxOutDegree");
    this.maxOutDegree = (param != null) ? Integer.decode(param) : 5;

    param = parameters.get("minRate");
    this.minRate = (param != null) ? Integer.decode(param) : 1;

    param = parameters.get("maxRate");
    this.maxRate = (param != null) ? Integer.decode(param) : 4;

    param = parameters.get("minTime");
    this.minTime = (param != null) ? Integer.decode(param) : 100;

    param = parameters.get("maxTime");
    this.maxTime = (param != null) ? Integer.decode(param) : 1000;

    param = parameters.get("rateMultiplier");
    this.rateMultiplier = (param != null) ? Integer.decode(param) : 1000;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put("nbVertex", "10");
    parameters.put("minInDegree", "1");
    parameters.put("maxInDegree", "5");
    parameters.put("minOutDegree", "1");
    parameters.put("maxOutDegree", "5");
    parameters.put("minRate", "1");
    parameters.put("maxRate", "4");
    parameters.put("minTime", "100");
    parameters.put("maxTime", "1000");
    parameters.put("rateMultiplier", "1000");

    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generating random SDF.";
  }

}
