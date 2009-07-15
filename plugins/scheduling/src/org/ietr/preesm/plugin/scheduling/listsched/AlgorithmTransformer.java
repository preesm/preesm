/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
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
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.plugin.scheduling.listsched;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.ComputationDescriptor;
import org.sdf4j.factories.DAGEdgeFactory;
import org.sdf4j.generator.DirectedAcyclicGraphGenerator;
import org.sdf4j.model.dag.types.DAGDefaultEdgePropertyType;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.visitors.ToHSDFVisitor;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * The AlgorithmTransformer converts different algorithms among SDF, DAG and
 * AlgorithmDescriptor
 * 
 * @author pmu
 */
public class AlgorithmTransformer {

	/**
	 * Construct a new AlgorithmTransformer
	 * 
	 */
	public AlgorithmTransformer() {
	}

	/**
	 * Generate random weights for vertices
	 * 
	 * @param sdf
	 *            A SDFGraph
	 * @param minWeight
	 *            Minimum weight of a vertex
	 * @param maxWeight
	 *            Maximum weight of a vertex
	 * @return A HashMap of vertices' name and their weights
	 */
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

	/**
	 * Generate DAG-like random SDF
	 * 
	 * @param nbVertex
	 *            Number of vertices
	 * @param minInDegree
	 *            Minimum in-degree of a vertex
	 * @param maxInDegree
	 *            Maximum in-degree of a vertex
	 * @param minOutDegree
	 *            Minimum out-degree of a vertex
	 * @param maxInDegree
	 *            Maximum out-degree of a vertex
	 * @param minDataSize
	 *            Minimum size of data for an edge
	 * @param maxDataSize
	 *            Maximum size of data for an edge
	 * @return A SDFGraph
	 */
	public SDFGraph randomSDF(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int minDataSize, int maxDataSize) {
		return randomSDF(nbVertex, minInDegree, maxInDegree, minOutDegree,
				maxOutDegree, minDataSize, maxDataSize, maxOutDegree);
	}

	/**
	 * Generate DAG-like random SDF
	 * 
	 * @param nbVertex
	 *            Number of vertices
	 * @param minInDegree
	 *            Minimum in-degree of a vertex
	 * @param maxInDegree
	 *            Maximum in-degree of a vertex
	 * @param minOutDegree
	 *            Minimum out-degree of a vertex
	 * @param maxInDegree
	 *            Maximum out-degree of a vertex
	 * @param minDataSize
	 *            Minimum size of data for an edge
	 * @param maxDataSize
	 *            Maximum size of data for an edge
	 * @param maxSensor
	 *            Maximum number of sensors
	 * @return A SDFGraph
	 */
	public SDFGraph randomSDF(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int minDataSize,
			int maxDataSize, int maxSensor) {

		// SDFtoDAGDemo applet = new SDFtoDAGDemo();
		DirectedAcyclicGraphGenerator DAGG = new DirectedAcyclicGraphGenerator();

		// Random dag
		Random rand = new Random(System.nanoTime());
		SDFGraph demoGraph = DAGG.createAcyclicRandomGraph(nbVertex,
				minInDegree, maxInDegree, minOutDegree, maxOutDegree, rand
						.nextInt(maxSensor));

		ToHSDFVisitor visitor2 = new ToHSDFVisitor();
		try {
			demoGraph.accept(visitor2);
		} catch (SDF4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	 * Convert a DAG-like SDF to an AlgorithmDescriptor
	 * 
	 * @param sdf
	 *            An SDFGraph to be converted
	 * @param scenario
	 *            A scenario associated to the SDFGraph
	 * @return An AlgorithmDescriptor
	 * @throws InvalidExpressionException
	 */
	public AlgorithmDescriptor sdf2Algorithm(SDFGraph sdf, IScenario scenario)
			throws InvalidExpressionException {

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
			dagedge.setOrigin(sdfedge.getSource().getName());
			ComputationDescriptorBuffer.get(sdfedge.getSource().getName())
					.addOutputCommunication(
							CommunicationDescriptorBuffer
									.get(dagedge.getName()));
			dagedge.setDestination(sdfedge.getTarget().getName());
			ComputationDescriptorBuffer.get(sdfedge.getTarget().getName())
					.addInputCommunication(
							CommunicationDescriptorBuffer
									.get(dagedge.getName()));
			dagedge.setAlgorithm(algorithm);
			algorithm.addCommunication(dagedge);
			dagedge.setWeight(sdfedge.getProd().intValue());
		}

		return algorithm;
	}
}
