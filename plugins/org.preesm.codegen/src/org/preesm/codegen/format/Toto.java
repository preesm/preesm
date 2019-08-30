package org.preesm.codegen.format;

import org.eclipse.emf.common.util.EList;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;

/**
 *
 */
public class Toto {

  /**
   *
   */
  public static void main(String[] args) {
    final CodegenModelUserFactory factory = CodegenModelUserFactory.eINSTANCE;
    final Buffer buff = factory.createBuffer();
    final FunctionCall fc = factory.createFunctionCall();

    final EList<Variable> parameters = fc.getParameters();

    final boolean add1 = parameters.add(buff);
    final boolean add2 = parameters.add(buff);

    System.out.println(add1);
    System.out.println(add2);
    System.out.println(parameters.size());

  }
}
