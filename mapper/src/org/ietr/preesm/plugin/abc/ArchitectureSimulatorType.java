/**
 * 
 */
package org.ietr.preesm.plugin.abc;

/**
 * Types of simulator to be used in parameters
 * 
 * @author mpelcat
 */
public enum ArchitectureSimulatorType {

	InfiniteHomogeneous,

	LooselyTimed,

	ApproximatelyTimed,

	AccuratelyTimed;

	@Override
	public String toString() {

		if(this == InfiniteHomogeneous){
			return "InfiniteHomogeneous";
		}
		else if(this == LooselyTimed){
			return "LooselyTimed";
		}
		else if(this == ApproximatelyTimed){
			return "ApproximatelyTimed";
		}
		else if(this == AccuratelyTimed){
			return "AccuratelyTimed";
			
		}
		
		return null;
	}
	
	public static ArchitectureSimulatorType fromString(String type) {

		if(type.equalsIgnoreCase("InfiniteHomogeneous")){
			return InfiniteHomogeneous;
		}
		else if(type.equalsIgnoreCase("LooselyTimed")){
			return LooselyTimed;
		}
		else if(type.equalsIgnoreCase("ApproximatelyTimed")){
			return ApproximatelyTimed;
		}
		else if(type.equalsIgnoreCase("AccuratelyTimed")){
			return AccuratelyTimed;
		}
		
		return null;
	}
}
