package org.ietr.preesm.algorithm.exportSdf3Xml

import java.io.FileWriter
import java.io.File
import java.io.IOException
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.preesm.core.scenario.PreesmScenario
import org.ietr.dftools.algorithm.model.sdf.SDFVertex
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.IInterface
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex
import org.ietr.dftools.algorithm.model.AbstractEdge
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.architecture.slam.Design
import java.util.HashSet

class Sdf3Printer {

	@Property
	val PreesmScenario scenario

	@Property
	val SDFGraph sdf
	
	@Property
	val Design archi

	new(SDFGraph sdf, PreesmScenario scenario, Design archi) {
		_scenario = scenario
		_sdf = sdf
		_archi = archi
	}

	def print() '''
		<sdf3 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0" type="sdf" 
		    			xsi:noNamespaceSchemaLocation="http://www.es.ele.tue.nl/sdf3/xsd/sdf3-sdf.xsd">
			<applicationGraph>
				<sdf name="«sdf.name.toFirstLower»" type="«sdf.name.toFirstUpper»">
					«FOR actor : sdf.vertexSet»
						«actor.print»
					«ENDFOR»
					«FOR edge : sdf.edgeSet»
						«edge.print»
					«ENDFOR»
				</sdf>
				<sdfProperties>
					«FOR actor : sdf.vertexSet»
						«actor.printProperties»
					«ENDFOR»
					«FOR edge : sdf.edgeSet»
						«edge.printProperties»
					«ENDFOR»
				</sdfProperties>
			</applicationGraph>
		</sdf3>
	'''

	def print(SDFAbstractVertex actor) '''
		<actor name="«actor.name»" type="«actor.name»">
			«FOR port : actor.interfaces»
				«print(port , actor.getAssociatedEdge(port) as SDFEdge)»
			«ENDFOR»
		</actor>
	'''
	
	def printProperties(SDFAbstractVertex actor){
		val timingManager = scenario.timingManager
		// Create a set of all the components
		val components = _archi.componentInstances.map[it.component].toSet
		val constraintManager = scenario.constraintGroupManager
		val simulationManager = scenario.simulationManager
		var firstIsDefault = true
		
		var nbMemCpy = 0 
		var size = 0
		
		if(actor.class != SDFVertex){
			nbMemCpy = actor.interfaces.size - 1 
			size = actor.sources.fold(0,[res, source | res + actor.getAssociatedEdge(source).prod.intValue])
		} 
		
		'''
		<actorProperties actor="«actor.name»">
			«IF actor.class == SDFVertex»
				«FOR component : components»
					«IF !(constraintManager.getGraphConstraintGroups(actor).map[it.operatorIds.head]).forall[!component.instances.map[it.instanceName].contains(it)]»
						<processor type="«component.vlnv.name»" default="«if(firstIsDefault) {firstIsDefault = false; true} else false»">
							<executionTime time="«timingManager.getTimingOrDefault(actor.name, component.vlnv.name)»"/>
						</processor>
					«ENDIF»
				«ENDFOR»
			«ELSE/*The vertex is a fork, join or broadcast */»
				«FOR component : components»
					«IF !(simulationManager.specialVertexOperatorIds.forall[!component.instances.map[it.instanceName].contains(it)])»
						<processor type="«component.vlnv.name»" default="«if(firstIsDefault) {firstIsDefault = false; true} else false»">
							<executionTime time="«(nbMemCpy*timingManager.getMemcpySetupTime(component.vlnv.name) + timingManager.getMemcpyTimePerUnit(component.vlnv.name)*size).intValue»"/>
						</processor>
					«ENDIF»
				«ENDFOR»
			«ENDIF»
		</actorProperties>
		'''
	}
	
	def printProperties(SDFEdge edge)'''
		<channelProperties channel="«edge.printName»">
			<tokenSize sz="«scenario.simulationManager.getDataTypeSizeOrDefault(edge.dataType.toString)»"/>
		</channelProperties>
	'''
	
	def print(IInterface port, SDFEdge edge)'''
		<port name="«port.name»" type="«if(port instanceof SDFSourceInterfaceVertex) "in" else "out"»" rate="«if(port instanceof SDFSourceInterfaceVertex) edge.cons else edge.prod»"/>
	'''
	
	def print(SDFEdge edge)'''
		<channel name="«edge.printName»" srcActor="«edge.source»" srcPort="«edge.sourceLabel»" dstActor="«edge.target»" dstPort="«edge.targetLabel»" initialTokens="«edge.delay.intValue»"/>
	'''
	
	def printName(SDFEdge edge)'''«edge.source».«edge.sourceLabel»__«edge.target».«edge.targetLabel»'''

	def write(File file) {
		try {
			val writer = new FileWriter(file);
			writer.write(this.print().toString);

			writer.close();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
