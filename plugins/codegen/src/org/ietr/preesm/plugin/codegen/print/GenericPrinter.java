/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.codegen.print;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.log.PreesmLogger;

/**
 * Prints the generated code in an intermediate xml file and applies the xslt
 * sheet transformation to this file in order to generate the output
 * 
 * @author mpelcat
 */
public class GenericPrinter {

	/**
	 * Directory in which we should put the generated files
	 */
	private String directory = null;

	public GenericPrinter(String directory) {
		super();
		this.directory = directory;
	}

	/**
	 * Visiting a source file list launches the choice of an appropriate printer
	 * for each source file and the accept() call of this printer
	 */
	public void printList(SourceFileList list) {

		// Iterates the files
		Iterator<SourceFile> iterator = list.iterator();
		// Displays every files
		while (iterator.hasNext()) {
			print(iterator.next()); // Prints each source file
		}

	}

	/**
	 * Visiting a source file
	 */
	public void print(SourceFile srcFile) {

		Operator operator = srcFile.getOperator();

		// Generating an xml file corresponding to the code of one file
		String fileName = operator.getName();
		IPath path = new Path(directory);
		path = path.append(fileName + ".xml");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		XMLPrinter printer = getPrinter(operator);


		IFile iFile = workspace.getRoot().getFile(path);
		try {
			if (!iFile.exists()) {
				iFile.create(null, false, new NullProgressMonitor());
			}

			srcFile.accept(printer, printer.getRoot());
			printer.writeDom(iFile);

		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Getting the file printer corresponding to a given operator
	 */
	public XMLPrinter getPrinter(Operator opRef) {
		XMLPrinter printer = null;
		String opRefId = opRef.getDefinition().getId();

		printer = new XMLPrinter();
		printer.setCoreType(opRefId);

		return printer;
	}
}
