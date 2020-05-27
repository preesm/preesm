/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
package org.preesm.codegen.model.util;

import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.preesm.codegen.model.ActorBlock;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.CallBlock;
import org.preesm.codegen.model.CodegenPackage;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.DataTransferAction;
import org.preesm.codegen.model.DistributedMemoryCommunication;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FpgaLoadAction;
import org.preesm.codegen.model.FreeDataTransferBuffer;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.GlobalBufferDeclaration;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.NullBuffer;
import org.preesm.codegen.model.OutputDataTransfer;
import org.preesm.codegen.model.PapifyFunctionCall;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.RegisterSetUpAction;
import org.preesm.codegen.model.SharedMemoryCommunication;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.impl.CallImpl;
import org.preesm.codegen.model.impl.CodegenFactoryImpl;
import org.preesm.commons.ecore.EObjectResolvingNonUniqueEList;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.slam.ComponentInstance;

/**
 *
 * @author anmorvan
 *
 */
public class CodegenModelUserFactory extends CodegenFactoryImpl {

  private CodegenModelUserFactory() {
    // Not meant to be instantiated: use static methods.
  }

  public static final CodegenModelUserFactory eINSTANCE = new CodegenModelUserFactory();

  /**
   *
   */
  public final CoreBlock createCoreBlock(final ComponentInstance cmp) {
    final CoreBlock coreBlock = super.createCoreBlock();
    final CallBlock initBlock = super.createCallBlock();
    final LoopBlock loopBlock = super.createLoopBlock();
    coreBlock.setInitBlock(initBlock);
    coreBlock.setLoopBlock(loopBlock);
    coreBlock.getCodeElts().add(initBlock);
    coreBlock.getCodeElts().add(loopBlock);

    if (cmp != null) {
      coreBlock.setName(cmp.getInstanceName());
      coreBlock.setCoreID(cmp.getHardwareId());
      coreBlock.setCoreType(cmp.getComponent().getVlnv().getName());
    }
    return coreBlock;
  }

  /**
   *
   */
  public final Constant createConstant(final String name, final long value) {
    final Constant res = super.createConstant();
    res.setType("long");
    res.setName(name);
    res.setValue(value);
    return res;
  }

  /**
   *
   */
  public final PortDirection createPortDirection(final PortKind direction) {
    switch (direction) {
      case CFG_INPUT:
        return PortDirection.NONE;
      case DATA_INPUT:
        return PortDirection.INPUT;
      case DATA_OUTPUT:
        return PortDirection.OUTPUT;
      case CFG_OUTPUT:
      default:
        throw new PreesmRuntimeException();
    }
  }

  /**
   *
   */
  public final PortDirection createPortDirection(final Direction direction) {
    switch (direction) {
      case IN:
        return PortDirection.INPUT;
      case OUT:
        return PortDirection.OUTPUT;
      default:
        return PortDirection.NONE;
    }
  }

  /**
   *
   */
  @Override
  public final ActorBlock createActorBlock() {
    final ActorBlock actorBlock = super.createActorBlock();
    final CallBlock initBlock = super.createCallBlock();
    final LoopBlock loopBlock = super.createLoopBlock();
    actorBlock.setInitBlock(initBlock);
    actorBlock.setLoopBlock(loopBlock);
    actorBlock.getCodeElts().add(initBlock);
    actorBlock.getCodeElts().add(loopBlock);
    return actorBlock;
  }

  /**
   *
   */
  @Override
  public final NullBuffer createNullBuffer() {
    final NullBuffer nullBuffer = super.createNullBuffer();
    nullBuffer.setName("NULL");
    return nullBuffer;
  }

  /**
   *
   */
  public final ActorFunctionCall createActorFunctionCall(final Actor actor, final FunctionPrototype prototype,
      final Map<Port, Variable> portValues) {
    final ActorFunctionCall afc = createActorFunctionCall();
    afc.setActorName(actor.getName());
    afc.setName(prototype.getName());
    afc.setOriActor(PreesmCopyTracker.getOriginalSource(actor));
    final EList<FunctionArgument> arguments = prototype.getArguments();
    for (FunctionArgument a : arguments) {
      final String name = a.getName();
      final Port lookupPort = actor.lookupPort(name);
      if (lookupPort == null) {
        throw new PreesmRuntimeException();
      }
      final PortKind portKind = lookupPort.getKind();
      final Variable variable = portValues.get(lookupPort);
      afc.addParameter(variable, createPortDirection(portKind));
    }
    return afc;
  }

  @Override
  public ActorFunctionCall createActorFunctionCall() {
    final ActorFunctionCall res = super.createActorFunctionCall();
    initCall(res);
    return res;
  }

  @Override
  public Communication createCommunication() {
    final Communication res = super.createCommunication();
    initCall(res);
    return res;
  }

  @Override
  public DistributedMemoryCommunication createDistributedMemoryCommunication() {
    final DistributedMemoryCommunication res = super.createDistributedMemoryCommunication();
    initCall(res);
    return res;
  }

  @Override
  public SharedMemoryCommunication createSharedMemoryCommunication() {
    final SharedMemoryCommunication res = super.createSharedMemoryCommunication();
    initCall(res);
    return res;
  }

  @Override
  public FifoCall createFifoCall() {
    final FifoCall res = super.createFifoCall();
    initCall(res);
    return res;
  }

  @Override
  public DataTransferAction createDataTransferAction() {
    final DataTransferAction res = super.createDataTransferAction();
    initCall(res);
    return res;
  }

  @Override
  public FunctionCall createFunctionCall() {
    final FunctionCall res = super.createFunctionCall();
    initCall(res);
    return res;
  }

  @Override
  public FpgaLoadAction createFpgaLoadAction() {
    final FpgaLoadAction res = super.createFpgaLoadAction();
    initCall(res);
    return res;
  }

  @Override
  public FreeDataTransferBuffer createFreeDataTransferBuffer() {
    final FreeDataTransferBuffer res = super.createFreeDataTransferBuffer();
    initCall(res);
    return res;
  }

  @Override
  public GlobalBufferDeclaration createGlobalBufferDeclaration() {
    final GlobalBufferDeclaration res = super.createGlobalBufferDeclaration();
    initCall(res);
    return res;
  }

  @Override
  public OutputDataTransfer createOutputDataTransfer() {
    final OutputDataTransfer res = super.createOutputDataTransfer();
    initCall(res);
    return res;
  }

  @Override
  public PapifyFunctionCall createPapifyFunctionCall() {
    final PapifyFunctionCall res = super.createPapifyFunctionCall();
    initCall(res);
    return res;
  }

  @Override
  public RegisterSetUpAction createRegisterSetUpAction() {
    final RegisterSetUpAction res = super.createRegisterSetUpAction();
    initCall(res);
    return res;
  }

  @Override
  public SpecialCall createSpecialCall() {
    final SpecialCall res = super.createSpecialCall();
    initCall(res);
    return res;
  }

  private void initCall(Call res) {
    final EObjectResolvingNonUniqueEList<Variable> newValue = new EObjectResolvingNonUniqueEList<>(Variable.class,
        (CallImpl) res, CodegenPackage.CALL__PARAMETERS);
    res.eSet(CodegenPackage.eINSTANCE.getCall_Parameters(), newValue);
  }

}
