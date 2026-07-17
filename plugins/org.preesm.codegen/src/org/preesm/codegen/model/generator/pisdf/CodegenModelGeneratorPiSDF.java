package org.preesm.codegen.model.generator.pisdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesisHelper;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.generator2.AllocationToCodegenBuffer;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class CodegenModelGeneratorPiSDF extends ScheduleSwitch<Void> {

  protected Design                    archi;
  protected PiGraph                   algo;
  protected Scenario                  scenario;
  protected Schedule                  schedule;
  protected Mapping                   mapping;
  protected Allocation                alloc;
  protected boolean                   papify;
  protected Map<AbstractVertex, Long> brv;

  public static final List<Block> generate(final Design archi, final PiGraph algo, final Scenario scenario,
      final Schedule schedule, final Mapping mapping, final Allocation memAlloc, final boolean papify) {
    return new CodegenModelGeneratorPiSDF(archi, algo, scenario, schedule, mapping, memAlloc, papify).generate();
  }

  protected CodegenModelGeneratorPiSDF(Design archi, PiGraph algo, Scenario scenario, Schedule schedule,
      Mapping mapping, Allocation memAlloc, boolean papify) {
    this.archi = archi;
    this.algo = algo;
    this.scenario = scenario;
    this.schedule = schedule;
    this.mapping = mapping;
    this.alloc = memAlloc;
    this.papify = papify;
    this.brv = PiBRV.compute(algo, BRVMethod.LCM);
  }

  protected AllocationToCodegenBuffer memoryLinker;
  // TODO : make coreBlock attribute a list, and build one core block per
  // component instance associated to the cluster in the mapping
  protected CoreBlock coreBlock;
  protected Block     currentBlock;
  protected int       loopCounter = 0;

  protected List<Block> generate() {

    // 0- Init blocks
    coreBlock = CodegenModelUserFactory.eINSTANCE.createCoreBlock(archi.getOperatorComponentInstances().get(0));
    currentBlock = coreBlock.getLoopBlock();

    // 1- Generate variables (and keep track of them with a linker)
    final List<AbstractActor> actors = new ArrayList<>();
    fromScheduleToListActor(schedule, actors);
    this.memoryLinker = AllocationToCodegenBuffer.link(alloc, scenario, algo, actors);

    // 2- Generate code : all case* methods will write their results in the coreBlock attribute
    doSwitch(schedule);

    final List<Block> blocks = new ArrayList<>();
    blocks.add(coreBlock);
    return blocks;
  }

  @Override
  public Void caseHierarchicalSchedule(HierarchicalSchedule object) {
    final int rep = (int) object.getRepetition();
    if (rep > 1) {
      final FiniteLoopBlock forLoop = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
      forLoop.setNbIter(rep);
      final IntVar iter = CodegenModelUserFactory.eINSTANCE.createIntVar();
      iter.setName("idx_" + loopCounter++);
      forLoop.setIter(iter);
      currentBlock.getCodeElts().add(forLoop);

      final Block oldCurrentBlock = currentBlock;
      currentBlock = forLoop;

      for (final Schedule child : object.getChildren()) {
        doSwitch(child);
      }

      loopCounter--;
      currentBlock = oldCurrentBlock;
    }

    return null;
  }

  @Override
  public Void caseSchedule(Schedule object) {
    for (final Schedule child : object.getChildren()) {
      doSwitch(child);
    }

    return null;
  }

  @Override
  public Void caseSequentialActorSchedule(SequentialActorSchedule object) {

    final List<AbstractActor> actors = object.getActorList();

    final Map<Port, Variable> p2v = memoryLinker.getPortToVariableMap();

    for (final AbstractActor a : actors) {

      // Creating a new finite loop block if actor is repeating more than once
      final int actorRep = (int) ClusterSynthesisHelper.getActorScopeRepetition(a, schedule);
      final Block oldCurrentBlock = currentBlock;

      if (actorRep > 1) {
        final FiniteLoopBlock aForLoop = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
        aForLoop.setNbIter(actorRep);
        final IntVar iter = CodegenModelUserFactory.eINSTANCE.createIntVar();
        iter.setName("idx_" + a.getName());
        aForLoop.setIter(iter);
        currentBlock.getCodeElts().add(aForLoop);
        currentBlock = aForLoop;
      }

      // Making calls depending on actor a type
      switch (a) {
        case final Actor na -> generateActorFiring(na, p2v);
        case final SpecialActor sa -> generateSpecialActorFiring(sa, p2v);
        default -> throw new PreesmRuntimeException("Unsupported actor [" + a + "]");
      }

      // "Closing" the loop by stepping back of one step in the loop block tree
      if (actorRep > 1) {
        currentBlock = oldCurrentBlock;
      }
    }
    return null;
  }

  private void generateActorFiring(final Actor a, final Map<Port, Variable> p2v) {

    // store buffers on which MD5 can be computed to check validity of transformations
    if (a.getDataOutputPorts().isEmpty()) {
      final EList<DataInputPort> inPorts = a.getDataInputPorts();
      for (final DataInputPort inPort : inPorts) {
        final Variable variable = p2v.get(inPort);
        coreBlock.getSinkFifoBuffers().add((Buffer) variable);
      }
    }

    final Refinement refinement = a.getRefinement();

    if (refinement instanceof final CHeaderRefinement cHeaderRef) {
      final FunctionPrototype initPrototype = cHeaderRef.getInitPrototype();
      if (initPrototype != null) {
        final ActorFunctionCall init = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(a, initPrototype, p2v);
        coreBlock.getInitBlock().getCodeElts().add(init);
      }
      final FunctionPrototype loopPrototype = cHeaderRef.getLoopPrototype();
      final ActorFunctionCall loop = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(a, loopPrototype, p2v);
      currentBlock.getCodeElts().add(loop);
      registerCallVariableToCoreBlock(coreBlock, loop);
    }

  }

  private static void registerCallVariableToCoreBlock(final CoreBlock operatorBlock, final Call call) {

    // Register the core Block as a user of the function variable
    for (final Variable variable : call.getParameters()) {

      // Currently, constants do not need to be declared nor
      // have creator since their value is directly used.
      // Consequently the used block can also be declared as the creator
      if (variable instanceof Constant) {
        variable.reaffectCreator(operatorBlock);
      }
      variable.getUsers().add(operatorBlock);
    }
  }

  private void generateSpecialActorFiring(final SpecialActor a, final Map<Port, Variable> p2v) {
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    specialCall.setName(a.getName());

    final Buffer lastBuffer;
    final FifoAllocation inputFifoAlloc = alloc.getFifoAllocations().get(a.getDataInputPorts().get(0).getFifo());
    final FifoAllocation outputFifoAlloc = alloc.getFifoAllocations().get(a.getDataOutputPorts().get(0).getFifo());

    PreesmLogger.getLogger().info("[DEBUG]: fifoAllocations = " + alloc.shortPrint());

    switch (a) {
      case final JoinActor ja -> {
        specialCall.setType(SpecialType.JOIN);
        lastBuffer = this.memoryLinker.getCodegenBuffer(outputFifoAlloc.getSourceBuffer());
      }
      case final RoundBufferActor rba -> {
        specialCall.setType(SpecialType.ROUND_BUFFER);
        lastBuffer = this.memoryLinker.getCodegenBuffer(outputFifoAlloc.getSourceBuffer());
      }

      case final ForkActor fa -> {
        specialCall.setType(SpecialType.FORK);
        lastBuffer = this.memoryLinker.getCodegenBuffer(inputFifoAlloc.getTargetBuffer());
      }

      case final BroadcastActor ba -> {
        specialCall.setType(SpecialType.BROADCAST);

        lastBuffer = this.memoryLinker.getCodegenBuffer(inputFifoAlloc.getTargetBuffer());

      }

      default -> throw new PreesmRuntimeException("special actor " + a + " has an unknown special type");

    }

    // Add it to the specialCall
    if (a instanceof JoinActor || a instanceof RoundBufferActor) {
      specialCall.addOutputBuffer(lastBuffer);
      a.getDataInputPorts().stream().map(port -> ((Buffer) p2v.get(port))).forEach(specialCall::addInputBuffer);
    } else {
      specialCall.addInputBuffer(lastBuffer);
      a.getDataOutputPorts().stream().map(port -> ((Buffer) p2v.get(port))).forEach(specialCall::addOutputBuffer);
    }

    currentBlock.getCodeElts().add(specialCall);
    registerCallVariableToCoreBlock(coreBlock, specialCall);
  }

  /**
   * Computes the list of actors contained in the schedule s.
   *
   * @param s
   *          the input
   * @param actors
   *          the output
   */
  private static void fromScheduleToListActor(Schedule s, List<AbstractActor> actors) {
    if (s instanceof final SequentialActorSchedule sa) {
      actors.addAll(sa.getActorList());
    } else {
      for (final Schedule child : s.getChildren()) {
        fromScheduleToListActor(child, actors);
      }
    }
  }

}
