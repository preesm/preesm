package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.scheduler.AbstractScheduler;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.demo.SDFtoDAGDemo;
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
 *         The AlgorithmTransformer converts different algorithms among SDF, DAG
 *         and AlgorithmDescriptor
 */
public class AlgorithmTransformer {

	public AlgorithmTransformer() {
	}

	/**
	 * Converts a DAG-like SDF to an AlgorithmDescriptor
	 */
	public AlgorithmDescriptor sdf2Algorithm(SDFGraph sdf) {

		// Construct AlgorithmDescriptor
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
					sdfedge.getSource().getName() + ":"
							+ sdfedge.getSourceInterface().getName() + "->"
							+ sdfedge.getTarget().getName() + ":"
							+ sdfedge.getTargetInterface().getName(),
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

	/**
	 * Converts an AlgorithmDescriptor to a MapperDAG
	 */
	public MapperDAG algorithm2DAG(AlgorithmDescriptor algorithm) {
		MapperDAG dag = new MapperDAG(null, null);
		return dag;
	}

	/**
	 * Converts an AlgorithmDescriptor to a MapperDAG
	 */
	public MapperDAG algorithm2DAG(AbstractScheduler scheduler) {
		MapperDAG dag = new MapperDAG(null, null);
		return dag;
	}

	/**
	 * Converts MapperDAG to an AlgorithmDescriptor
	 */
	public AlgorithmDescriptor dag2Algorithm(MapperDAG dag) {
		AlgorithmDescriptor algorithm = new AlgorithmDescriptor(null);
		return algorithm;
	}

	/**
	 * Generate DAG-like random SDF
	 */
	public SDFGraph randomSDF(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int dataSize) {
		return randomSDF(nbVertex, minInDegree, maxInDegree, minOutDegree,
				maxOutDegree, dataSize, dataSize, maxOutDegree);
	}

	/**
	 * Generate DAG-like random SDF
	 */
	public SDFGraph randomSDF(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int minDataSize, int maxDataSize) {
		return randomSDF(nbVertex, minInDegree, maxInDegree, minOutDegree,
				maxOutDegree, minDataSize, maxDataSize, maxOutDegree);
	}

	/**
	 * Generate DAG-like random SDF
	 */
	public SDFGraph randomSDF(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int minDataSize,
			int maxDataSize, int maxSensor) {

		SDFtoDAGDemo applet = new SDFtoDAGDemo();
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
		applet.init(demoGraph);

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

	public HashMap<String, Integer> generateRandomNodeWeight(SDFGraph sdf,
			double minWeight, double maxWeight) {
		HashMap<String, Integer> computationWeights = new HashMap<String, Integer>();

		for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
			Double taskSize = Math.random() * (maxWeight - minWeight)
					+ minWeight;
			computationWeights.put(indexVertex.getName(), taskSize.intValue());
			// System.out.println("name: " + indexVertex.getName() + "; weight:"
			// + taskSize.intValue());
		}
		return computationWeights;
	}

	public HashMap<String, Integer> generateNodeWeight(SDFGraph sdf, int weight) {
		HashMap<String, Integer> computationWeights = new HashMap<String, Integer>();

		for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
			if (indexVertex.getName().equalsIgnoreCase("copy")) {
				computationWeights.put(indexVertex.getName(), 10 * weight);
			} else {
				computationWeights.put(indexVertex.getName(), weight);
			}
		}
		return computationWeights;
	}
}
