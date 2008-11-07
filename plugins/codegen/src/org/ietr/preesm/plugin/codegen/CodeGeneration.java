/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

/**
 * 
 */
package org.ietr.preesm.plugin.codegen;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.ietr.preesm.core.codegen.sdfProperties.BufferProperties;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.codegen.print.PrinterChooser;
import org.sdf4j.model.PropertyBean;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFDefaultEdgePropertyType;

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
	public DAGEdge addExampleEdge(DirectedAcyclicGraph graph, String source,
			String dest, String type, int prodCons) {

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
	 * Adding send and receive between v1 and v2. It removes the original vertex
	 * and copies its buffer aggregate
	 */
	public DAGVertex addComVertices(DAGVertex v1, DAGVertex v2, Medium medium,
			Operator sendOp, Operator receiveOp, int schedulingOrder) {

		DirectedAcyclicGraph graph = v1.getBase();

		DAGEdge originalEdge = graph.getEdge(v1, v2);
		Object aggregate = originalEdge.getPropertyBean().getValue(
				BufferAggregate.propertyBeanName);
		graph.removeEdge(originalEdge);

		// Tagging the communication vertices with their operator, type and
		// medium
		DAGVertex send = new DAGVertex();
		send.setId("snd" + v2.getName() + sendOp.getName());
		send.getPropertyBean().setValue("schedulingOrder", schedulingOrder);
		send.getPropertyBean().setValue(VertexType.propertyBeanName,
				VertexType.send);
		send.getPropertyBean().setValue(Medium.propertyBeanName, medium);
		send.getPropertyBean().setValue(Operator.propertyBeanName, sendOp);

		DAGVertex receive = new DAGVertex();
		receive.setId("rcv" + v1.getName() + receiveOp.getName());
		receive.getPropertyBean().setValue("schedulingOrder", schedulingOrder);
		receive.getPropertyBean().setValue(VertexType.propertyBeanName,
				VertexType.receive);
		receive.getPropertyBean().setValue(Medium.propertyBeanName, medium);
		receive.getPropertyBean()
				.setValue(Operator.propertyBeanName, receiveOp);

		graph.addVertex(send);
		graph.addVertex(receive);

		graph.addEdge(v1, send).getPropertyBean().setValue(
				BufferAggregate.propertyBeanName, aggregate);
		graph.addEdge(send, receive).getPropertyBean().setValue(
				BufferAggregate.propertyBeanName, aggregate);
		graph.addEdge(receive, v2).getPropertyBean().setValue(
				BufferAggregate.propertyBeanName, aggregate);

		return receive;
	}

	/**
	 * Kwok example 2 -> implanted DAG on one processor
	 */
	public DirectedAcyclicGraph implanteddagexample2_single(
			MultiCoreArchitecture architecture) {

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
	public DirectedAcyclicGraph implanteddagexample2_multi(
			MultiCoreArchitecture architecture) {

		DAGVertex vertex;

		Operator c64x_1 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_1");
		Operator c64x_2 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_2");
		Operator c64x_3 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_3");
		Operator c64x_4 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_4");

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

		// n1,n3,n2,n7 on C64x_1
		// n4 on C64x_2
		// n6 on C64x_3
		// n5,n8,n9 on C64x_4

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
		addComVertices(graph.getVertex("n1"), graph.getVertex("n4"), edma,
				c64x_1, c64x_2, 1);

		edge = addExampleEdge(graph, "n1", "n5", "char", 1);
		addComVertices(graph.getVertex("n1"), graph.getVertex("n5"), edma,
				c64x_1, c64x_4, 2);

		edge = addExampleEdge(graph, "n2", "n6", "char", 1);
		addComVertices(graph.getVertex("n2"), graph.getVertex("n6"), edma,
				c64x_1, c64x_3, 3);

		edge = addExampleEdge(graph, "n2", "n7", "char", 5);
		edge = addExampleEdge(graph, "n2", "n8", "char", 5);
		addComVertices(graph.getVertex("n2"), graph.getVertex("n8"), edma,
				c64x_1, c64x_4, 4);

		edge = addExampleEdge(graph, "n3", "n7", "char", 5);
		edge = addExampleEdge(graph, "n3", "n8", "char", 1);
		addComVertices(graph.getVertex("n3"), graph.getVertex("n8"), edma,
				c64x_1, c64x_4, 5);

		edge = addExampleEdge(graph, "n4", "n8", "char", 1);
		addComVertices(graph.getVertex("n4"), graph.getVertex("n8"), edma,
				c64x_2, c64x_4, 6);

		edge = addExampleEdge(graph, "n5", "n8", "char", 10);

		edge = addExampleEdge(graph, "n6", "n9", "char", 10);
		addComVertices(graph.getVertex("n6"), graph.getVertex("n9"), edma,
				c64x_3, c64x_4, 7);

		edge = addExampleEdge(graph, "n7", "n9", "char", 10);
		addComVertices(graph.getVertex("n7"), graph.getVertex("n9"), edma,
				c64x_1, c64x_4, 8);

		edge = addExampleEdge(graph, "n8", "n9", "char", 10);

		return graph;
	}

	/**
	 * Kwok example 2 -> implanted DAG on 4 processors like with list sched
	 */
	public DirectedAcyclicGraph implanteddagexample2_multi_with_routes(
			MultiCoreArchitecture architecture) {

		DAGVertex vertex;

		Operator c64x_1 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_1");
		Operator c64x_2 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_2");
		Operator c64x_3 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_3");
		Operator c64x_4 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_4");
		Operator c64x_5 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_5");
		Operator c64x_6 = (Operator) architecture.getComponent(
				ArchitectureComponentType.operator, "C64x_6");

		Medium edma1 = (Medium) architecture.getComponent(
				ArchitectureComponentType.medium, "edma_Faraday1");
		Medium edma2 = (Medium) architecture.getComponent(
				ArchitectureComponentType.medium, "edma_Faraday2");
		Medium rIO = (Medium) architecture.getComponent(
				ArchitectureComponentType.medium, "rapidIO");

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

		// n1,n3,n2,n7 on C64x_1
		// n4 on C64x_2
		// n6 on C64x_3
		// n5,n8,n9 on C64x_4

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
		receive = addComVertices(graph.getVertex("n1"), graph.getVertex("n4"),
				edma1, c64x_1, c64x_2, 1);

		edge = addExampleEdge(graph, "n1", "n5", "char", 1);
		receive = addComVertices(graph.getVertex("n1"), graph.getVertex("n5"),
				rIO, c64x_1, c64x_4, 2);

		edge = addExampleEdge(graph, "n2", "n6", "char", 1);
		receive = addComVertices(graph.getVertex("n2"), graph.getVertex("n6"),
				edma1, c64x_1, c64x_3, 3);

		edge = addExampleEdge(graph, "n2", "n7", "char", 5);
		edge = addExampleEdge(graph, "n2", "n8", "char", 5);
		receive = addComVertices(graph.getVertex("n2"), graph.getVertex("n8"),
				rIO, c64x_1, c64x_4, 4);

		edge = addExampleEdge(graph, "n3", "n7", "char", 5);
		edge = addExampleEdge(graph, "n3", "n8", "char", 1);
		receive = addComVertices(graph.getVertex("n3"), graph.getVertex("n8"),
				rIO, c64x_1, c64x_4, 5);

		edge = addExampleEdge(graph, "n4", "n8", "char", 1);
		receive = addComVertices(graph.getVertex("n4"), graph.getVertex("n8"),
				edma1, c64x_2, c64x_1, 6);
		addComVertices(receive, graph.getVertex("n8"), rIO, c64x_1, c64x_4, 7);

		edge = addExampleEdge(graph, "n5", "n8", "char", 10);

		edge = addExampleEdge(graph, "n6", "n9", "char", 10);
		receive = addComVertices(graph.getVertex("n6"), graph.getVertex("n9"),
				edma1, c64x_3, c64x_1, 8);
		addComVertices(receive, graph.getVertex("n9"), rIO, c64x_1, c64x_4, 9);

		edge = addExampleEdge(graph, "n7", "n9", "char", 10);
		receive = addComVertices(graph.getVertex("n7"), graph.getVertex("n9"),
				rIO, c64x_1, c64x_4, 10);

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

		// MultiCoreArchitecture architecture = Examples.get4C64Archi();
		// SDFGraph algorithm = gen.implanteddagexample2_multi(architecture);

		MultiCoreArchitecture architecture = Examples.get2C64Archi();
		DirectedAcyclicGraph algorithm = gen
				.implanteddagexample2_multi_with_routes(architecture);

		// Input file list

		Map<String, String> map = new HashMap<String, String>();
		map.put("sourcePath", "d:/Test");
		TextParameters params = new TextParameters(map);

		gen.transform(algorithm, architecture, params);

		logger.log(Level.FINER, "Code generated");

	}

	@Override
	public TaskResult transform(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture, TextParameters parameters) {
		String sourcePath = parameters.getVariable("sourcePath");
		TaskResult result = new TaskResult();
		SourceFileList list = new SourceFileList();

		// Generate source file class
		generateSourceFiles(algorithm, architecture, list);

		// Generates the code
		PrinterChooser printerChooser = new PrinterChooser(sourcePath);
		printerChooser.printList(list);

		result.setSourcefilelist(list);

		return result;
	}

	/**
	 * Generates the source files from an implementation and an architecture.
	 * The implementation is a tagged SDF graph.
	 */
	private void generateSourceFiles(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture, SourceFileList list) {
		CodeGenerator codegen = new CodeGenerator(list);
		codegen.generateSourceFiles(algorithm, architecture);
	}

}
