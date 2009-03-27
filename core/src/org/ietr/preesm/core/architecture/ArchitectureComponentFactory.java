/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.core.architecture;

import org.ietr.preesm.core.architecture.advancedmodel.Bus;
import org.ietr.preesm.core.architecture.advancedmodel.BusDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicationNode;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicationNodeDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Communicator;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicatorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Fifo;
import org.ietr.preesm.core.architecture.advancedmodel.FifoDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessor;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Memory;
import org.ietr.preesm.core.architecture.advancedmodel.MemoryDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.Processor;
import org.ietr.preesm.core.architecture.advancedmodel.ProcessorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNode;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.DmaDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Ram;
import org.ietr.preesm.core.architecture.simplemodel.RamDefinition;

/**
 * Factory able to create an architecture component of any type
 * 
 * @author mpelcat
 */
public class ArchitectureComponentFactory {
	
	public static ArchitectureComponent createElement(ArchitectureComponentDefinition type,String name){

		ArchitectureComponent result = null;
		
		if(type != null){
			// Simple model
			if(type instanceof MediumDefinition){
				result = new Medium(name,(MediumDefinition)type);
			}
			else if(type instanceof OperatorDefinition){
				result = new Operator(name,(OperatorDefinition)type);
			}
			else if(type instanceof ContentionNodeDefinition){
				result = new ContentionNode(name,(ContentionNodeDefinition)type);
			}
			else if(type instanceof DmaDefinition){
				result = new Dma(name,(DmaDefinition)type);
			}
			else if(type instanceof ParallelNodeDefinition){
				result = new ParallelNode(name,(ParallelNodeDefinition)type);
			}
			else if(type instanceof RamDefinition){
				result = new Ram(name,(RamDefinition)type);
			}
			
			// Advanced model
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
