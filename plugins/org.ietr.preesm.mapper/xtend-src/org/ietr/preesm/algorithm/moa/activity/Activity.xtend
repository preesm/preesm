package org.ietr.preesm.algorithm.moa.activity

import java.util.Map
import java.util.HashMap

/**
 * Information on the activity of a system (number of tokens and quanta per architecture element).
 * This information can be printed in a human readable table or in a matrix (with 0s when no token appears)
 * where PEs in increasing name order are followed by CNs in increasing name order.
 *
 * @author mpelcat
 *
 */
class Activity {

	/*
	 * Information on number of tokens per architecture element (identified by name)
	 */
	Map<String, Long> tokens = null

	/*
	 * Information on number of quanta per architecture element (identified by name)
	 */
	Map<String, Long> quanta = null

	new(){
		tokens = new HashMap<String, Long>();
		quanta = new HashMap<String, Long>();
	}

	def addTokenNumber(String archiEltName, long tokenNr) {
		if(tokens.keySet.contains(archiEltName)){
			tokens.put(archiEltName, tokens.get(archiEltName) + tokenNr)
		}
		else{
			tokens.put(archiEltName, tokenNr)
		}
	}

	def addQuantaNumber(String archiEltName, long quantaNr) {
		if(quanta.keySet.contains(archiEltName)){
			quanta.put(archiEltName, quanta.get(archiEltName) + quantaNr)
		}
		else{
			quanta.put(archiEltName, quantaNr)
		}
	}

	override toString() '''

		tokens «tokens»
		quanta «quanta»'''

	def clear() {
		tokens.clear
		quanta.clear
	}

	/*
	 * Generating a string in CSV format from token information
	 * If human_readable=true, the names of the architecture components are displayed.
	 */
	def String tokensString(boolean human_readable){
		var str = ""
		var sortedNames = tokens.keySet.sort()

		if(human_readable){
			for(String name : sortedNames){
				str += name + ","
			}
			str = str.substring(0,str.length-1)
			str = str + "\n"
		}

		for(String name : sortedNames){
			str += tokens.get(name) + ","
		}
		str = str.substring(0,str.length-1)
		str = str + "\n"

		return str
	}

	/*
	 * Generating a string in CSV format from quanta information
	 * If human_readable=true, the names of the architecture components are displayed.
	 */
	def String quantaString(boolean human_readable){
		var str = ""
		var sortedNames = quanta.keySet.sort()

		if(human_readable){
			for(String name : sortedNames){
				str += name + ","
			}
			str = str.substring(0,str.length-1)
			str = str + "\n"
		}

		for(String name : sortedNames){
			str += quanta.get(name) + ","
		}
		str = str.substring(0,str.length-1)
		str = str + "\n"

		return str
	}
}