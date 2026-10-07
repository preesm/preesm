package org.preesm.codegen.xtend.printer.c

import org.preesm.codegen.model.FiniteLoopBlock
import org.preesm.codegen.model.SectionBlock
import org.preesm.codegen.model.ClusterBlock

class OpenMPPrinter extends CPrinter {
	
	//#pragma omp parallel for private(«block2.iter.name»)
	override printFiniteLoopBlockHeader(FiniteLoopBlock block2) '''
		// Begin the for loop
		{
			int «block2.iter.name»;
			«IF block2.parallel.equals(true)»
			#pragma omp parallel for private(«block2.iter.name»)
			«ENDIF»
			for(«block2.iter.name»=0;«block2.iter.name»<«block2.nbIter»;«block2.iter.name»++) {

	'''
		
	override printSectionBlockHeader(SectionBlock block) '''
		#pragma omp section
		{

	'''
	
	override printClusterBlockHeader(ClusterBlock block) '''
		// Cluster: «block.name»
		// Schedule: «block.schedule»
		«IF block.parallel.equals(true)»
		#pragma omp parallel sections
		«ENDIF»
		{

	'''

}