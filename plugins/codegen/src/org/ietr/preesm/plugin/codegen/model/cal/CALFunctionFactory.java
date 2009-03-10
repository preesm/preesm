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
 
package org.ietr.preesm.plugin.codegen.model.cal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.ietr.preesm.core.codegen.model.CodeGenArgument;
import org.ietr.preesm.core.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.plugin.codegen.model.IFunctionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CALFunctionFactory implements IFunctionFactory{

	public HashMap<String, FunctionCall> createdCall;
	public static CALFunctionFactory instance = null;
	
	public static CALFunctionFactory getInstance(){
		if(instance == null){
			instance = new CALFunctionFactory() ;
		}
		return instance ;
	}
	
	private CALFunctionFactory() {
		createdCall = new HashMap<String, FunctionCall>();
	}

	public FunctionCall create(String calPath) {
		Document aDocument;
		FunctionCall result ;
		try {
			if (createdCall.get(calPath) == null) {
				FileInputStream inputStream = new FileInputStream(calPath);
				Lexer aLexer = new Lexer(inputStream);
				Parser aParser = new Parser(aLexer);
				aDocument = aParser.parseActor(calPath);
				Node elt = (Node) aDocument.getFirstChild();
				while(!elt.getNodeName().equals("Actor")){
					elt = elt.getNextSibling();
				}
				result = new FunctionCall(((Element) elt).getAttribute("name"));
				NodeList nodes = elt.getChildNodes();
				for(int i = 0 ; i < nodes.getLength() ; i ++){
					Element e = (Element) nodes.item(i);
					if(e.getNodeName().equals("Port") && e.getAttribute("kind").equals("Input")){
						result.addArgument(new CodeGenArgument(e.getAttribute("name"),CodeGenArgument.INPUT));
					}else if(e.getNodeName().equals("Port") && e.getAttribute("kind").equals("Output")){
						result.addArgument(new CodeGenArgument(e.getAttribute("name"),CodeGenArgument.OUTPUT));
					}
					else if(e.getNodeName().equals("Decl") && e.getAttribute("kind").equals("Parameter")){
						result.addParameter(new CodeGenParameter(e.getAttribute("name")));
					}
				}
			}else{
				result = createdCall.get(calPath);
			}
			return result;
		} catch (ParserErrorException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String [] args){
		if (args.length != 1) {
            return;
        }
		CALFunctionFactory factory = new CALFunctionFactory();
		FunctionCall call = factory.create(args[0]);
		System.out.println("creation done");
	}

}
