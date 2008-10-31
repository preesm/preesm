/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.ietr.preesm.core.architecture.advancedmodel.BusDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicationNodeDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicatorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.MemoryDefinition;
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
			else if(type == ArchitectureComponentType.communicationNode){
				result = new CommunicationNodeDefinition(name);
			}
			else if(type == ArchitectureComponentType.communicator){
				result = new CommunicatorDefinition(name);
			}
			else if(type == ArchitectureComponentType.memory){
				result = new MemoryDefinition(name);
			}
			else if(type == ArchitectureComponentType.bus){
				result = new BusDefinition(name);
			}
		}
		
		return result;
	}
}
