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


package org.ietr.preesm.plugin.mapper.timekeeper;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * The interface of the ABC time keeper which calculates and stores time
 * data on tasks
 * 
 * @author mpelcat
 */
public interface ITimeKeeper {

	/**
	 * Gives the implementation time of the implementation if possible. If current
	 * implementation information is not enough to calculate this timing, returns
	 * TIME_UNKNOWN
	 */
	public int getFinalTime(MapperDAG implementation);

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially implanted graph and ignores the non implanted
	 * vertices If current implementation information is not enough to calculate
	 * the timings, returns TIME_UNKNOWN but this should not occur
	 */
	public int getFinalTime(MapperDAG implementation,
			ArchitectureComponent component);

	/**
	 * Gives the final time of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns TIME_UNKNOWN but this should not occur
	 */
	public int getFinalTime(MapperDAGVertex vertex);

	/**
	 * Updates the whole timing info of the implementation
	 */
	public void updateTLevels(MapperDAG implementation);

	/**
	 * Updates the whole timing info of the implementation
	 */
	public void updateTandBLevels(MapperDAG implementation);
	public void updateTandBLevels(MapperDAG implementation, MapperDAGVertex vertex);
	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings(MapperDAG implementation);
}
