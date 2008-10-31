/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;


/**
 * Factory able to create an architecture component of any type
 * 
 * @author mpelcat
 */
public class ArchitectureComponentDefinitionFactory {
	
	public static ArchitectureComponentDefinition createElement(ArchitectureComponentType type,String name){

		ArchitectureComponentDefinition result = null;
		
		if(type != null){
			if(type == ArchitectureComponentType.medium){
				result = new MediumDefinition(name);
			}
			else if(type == ArchitectureComponentType.operator){
				result = new OperatorDefinition(name);
			}
		}
		
		return result;
	}
}
