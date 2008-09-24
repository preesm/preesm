package org.ietr.preesm.plugin.fpga_scheduler.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor; //import org.sdf4j.demo.SDFGeneratorDemo;
import org.sdf4j.factories.DAGEdgeFactory;
import org.sdf4j.generator.DirectedAcyclicGraphGenerator;
import org.sdf4j.model.dag.DAGDefaultEdgePropertyType;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.visitors.ToHSDFVisitor;
import org.sdf4j.visitors.TopologyVisitor;

/**
 * @author pmu
 * 
 * The DAGCreator converts a SDF to a DAG
 */
public class DAGCreator {

	public DAGCreator() {
	}

	/**
	 * Scheduler DAG constructed from SDF DAG randomly generated
	 */
	public SDFGraph randomDAG(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int dataSize) {
		return randomDAG(nbVertex, minInDegree, maxInDegree, minOutDegree,
				maxOutDegree, dataSize, dataSize, maxOutDegree);
	}

	/**
	 * Scheduler DAG constructed from SDF DAG randomly generated
	 */
	public SDFGraph randomDAG(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int minDataSize, int maxDataSize) {
		return randomDAG(nbVertex, minInDegree, maxInDegree, minOutDegree,
				maxOutDegree, minDataSize, maxDataSize, maxOutDegree);
	}

	/**
	 * Scheduler DAG constructed from SDF DAG randomly generated
	 */
	public SDFGraph randomDAG(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int minDataSize,
			int maxDataSize, int maxSensor) {

		// SDFGeneratorDemo applet = new SDFGeneratorDemo();
		DirectedAcyclicGraphGenerator DAGG = new DirectedAcyclicGraphGenerator();
		TopologyVisitor topo = new TopologyVisitor();

		// Random dag
		Random rand = new Random(System.nanoTime());
		SDFGraph demoGraph = DAGG.createAcyclicRandomGraph(nbVertex,
				minInDegree, maxInDegree, minOutDegree, maxOutDegree, rand
						.nextInt(maxSensor));

		ToHSDFVisitor visitor2 = new ToHSDFVisitor();
		demoGraph.accept(visitor2);

		demoGraph.accept(topo);
		// applet.init(demoGraph);

		// Random edgeSizeRand = new Random();

		Set<SDFEdge> edgeSet = demoGraph.edgeSet();
		Iterator<SDFEdge> iterator = edgeSet.iterator();

		while (iterator.hasNext()) {
			SDFEdge edge = iterator.next();

			// Sets random data sizes between 0 and maxDataSize
			// Double datasize = (double) edgeSizeRand.nextInt(maxDataSize);
			Double datasize = Math.random() * (maxDataSize - minDataSize)
					+ minDataSize;
			DAGDefaultEdgePropertyType size = new DAGDefaultEdgePropertyType(
					datasize.intValue());

			edge.setProd(size);
			edge.setCons(size);
		}

		return demoGraph;
	}

	/**
	 * Converts a SDF DAG in a Scheduler DAG
	 */
	public AlgorithmDescriptor sdf2dag(SDFGraph sdf) {

		/* Construct DAG */
		AlgorithmDescriptor algorithm = new AlgorithmDescriptor(
				new DAGEdgeFactory());
		HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer = algorithm
				.getComputations();
		HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer = algorithm
				.getCommunications();
		// Adding Vertices
		Set<SDFAbstractVertex> vertexSet = sdf.vertexSet();
		Iterator<SDFAbstractVertex> vertexiterator = vertexSet.iterator();

		while (vertexiterator.hasNext()) {
			SDFAbstractVertex sdfvertex = vertexiterator.next();

			ComputationDescriptor dagvertex = new ComputationDescriptor(
					sdfvertex.getName(), ComputationDescriptorBuffer);
			dagvertex.setAlgorithm(algorithm);
			algorithm.addComputation(dagvertex);
		}

		// Adding Edges
		Set<SDFEdge> edgeSet = sdf.edgeSet();
		Iterator<SDFEdge> edgeiterator = edgeSet.iterator();

		while (edgeiterator.hasNext()) {
			SDFEdge sdfedge = edgeiterator.next();

			CommunicationDescriptor dagedge = new CommunicationDescriptor(
					sdfedge.getSource().getName().concat(
							"->" + sdfedge.getTarget().getName()),
					CommunicationDescriptorBuffer);
			dagedge.setSource(sdfedge.getSource().getName());
			ComputationDescriptorBuffer.get(sdfedge.getSource().getName())
					.addFollowingCommunication(
							CommunicationDescriptorBuffer
									.get(dagedge.getName()));
			dagedge.setDestination(sdfedge.getTarget().getName());
			ComputationDescriptorBuffer.get(sdfedge.getTarget().getName())
					.addPrecedingCommunication(
							CommunicationDescriptorBuffer
									.get(dagedge.getName()));
			dagedge.setAlgorithm(algorithm);
			algorithm.addCommunication(dagedge);
			dagedge.setWeight(sdfedge.getProd().intValue());
		}

		return algorithm;
	}
}
