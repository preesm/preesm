package org.ietr.workflow.hypervisor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This workflow task generate an XML file containing topological network information for parameterized input (number of
 * nodes and network identifier). This XML file is exploited by SimGrid in the framework of SimSDP.
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "SimSDPnetworkTask.identifier", name = "SimSDP network",

    parameters = {

        @Parameter(name = "config ID", description = "ID =[0;4]",
            values = { @Value(name = "integer", effect = "...") }),

        @Parameter(name = "number of nodes", description = "number of nodes",
            values = { @Value(name = "integer", effect = "...") }),

        @Parameter(name = "number of cores", description = "number of cores per node",
            values = { @Value(name = "integer", effect = "...") }),

        @Parameter(name = "core frequency", description = "core frequency",
            values = { @Value(name = "integer", effect = "...") })

    })
public class SimSDPnetworkTask extends AbstractTaskImplementation {
  public static final String CONFIG_ID_DEFAULT = "1";
  public static final String CONFIG_ID_PARAM   = "config ID";
  public static final String NODE_NB_DEFAULT   = "2";
  public static final String NODE_NB_PARAM     = "number of nodes";
  public static final String CORE_NB_DEFAULT   = "2";
  public static final String CORE_NB_PARAM     = "number of cores";
  public static final String CORE_FC_DEFAULT   = "2";
  public static final String CORE_FC_PARAM     = "core frequency";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    final String project = "/" + workflow.getProjectName();
    final int config = Integer.parseInt(parameters.get(CONFIG_ID_PARAM));
    final int node = Integer.parseInt(parameters.get(NODE_NB_PARAM));
    final int core = Integer.parseInt(parameters.get(CORE_NB_PARAM));
    final int cFreq = Integer.parseInt(parameters.get(CORE_FC_PARAM));
    final SimSDPnetwork simSDPnetwork = new SimSDPnetwork(config, core, cFreq, node, project);
    simSDPnetwork.execute();
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(CONFIG_ID_PARAM, CONFIG_ID_DEFAULT);
    parameters.put(NODE_NB_PARAM, NODE_NB_DEFAULT);
    parameters.put(CORE_NB_PARAM, CORE_NB_DEFAULT);
    parameters.put(CORE_FC_PARAM, CORE_FC_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Pipeline/cycle identifier Task";

  }

}
