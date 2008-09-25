package org.ietr.preesm.plugin.mapper.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.jgrapht.traverse.AbstractGraphIterator;

/**
 * Iterates the graph in ascending or descending order using the given
 * compare function
 *         
 * @author mpelcat
 */
public abstract class ImplantationIterator extends
		AbstractGraphIterator<MapperDAGVertex, MapperDAGEdge> implements
		Comparator<MapperDAGVertex> {

	/**
	 * Ordered vertex list parsed by the iterator
	 */
	private int currentIndex = -1;

	protected boolean directOrder;

	/**
	 * Ordered vertex list parsed by the iterator
	 */
	private List<MapperDAGVertex> orderedlist;

	/**
	 * Current architecture simulator
	 */
	protected IAbc simulator;

	public ImplantationIterator(MapperDAG dag,
			IAbc simulator, boolean directOrder) {
		super();
		this.directOrder = directOrder;
		this.simulator = simulator;

		createOrderedList(dag);

		currentIndex = 0;
	}

	@Override
	public abstract int compare(MapperDAGVertex arg0, MapperDAGVertex arg1);

	public void createOrderedList(MapperDAG implementation) {
		// Creating a sorted list using the current class as a comparator
		SortedSet<MapperDAGVertex> vertexSet = new ConcurrentSkipListSet<MapperDAGVertex>(
				this);
		MapperDAGVertex currentvertex;
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(
				implementation);

		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex)iterator.next();

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
