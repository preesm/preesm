package org.ietr.preesm.algorithm.exportSdf3Xml

import java.io.FileWriter
import java.io.File
import java.io.IOException
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.preesm.core.scenario.PreesmScenario

class Sdf3Printer {
	
	@Property
	val PreesmScenario scenario
	
	@Property
	val SDFGraph sdf
	
	new(SDFGraph sdf, PreesmScenario scenario){
		_scenario = scenario
		_sdf = sdf
	}
	
	def print()'''
		printer
	'''
	def write(File file){
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
