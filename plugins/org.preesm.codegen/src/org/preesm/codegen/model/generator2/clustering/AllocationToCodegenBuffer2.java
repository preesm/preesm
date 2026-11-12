/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.codegen.model.generator2.clustering;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualLinkedHashBidiMap;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.memalloc.model.AbstractAllocation;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemBuffer;
import org.preesm.algorithm.memalloc.model.NullBuffer;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.memalloc.model.WorkingMemory;
import org.preesm.algorithm.memalloc.model.util.MemoryAllocationSwitch;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.generator2.CodegenModelGenerator2;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.scenario.Scenario;

/**
 *
 * @author anmorvan
 *
 *         WIP
 *
 *         TODO test it with PAFG
 *
 */
public class AllocationToCodegenBuffer2 extends MemoryAllocationSwitch<Boolean> {

  /**
   *
   */
  public static final AllocationToCodegenBuffer2 link(AbstractAllocation memAlloc, Scenario scenario, PiGraph algo,
      List<AbstractActor> totallyOrderedActors) {
    final AllocationToCodegenBuffer2 allocationToCodegenBuffer = new AllocationToCodegenBuffer2(memAlloc, scenario,
        algo, totallyOrderedActors);
    allocationToCodegenBuffer.link();

    return allocationToCodegenBuffer;
  }

  private final Scenario scenario;

  private final PiGraph algo;

  private final AbstractAllocation memAlloc;

  private final List<AbstractActor> totallyOrderedActors;

  private final Deque<Buffer> codegenBufferStack = new LinkedList<>();

  private final Deque<MemBuffer> allocBufferStack = new LinkedList<>();

  private final BidiMap<MemBuffer, Buffer> btb = new DualLinkedHashBidiMap<>();

  private final Map<Port, Variable> portToVariable = new LinkedHashMap<>();

  // for generating unique names
  private final Map<String, Long> bufferNames = new LinkedHashMap<>();

  private final Map<AbstractActor, Buffer> workingMems = new LinkedHashMap<>();

  /**
   *
   */
  private AllocationToCodegenBuffer2(AbstractAllocation memAlloc, Scenario scenario, PiGraph algo,
      List<AbstractActor> totallyOrderedActors) {
    this.memAlloc = memAlloc;
    this.scenario = scenario;
    this.algo = algo;
    this.totallyOrderedActors = totallyOrderedActors;
  }

  /**
   *
   */
  private void link() {

    this.doSwitch(this.memAlloc);

    // link variables for Fifos and set names
    final Set<Fifo> alreadyVisited = new HashSet<>();

    final List<AbstractActor> actors = new ArrayList<>(totallyOrderedActors);
    actors.addAll(this.algo.getDataInterfaces());

    for (final AbstractActor actor : actors) {

      final List<Fifo> fifos;
      if (actor instanceof final DataInterface di) {
        fifos = new ArrayList<>();
        fifos.add(di.getDataPort().getFifo());
      } else {
        fifos = actor.getAllDataPorts().stream().map(DataPort::getFifo).toList();
      }

      for (final Fifo fifo : fifos) {
        if (alreadyVisited.contains(fifo)) {
          continue;
        }
        alreadyVisited.add(fifo);

        final FifoAllocation fifoAllocation = this.memAlloc.getFifoAllocations().get(fifo);

        if (fifoAllocation == null) {
          throw new PreesmRuntimeException("Fifo [" + fifo.getId() + "] has no allocation.");
        }

        final MemBuffer srcBuffer = fifoAllocation.getSourceBuffer();
        final MemBuffer tgtBuffer = fifoAllocation.getTargetBuffer();
        final Buffer srcCodegenBuffer = this.btb.get(srcBuffer);
        final Buffer tgtCodegenBuffer = this.btb.get(tgtBuffer);

        if (srcCodegenBuffer == null) {
          throw new PreesmRuntimeException("Fifo [" + fifo.getId() + "] has no source codegen buffer.");
        }

        final long allocSize = srcCodegenBuffer.getSizeInBit();
        final long typeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());

        if (tgtCodegenBuffer != srcCodegenBuffer) {
          // generate 2 codegen buffers and route

          srcCodegenBuffer.setType(fifo.getType());

          if (allocSize % typeSize != 0) {
            throw new PreesmRuntimeException("Buffer size in bits is not a multiple of its type sizes.");
          }
          srcCodegenBuffer.setNbToken(allocSize / typeSize);
          srcCodegenBuffer.setTokenTypeSizeInBit(typeSize);

          final String scomment = fifo.getSourcePort().getId();
          srcCodegenBuffer.setComment(scomment);

          // If Buffer is a NullBuffer, leave the original name (NULL)
          if (!(tgtCodegenBuffer instanceof NullBuffer) && !(actor instanceof DataInterface)) {
            tgtCodegenBuffer.setName(generateUniqueBufferName("tgt_" + fifo.getTargetPort().getId()));
          }
          if (!(srcCodegenBuffer instanceof NullBuffer) && !(actor instanceof DataInterface)) {
            srcCodegenBuffer.setName(generateUniqueBufferName("src_" + fifo.getSourcePort().getId()));
          } else {
            srcCodegenBuffer.setComment("NULL_" + scomment);
          }

          this.portToVariable.put(fifo.getSourcePort(), srcCodegenBuffer);

          // TODO handle all route buffers
        } else {

          // If Buffer is a NullBuffer, leave the original name (NULL)
          if (!(tgtCodegenBuffer instanceof NullBuffer) && !(actor instanceof DataInterface)) {
            // XXX old style naming
            tgtCodegenBuffer.setName(
                generateUniqueBufferName(fifo.getSourcePort().getName() + "__" + fifo.getTargetPort().getName()));
          }

          this.portToVariable.put(fifo.getSourcePort(), tgtCodegenBuffer);
        }

        tgtCodegenBuffer.setType(fifo.getType());

        tgtCodegenBuffer.setNbToken(allocSize / typeSize);
        tgtCodegenBuffer.setTokenTypeSizeInBit(typeSize);

        final String tcomment = fifo.getTargetPort().getId();
        tgtCodegenBuffer.setComment(tcomment);

        if ((tgtCodegenBuffer instanceof NullBuffer)) {
          tgtCodegenBuffer.setComment("NULL_" + tcomment);
        }

        this.portToVariable.put(fifo.getTargetPort(), tgtCodegenBuffer);
      }
    }
    // link variables for Delays and set names
    for (final Entry<InitActor, MemBuffer> delayAllocation : this.memAlloc.getDelayAllocations()) {
      final InitActor initActor = delayAllocation.getKey();
      final Fifo fifo = initActor.getDataPort().getFifo();
      final MemBuffer buffer = delayAllocation.getValue();
      final Buffer codegenBuffer = this.btb.get(buffer);

      // XXX old naming
      final String sink = initActor.getName();
      final String source = initActor.getEndReference().getName();
      final String comment = source + " > " + sink;

      // If Buffer is a NullBuffer, leave the original name (NULL)
      if (codegenBuffer instanceof NullBuffer) {
        codegenBuffer.setComment("NULL_" + comment);
      } else {
        codegenBuffer.setComment(comment);

        final String name = source + "__" + sink;
        final String uniqueName = generateUniqueBufferName(MemoryExclusionGraph.FIFO_HEAD_PREFIX + name);
        codegenBuffer.setName(uniqueName);
      }

      codegenBuffer.setType(fifo.getType());
      final long allocSize = codegenBuffer.getSizeInBit();
      final long typeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());
      codegenBuffer.setNbToken(allocSize / typeSize);
      codegenBuffer.setTokenTypeSizeInBit(typeSize);
    }

    final EList<AbstractActor> allActors = this.algo.getActors();
    for (final AbstractActor actor : allActors) {
      for (final ConfigInputPort cip : actor.getConfigInputPorts()) {
        if (cip.getIncomingDependency() == null) {
          PreesmLogger.getLogger()
              .info("PORT SANS DEPENDANCE: actor=" + actor.getVertexPath() + " port=" + cip.getName());
          throw new PreesmRuntimeException("wtf");
        }
        final ISetter setter = cip.getIncomingDependency().getSetter();
        if (!(setter instanceof final Parameter parameter)) {
          throw new PreesmRuntimeException();
        }
        final long evaluate = parameter.getValueExpression().evaluateAsLong();
        portToVariable.put(cip, CodegenModelUserFactory.eINSTANCE.createConstant(cip.getName(), evaluate));
      }
    }
  }

  private String generateUniqueBufferName(final String name) {
    final String candidate = name.replace(".", "_").replace("-", "_");
    long idx;
    String key = candidate;
    if (key.length() > 58) {
      key = key.substring(0, 58);
    }
    if (this.bufferNames.containsKey(key)) {
      idx = this.bufferNames.get(key);
    } else {
      idx = 0;
      this.bufferNames.put(key, idx);
    }

    final String bufferName = key + "__" + idx;
    idx += 1;
    this.bufferNames.put(key, idx);
    return bufferName;
  }

  @Override
  public Boolean caseAllocation(final Allocation alloc) {
    // init internal variables with physical buffer children
    alloc.getPhysicalBuffers().forEach(this::doSwitch);

    // init working memories
    alloc.getWorkingMemories().forEach(this::doSwitch);
    return true;
  }

  @Override
  public Boolean caseWorkingMemory(final WorkingMemory workMem) {
    doSwitch(workMem.getMainBuffer());
    if (this.memAlloc == workMem) {
      workMem.getInterfaceBuffers().forEach(entry -> doSwitch(entry.getValue()));
    }
    final Buffer subBuf = btb.get(workMem.getMainBuffer());

    if (this.memAlloc != workMem) {
      subBuf.setName(generateUniqueBufferName("workingMemory_" + workMem.getAttachedActor().getName()));
    }

    this.workingMems.put(workMem.getAttachedActor(), subBuf);
    return true;
  }

  @Override
  public Boolean caseLogicalBuffer(final LogicalBuffer logicalBuffer) {
    if (this.btb.containsKey(logicalBuffer)) {
      return true;
    }
    final SubBuffer subBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    return setLogicalBufferProperties(logicalBuffer, subBuffer);

  }

  @Override
  public Boolean caseNullBuffer(final NullBuffer nullBuffer) {

    final SubBuffer subBuffer = CodegenModelUserFactory.eINSTANCE.createNullBuffer();

    return setLogicalBufferProperties(nullBuffer, subBuffer);
  }

  private boolean isWorkingMemoryRoot(final LogicalBuffer lb) {
    return this.memAlloc instanceof final WorkingMemory wm && wm.getMainBuffer() == lb;
  }

  private boolean isInterfaceRoot(final LogicalBuffer lb) {
    return this.memAlloc instanceof final WorkingMemory wm
        && wm.getInterfaceBuffers().stream().anyMatch(entry -> entry.getValue() == lb);
  }

  boolean setLogicalBufferProperties(final LogicalBuffer logicalBuffer, final SubBuffer subBuffer) {
    subBuffer.setType("bit");
    subBuffer.setTokenTypeSizeInBit(1);
    subBuffer.setNbToken(logicalBuffer.getSizeInBit());
    subBuffer.setOffsetInBit(logicalBuffer.getOffsetInBit());
    subBuffer.setWorkMemValue(logicalBuffer.isWorkMem());

    if (logicalBuffer.isWorkMem()) {
      subBuffer.setType("char");
      subBuffer.setTokenTypeSizeInBit(8);
      subBuffer.setNbToken((logicalBuffer.getSizeInBit() + 7) / 8);
    }

    this.btb.put(logicalBuffer, subBuffer);
    this.codegenBufferStack.push(subBuffer);
    this.allocBufferStack.push(logicalBuffer);

    final boolean mainBufferRoot = isWorkingMemoryRoot(logicalBuffer);
    final boolean interfaceRoot = isInterfaceRoot(logicalBuffer);

    if (mainBufferRoot) {
      subBuffer.setName(CodegenModelGenerator2.WORKING_MEMORY_NAME);

    } else if (interfaceRoot) {
      subBuffer.setInterfaceValue(true);
      final WorkingMemory workMem = (WorkingMemory) this.memAlloc;
      subBuffer.setName(workMem.getInterface(logicalBuffer).getDataPort().getName());

    } else {
      final Buffer container = this.btb.get(logicalBuffer.getContainingBuffer());
      if (container == null) {
        throw new PreesmRuntimeException("Container of logical buffer " + logicalBuffer + " is not registered");
      }
      subBuffer.reaffectContainer(container);
    }

    if (!logicalBuffer.isWorkMem() || mainBufferRoot) {
      logicalBuffer.getChildren().forEach(this::doSwitch);
    }

    this.codegenBufferStack.pop();
    this.allocBufferStack.pop();
    return true;
  }

  @Override
  public Boolean casePhysicalBuffer(final PhysicalBuffer phys) {
    final Buffer mainBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
    // Size in PhysicalBuffer phys in in BITS

    mainBuffer.setType("char");
    mainBuffer.setTokenTypeSizeInBit(8); // char is 8 bits
    mainBuffer.setNbToken((phys.getSizeInBit() + 7) / 8);

    mainBuffer.setName(phys.getMemoryBank().getInstanceName());

    this.btb.put(phys, mainBuffer);
    this.codegenBufferStack.push(mainBuffer);
    this.allocBufferStack.push(phys);

    phys.getChildren().forEach(this::doSwitch);

    this.codegenBufferStack.pop();
    this.allocBufferStack.pop();
    return true;
  }

  public List<Buffer> getCodegenBuffers() {
    return new ArrayList<>(this.btb.values());
  }

  public MemBuffer getAllocationBuffer(Buffer codegenBuffer) {
    return this.btb.getKey(codegenBuffer);
  }

  public Buffer getCodegenBuffer(MemBuffer allocationBuffer) {
    return this.btb.get(allocationBuffer);
  }

  public Map<Port, Variable> getPortToVariableMap() {
    return this.portToVariable;
  }

  public Buffer getWorkingMemory(AbstractActor a) {
    return this.workingMems.get(a);
  }
}
