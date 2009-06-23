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

import java.util.Enumeration;
import java.util.HashMap;

import org.ietr.preesm.core.codegen.model.CodeGenArgument;
import org.ietr.preesm.core.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.plugin.codegen.model.IFunctionFactory;
import org.jacorb.idl.AliasTypeSpec;
import org.jacorb.idl.ConstrTypeSpec;
import org.jacorb.idl.Declaration;
import org.jacorb.idl.Definition;
import org.jacorb.idl.Definitions;
import org.jacorb.idl.EnumType;
import org.jacorb.idl.IDLTreeVisitor;
import org.jacorb.idl.IdlSymbol;
import org.jacorb.idl.Interface;
import org.jacorb.idl.InterfaceBody;
import org.jacorb.idl.Method;
import org.jacorb.idl.Module;
import org.jacorb.idl.NativeType;
import org.jacorb.idl.OpDecl;
import org.jacorb.idl.Operation;
import org.jacorb.idl.ParamDecl;
import org.jacorb.idl.SimpleTypeSpec;
import org.jacorb.idl.Spec;
import org.jacorb.idl.StructType;
import org.jacorb.idl.TypeDeclaration;
import org.jacorb.idl.TypeDef;
import org.jacorb.idl.UnionType;
import org.jacorb.idl.Value;
import org.jacorb.idl.VectorType;
import org.jacorb.idl.parser;

/**
 * Retrieving prototype data from an idl file
 * 
 * @author jpiat
 */
public class IDLFunctionFactory implements IFunctionFactory, IDLTreeVisitor {

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

	public static void reset() {
		instance = null;
	}

	private IDLFunctionFactory() {
		createdIdl = new HashMap<String, FunctionCall>();
	}

	@Override
	public FunctionCall create(String idlPath) {
		if (createdIdl.get(idlPath) == null) {
			currentCall = null;
			parser.setGenerator(this);

			try {
				finalCall = new FunctionCall();
				IDLParser.parse(idlPath, this);
				createdIdl.put(idlPath, finalCall);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			currentCall = null;
		}
		return createdIdl.get(idlPath);
	}

	@Override
	public void visitAlias(AliasTypeSpec arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitConstrTypeSpec(ConstrTypeSpec arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitDeclaration(Declaration arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitDefinition(Definition arg0) {
		arg0.get_declaration().accept(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visitDefinitions(Definitions arg0) {
		Enumeration e = arg0.getElements();
		while (e.hasMoreElements()) {
			IdlSymbol s = (IdlSymbol) e.nextElement();
			s.accept(this);
		}

	}

	@Override
	public void visitEnum(EnumType arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitInterface(Interface arg0) {
		if (arg0.name().equals("init")) {
			currentCall = new FunctionCall();
			arg0.body.accept(this);
			finalCall.setInitCall(currentCall);
		} else if (arg0.name().equals("loop")) {
			currentCall = finalCall;
			arg0.body.accept(this);
		} else if (arg0.name().equals("end")) {
			currentCall = new FunctionCall();
			arg0.body.accept(this);
			finalCall.setEndCall(currentCall);
		}
	}

	@Override
	public void visitInterfaceBody(InterfaceBody arg0) {
		Operation[] ops = arg0.getMethods();
		for (int i = 0; i < ops.length; i++) {
			ops[i].accept(this);
		}
	}

	@Override
	public void visitMethod(Method arg0) {
		currentCall.setFunctionName(arg0.name());
		arg0.parameterType.accept(this);
	}

	@Override
	public void visitModule(Module arg0) {
		arg0.getDefinitions().accept(this);
		System.out.println(arg0.toString());
	}

	@Override
	public void visitNative(NativeType arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0.toString());
	}

	@Override
	public void visitOpDecl(OpDecl arg0) {
		currentCall.setFunctionName(arg0.name());
		for (Object param : arg0.paramDecls) {
			((ParamDecl) param).accept(this);
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void visitParamDecl(ParamDecl arg0) {
		if (arg0.paramAttribute == ParamDecl.MODE_IN) {
			if (arg0.paramTypeSpec.name().equals("parameter")) {
				CodeGenParameter parameter = new CodeGenParameter(
						arg0.simple_declarator.name());
				currentCall.addParameter(parameter);
			} else {
				CodeGenArgument argument = new CodeGenArgument(
						arg0.simple_declarator.name(),CodeGenArgument.INPUT);
				argument.setType(arg0.paramTypeSpec.getIDLTypeName());
				currentCall.addArgument(argument);
			}
		} else if (arg0.paramAttribute == ParamDecl.MODE_OUT) {
			CodeGenArgument argument = new CodeGenArgument(
					arg0.simple_declarator.name(),CodeGenArgument.OUTPUT);
			argument.setType(arg0.paramTypeSpec.getIDLTypeName());
			currentCall.addArgument(argument);
		}
	}

	@Override
	public void visitSimpleTypeSpec(SimpleTypeSpec arg0) {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visitSpec(Spec arg0) {
		Enumeration e = arg0.definitions.elements();
		while (e.hasMoreElements()) {
			IdlSymbol s = (IdlSymbol) e.nextElement();
			s.accept(this);
		}
	}

	@Override
	public void visitStruct(StructType arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitTypeDef(TypeDef arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitUnion(UnionType arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitValue(Value arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitVectorType(VectorType arg0) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			return;
		}
		IDLFunctionFactory factory = new IDLFunctionFactory();
		@SuppressWarnings("unused")
		FunctionCall call = factory.create(args[0]);
		System.out.println("creation done");
	}

}
