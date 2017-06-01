/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.preesm.algorithm.exportPromela;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.utils.paths.PathTools;

// TODO: Auto-generated Javadoc
/**
 * Workflow task {@link PromelaExporter} for multi-SDF workflows.
 *
 * @author kdesnos
 *
 */
public class MultiPromelaExporter extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    // Retrieve the inputs
    @SuppressWarnings("unchecked")
    final Set<SDFGraph> sdfs = (Set<SDFGraph>) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPHS_SET);
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final String paramFifo = parameters.get(PromelaExporter.PARAM_FIFO_POLICY);
    final boolean fifoShared = paramFifo.equalsIgnoreCase(PromelaExporter.VALUE_FIFO_SHARED);

    final String paramActor = parameters.get(PromelaExporter.PARAM_ACTOR_POLICY);
    final boolean synchronousActor = Boolean.parseBoolean(paramActor);

    // Locate the output file
    final String sPath = PathTools.getAbsolutePath(parameters.get("path"), workflow.getProjectName());
    final IPath path = new Path(sPath);

    for (final SDFGraph sdf : sdfs) {
      final PromelaExporterEngine engine = new PromelaExporterEngine();
      engine.printSDFGraphToPromelaFile(sdf, scenario, path, fifoShared, synchronousActor);
    }

    return new HashMap<>();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put(PromelaExporter.PARAM_PATH, PromelaExporter.VALUE_PATH_DEFAULT);
    parameters.put(PromelaExporter.PARAM_FIFO_POLICY, PromelaExporter.VALUE_FIFO_DEFAULT);
    parameters.put(PromelaExporter.PARAM_ACTOR_POLICY, PromelaExporter.VALUE_ACTOR_DEFAULT);
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Exporting Promela code";
  }
}
