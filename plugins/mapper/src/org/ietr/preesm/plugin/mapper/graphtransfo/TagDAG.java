/**
 * 
 */
package org.ietr.preesm.plugin.mapper.graphtransfo;

import java.util.Iterator;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.ietr.preesm.core.codegen.sdfProperties.BufferProperties;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.SendVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertexAdder;
import org.sdf4j.model.PropertyBean;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Tags an SDF with the implementation information necessary for code generation
 * 
 * @author pmenuet
 */
public class TagDAG {

	/**
	 * Main for test
	 */
	public static void main(String[] args) {

	}

	/**
	 * Constructor
	 */
	public TagDAG() {
		super();
	}

	/**
	 * tag adds the send and receive operations necessary to the code generation.
	 * It also adds the necessary properies.
	 */
	public void tag(MapperDAG dag, IArchitecture architecture) {


		addTransfers(dag,architecture);
		addProperties(dag);
		addAllAggregates(dag);
	}

	public void addTransfers(MapperDAG dag, IArchitecture architecture) {
		
		// Temporary
		// TODO: add a scheduling order for Send/Receive.
		SchedulingOrderManager orderMgr = new SchedulingOrderManager();
		orderMgr.reconstructTotalOrderFromDAG(dag, dag);
		TransferVertexAdder tvAdder = new TransferVertexAdder(new CommunicationRouter(architecture),orderMgr, true);
		tvAdder.addTransferVertices(dag, new TransactionManager());
		orderMgr.tagDAG(dag);
	}

	public void addProperties(MapperDAG dag) {

		MapperDAGVertex currentVertex;
		
		Iterator<DAGVertex> iter = dag.vertexSet().iterator();
		
		// Tagging the vertices with informations for code generation
		while (iter.hasNext()) {
			currentVertex = (MapperDAGVertex)iter.next();
			PropertyBean bean = currentVertex.getPropertyBean();
			
			if(currentVertex instanceof SendVertex){

				bean.setValue(VertexType.propertyBeanName, VertexType.send);
				bean.setValue(Operator.propertyBeanName,
						((SendVertex) currentVertex).getRouteStep().getSender());
				bean.setValue(Medium.propertyBeanName, ((SendVertex) currentVertex).getRouteStep().getMedium());
			}
			else if(currentVertex instanceof ReceiveVertex){

				bean.setValue(VertexType.propertyBeanName, VertexType.receive);
				bean.setValue(Operator.propertyBeanName,
						((ReceiveVertex) currentVertex).getRouteStep().getReceiver());
				bean.setValue(Medium.propertyBeanName, ((ReceiveVertex) currentVertex).getRouteStep().getMedium());
			}
			else{
	
				bean.setValue(Operator.propertyBeanName,
						currentVertex.getImplementationVertexProperty().getEffectiveOperator());
				bean.setValue(VertexType.propertyBeanName, VertexType.task);
			}
			
			bean.setValue("schedulingOrder", currentVertex.getImplementationVertexProperty().getSchedulingTotalOrder());
		}
	}

	public void addAllAggregates(MapperDAG dag) {

		MapperDAGEdge edge;
		
		Iterator<DAGEdge> iter = dag.edgeSet().iterator();
		
		// Tagging the vertices with informations for code generation
		while (iter.hasNext()) {
			edge = (MapperDAGEdge)iter.next();
			addAggregate(edge, "char", 10);
		}
	}

	/**
	 * Adding a properties to an edge from a type and size
	 */
	public void addAggregate(MapperDAGEdge edge, String type, int size) {

		edge.getPropertyBean().setValue("dataType", type);

		// Example buffer aggregate with one single buffer
		BufferAggregate agg = new BufferAggregate();
		agg.add(new BufferProperties(type, "out", "in", size));

		edge.getPropertyBean().setValue(BufferAggregate.propertyBeanName, agg);
	}

}
