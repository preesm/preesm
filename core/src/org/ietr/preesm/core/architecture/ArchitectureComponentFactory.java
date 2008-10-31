/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.ietr.preesm.core.architecture.parser.Bus;
import org.ietr.preesm.core.architecture.parser.BusDefinition;
import org.ietr.preesm.core.architecture.parser.CommunicationNode;
import org.ietr.preesm.core.architecture.parser.CommunicationNodeDefinition;
import org.ietr.preesm.core.architecture.parser.Communicator;
import org.ietr.preesm.core.architecture.parser.CommunicatorDefinition;
import org.ietr.preesm.core.architecture.parser.Memory;
import org.ietr.preesm.core.architecture.parser.MemoryDefinition;
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
			else if(type instanceof CommunicationNodeDefinition){
				result = new CommunicationNode(name,(CommunicationNodeDefinition)type);
			}
			else if(type instanceof CommunicatorDefinition){
				result = new Communicator(name,(CommunicatorDefinition)type);
			}
			else if(type instanceof MemoryDefinition){
				result = new Memory(name,(MemoryDefinition)type);
			}
			else if(type instanceof BusDefinition){
				result = new Bus(name,(BusDefinition)type);
			}
		}
		
		return result;
	}
}
