/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2009 - 2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.codegen.idl;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
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
import org.preesm.algorithm.codegen.model.CodeGenArgument;
import org.preesm.algorithm.codegen.model.CodeGenParameter;
import org.preesm.algorithm.codegen.model.IFunctionFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Retrieving prototype data from an idl file.
 *
 * @author jpiat
 * @author mpelcat
 */
public class IDLPrototypeFactory implements IFunctionFactory, IDLTreeVisitor {

  /** The Constant INSTANCE. */
  public static final IDLPrototypeFactory INSTANCE = new IDLPrototypeFactory();

  /** Keeping memory of all created IDL prototypes. */
  private Map<String, ActorPrototypes> createdIdls;

  /**
   * Keeping memory of the highest index of init declared. Used to determine how many init phases must be generated
   */
  private int maxInitIndex = -1;

  /** Generated prototypes. */
  private ActorPrototypes finalPrototypes;

  /** Temporary prototype used during parsing. */
  private Prototype currentPrototype;

  /**
   * Instantiates a new IDL prototype factory.
   */
  public IDLPrototypeFactory() {
    resetPrototypes();
  }

  /**
   * Reset prototypes.
   */
  private void resetPrototypes() {
    this.createdIdls = new LinkedHashMap<>();
    this.maxInitIndex = -1;
  }

  /**
   * Retrieving prototypes from an IDL file.
   *
   * @param idlPath
   *          the idl path
   * @return the actor prototypes
   */
  @Override
  public ActorPrototypes create(final String idlPath) {
    if (this.createdIdls.get(idlPath) == null) {
      parser.setGenerator(this);

      try {
        this.finalPrototypes = new ActorPrototypes(idlPath);
        IDLParser.parse(idlPath, this);
        this.createdIdls.put(idlPath, this.finalPrototypes);
      } catch (final Exception e) {
        throw new PreesmRuntimeException(e);
      }
    }
    return this.createdIdls.get(idlPath);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitDefinition(org.jacorb.idl.Definition)
   */
  @Override
  public void visitDefinition(final Definition arg0) {
    arg0.get_declaration().accept(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitDefinitions(org.jacorb.idl.Definitions)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void visitDefinitions(final Definitions arg0) {
    final Enumeration e = arg0.getElements();
    while (e.hasMoreElements()) {
      final IdlSymbol s = (IdlSymbol) e.nextElement();
      s.accept(this);
    }

  }

  /**
   * Retrieving prototypes for the different code phases.
   *
   * @param arg0
   *          the arg 0
   */
  @Override
  public void visitInterface(final Interface arg0) {
    // Latest init phase prototype is in the interface "init"
    if (arg0.name().equals("init")) {
      this.currentPrototype = new Prototype();
      arg0.body.accept(this);
      this.finalPrototypes.setInitPrototype(this.currentPrototype);

      if (this.maxInitIndex < 0) {
        this.maxInitIndex = 0;
      }
    } else if (arg0.name().startsWith("init_")) {
      // Previous init phases to fill the delays
      final String sIndex = arg0.name().replaceFirst("init_", "");

      // Retrieving the index of the init phase
      try {
        final int index = Integer.parseInt(sIndex);

        this.currentPrototype = new Prototype();
        this.finalPrototypes.setInitPrototype(this.currentPrototype, index);
        arg0.body.accept(this);

        if (this.maxInitIndex < index) {
          this.maxInitIndex = index;
        }
      } catch (final NumberFormatException e) {
        final String message = "Badly formatted IDL interface, loop, init or init-i accepted : " + arg0.name();
        throw new PreesmRuntimeException(message, e);
      }
    } else if (arg0.name().equals("loop")) {
      // loop phase prototype is in the interphase "loop"
      this.currentPrototype = new Prototype();
      this.finalPrototypes.setLoopPrototype(this.currentPrototype);
      arg0.body.accept(this);
    } else {
      final String mesage = String.format("Ignored badly formatted IDL interface, loop, init or init-i accepted : %s",
          arg0.name());
      PreesmLogger.getLogger().log(Level.WARNING, mesage);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitInterfaceBody(org.jacorb.idl.InterfaceBody)
   */
  @Override
  public void visitInterfaceBody(final InterfaceBody arg0) {
    final Operation[] ops = arg0.getMethods();
    for (final Operation op : ops) {
      op.accept(this);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitMethod(org.jacorb.idl.Method)
   */
  @Override
  public void visitMethod(final Method arg0) {
    this.currentPrototype.setFunctionName(arg0.name());
    arg0.parameterType.accept(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitModule(org.jacorb.idl.Module)
   */
  @Override
  public void visitModule(final Module arg0) {
    arg0.getDefinitions().accept(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitNative(org.jacorb.idl.NativeType)
   */
  @Override
  public void visitNative(final NativeType arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitOpDecl(org.jacorb.idl.OpDecl)
   */
  @Override
  public void visitOpDecl(final OpDecl arg0) {
    this.currentPrototype.setFunctionName(arg0.name());
    for (final Object param : arg0.paramDecls) {
      ((ParamDecl) param).accept(this);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitParamDecl(org.jacorb.idl.ParamDecl)
   */
  @Override
  public void visitParamDecl(final ParamDecl arg0) {
    if (arg0.paramAttribute == ParamDecl.MODE_IN) {
      if (arg0.paramTypeSpec.name().equals("parameter")) {
        final CodeGenParameter parameter = new CodeGenParameter(arg0.simple_declarator.name(), 0);
        this.currentPrototype.addParameter(parameter);
      } else {
        final CodeGenArgument argument = new CodeGenArgument(arg0.simple_declarator.name(), CodeGenArgument.INPUT);
        if ((arg0.paramTypeSpec.name() == null) || (arg0.paramTypeSpec.name().length() == 0)) {
          argument.setType(arg0.paramTypeSpec.getIDLTypeName());
        } else {
          argument.setType(arg0.paramTypeSpec.name());
        }
        this.currentPrototype.addArgument(argument);
      }
    } else if (arg0.paramAttribute == ParamDecl.MODE_OUT) {
      if (arg0.paramTypeSpec.name().equals("parameter")) {
        final CodeGenParameter parameter = new CodeGenParameter(arg0.simple_declarator.name(), 1);
        this.currentPrototype.addParameter(parameter);
      } else {
        final CodeGenArgument argument = new CodeGenArgument(arg0.simple_declarator.name(), CodeGenArgument.OUTPUT);
        if ((arg0.paramTypeSpec.name() == null) || (arg0.paramTypeSpec.name().length() == 0)) {
          argument.setType(arg0.paramTypeSpec.getIDLTypeName());
        } else {
          argument.setType(arg0.paramTypeSpec.name());
        }
        this.currentPrototype.addArgument(argument);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitSpec(org.jacorb.idl.Spec)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void visitSpec(final Spec arg0) {
    final Enumeration e = arg0.definitions.elements();
    while (e.hasMoreElements()) {
      final IdlSymbol s = (IdlSymbol) e.nextElement();
      s.accept(this);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitSimpleTypeSpec(org.jacorb.idl.SimpleTypeSpec)
   */
  @Override
  public void visitSimpleTypeSpec(final SimpleTypeSpec arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitStruct(org.jacorb.idl.StructType)
   */
  @Override
  public void visitStruct(final StructType arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitTypeDeclaration(org.jacorb.idl.TypeDeclaration)
   */
  @Override
  public void visitTypeDeclaration(final TypeDeclaration arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitTypeDef(org.jacorb.idl.TypeDef)
   */
  @Override
  public void visitTypeDef(final TypeDef arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitUnion(org.jacorb.idl.UnionType)
   */
  @Override
  public void visitUnion(final UnionType arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitValue(org.jacorb.idl.Value)
   */
  @Override
  public void visitValue(final Value arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitVectorType(org.jacorb.idl.VectorType)
   */
  @Override
  public void visitVectorType(final VectorType arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitAlias(org.jacorb.idl.AliasTypeSpec)
   */
  @Override
  public void visitAlias(final AliasTypeSpec arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitConstrTypeSpec(org.jacorb.idl.ConstrTypeSpec)
   */
  @Override
  public void visitConstrTypeSpec(final ConstrTypeSpec arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitDeclaration(org.jacorb.idl.Declaration)
   */
  @Override
  public void visitDeclaration(final Declaration arg0) {
    // nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jacorb.idl.IDLTreeVisitor#visitEnum(org.jacorb.idl.EnumType)
   */
  @Override
  public void visitEnum(final EnumType arg0) {
    // nothing
  }

  /**
   * IDL prototypes determine the number of initialization phases that are necessary.
   *
   * @return The number of code init phases that must be generated
   */
  public int getNumberOfInitPhases() {
    return this.maxInitIndex + 1;
  }
}
