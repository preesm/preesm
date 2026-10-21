package org.preesm.codegen.model.generator2.clustering;

import java.util.List;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CallFunctionBlock;
import org.preesm.codegen.model.LoopFunctionBlock;

public interface IClusterCodegenModelGenerator {

  public void generate();

  public CallFunctionBlock getCallFunctionBlock();

  public LoopFunctionBlock getLoopFunctionBlock();

  public List<Buffer> getSinkFifoBuffers();

}
