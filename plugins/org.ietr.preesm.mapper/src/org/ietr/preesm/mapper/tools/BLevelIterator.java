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

import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Iterates the graph in ascending or descending BLevel order. Uses abc
 * implementation to retrieve b levels.
 * 
 * @author mpelcat
 */
public class BLevelIterator extends ImplementationIterator {

	public BLevelIterator(IAbc abc, MapperDAG dag, boolean directOrder) {
		super(abc, dag, directOrder);
	}

	@Override
	public int compare(MapperDAGVertex arg0, MapperDAGVertex arg1) {

		if(abc != null){
			arg0 = abc.translateInImplementationVertex(arg0);
			arg1 = abc.translateInImplementationVertex(arg1);
		}
		
		if(!arg0.getTiming().hasBLevel() || !arg1.getTiming().hasBLevel()){
			WorkflowLogger.getLogger().log(Level.SEVERE, "B Level Iterator problem");
		}
			
		long bLevelDifference = (arg0.getTiming().getBLevel() - arg1
				.getTiming().getBLevel());

		if (!directOrder)
			bLevelDifference = -bLevelDifference;

		if (bLevelDifference == 0) {
			bLevelDifference = arg0.getName().compareTo(arg1.getName());

			if (bLevelDifference == 0) {
				bLevelDifference++;
			}
		}

		return (int) bLevelDifference;
	}
}
