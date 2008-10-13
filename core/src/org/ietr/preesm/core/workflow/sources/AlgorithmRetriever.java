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


/**
 * 
 */
package org.ietr.preesm.core.workflow.sources;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
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
		
		Path relativePath = new Path(filename);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
		
		try {
			algorithm = (SDFGraph) importer.parse(file.getContents(), file.getFullPath().toOSString());
		} catch (InvalidFileException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AlgorithmRetriever(String algorithmRelativePath) {
		super();
		
		GMLSDFImporter importer = new GMLSDFImporter() ;
		
		Path relativePath = new Path(algorithmRelativePath);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
		
		try {
			algorithm = (SDFGraph) importer.parse(file.getContents(), file.getLocation().toOSString());
		} catch (InvalidFileException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SDFGraph getAlgorithm() {
		return algorithm;
	}
	
	
}
