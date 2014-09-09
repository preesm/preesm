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

package org.ietr.preesm.codegen.idl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.codegen.model.containers.CodeSectionType;

/**
 * Different function prototypes associated to an actor and retrieved from a
 * prototype file
 * 
 * @author jpiat
 * @author mpelcat
 */
public class ActorPrototypes implements IRefinement {

	/**
	 * Prototypes for the different phases of initialization of an actor
	 */
	private Map<Integer, Prototype> initPrototypes = null;
	
	/**
	 * Prototype for the loop execution of an actor
	 */
	private Prototype loopPrototype = null;
	
	private String path = null;

	public ActorPrototypes(String path) {
		initPrototypes = new HashMap<Integer, Prototype>();
		this.path = path;
	}

	/**
	 * Getting the initialization prototype for phase 0
	 */
	public Prototype getInitPrototype() {
		return getInitPrototype(0);
	}

	/**
	 * Getting the initialization prototype for phase (-) i
	 * 
	 */
	public Prototype getInitPrototype(int i) {
		if (initPrototypes.keySet().contains(0)) {
			return initPrototypes.get(i);
		} else {
			// Returning null means that the actor has no prototype for phase is
			return null;
		}
	}

	/**
	 * Default init call is call 0
	 */
	public void setInitPrototype(Prototype init) {
		setInitPrototype(init, 0);
	}

	/**
	 * Init i is added before init i-1 in the code
	 */
	public void setInitPrototype(Prototype init, int i) {
		if(initPrototypes.containsKey(i)){
			WorkflowLogger.getLogger().log(
					Level.WARNING,
					"IDL: Init phase number (-)" + i
							+ " was defined several time for file " + path);
		}
		initPrototypes.put(i, init);
	}

	public Prototype getLoopPrototype() {
		return loopPrototype;
	}

	public void setLoopPrototype(Prototype init) {
		loopPrototype = init;
	}
	
	public Prototype getPrototype(CodeSectionType sectionType){
		if(sectionType.getMajor().equals(CodeSectionType.MajorType.INIT)){
			return getInitPrototype(sectionType.getMinor());
		}
		else if(sectionType.getMajor().equals(CodeSectionType.MajorType.LOOP)){
			return getLoopPrototype();
		}
		return null;
	}
	
	public boolean hasPrototype(CodeSectionType sectionType){
		return getPrototype(sectionType) != null;
	}
}
