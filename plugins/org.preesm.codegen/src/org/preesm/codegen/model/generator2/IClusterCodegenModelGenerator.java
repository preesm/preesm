package org.preesm.codegen.model.generator2;

import org.preesm.codegen.model.CallFunctionBlock;
import org.preesm.codegen.model.LoopFunctionBlock;

public interface IClusterCodegenModelGenerator {

  public void generate();

  public CallFunctionBlock getCallFunctionBlock();

  public LoopFunctionBlock getLoopFunctionBlock();

}
