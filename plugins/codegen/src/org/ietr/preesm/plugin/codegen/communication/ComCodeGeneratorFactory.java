/**
 * 
 */
package org.ietr.preesm.plugin.codegen.communication;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.core.architecture.route.RamRouteStep;
import org.ietr.preesm.core.codegen.CommunicationThreadDeclaration;
import org.ietr.preesm.core.tools.PreesmLogger;

/**
 * Given a route step, returns the correct communication code generator
 * 
 * @author mpelcat
 */
public class ComCodeGeneratorFactory {

	private Map<AbstractRouteStep,IComCodeGenerator> generators = null;
	private CommunicationThreadDeclaration thread;
	
	
	public ComCodeGeneratorFactory(CommunicationThreadDeclaration thread) {
		super();
		this.generators = new HashMap<AbstractRouteStep,IComCodeGenerator>();
		this.thread = thread;
	}

	public IComCodeGenerator getCodeGenerator(AbstractRouteStep step){
		
		if(!generators.containsKey(step)){
			generators.put(step,createCodeGenerator(step));
		}
		
		return generators.get(step);
	}

	private IComCodeGenerator createCodeGenerator(AbstractRouteStep step){
		IComCodeGenerator generator = null;
		
		if(step.getType() == MediumRouteStep.type){
			generator = new MediumComCodeGenerator(thread);
		}else if(step.getType() == DmaRouteStep.type){
			generator = new DmaComCodeGenerator(thread);
		}else if(step.getType() == MessageRouteStep.type){
			generator = new MessageComCodeGenerator(thread);
		}else if(step.getType() == RamRouteStep.type){
			generator = new RamComCodeGenerator(thread);
		}else{
			PreesmLogger.getLogger().log(Level.SEVERE,"A route step with unknown type was found during code generation: " + step);
		}
		
		return generator;
	}
}
