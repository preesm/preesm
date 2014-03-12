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

class Sdf3Printer {

	@Property
	val PreesmScenario scenario

	@Property
	val SDFGraph sdf

	new(SDFGraph sdf, PreesmScenario scenario) {
		_scenario = scenario
		_sdf = sdf
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
				</sdfProperties>
		</applicationGraph>
	'''

	def print(SDFAbstractVertex actor) '''
		<actor name="«actor.name»" type="«actor.name»">
			«FOR port : actor.interfaces»
				«print(port , actor.getAssociatedEdge(port) as SDFEdge)»
			«ENDFOR»
		</actor>
	'''
	
	def print(IInterface port, SDFEdge edge)'''
		<port name="«port.name»" type="«if(port instanceof SDFSourceInterfaceVertex) "in" else "out"»" rate="«if(port instanceof SDFSourceInterfaceVertex) edge.cons else edge.prod»"/>
	'''
	
	def print(SDFEdge edge)'''
		<channel name="«edge.source».«edge.sourceLabel»__«edge.target».«edge.targetLabel»" srcActor="«edge.source»" srcPort="«edge.sourceLabel»" dstActor="«edge.target»" dstPort="«edge.targetLabel»" initialTokens="«edge.delay.intValue»"/>
	'''

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
