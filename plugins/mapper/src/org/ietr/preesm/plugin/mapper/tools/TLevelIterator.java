package org.ietr.preesm.plugin.mapper.tools;

import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Iterates the graph in ascending or descending TLevel order
 * 
 * @author mpelcat
 */
public class TLevelIterator extends ImplantationIterator {

	public TLevelIterator(MapperDAG dag, IAbc simulator,
			boolean directOrder) {
		super(dag, simulator, directOrder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(MapperDAGVertex arg0, MapperDAGVertex arg1) {

		int TLevelDifference = (simulator.getTLevel(arg0) - simulator
				.getTLevel(arg1));

		if (!directOrder)
			TLevelDifference = -TLevelDifference;

		if (TLevelDifference == 0)
			TLevelDifference++;

		return TLevelDifference;
	}

}
