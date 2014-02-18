/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.mapper.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import net.sf.dftools.algorithm.model.dag.DAGVertex;

import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.jgrapht.traverse.AbstractGraphIterator;

/**
 * Iterates the graph in ascending or descending order using the given compare
 * function that respects topological order. If an ABC is given, the implementation iterator
 * makes it available for the compare method
 * 
 * @author mpelcat
 */
public abstract class ImplementationIterator extends
		AbstractGraphIterator<MapperDAGVertex, MapperDAGEdge> implements
		Comparator<MapperDAGVertex> {

	/**
	 * Ordered vertex list parsed by the iterator
	 */
	private int currentIndex = -1;

	/**
	 * Iteration in ascending or descending iteration order
	 */
	protected boolean directOrder;

	/**
	 * ABC made available for the compare method
	 */
	protected IAbc abc = null;

	/**
	 * Ordered vertex list parsed by the iterator
	 */
	private List<MapperDAGVertex> orderedlist;

	public ImplementationIterator() {
		super();
	}

	public ImplementationIterator(IAbc abc, MapperDAG dag, boolean directOrder) {
		super();
		initParams(abc, dag, directOrder);
	}

	public void initParams(IAbc abc, MapperDAG dag, boolean directOrder) {
		this.directOrder = directOrder;
		this.abc = abc;
		createOrderedList(dag);
		currentIndex = 0;
	}

	@Override
	public abstract int compare(MapperDAGVertex arg0, MapperDAGVertex arg1);

	public void createOrderedList(MapperDAG implementation) {
		// Creating a sorted list using the current class as a comparator
		SortedSet<MapperDAGVertex> vertexSet = new ConcurrentSkipListSet<MapperDAGVertex>(
				this);

		for (DAGVertex dv : implementation.vertexSet()) {
			MapperDAGVertex currentvertex = (MapperDAGVertex) dv;
			vertexSet.add(currentvertex);
		}

		orderedlist = new ArrayList<MapperDAGVertex>(vertexSet);
	}

	public List<MapperDAGVertex> getOrderedlist() {
		return orderedlist;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return (currentIndex < orderedlist.size());
	}

	@Override
	public MapperDAGVertex next() {
		// TODO Auto-generated method stub
		return orderedlist.get(currentIndex++);
	}

}
