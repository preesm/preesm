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
				val m = o as MapKeyPair
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

	def addQuantaExpression(String actor, String operator, String expression) {
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

	def clear() {
		customQuanta.clear
	}

}