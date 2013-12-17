package xtend.writer

import org.eclipse.emf.common.util.EList
import org.ietr.preesm.experiment.model.pimm.AbstractActor
import org.ietr.preesm.experiment.model.pimm.Actor
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface
import org.ietr.preesm.experiment.model.pimm.DataInputInterface
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface

class CWriter {
	def writeHierarchyLevel(String levelName, EList<AbstractActor> vertices)'''
		void «levelName» (PiSDFGraph* graph, BaseVertex* parentVertex){
			// Creating vertices.
	  	«FOR vertex : vertices»
	  		«IF vertex.getClass() == DataInputInterface»
	  		PiSDFIfVertex* «vertex.getName()» = (PiSDFIfVertex*)graph->addVertex(«vertex.getName()», input_vertex);
	  		«ENDIF»
	  		«IF vertex.getClass() == DataOutputInterface»
	  		PiSDFIfVertex* «vertex.getName()» = (PiSDFIfVertex*)graph->addVertex(«vertex.getName()», output_vertex);
	  		«ENDIF»
	  		«IF vertex.getClass() == ConfigOutputInterface»
	  		PiSDFConfigVertex* «vertex.getName()» = (PiSDFConfigVertex*)graph->addVertex(«vertex.getName()», config_vertex);
	  		«ENDIF»
	  		«IF vertex.getClass() == Actor»
	  		PiSDFVertex* «vertex.getName()» = (PiSDFVertex*)graph->addVertex(«vertex.getName()», pisdf_vertex);
	  		«ENDIF»
	  	«ENDFOR»
		}'''
	  		
//	def writeAddVertex(String vertexType)'''
//		«switch vertexType{
//			case "PiSDFVertex":» PiSDFVertex* addVertex(){
//			«case "PiSDFIfVertex":» 
//				PiSDFIfVertex* addVertex(){
//		«}»
//		}
//	'''
//	
//	def writeAddEdge(Fifo edge){
//		'''	void addEdge(PiSDFGraph graph){
//			graph->addEdge(
//		}'''
//	}
}