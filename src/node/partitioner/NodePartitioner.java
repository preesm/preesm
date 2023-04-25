package node.partitioner;

import java.util.Map;
import java.util.List;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class NodePartitioner {
	/**
	   * Input graph.
	   */
	  private PiGraph  graph;
	  /**
	   * Workflow scenario.
	   */
	  private Scenario scenario;
	  /**
	   * Architecture design.
	   */
	  private Design   archi;
	private int node;
	private int offset;
	public NodePartitioner(PiGraph graph, Scenario scenario, Design archi,int node,int offset) {
		this.graph = graph;
		this.scenario = scenario;
		this.archi = archi;
		this.node = node;
		this.offset = offset;
	}
	public PiGraph execute() {
		Long sum = computebrvSum();
		List <AbstractActor> topoOrder = computeTopoOrder();
		return null;
		
	}
	private List<AbstractActor> computeTopoOrder() {
		// TODO Auto-generated method stub
		return null;
	}
	private Long computebrvSum() {
		Long sum = 0L;
		Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
		for(AbstractActor a: graph.getAllActors())
			sum = sum+brv.get(a);
		return sum;
	}

}
