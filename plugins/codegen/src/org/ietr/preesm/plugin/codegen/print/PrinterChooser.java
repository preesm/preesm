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


/**
 * 
 */
package org.ietr.preesm.plugin.codegen.print;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.ietr.preesm.core.log.PreesmLogger;

/**
 * @author mpelcat
 * 
 */
public class PrinterChooser {

	/**
	 * The key is the name of the current operator. The value is the code for
	 * this operator
	 */
	private Map<String, String> sourceMap = null;

	/**
	 * Directory in which we should put the generated files
	 */
	private String directory = null;

	public PrinterChooser(String directory) {
		super();
		this.directory = directory;
	}

	public Map<String, String> getSourceMap() {
		return sourceMap;
	}

	/**
	 * Visiting a source file list launches the choice of an appropriate printer
	 * for each source file and the accept() call of this printer
	 */
	public void printList(SourceFileList list) {
		// Initializes the map
		sourceMap = new HashMap<String, String>();

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
	public void print(SourceFile file) {
		Operator operator = file.getOperator();
		String fileName = operator.getName();
		Path path = new Path(directory);
		String filePath = path.toString() + "\\" + fileName + ".c";

		AbstractPrinter printer = getPrinter(operator);

		if (printer == null) {
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"No printer for the type of operator definition: "
							+ operator.getDefinition().getId());
		} else {
			file.accept(printer);

			String code = printer.getCurrentSource();

			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream(
						new File(filePath)), true);
				writer.print(code);
				writer.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getting the file printer corresponding to a given operator
	 */
	public AbstractPrinter getPrinter(Operator opRef) {
		AbstractPrinter printer = null;
		String opDefId = opRef.getDefinition().getId();

		if (opDefId.equals("C64x")) {
			printer = new C64Printer();
		} else if (opDefId.equals("PC")) {
			printer = new PCPrinter();
		}

		return printer;
	}
}
