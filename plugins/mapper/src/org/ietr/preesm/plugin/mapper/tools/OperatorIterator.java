/**
 * 
 */
package org.ietr.preesm.plugin.mapper.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Iterator on operators able to execute a given vertex
 * 
 * @author mpelcat
 */
public class OperatorIterator implements Iterator<Operator> {

	private int currentIndex = -1;

	private List<Operator> operatorlist;

	/**
	 * Constructor from an architecture and a reference vertex
	 */
	public OperatorIterator(MapperDAGVertex vertex, IArchitecture archi) {
		super();

		createList(vertex, archi);

		currentIndex = 0;
	}

	public void createList(MapperDAGVertex vertex, IArchitecture archi) {

		Set<OperatorDefinition> defset = vertex.getInitialVertexProperty()
				.getOperatorDefinitionSet();

		operatorlist = new ArrayList<Operator>();

		Iterator<OperatorDefinition> iterator = defset.iterator();

		while (iterator.hasNext()) {
			operatorlist.addAll(archi.getOperators(iterator.next()));
		}

	}

	public List<Operator> getOperatorList() {
		return operatorlist;
	}

	@Override
	public boolean hasNext() {
		return (currentIndex < operatorlist.size());
	}

	@Override
	public Operator next() {
		return operatorlist.get(currentIndex++);
	}

	@Override
	public void remove() {
		if (currentIndex > 0) {
			operatorlist.remove(currentIndex - 1);
			currentIndex -= 1;
		}
	}
}
