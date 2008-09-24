package org.ietr.preesm.plugin.mapper.tools;

import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Iterates the graph in ascending or descending BLevel order
 * 
 * @author mpelcat
 */
public class BLevelIterator extends ImplantationIterator {

	public BLevelIterator(MapperDAG implantation,
			IAbc simulator, boolean directOrder) {
		super(implantation, simulator, directOrder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(MapperDAGVertex arg0, MapperDAGVertex arg1) {

		int TLevelDifference = (simulator.getBLevel(arg0) - simulator
				.getBLevel(arg1));

		if (!directOrder)
			TLevelDifference = -TLevelDifference;

		if (TLevelDifference == 0)
			TLevelDifference++;

		return TLevelDifference;
	}

}
