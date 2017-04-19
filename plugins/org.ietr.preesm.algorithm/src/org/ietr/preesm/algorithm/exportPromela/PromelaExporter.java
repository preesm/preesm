/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.algorithm.exportPromela;

import java.util.HashMap;
import java.util.Map;
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
 * Workflow task used to generate a Promela program as specified in : <br>
 * <br>
 * <code>Marc Geilen, Twan Basten, and Sander Stuijk. 2005. Minimising buffer
 * requirements of synchronous dataflow graphs with model checking. In
 * Proceedings of the 42nd annual Design Automation Conference (DAC '05). ACM,
 * New York, NY, USA, 819-824. DOI=10.1145/1065579.1065796
 * http://doi.acm.org/10.1145/1065579.1065796 </code>
 *
 * @author kdesnos
 *
 */
public class PromelaExporter extends AbstractTaskImplementation {

  /** The Constant PARAM_PATH. */
  public static final String PARAM_PATH = "path";

  /** The Constant VALUE_PATH_DEFAULT. */
  public static final String VALUE_PATH_DEFAULT = "./Code/Promela/graph.pml";

  /** The Constant PARAM_FIFO_POLICY. */
  public static final String PARAM_FIFO_POLICY = "FIFO allocation policy";

  /** The Constant VALUE_FIFO_DEFAULT. */
  public static final String VALUE_FIFO_DEFAULT = "Separated or Shared";

  /** The Constant VALUE_FIFO_SEPARATED. */
  public static final String VALUE_FIFO_SEPARATED = "Separated";

  /** The Constant VALUE_FIFO_SHARED. */
  public static final String VALUE_FIFO_SHARED = "Shared";

  /** The Constant PARAM_ACTOR_POLICY. */
  public static final String PARAM_ACTOR_POLICY = "Synchronous production/consumption";

  /** The Constant VALUE_TRUE. */
  public static final String VALUE_TRUE = "True";

  /** The Constant VALUE_FALSE. */
  public static final String VALUE_FALSE = "False";

  /** The Constant VALUE_ACTOR_DEFAULT. */
  public static final String VALUE_ACTOR_DEFAULT = PromelaExporter.VALUE_TRUE;

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map,
   * java.util.Map, org.eclipse.core.runtime.IProgressMonitor, java.lang.String,
   * org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs,
      final Map<String, String> parameters, final IProgressMonitor monitor, final String nodeName,
      final Workflow workflow) throws WorkflowException {
    // Retrieve the inputs
    final SDFGraph sdf = (SDFGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPH);
    final PreesmScenario scenario = (PreesmScenario) inputs
        .get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final String paramFifo = parameters.get(PromelaExporter.PARAM_FIFO_POLICY);
    final boolean fifoShared = paramFifo.equalsIgnoreCase(PromelaExporter.VALUE_FIFO_SHARED);

    final String paramActor = parameters.get(PromelaExporter.PARAM_ACTOR_POLICY);
    final boolean synchronousActor = Boolean.parseBoolean(paramActor);

    // Locate the output file
    final String sPath = PathTools.getAbsolutePath(parameters.get("path"),
        workflow.getProjectName());
    final IPath path = new Path(sPath);

    final PromelaExporterEngine engine = new PromelaExporterEngine();
    engine.printSDFGraphToPromelaFile(sdf, scenario, path, fifoShared, synchronousActor);

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
    return "Export Promela code";
  }

}
