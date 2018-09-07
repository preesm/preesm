package org.ietr.preesm.codegen.xtend.model.codegen.util;

import org.ietr.preesm.codegen.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.model.codegen.CallBlock;
import org.ietr.preesm.codegen.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.model.codegen.NullBuffer;

/**
 *
 * @author anmorvan
 *
 */
public class CodegenModelUserFactory {

  private CodegenModelUserFactory() {
    // Not meant to be instantiated: use static methods.
  }

  private static final CodegenFactory factory = CodegenFactory.eINSTANCE;

  /**
   *
   */
  public static final CoreBlock createCoreBlock() {
    final CoreBlock coreBlock = factory.createCoreBlock();
    final CallBlock initBlock = CodegenFactory.eINSTANCE.createCallBlock();
    final LoopBlock loopBlock = CodegenFactory.eINSTANCE.createLoopBlock();
    coreBlock.setInitBlock(initBlock);
    coreBlock.setLoopBlock(loopBlock);
    coreBlock.getCodeElts().add(initBlock);
    coreBlock.getCodeElts().add(loopBlock);
    return coreBlock;
  }

  /**
   *
   */
  public static final ActorBlock createActorBlock() {
    final ActorBlock actorBlock = factory.createActorBlock();
    final CallBlock initBlock = CodegenFactory.eINSTANCE.createCallBlock();
    final LoopBlock loopBlock = CodegenFactory.eINSTANCE.createLoopBlock();
    actorBlock.setInitBlock(initBlock);
    actorBlock.setLoopBlock(loopBlock);
    actorBlock.getCodeElts().add(initBlock);
    actorBlock.getCodeElts().add(loopBlock);
    return actorBlock;
  }

  /**
   *
   */
  public static final NullBuffer createNullBuffer() {
    final NullBuffer nullBuffer = factory.createNullBuffer();
    nullBuffer.setName("NULL");
    return nullBuffer;
  }
}
