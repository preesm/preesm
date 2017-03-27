/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.codegen.idl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.codegen.model.IFunctionFactory;
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
 * @author mpelcat
 */
public class IDLPrototypeFactory implements IFunctionFactory, IDLTreeVisitor {

	public final static IDLPrototypeFactory INSTANCE = new IDLPrototypeFactory();
	
	/**
	 * Keeping memory of all created IDL prototypes
	 */
	private HashMap<String, ActorPrototypes> createdIdls;

	/**
	 * Keeping memory of the highest index of init declared. Used to determine
	 * how many init phases must be generated
	 */
	private int maxInitIndex = -1;

	/**
	 * Generated prototypes
	 */
	private ActorPrototypes finalPrototypes;

	/**
	 * Temporary prototype used during parsing
	 */
	private Prototype currentPrototype;

	public IDLPrototypeFactory() {
		resetPrototypes();
	}

	public void resetPrototypes() {
		createdIdls = new HashMap<String, ActorPrototypes>();
		maxInitIndex = -1;
	}

	/**
	 * Retrieving prototypes from an IDL file
	 */
	@Override
	public ActorPrototypes create(String idlPath) {
		if (createdIdls.get(idlPath) == null) {
			parser.setGenerator(this);

			try {
				finalPrototypes = new ActorPrototypes(idlPath);
				IDLParser.parse(idlPath, this);
				createdIdls.put(idlPath, finalPrototypes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return createdIdls.get(idlPath);
	}

	@Override
	public void visitDefinition(Definition arg0) {
		arg0.get_declaration().accept(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void visitDefinitions(Definitions arg0) {
		Enumeration e = arg0.getElements();
		while (e.hasMoreElements()) {
			IdlSymbol s = (IdlSymbol) e.nextElement();
			s.accept(this);
		}

	}

	/**
	 * Retrieving prototypes for the different code phases
	 */
	@Override
	public void visitInterface(Interface arg0) {
		// Latest init phase prototype is in the interface "init"
		if (arg0.name().equals("init")) {
			currentPrototype = new Prototype();
			arg0.body.accept(this);
			finalPrototypes.setInitPrototype(currentPrototype);

			if (maxInitIndex < 0)
				maxInitIndex = 0;
		}

		// Previous init phases to fill the delays
		else if (arg0.name().startsWith("init_")) {
			String sIndex = arg0.name().replaceFirst("init_", "");

			// Retrieving the index of the init phase
			try {
				int index = Integer.parseInt(sIndex);

				currentPrototype = new Prototype();
				finalPrototypes.setInitPrototype(currentPrototype, index);
				arg0.body.accept(this);

				if (maxInitIndex < index)
					maxInitIndex = index;
			} catch (NumberFormatException e) {
				WorkflowLogger.getLogger().log(
						Level.SEVERE,
						"Badly formatted IDL interface, loop, init or init-i accepted : "
								+ arg0.name());
			}
		}

		// loop phase prototype is in the interphase "loop"
		else if (arg0.name().equals("loop")) {
			currentPrototype = new Prototype();
			finalPrototypes.setLoopPrototype(currentPrototype);
			arg0.body.accept(this);
		} else {
			WorkflowLogger.getLogger().log(
					Level.WARNING,
					"Ignored badly formatted IDL interface, loop, init or init-i accepted : "
							+ arg0.name());
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
		currentPrototype.setFunctionName(arg0.name());
		arg0.parameterType.accept(this);
	}

	@Override
	public void visitModule(Module arg0) {
		System.out.println(arg0.toString());

		arg0.getDefinitions().accept(this);
	}

	@Override
	public void visitNative(NativeType arg0) {
		System.out.println(arg0.toString());
	}

	@Override
	public void visitOpDecl(OpDecl arg0) {
		currentPrototype.setFunctionName(arg0.name());
		for (Object param : arg0.paramDecls) {
			((ParamDecl) param).accept(this);
		}
	}

	@Override
	public void visitParamDecl(ParamDecl arg0) {
		if (arg0.paramAttribute == ParamDecl.MODE_IN) {
			if (arg0.paramTypeSpec.name().equals("parameter")) {
				CodeGenParameter parameter = new CodeGenParameter(
						arg0.simple_declarator.name(), 0);
				currentPrototype.addParameter(parameter);
			} else {
				CodeGenArgument argument = new CodeGenArgument(
						arg0.simple_declarator.name(), CodeGenArgument.INPUT);
				if (arg0.paramTypeSpec.name() == null
						|| arg0.paramTypeSpec.name().length() == 0) {
					argument.setType(arg0.paramTypeSpec.getIDLTypeName());
				} else {
					argument.setType(arg0.paramTypeSpec.name());
				}
				currentPrototype.addArgument(argument);
			}
		} else if (arg0.paramAttribute == ParamDecl.MODE_OUT) {
			if (arg0.paramTypeSpec.name().equals("parameter")) {
				CodeGenParameter parameter = new CodeGenParameter(
						arg0.simple_declarator.name(), 1);
				currentPrototype.addParameter(parameter);
			} else {
				CodeGenArgument argument = new CodeGenArgument(
						arg0.simple_declarator.name(), CodeGenArgument.OUTPUT);
				if (arg0.paramTypeSpec.name() == null
						|| arg0.paramTypeSpec.name().length() == 0) {
					argument.setType(arg0.paramTypeSpec.getIDLTypeName());
				} else {
					argument.setType(arg0.paramTypeSpec.name());
				}
				currentPrototype.addArgument(argument);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void visitSpec(Spec arg0) {
		Enumeration e = arg0.definitions.elements();
		while (e.hasMoreElements()) {
			IdlSymbol s = (IdlSymbol) e.nextElement();
			s.accept(this);
		}
	}

	@Override
	public void visitSimpleTypeSpec(SimpleTypeSpec arg0) {
	}

	@Override
	public void visitStruct(StructType arg0) {
	}

	@Override
	public void visitTypeDeclaration(TypeDeclaration arg0) {
	}

	@Override
	public void visitTypeDef(TypeDef arg0) {
	}

	@Override
	public void visitUnion(UnionType arg0) {
	}

	@Override
	public void visitValue(Value arg0) {
	}

	@Override
	public void visitVectorType(VectorType arg0) {
	}

	@Override
	public void visitAlias(AliasTypeSpec arg0) {
	}

	@Override
	public void visitConstrTypeSpec(ConstrTypeSpec arg0) {
	}

	@Override
	public void visitDeclaration(Declaration arg0) {
	}

	@Override
	public void visitEnum(EnumType arg0) {
	}

	/**
	 * IDL prototypes determine the number of initialization phases that are
	 * necessary
	 * 
	 * @return The number of code init phases that must be generated
	 */
	public int getNumberOfInitPhases() {
		return maxInitIndex + 1;
	}
}
