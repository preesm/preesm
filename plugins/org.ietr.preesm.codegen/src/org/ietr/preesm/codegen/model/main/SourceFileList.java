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

package org.ietr.preesm.codegen.model.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.ietr.dftools.architecture.slam.ComponentInstance;

/**
 * A generated code is the gathering of source files, each one corresponding to
 * one core. A source file list extends an {@link ArrayList} of
 * {@link SourceFile}s with the ability of having a <b>main</b> file.
 * 
 * @author Maxime Pelcat
 * @author Matthieu Wipliez
 */
public class SourceFileList extends ArrayList<SourceFile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The main source file.
	 */
	private SourceFile main;

	/**
	 * A set of defined buffer names to avoid global double definition
	 */
	private Set<String> bufferNames = new HashSet<String>();

	/**
	 * Returns the main source file.
	 * 
	 * @return The main {@link SourceFile}.
	 */
	public SourceFile getMain() {
		return main;
	}

	/**
	 * Sets the given source file as being the main file. If the source file is
	 * not part of the source file list, it will be added.
	 * 
	 * @param main
	 *            A source file.
	 */
	public void setMain(SourceFile main) {
		if (!contains(main)) {
			add(main);
		}

		this.main = main;
	}

	/**
	 * Returns the defined buffer names
	 */
	public Set<String> getBufferNames() {
		return bufferNames;
	}

	/**
	 * Returns the source file corresponding to an operator
	 */
	public SourceFile get(ComponentInstance operator) {
		for(SourceFile file : this){
			if(file.getOperator().getInstanceName().equals(operator.getInstanceName())){
				return file;
			}
		}
		return null;
	}

	/**
	 * Adding a buffer name in a file list to avoid multiple defines
	 */
	public void addBufferName(String name) {
		bufferNames.add(name);
	}

	/**
	 * Displays pseudo-code for test
	 */
	@Override
	public String toString() {
		String code = "";

		// Displays every files
		for (SourceFile file : this) {
			code += file.toString();
			code += "\n//---------------------------\n";
		}

		return code;
	}
}
