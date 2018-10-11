/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.algorithm.moa.activity

import java.util.Map
import java.util.HashMap

/**
 * Storing the number of custom quanta per actor.
 * Information is stored as strings containing JEP compatible formula.
 *
 * @author mpelcat
 *
 */
class CustomQuanta {

	/**
	 * Key for storing the number of custom quanta in a 2D map
	 */
	static private class MapKeyPair{
		String actor;
		String operator;

		new(String actor, String operator){
			this.actor = actor
			this.operator = operator
		}

		override equals(Object o){
			if(o instanceof MapKeyPair){
				val m = o
				return m.actor.equals(actor) &&
					   m.operator.equals(operator)
			}
			return false
		}

		override hashCode(){
			return actor.hashCode
		}

		override toString()'''(«actor»,«operator»)'''
	}

	Map<MapKeyPair, String> customQuanta = null

	new(){
		customQuanta = new HashMap<MapKeyPair, String>();
	}

	def void addQuantaExpression(String actor, String operator, String expression) {
		val mkp = new MapKeyPair(actor, operator)
		customQuanta.put(mkp, expression)
	}

	def String getQuanta(String actor, String operator) {
		val mkp = new MapKeyPair(actor, operator)
		if(customQuanta.containsKey(mkp)){
			return customQuanta.get(mkp)
		}
		return ""
	}

	def boolean hasQuanta(String actor, String operator) {
		val mkp = new MapKeyPair(actor, operator)
		if(customQuanta.containsKey(mkp)){
			return true
		}
		return false
	}

	override toString() '''
		«customQuanta»'''

	def void clear() {
		customQuanta.clear
	}

}
