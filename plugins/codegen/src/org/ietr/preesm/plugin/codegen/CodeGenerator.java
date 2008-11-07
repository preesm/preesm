package org.ietr.preesm.plugin.codegen;

import java.util.Iterator;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.sdf4j.model.dag.DirectedAcyclicGraph;

public class CodeGenerator {

	private SourceFileList list;

	public CodeGenerator(SourceFileList list) {
		this.list = list;
	}

	/**
	 * Creates the source files from an architecture
	 */
	private void createSourceFiles(MultiCoreArchitecture architecture) {
		Iterator<ArchitectureComponent> iterator = architecture.getComponents(
				ArchitectureComponentType.operator).iterator();

		// Generates and populates one source file per core
		while (iterator.hasNext()) {

			Operator currentOp = (Operator) iterator.next();

			SourceFile sourceFile = new SourceFile(currentOp.getName(),
					currentOp);
			list.add(sourceFile);

			// The main operator leads to the main source file
			if (architecture.getMainOperator().equals(currentOp)) {
				list.setMain(sourceFile);
			}
		}
	}

	/**
	 * Creates and fills source files from an SDF and an architecture
	 */
	public void generateSourceFiles(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture) {
		// Creates one source file per operator
		createSourceFiles(architecture);

		// For each source file, generates source code
		for (SourceFile file : list) {
			SourceFileCodeGenerator codegen = new SourceFileCodeGenerator(file);
			codegen.generateSource(algorithm, architecture);
		}
	}
}
