/**
 * 
 */
package org.ietr.preesm.core.workflow.sources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.sdf4j.generator.DirectedAcyclicGraphGenerator;
import org.sdf4j.importer.GMLSDFImporter;
import org.sdf4j.importer.InvalidFileException;
import org.sdf4j.model.dag.DAGDefaultEdgePropertyType;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.visitors.ToHSDFVisitor;
import org.sdf4j.visitors.TopologyVisitor;

/**
 * @author mpelcat
 *
 */
public class AlgorithmRetriever {

	/**
	 * Generates a random SDF with DAG properties
	 */
	static public SDFGraph randomDAG(int nbVertex, int minInDegree, int maxInDegree,
			int minOutDegree, int maxOutDegree, int maxDataSize, boolean randomDataSize) {

		DirectedAcyclicGraphGenerator DAGG = new DirectedAcyclicGraphGenerator();
		TopologyVisitor topo = new TopologyVisitor();

		// Generates the random DAG
		SDFGraph demoGraph = DAGG.createAcyclicRandomGraph(nbVertex,
				minInDegree, maxInDegree, minOutDegree, maxOutDegree);

		// Transforms it with HSDF
		ToHSDFVisitor visitor2 = new ToHSDFVisitor();
		demoGraph.accept(visitor2);
		demoGraph.accept(topo);

		// Possibly sets random cost to edges
		Random edgeSizeRand = new Random();

		Set<SDFEdge> edgeSet = demoGraph.edgeSet();
		Iterator<SDFEdge> iterator = edgeSet.iterator();

		while (iterator.hasNext()) {
			SDFEdge edge = iterator.next();

			// Sets random data sizes between 0 and maxDataSize + 1
			Double datasize = 0.0;
			
			if(randomDataSize)
				datasize = (double) edgeSizeRand.nextInt(maxDataSize);
			else
				datasize = (double) maxDataSize;

			DAGDefaultEdgePropertyType size = new DAGDefaultEdgePropertyType(
					datasize.intValue());

			edge.setProd(size);
			edge.setCons(size);
		}

		return demoGraph;
	}
	
	SDFGraph algorithm = null;

	public AlgorithmRetriever(AlgorithmConfiguration algorithmConfiguration) {
		super();
		String filename = algorithmConfiguration.getAlgorithmFileName();
		GMLSDFImporter importer = new GMLSDFImporter() ;
		try {
			algorithm = (SDFGraph) importer.parse(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFileException e) {
			e.printStackTrace();
		}
	}
	
	public SDFGraph getAlgorithm() {
		return algorithm;
	}
	
	
}
