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

package org.ietr.preesm.plugin.codegen.model.idl;

import java.util.HashMap;
import java.util.logging.Level;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.preesm.core.codegen.model.CodeGenArgument;
import org.ietr.preesm.core.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.editor.IDLLanguageStandaloneSetup;
import org.ietr.preesm.editor.iDLLanguage.Direction;
import org.ietr.preesm.editor.iDLLanguage.Function;
import org.ietr.preesm.editor.iDLLanguage.IDL;
import org.ietr.preesm.editor.iDLLanguage.Interface;
import org.ietr.preesm.editor.iDLLanguage.InterfaceName;
import org.ietr.preesm.editor.iDLLanguage.Module;
import org.ietr.preesm.editor.iDLLanguage.Parameter;
import org.ietr.preesm.plugin.codegen.model.IFunctionFactory;

/**
 * Retrieving prototype data from an idl file
 * 
 * Modified to use the XText-generated parser
 * 
 * @author jpiat
 * @author mpelcat
 */
public class IDLFunctionFactory implements IFunctionFactory {

	public HashMap<String, FunctionCall> createdIdl;
	private FunctionCall finalCall;
	private FunctionCall currentCall;

	public static IDLFunctionFactory instance = null;

	public static IDLFunctionFactory getInstance() {
		if (instance == null) {
			instance = new IDLFunctionFactory();
		}
		return instance;
	}

	private IDLFunctionFactory() {
		createdIdl = new HashMap<String, FunctionCall>();
	}

	@Override
	public FunctionCall create(String idlPath) {
		if (createdIdl.get(idlPath) == null) {
			
			currentCall = null;
			finalCall = new FunctionCall();
			
			//new IDLLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
			ResourceSet rs = new ResourceSetImpl();
			
			Resource resource = rs.getResource(URI.createURI("file:/"+idlPath), true);
			
			EObject eobject = resource.getContents().get(0);

			if(eobject instanceof IDL){
				Module module = ((IDL) eobject).getElements().get(0);
				
				for(Interface intf : module.getInterfaces()){
					if(intf.getName().equals(InterfaceName.LOOP)){
						currentCall = finalCall;
					}
					else if(intf.getName().equals(InterfaceName.INIT)){
						currentCall = new FunctionCall();
						finalCall.setInitCall(currentCall);
					}
					else if(intf.getName().equals(InterfaceName.END)){
						currentCall = new FunctionCall();
						finalCall.setEndCall(currentCall);
					}
					
					Function function = intf.getFunction();
					currentCall.setFunctionName(function.getName());
					
					for(Parameter param : function.getParameters()){
						String stype = null;
						if(param.getType().getBtype()== null){
							stype = param.getType().getCtype().getName();
						}
						else{
							stype = param.getType().getBtype().getName();
						}
						
						if(param.getDirection().equals(Direction.IN)){
							if(stype.equals("parameter")){
								CodeGenParameter parameter = new CodeGenParameter(
										param.getName(), 0);
								currentCall.addParameter(parameter);
							}
							else{
								CodeGenArgument argument = new CodeGenArgument(
										param.getName(), CodeGenArgument.INPUT);
								argument.setType(stype);
								currentCall.addArgument(argument);
							}
						}
						else if(param.getDirection().equals(Direction.OUT)){
							if(stype.equals("parameter")){
								CodeGenParameter parameter = new CodeGenParameter(
										param.getName(), 1);
								currentCall.addParameter(parameter);
							}
							else{
								CodeGenArgument argument = new CodeGenArgument(
										param.getName(), CodeGenArgument.OUTPUT);
								argument.setType(stype);
								currentCall.addArgument(argument);
							}
						}
					}
				}
			}
			else{
				PreesmLogger.getLogger().log(Level.SEVERE,"wrong IDL file content: idlPath");
			}
			
			/////////////////////

			createdIdl.put(idlPath, finalCall);
			currentCall = null;
		}
		
		return createdIdl.get(idlPath);
	}
}
