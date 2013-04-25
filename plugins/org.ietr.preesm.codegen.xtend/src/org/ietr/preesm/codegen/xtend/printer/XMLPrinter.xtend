package org.ietr.preesm.codegen.xtend.printer

import org.eclipse.emf.common.util.EList
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Variable

class XMLPrinter extends DefaultPrinter {
	override printCoreBlockHeader(CoreBlock coreBlock) '''
		<?xml version="1.0" encoding="UTF-8"?>
		<sourceCode xmlns="http://org.ietr.preesm.sourceCode">
			<coreType>«"XXX"»</coreType>
			<coreName>«coreBlock.name»</coreName>
			<SourceFile>
				
	'''

	override printCoreBlockFooter(CoreBlock block) '''
			</SourceFile>
		</sourceCode>
	'''

	override printDeclarationsHeader(EList<Variable> list) {
		return printEmptyHeaderWithNIndentation(1)
	}

	override printDefinitionsHeader(EList<Variable> list) '''
		<bufferContainer>
			
	'''

	override printDeclarationsFooter(EList<Variable> list) '''
		</bufferContainer>
	'''

}
