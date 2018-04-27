/**
 *
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.StaticPiMM2SrDAGLauncher.StaticPiMM2SrDAGException;

/**
 * @author farresti
 *
 */
public class StaticPiMM2SrDAGTask extends AbstractTaskImplementation {

  /** The Constant TOPOLOGY_METHOD. */
  public static final String TOPOLOGY_METHOD = "Topology";

  /** The Constant LCM_METHOD. */
  public static final String LCM_METHOD = "LCM";

  /** The Constant CONSISTENCY_METHOD. */
  public static final String CONSISTENCY_METHOD = "Consistency_Method";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    final StaticPiMM2SrDAGLauncher launcher = new StaticPiMM2SrDAGLauncher(scenario, graph);

    MapperDAG result = new MapperDAG(new MapperEdgeFactory(), new SDFGraph());
    final Logger logger = WorkflowLogger.getLogger();
    VisitorOutput.setLogger(logger);
    try {
      logger.setLevel(Level.FINEST);
      logger.log(Level.INFO, "Computing Repetition Vector for graph [" + graph.getName() + "]");
      // Check the consistency of the PiGraph and compute the associated Basic Repetition Vector
      // We use Topology-Matrix based method by default
      final String consistencyMethod = parameters.get(StaticPiMM2SrDAGTask.CONSISTENCY_METHOD);
      int method = 0;
      if (consistencyMethod.equals(StaticPiMM2SrDAGTask.LCM_METHOD)) {
        method = 1;
      } else if (!consistencyMethod.equals(StaticPiMM2SrDAGTask.TOPOLOGY_METHOD)) {
        throw new WorkflowException("Unsupported method for checking consistency [" + consistencyMethod + "]");
      }
      // Convert the PiGraph to the Single-Rate Directed Acyclic Graph
      result = launcher.launch(method);
    } catch (final StaticPiMM2SrDAGException e) {
      throw new WorkflowException(e.getMessage());
    }

    SdfToDagConverter.addInitialProperties(result, architecture, scenario);

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, result);
    return output;// new LinkedHashMap<>();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return Collections.emptyMap();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return "Transforming PiGraph to Single-Rate Directed Acyclic Graph.";
  }

}
