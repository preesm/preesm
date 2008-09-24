/**
 * 
 */
package org.ietr.preesm.plugin.codegen;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.ietr.preesm.core.codegen.sdfProperties.BufferProperties;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.codegen.print.C64Printer;
import org.sdf4j.model.PropertyBean;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFAbstractGraph;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFDefaultEdgePropertyType;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Code generation.
 * 
 * @author Matthieu Wipliez
 */
public class CodeGeneration implements ICodeGeneration {

	/**
	 * 
	 */
	public CodeGeneration() {
	}


	/**
	 * Adding a new edge to graph from a few properties
	 */
	public DAGEdge addExampleEdge(DirectedAcyclicGraph graph, String source, String dest,
			String type, int prodCons) {

		DAGEdge edge;

		edge = graph.addEdge(graph.getVertex(source), graph.getVertex(dest));
		edge.getPropertyBean().setValue("dataType", type);

		// DAG => prod = cons
		edge.setWeight(new SDFDefaultEdgePropertyType(prodCons));

		// DAG => no delay

		// Example buffer aggregate with one single buffer
		BufferAggregate agg = new BufferAggregate();
		agg.add(new BufferProperties(type, "out", "in", prodCons));

		edge.getPropertyBean().setValue(BufferAggregate.propertyBeanName, agg);

		return edge;
	}
	
	/**
	 * Adding send and receive between v1 and v2. It removes the original vertex and
	 * copies its buffer aggregate
	 */
	public DAGVertex addComVertices(DAGVertex v1, DAGVertex v2, Medium medium,Operator sendOp,Operator receiveOp, int schedulingOrder) {

		DirectedAcyclicGraph graph = v1.getBase();

		DAGEdge originalEdge = graph.getEdge(v1, v2);
		Object aggregate = originalEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
		graph.removeEdge(originalEdge);
		
		// Tagging the communication vertices with their operator, type and medium
		DAGVertex send = new DAGVertex();
		send.setId("snd" + v2.getName() + sendOp.getName());
		send.getPropertyBean().setValue("schedulingOrder", schedulingOrder);
		send.getPropertyBean().setValue(VertexType.propertyBeanName, VertexType.send);
		send.getPropertyBean().setValue(Medium.propertyBeanName, medium);
		send.getPropertyBean().setValue(Operator.propertyBeanName, sendOp);
		
		DAGVertex receive = new DAGVertex();
		receive.setId("rcv" + v1.getName() + receiveOp.getName());
		receive.getPropertyBean().setValue("schedulingOrder", schedulingOrder);
		receive.getPropertyBean().setValue(VertexType.propertyBeanName, VertexType.receive);
		receive.getPropertyBean().setValue(Medium.propertyBeanName, medium);
		receive.getPropertyBean().setValue(Operator.propertyBeanName, receiveOp);
		
		graph.addVertex(send);
		graph.addVertex(receive);
		
		graph.addEdge(v1, send).getPropertyBean().setValue(BufferAggregate.propertyBeanName, aggregate);
		graph.addEdge(send, receive).getPropertyBean().setValue(BufferAggregate.propertyBeanName, aggregate);
		graph.addEdge(receive, v2).getPropertyBean().setValue(BufferAggregate.propertyBeanName, aggregate);
		
		return receive;
	}

	/**
	 * Kwok example 2 -> implanted DAG on one processor
	 */
	public DirectedAcyclicGraph implanteddagexample2_single(IArchitecture architecture) {

		/* Construct DAG */
		DirectedAcyclicGraph graph = new DirectedAcyclicGraph();

		for (int index = 1; index < 10; index++) {
			DAGVertex vertex = new DAGVertex();
			vertex.setId(String.format("n%d", index));
			vertex.setName(String.format("n%d", index));
			graph.addVertex(vertex);

			PropertyBean bean = vertex.getPropertyBean();
			bean.setValue(Operator.propertyBeanName, architecture
					.getMainOperator());
			bean.setValue(VertexType.propertyBeanName, VertexType.task);
		}

		graph.getVertex("n1").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n3").getPropertyBean().setValue("schedulingOrder", 2);
		graph.getVertex("n2").getPropertyBean().setValue("schedulingOrder", 3);
		graph.getVertex("n7").getPropertyBean().setValue("schedulingOrder", 4);
		graph.getVertex("n6").getPropertyBean().setValue("schedulingOrder", 5);
		graph.getVertex("n5").getPropertyBean().setValue("schedulingOrder", 6);
		graph.getVertex("n4").getPropertyBean().setValue("schedulingOrder", 7);
		graph.getVertex("n8").getPropertyBean().setValue("schedulingOrder", 8);
		graph.getVertex("n9").getPropertyBean().setValue("schedulingOrder", 9);

		// Route route = new Route();
		// edge.getPropertyBean().setValue("route", route);

		addExampleEdge(graph, "n1", "n2", "char", 4);
		addExampleEdge(graph, "n1", "n3", "char", 1);
		addExampleEdge(graph, "n1", "n7", "char", 20);
		addExampleEdge(graph, "n1", "n4", "char", 1);
		addExampleEdge(graph, "n1", "n5", "char", 1);

		addExampleEdge(graph, "n2", "n6", "char", 1);
		addExampleEdge(graph, "n2", "n7", "char", 5);
		addExampleEdge(graph, "n2", "n8", "char", 5);

		addExampleEdge(graph, "n3", "n7", "char", 5);
		addExampleEdge(graph, "n3", "n8", "char", 1);

		addExampleEdge(graph, "n4", "n8", "char", 1);

		addExampleEdge(graph, "n5", "n8", "char", 10);

		addExampleEdge(graph, "n6", "n9", "char", 10);

		addExampleEdge(graph, "n7", "n9", "char", 10);

		addExampleEdge(graph, "n8", "n9", "char", 10);

		return graph;
	}

	/**
	 * Kwok example 2 -> implanted DAG on 4 processors like with list sched
	 */
	public DirectedAcyclicGraph implanteddagexample2_multi(IArchitecture architecture) {

		DAGVertex vertex;
		
		Operator c64x_1 = architecture.getOperator("C64x_1");
		Operator c64x_2 = architecture.getOperator("C64x_2");
		Operator c64x_3 = architecture.getOperator("C64x_3");
		Operator c64x_4 = architecture.getOperator("C64x_4");

		Medium edma = architecture.getMainMedium();
		
		/* Construct DAG */
		DirectedAcyclicGraph graph = new DirectedAcyclicGraph();

		for (int index = 1; index < 10; index++) {
			vertex = new DAGVertex();
			vertex.setId(String.format("n%d", index));
			vertex.setName(String.format("n%d", index));
			graph.addVertex(vertex);

			PropertyBean bean = vertex.getPropertyBean();
			bean.setValue(Operator.propertyBeanName, architecture
					.getMainOperator());
			bean.setValue(VertexType.propertyBeanName, VertexType.task);
		}

		//n1,n3,n2,n7 on C64x_1
		//n4 on C64x_2
		//n6 on C64x_3
		//n5,n8,n9 on C64x_4
		
		graph.getVertex("n1").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n1").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n3").getPropertyBean().setValue("schedulingOrder", 2);
		graph.getVertex("n3").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n2").getPropertyBean().setValue("schedulingOrder", 3);
		graph.getVertex("n2").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n7").getPropertyBean().setValue("schedulingOrder", 4);
		graph.getVertex("n7").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n4").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n4").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_2);
		graph.getVertex("n6").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n6").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_3);
		graph.getVertex("n5").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n5").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_4);
		graph.getVertex("n8").getPropertyBean().setValue("schedulingOrder", 2);
		graph.getVertex("n8").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_4);
		graph.getVertex("n9").getPropertyBean().setValue("schedulingOrder", 3);
		graph.getVertex("n9").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_4);

		// Adding vertices for transfers
		DAGEdge edge = null;

		edge = addExampleEdge(graph, "n1", "n2", "char", 4);
		edge = addExampleEdge(graph, "n1", "n3", "char", 1);
		edge = addExampleEdge(graph, "n1", "n7", "char", 20);
		edge = addExampleEdge(graph, "n1", "n4", "char", 1);
		addComVertices(graph.getVertex("n1"),graph.getVertex("n4"),edma,c64x_1,c64x_2,1);
		
		edge = addExampleEdge(graph, "n1", "n5", "char", 1);
		addComVertices(graph.getVertex("n1"),graph.getVertex("n5"),edma,c64x_1,c64x_4,2);

		edge = addExampleEdge(graph, "n2", "n6", "char", 1);
		addComVertices(graph.getVertex("n2"),graph.getVertex("n6"),edma,c64x_1,c64x_3,3);
		
		edge = addExampleEdge(graph, "n2", "n7", "char", 5);
		edge = addExampleEdge(graph, "n2", "n8", "char", 5);
		addComVertices(graph.getVertex("n2"),graph.getVertex("n8"),edma,c64x_1,c64x_4,4);
		
		edge = addExampleEdge(graph, "n3", "n7", "char", 5);
		edge = addExampleEdge(graph, "n3", "n8", "char", 1);
		addComVertices(graph.getVertex("n3"),graph.getVertex("n8"),edma,c64x_1,c64x_4,5);
		
		edge = addExampleEdge(graph, "n4", "n8", "char", 1);
		addComVertices(graph.getVertex("n4"),graph.getVertex("n8"),edma,c64x_2,c64x_4,6);
		
		edge = addExampleEdge(graph, "n5", "n8", "char", 10);

		edge = addExampleEdge(graph, "n6", "n9", "char", 10);
		addComVertices(graph.getVertex("n6"),graph.getVertex("n9"),edma,c64x_3,c64x_4,7);

		edge = addExampleEdge(graph, "n7", "n9", "char", 10);
		addComVertices(graph.getVertex("n7"),graph.getVertex("n9"),edma,c64x_1,c64x_4,8);

		edge = addExampleEdge(graph, "n8", "n9", "char", 10);
		
		return graph;
	}

	/**
	 * Kwok example 2 -> implanted DAG on 4 processors like with list sched
	 */
	public DirectedAcyclicGraph implanteddagexample2_multi_with_routes(IArchitecture architecture) {

		DAGVertex vertex;
		
		Operator c64x_1 = architecture.getOperator("C64x_1");
		Operator c64x_2 = architecture.getOperator("C64x_2");
		Operator c64x_3 = architecture.getOperator("C64x_3");
		Operator c64x_4 = architecture.getOperator("C64x_4");
		Operator c64x_5 = architecture.getOperator("C64x_5");
		Operator c64x_6 = architecture.getOperator("C64x_6");

		Medium edma1 = architecture.getMedium("edma_Faraday1");
		Medium edma2 = architecture.getMedium("edma_Faraday2");
		Medium rIO = architecture.getMedium("rapidIO");
		
		/* Construct DAG */
		DirectedAcyclicGraph graph = new DirectedAcyclicGraph();

		for (int index = 1; index < 10; index++) {
			vertex = new DAGVertex();
			vertex.setId(String.format("n%d", index));
			vertex.setName(String.format("n%d", index));
			graph.addVertex(vertex);

			PropertyBean bean = vertex.getPropertyBean();
			bean.setValue(Operator.propertyBeanName, architecture
					.getMainOperator());
			bean.setValue(VertexType.propertyBeanName, VertexType.task);
		}

		//n1,n3,n2,n7 on C64x_1
		//n4 on C64x_2
		//n6 on C64x_3
		//n5,n8,n9 on C64x_4
		
		graph.getVertex("n1").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n1").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n3").getPropertyBean().setValue("schedulingOrder", 2);
		graph.getVertex("n3").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n2").getPropertyBean().setValue("schedulingOrder", 3);
		graph.getVertex("n2").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n7").getPropertyBean().setValue("schedulingOrder", 4);
		graph.getVertex("n7").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_1);
		graph.getVertex("n4").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n4").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_2);
		graph.getVertex("n6").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n6").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_3);
		graph.getVertex("n5").getPropertyBean().setValue("schedulingOrder", 1);
		graph.getVertex("n5").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_4);
		graph.getVertex("n8").getPropertyBean().setValue("schedulingOrder", 2);
		graph.getVertex("n8").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_4);
		graph.getVertex("n9").getPropertyBean().setValue("schedulingOrder", 3);
		graph.getVertex("n9").getPropertyBean().setValue(
				Operator.propertyBeanName, c64x_4);

		// Adding vertices for transfers
		DAGEdge edge = null;
		DAGVertex receive;

		edge = addExampleEdge(graph, "n1", "n2", "char", 4);
		edge = addExampleEdge(graph, "n1", "n3", "char", 1);
		edge = addExampleEdge(graph, "n1", "n7", "char", 20);
		edge = addExampleEdge(graph, "n1", "n4", "char", 1);
		receive = addComVertices(graph.getVertex("n1"),graph.getVertex("n4"),edma1,c64x_1,c64x_2,1);
		
		edge = addExampleEdge(graph, "n1", "n5", "char", 1);
		receive = addComVertices(graph.getVertex("n1"),graph.getVertex("n5"),rIO,c64x_1,c64x_4,2);
		
		edge = addExampleEdge(graph, "n2", "n6", "char", 1);
		receive = addComVertices(graph.getVertex("n2"),graph.getVertex("n6"),edma1,c64x_1,c64x_3,3);
		
		edge = addExampleEdge(graph, "n2", "n7", "char", 5);
		edge = addExampleEdge(graph, "n2", "n8", "char", 5);
		receive = addComVertices(graph.getVertex("n2"),graph.getVertex("n8"),rIO,c64x_1,c64x_4,4);
		
		edge = addExampleEdge(graph, "n3", "n7", "char", 5);
		edge = addExampleEdge(graph, "n3", "n8", "char", 1);
		receive = addComVertices(graph.getVertex("n3"),graph.getVertex("n8"),rIO,c64x_1,c64x_4,5);
		
		edge = addExampleEdge(graph, "n4", "n8", "char", 1);
		receive = addComVertices(graph.getVertex("n4"),graph.getVertex("n8"),edma1,c64x_2,c64x_1,6);
		addComVertices(receive,graph.getVertex("n8"),rIO,c64x_1,c64x_4,7);
		
		edge = addExampleEdge(graph, "n5", "n8", "char", 10);

		edge = addExampleEdge(graph, "n6", "n9", "char", 10);
		receive = addComVertices(graph.getVertex("n6"),graph.getVertex("n9"),edma1,c64x_3,c64x_1,8);
		addComVertices(receive,graph.getVertex("n9"),rIO,c64x_1,c64x_4,9);

		edge = addExampleEdge(graph, "n7", "n9", "char", 10);
		receive = addComVertices(graph.getVertex("n7"),graph.getVertex("n9"),rIO,c64x_1,c64x_4,10);

		edge = addExampleEdge(graph, "n8", "n9", "char", 10);
		
		return graph;
	}

	/**
	 * Main for test
	 */
	public static void main(String[] args) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		logger.log(Level.FINER, "Testing code generation with an example");

		// Creating generator
		CodeGeneration gen = new CodeGeneration();
		// Input archi & algo
		
		//IArchitecture architecture = Examples.get4C64Archi();
		//SDFGraph algorithm = gen.implanteddagexample2_multi(architecture);
	
		IArchitecture architecture = Examples.get2FaradayArchi();
		DirectedAcyclicGraph algorithm = gen.implanteddagexample2_multi_with_routes(architecture);
		
		// Input file list
		SourceFileList list = new SourceFileList();

		gen.transform(algorithm, architecture,null);

		logger.log(Level.FINER, "Code generated");
		
	}

	@Override
	public TaskResult transform(DirectedAcyclicGraph algorithm, IArchitecture architecture, TextParameters parameters) {

		String sourcePath = parameters.getVariable("sourcePath");
		TaskResult result = new TaskResult();
		SourceFileList list = new SourceFileList();
		
		generateSourceFiles(algorithm, architecture, list);

		list.accept(new C64Printer(sourcePath));
		
		result.setSourcefilelist(list);
		return result;
	}

	/**
	 * Generates the source files from an implementation and an architecture.
	 * The implementation is a tagged SDF graph.
	 */
	private void generateSourceFiles(DirectedAcyclicGraph algorithm,
			IArchitecture architecture, SourceFileList list) {

		list.generateSourceFiles(algorithm, architecture);
		
	}

}
