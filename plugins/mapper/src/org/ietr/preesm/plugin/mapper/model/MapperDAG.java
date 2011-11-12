/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;

/**
 * @author mpelcat
 * 
 *         This class represents a Directed Acyclic Graph in the mapper
 */
public class MapperDAG extends DirectedAcyclicGraph {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6757893466692519433L;

	/**
	 * Corresponding vertex in the input SDF graph
	 */
	private SDFGraph sdfGraph;

	/**
	 * The cost of the implementation
	 */
	private long ScheduleCost;

	/**
	 * Creactor of a DAG from a edge factory and a converted graph
	 */
	public MapperDAG(MapperEdgeFactory factory, SDFGraph graph) {
		super(factory);
		this.sdfGraph = graph;
		this.ScheduleCost = 0;
	}

	/**
	 * give the number of vertices in the DAG
	 */
	public int getNumberOfVertices() {
		return vertexSet().size();
	}

	/**
	 * Adds all vertices of a given set
	 */
	public void addAllVertices(Set<MapperDAGVertex> set) {
		Iterator<MapperDAGVertex> iterator = set.iterator();

		while (iterator.hasNext()) {
			addVertex(iterator.next());
		}
	}

	public long getScheduleCost() {
		return ScheduleCost;
	}

	public void setScheduleCost(long scheduleLatency) {
		ScheduleCost = scheduleLatency;
	}

	public SDFGraph getReferenceSdfGraph() {
		return sdfGraph;
	}

	public void setReferenceSdfGraph(SDFGraph sdfGraph) {
		this.sdfGraph = sdfGraph;
	}

	/**
	 * Clone a MapperDAG
	 */
	@Override
	public MapperDAG clone() {

		// create clone
		MapperDAG newDAG = new MapperDAG(new MapperEdgeFactory(),
				this.getReferenceSdfGraph());
		newDAG.setScheduleCost(this.getScheduleCost());

		// add vertex
		Iterator<DAGVertex> iterV = this.vertexSet().iterator();
		while (iterV.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) iterV.next();
			currentVertex = ((MapperDAGVertex) currentVertex).clone();
			newDAG.addVertex(currentVertex);
		}

		// add edge
		Iterator<DAGEdge> iterE = this.edgeSet().iterator();
		while (iterE.hasNext()) {
			MapperDAGEdge origEdge = (MapperDAGEdge) iterE.next();

			DAGVertex source = origEdge.getSource();
			DAGVertex target = origEdge.getTarget();

			String sourceName = source.getName();
			String targetName = target.getName();
			MapperDAGEdge newEdge = (MapperDAGEdge) newDAG.addEdge(
					newDAG.getVertex(sourceName), newDAG.getVertex(targetName));
			newEdge.setInitialEdgeProperty(origEdge.getInitialEdgeProperty()
					.clone());
			newEdge.setTimingEdgeProperty(origEdge.getTimingEdgeProperty()
					.clone());
			newEdge.copyProperties(origEdge);
		}
		newDAG.copyProperties(this);
		return newDAG;
	}

	/**
	 * Gets the vertex with the given reference graph
	 */
	public MapperDAGVertex getVertex(SDFAbstractVertex sdfvertex) {

		Iterator<DAGVertex> iter = vertexSet().iterator();
		MapperDAGVertex currentvertex = null;
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex) iter.next();
			if (currentvertex.getName().equals(sdfvertex.getName())) {
				return currentvertex;
			}
		}
		return null;
	}

	/**
	 * Gets all the DAG vertices corresponding to a given SDF graph
	 */
	public Set<MapperDAGVertex> getVertices(SDFAbstractVertex sdfvertex) {

		Set<MapperDAGVertex> currentset = new HashSet<MapperDAGVertex>();
		MapperDAGVertex currentvertex = null;
		for (DAGVertex currentv : vertexSet()) {
			currentvertex = (MapperDAGVertex) currentv;

			// Special vertices have null info
			if (currentvertex.getCorrespondingSDFVertex().getInfo() != null
					&& currentvertex.getCorrespondingSDFVertex().getInfo()
							.equals(sdfvertex.getInfo())) {
				currentset.add(currentvertex);
			}
		}
		return currentset;
	}

	/**
	 * Give a list of vertices in topological order
	 */
	public List<MapperDAGVertex> getVertexTopologicalList() {

		TopologicalDAGIterator iter = new TopologicalDAGIterator(this);
		List<MapperDAGVertex> list = new ArrayList<MapperDAGVertex>();
		MapperDAGVertex currentvertex = null;
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex) iter.next();
			if (!list.contains(currentvertex))
				list.add(currentvertex);
		}
		return list;
	}

	public Set<MapperDAGVertex> getVertexSet(Set<String> nameSet) {
		Set<MapperDAGVertex> vSet = new HashSet<MapperDAGVertex>();

		Iterator<String> iterator = nameSet.iterator();

		while (iterator.hasNext()) {
			String name = iterator.next();
			MapperDAGVertex v = (MapperDAGVertex) this.getVertex(name);
			vSet.add(v);

		}

		return vSet;
	}

	public MapperDAGVertex getMapperDAGVertex(String name) {

		return (MapperDAGVertex) super.getVertex(name);
	}
}
