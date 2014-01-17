package org.ietr.preesm.experiment.memory

import bsh.Interpreter
import java.io.IOException
import java.util.Map

class ScriptRunner {

	def run() {

		val interpreter = new Interpreter;
		
		// Import the necessary libraries
		interpreter.eval("import "+ Buffer.name+ ";")
		//interpreter.eval("import " + Map.name + ";")
		
		// Retrieve Parameters
		var parameters = newHashMap("NbSplit" -> 3, "Overlap" -> 1, "Height" -> 3, "Width" -> 2)
		parameters.forEach[name, value | interpreter.set(name,value)] 
		
		
		// Retrieve buffers
		var inputs =newArrayList(new Buffer("input",parameters.get("Height")*parameters.get("Width"),1))
		inputs.forEach[interpreter.set("i_"+it.name,it)]
		
		
		var outputs = newArrayList(new Buffer("output",parameters.get("Height")*parameters.get("Width")+parameters.get("NbSplit")*parameters.get("Overlap")*2*parameters.get("Width"),1))
		outputs.forEach[interpreter.set("o_"+it.name,it)]
		
		//interpreter.classManager. 
		var o = try {
			interpreter.source("D:/SVN-ParcoursRecherche/Preesm/script.bsh");
		} catch (IOException e) {
			e.printStackTrace
			null // return null for the try catch block
		}
		
		System.out.println(inputs.get(0));
	}

}
