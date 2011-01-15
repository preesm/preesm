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

package org.ietr.preesm.workflow.elements;

import java.util.Map;
import java.util.Set;

import org.ietr.preesm.workflow.WorkflowException;

/**
 * This interface must be implemented by any workflow task element.
 * 
 * @author mpelcat
 */
public abstract class Task {

	/**
	 * The workflow task element must accept the names of the input/output ports
	 * in the graph. Otherwise, the graph is considered not valid.
	 */
	public abstract boolean accept(Set<String> inputs, Set<String> outputs);

	/**
	 * Displays the preferred prototype
	 */
	public abstract String displayPrototype();

	/**
	 * The workflow task element must have a execute method that is called by
	 * the workflow
	 */
	public abstract Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters) throws WorkflowException;

	/**
	 * Returns the task parameters and their default values
	 */
	public abstract Map<String, String> getDefaultParameters();
}
