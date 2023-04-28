package org.preesm.clustering;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class partition graph into subgraph assigned to a node
 * @author orenaud
 *
 */

@PreesmTask(id = "node.partitioner.task.identifier", name = "Node Partitioner",
inputs= { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),@Port(name = "architecture", type = Design.class)},
//outputs= {},
parameters = {@Parameter(name = "Node number", description = "number of target nodes",
values = { @Value(name = "Fixed:=n",
effect = "the number of nodes of the target allows to quantify the number of possible subgraphs") }),
		@Parameter(name = "% offset", description = "offset of the split",
        values = { @Value(name = "Fixed:=n",
            effect = "cluster more or less actor than the previous iteration at a certain %") }),

})
public class NodePartitionerTask extends AbstractTaskImplementation {
public static final String NODE_NUMBER_DEFAULT = "1";
public static final String NODE_NUMBER_PARAM = "Node number";
public static final String OFFSET_DEFAULT = "0";
public static final String OFFSET_PARAM = "% offset";

protected int node;
protected int offset;
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) {
		String node = parameters.get(NodePartitionerTask.NODE_NUMBER_PARAM);
		this.node = Integer.decode(node);
		String offset = parameters.get(NodePartitionerTask.OFFSET_PARAM);
		this.offset = Integer.decode(offset);
		PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
	    Scenario scenario = (Scenario) inputs.get("scenario");
	    Design archi = (Design) inputs.get("architecture");
PiGraph graph = new NodePartitioner(inputGraph,scenario,archi,this.node,this.offset).execute();
		return null;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		final Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put(NodePartitionerTask.NODE_NUMBER_PARAM, NodePartitionerTask.NODE_NUMBER_DEFAULT);
		parameters.put(NodePartitionerTask.OFFSET_PARAM, NodePartitionerTask.OFFSET_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		// TODO Auto-generated method stub
		return "Starting Execution of node partitioner Task";
	}

}

