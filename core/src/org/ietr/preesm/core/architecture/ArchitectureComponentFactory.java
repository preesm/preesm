/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;


/**
 * Factory able to create an architecture component of any type
 * 
 * @author mpelcat
 */
public class ArchitectureComponentFactory {
	
	public static ArchitectureComponent createElement(ArchitectureComponentDefinition type,String name){

		ArchitectureComponent result = null;
		
		if(type != null){
			if(type instanceof MediumDefinition){
				result = new Medium(name,(MediumDefinition)type);
			}
			else if(type instanceof OperatorDefinition){
				result = new Operator(name,(OperatorDefinition)type);
			}
		}
		
		return result;
	}
}
