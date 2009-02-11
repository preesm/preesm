package org.ietr.preesm.plugin.codegen.model.cal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.ietr.preesm.plugin.codegen.model.FunctionCall;
import org.ietr.preesm.plugin.codegen.model.IFunctionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CALFunctionFactory implements IFunctionFactory{

	public HashMap<String, FunctionCall> createdCall;

	public CALFunctionFactory() {
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
						result.addInput(e.getAttribute("name"));
					}else if(e.getNodeName().equals("Port") && e.getAttribute("kind").equals("Output")){
						result.addOutput(e.getAttribute("name"));
					}
					else if(e.getNodeName().equals("Decl") && e.getAttribute("kind").equals("Parameter")){
						result.addParameter(e.getAttribute("name"));
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
