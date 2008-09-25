/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * A generated code is the gathering of source files, each
 * one corresponding to one core.
 * 
 * @author mwipliez
 * @author mpelcat
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
	 * 
	 */
	public SourceFileList() {
	}

	/**
	 * Creates the source files from an architecture
	 */
	private void createSourceFiles(IArchitecture architecture) {

		Iterator<Operator> iterator = architecture.getOperators().iterator();
		
		// Generates and populates one source file per core
		while(iterator.hasNext()){
			
			Operator currentOp = iterator.next();
			
			SourceFile sourceFile = new SourceFile(currentOp.getName(), currentOp);
			add(sourceFile);
			
			// The main operator leads to the main source file
			if(architecture.getMainOperator().equals(currentOp)){
				setMain(sourceFile);
			}
		}
	}

	/**
	 * Creates and fills source files from an SDF and an architecture
	 */
	public void generateSourceFiles(DirectedAcyclicGraph algorithm, IArchitecture architecture) {

		// Creates one source file per operator
		createSourceFiles(architecture);
		
		Iterator<SourceFile> iterator = this.iterator();

		// For each source file, generates source code
		while(iterator.hasNext()){
			
			SourceFile currentSource = iterator.next();
			
			currentSource.generateSource(algorithm, architecture);
		}
	}
	
	/**
	 * Returns the main source file.
	 * 
	 * @return
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
	 * Displays pseudo-code for test
	 */
	public String toString() {
		String code = "";
		
		Iterator<SourceFile> iterator = iterator();
		
		// Displays every files
		while(iterator.hasNext()){
			SourceFile file = iterator.next();
			
			code += file.toString();

			code += "\n//---------------------------\n";
		}
		
		return code;
	}
}
