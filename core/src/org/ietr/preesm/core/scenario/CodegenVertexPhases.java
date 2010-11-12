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

package org.ietr.preesm.core.scenario;

import java.util.HashMap;
import java.util.Map;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Represents the phases for which code should be generated for a given vertex.
 * 
 * @author mpelcat
 */
public class CodegenVertexPhases {

	/**
	 * list of the phases for a vertex with their names and a boolean precising
	 * if code should be generated for this phase
	 */
	private Map<String, Boolean> phases;
	public static final String initPhaseId = "init";
	public static final String loopPhaseId = "loop";
	public static final String closePhaseId = "close";

	/**
	 * Vertex to which the preceding phases are associated
	 */
	private SDFAbstractVertex vertex = null;

	public CodegenVertexPhases(SDFAbstractVertex vertex) {
		super();
		this.vertex = vertex;
		phases = new HashMap<String, Boolean>();
		phases.put(initPhaseId, false);
		phases.put(loopPhaseId, true);
		phases.put(closePhaseId, false);
	}

	public boolean hasPhase(String name) {
		return phases.get(name);
	}

	public void setPhase(String name, Boolean exist) {
		phases.put(name, exist);
	}

	public SDFAbstractVertex getVertex() {
		return vertex;
	}

	public Map<String, Boolean> getPhases() {
		return phases;
	}

}
