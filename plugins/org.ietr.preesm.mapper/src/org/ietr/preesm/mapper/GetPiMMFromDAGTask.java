package org.ietr.preesm.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.mapper.model.MapperDAG;

/**
 */
public class GetPiMMFromDAGTask extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final Map<String, Object> outputs = new LinkedHashMap<>();
    final MapperDAG dag = (MapperDAG) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_DAG);
    final PiGraph referencePiMMGraph = dag.getReferencePiMMGraph();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, referencePiMMGraph);
    return outputs;
  }

  /**
   * Generic mapping message.
   *
   * @return the string
   */
  @Override
  public String monitorMessage() {
    return "Get reference PiMM from DAG";
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

}
