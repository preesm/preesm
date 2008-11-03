/**
 * 
 */
package org.ietr.preesm.core.architecture;

import org.ietr.preesm.core.architecture.advancedmodel.Bus;
import org.ietr.preesm.core.architecture.advancedmodel.BusDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicationNode;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicationNodeDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Communicator;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicatorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Memory;
import org.ietr.preesm.core.architecture.advancedmodel.MemoryDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Processor;
import org.ietr.preesm.core.architecture.advancedmodel.ProcessorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessor;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Fifo;
import org.ietr.preesm.core.architecture.advancedmodel.FifoDefinition;

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
			else if(type instanceof ProcessorDefinition){
				result = new Processor(name,(ProcessorDefinition)type);
			}
			else if(type instanceof IpCoprocessorDefinition){
				result = new IpCoprocessor(name,(IpCoprocessorDefinition)type);
			}
			else if(type instanceof FifoDefinition){
				result = new Fifo(name,(FifoDefinition)type);
			}
		}
		
		return result;
	}
}
