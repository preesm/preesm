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

package org.ietr.preesm.plugin.mapper.model;

import org.sdf4j.model.dag.DAGEdge;

/**
 * Represents an edge in a DAG of type {@link MapperDAG} used in the mapper
 * 
 * @author mpelcat
 */
public class MapperDAGEdge extends DAGEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8013444915048927047L;

	protected InitialEdgeProperty initialEdgeProperty;

	protected TimingEdgeProperty timingEdgeProperty;
	
	/**
	 */
	public MapperDAGEdge(MapperDAGVertex source, MapperDAGVertex destination) {
		initialEdgeProperty = new InitialEdgeProperty();
		timingEdgeProperty = new TimingEdgeProperty();
	}

	public InitialEdgeProperty getInitialEdgeProperty() {
		return initialEdgeProperty;
	}

	public void setInitialEdgeProperty(InitialEdgeProperty initialEdgeProperty) {
		this.initialEdgeProperty = initialEdgeProperty;
	}

	public TimingEdgeProperty getTimingEdgeProperty() {
		return timingEdgeProperty;
	}

	public void setTimingEdgeProperty(TimingEdgeProperty timingEdgeProperty) {
		this.timingEdgeProperty = timingEdgeProperty;
	}

	@Override
	public String toString() {
		return "<" + getSource().getName() + "," + getTarget().getName() + ">";
	}
}
