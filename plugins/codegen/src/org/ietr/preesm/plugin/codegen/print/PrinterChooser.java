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
