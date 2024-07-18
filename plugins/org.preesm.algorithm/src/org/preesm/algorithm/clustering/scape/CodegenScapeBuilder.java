package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import org.preesm.algorithm.schedule.model.ScapeBuilder;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.impl.ActorImpl;

/**
 * This class fill the clustering SCAPE structure
 *
 * @author orenaud
 * @author emichel
 *
 */
public class CodegenScapeBuilder {

  private static final String INDEX       = "index";
  private static final String SIZEOF_TEXT = "*sizeof(";
  private static final String MEMCPY_TEXT = "memcpy(";

  public CodegenScapeBuilder(ScapeBuilder build, List<ScapeSchedule> cs, PiGraph subGraph, Long stackSize) {

    // build initial function
    String funcI = " void " + subGraph.getName() + "Init()";
    if (subGraph.getContainingPiGraph() != null && subGraph.getContainingPiGraph().getName().contains("sub")) {
      funcI = " void " + "Cluster_" + subGraph.getContainingPiGraph().getName() + "_" + subGraph.getName() + "Init()";
    }
    build.setInitFunc(funcI);

    // build loop function
    final String funcL = loopFunction(subGraph);
    build.setLoopFunc(funcL);

    // build buffer
    final Runnable task = subGraph.isOnGPU() ? () -> processGPUBuffer(subGraph, build)
        : () -> processCPUBuffer(subGraph, build, stackSize);
    task.run();

    // build body

    final String body = bodyFunction(subGraph, cs);
    build.setBody(body);
  }

  /**
   * This method processes the CPU buffer for a given subgraph and builds the buffer declarations or dynamic allocations
   * based on the stack size limit.
   *
   * @param subGraph
   *          The subgraph of the PiGraph being processed.
   * @param build
   *          The ScapeBuilder object that accumulates buffer declarations and allocations.
   * @param stackSize
   *          The maximum stack size allowed for buffer declarations.
   */
  private void processCPUBuffer(PiGraph subGraph, ScapeBuilder build, Long stackSize) {
    Long count = 0L;
    final Map<AbstractVertex, Long> brv = PiBRV.compute(subGraph, BRVMethod.LCM);
    for (final AbstractActor actor : subGraph.getExecutableActors()) {
      for (final DataOutputPort dout : actor.getDataOutputPorts()) {
        String buff = "";
        if (!(dout.getOutgoingFifo().getTarget() instanceof DataOutputInterface) && !dout.getFifo().isHasADelay()
            && !dout.getContainingActor().getName().equals("single_source")) {

          final String buffName = dout.getContainingActor().getName() + "_" + dout.getName() + "__"
              + ((AbstractVertex) dout.getFifo().getTarget()).getName() + "_"
              + dout.getFifo().getTargetPort().getName();
          final Long nbToken = dout.getExpression().evaluate() * brv.get(dout.getContainingActor());
          if (count < stackSize) {
            buff = dout.getOutgoingFifo().getType() + " " + buffName + "[" + nbToken + "];\n";
          } else {
            buff = dout.getOutgoingFifo().getType() + " *" + buffName + " = (" + dout.getOutgoingFifo().getType()
                + " *)malloc(" + nbToken + SIZEOF_TEXT + dout.getOutgoingFifo().getType() + "));\n";
            build.getDynmicBuffer().add(buffName);
          }
          count += nbToken;

          build.getBuffer().add(buff);
        }
      }
    }

  }

  /**
   * Translate the schedule firing of actor into string C or CUDA code.
   *
   * @param subGraph
   *          The subgraph of the PiGraph being processed
   * @param cs
   *          Schedule structure of the cluster
   * @return The string content of the bodyFunction.
   */
  private String bodyFunction(PiGraph subGraph, List<ScapeSchedule> cs) {

    final StringBuilder body = new StringBuilder();
    // Iterate through each schedule step (ScapeSchedule)
    final Map<String, AbstractActor> processedActors = new HashMap<>();

    for (final ScapeSchedule sc : cs) {
      // Skip if the actor's name is empty
      if (sc.getActor().getName().isEmpty()) {
        continue;
      }
      // Add a loop structure if the schedule step begins a loop and the subgraph is not on the GPU
      if (sc.isBeginLoop() && !subGraph.isOnGPU()) {
        final String bodyLine = "for(int index" + sc.getActor().getName() + " = 0; index" + sc.getActor().getName()
            + "<" + sc.getRepetition() + ";index" + sc.getActor().getName() + "++){\n";
        body.append(bodyLine);

      }

      final StringBuilder actor = new StringBuilder();
      String memcpy = "";

      // Process regular actors
      if (sc.getActor() instanceof Actor && !processedActors.containsValue(sc.getActor())) {
        final Runnable task = sc.getActor().isOnGPU() ? () -> actor.append(processActorGPU(sc))
            : () -> actor.append(processActorCPU(sc));

        task.run(); // Execute the appropriate actor processing method
        if (!subGraph.isOnGPU()) {
          memcpy = processClusteredDelay(sc);
        }
      }
      // Process special actors
      if (sc.getActor() instanceof SpecialActor && !subGraph.isOnGPU()) {
        actor.append(processSpecialActor(sc));
      } else if (sc.getActor() instanceof SpecialActor && subGraph.isOnGPU()) {
        if (sc.getActor() instanceof final BroadcastActor brd) {
          actor.append(processBroadcastActorGPU(brd, sc.getRepetition()));
        }
      }

      body.append(actor);
      body.append(memcpy);

      // Close the loop structure if the subgraph is not on the GPU
      if (!subGraph.isOnGPU()) {
        IntStream.range(0, sc.getEndLoopNb()).forEach(i -> body.append("\n }"));
      }

      processedActors.put(sc.getActor().getName(), sc.getActor());

    }

    return body.toString();
  }

  /**
   * This method processes a special actor (BroadcastActor, ForkActor, or JoinActor) based on the given schedule step
   * (ScapeSchedule). It generates the appropriate implementation for the special actor and appends it to a
   * StringBuilder.
   *
   * @param sc
   *          The ScapeSchedule representing the schedule step to be processed.
   * @return A StringBuilder containing the implementation of the special actor.
   */
  private StringBuilder processSpecialActor(ScapeSchedule sc) {
    // TODO: add case for GPU
    final StringBuilder actorImplem = new StringBuilder("//" + sc.getActor().getName() + "\n");

    if (sc.getActor() instanceof final BroadcastActor brd) {
      actorImplem.append(processBroadcastActor(brd, sc.getRepetition()));
    } else if (sc.getActor() instanceof final ForkActor frk) {
      actorImplem.append(processForkActor(frk, sc.getRepetition()));
    } else if (sc.getActor() instanceof final JoinActor join) {
      actorImplem.append(processJoinActor(join, sc.getRepetition()));
    }

    return actorImplem;
  }

  /**
   * This method processes a BroadcastActor by generating the implementation code for its data output ports based on the
   * provided actor and repetition count. It calculates scaling factors and buffer names for input and output ports, and
   * constructs memcpy operations to copy data from input to output buffers.
   *
   * @param brd
   *          The BroadcastActor instance to process.
   * @param repetition
   *          The number of repetitions for the broadcast actor.
   * @return A StringBuilder containing the implementation code for the broadcast actor.
   */
  private StringBuilder processBroadcastActor(BroadcastActor brd, int repetition) {

    final StringBuilder actorImplem = new StringBuilder();
    Long scaleIn = 1L;
    String inBuffName = "";

    if (brd.getDataInputPorts().get(0).getFifo().getSource() instanceof final DataInputInterface din) {
      scaleIn = din.getDataPort().getExpression().evaluate() / repetition;
      inBuffName = din.getName();
    } else {
      final String srcActor = ((AbstractVertex) brd.getDataInputPorts().get(0).getFifo().getSource()).getName() + "_"
          + brd.getDataInputPorts().get(0).getFifo().getSourcePort().getName();
      final String snkActor = brd.getName() + "_" + brd.getDataInputPorts().get(0).getName();
      inBuffName = srcActor + "__" + snkActor;
    }

    for (final DataOutputPort out : brd.getDataOutputPorts()) {

      Long scaleOut = 1L;
      String outBuffName = "";
      String iterOut = "0";
      String iterIn = "0";

      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        outBuffName = dout.getName();
        scaleOut = dout.getDataPort().getExpression().evaluate() / repetition;
      } else {
        final String targetActorName = ((AbstractActor) out.getFifo().getTarget()).getName();
        final String targetActorPortName = out.getFifo().getTargetPort().getName();
        outBuffName = out.getContainingActor().getName() + "_" + out.getName() + "__" + targetActorName + "_"
            + targetActorPortName;
      }

      if (repetition > 1) {
        iterOut = " " + INDEX + brd.getName() + "*" + scaleOut;
        iterIn = " " + INDEX + brd.getName() + "*" + scaleIn;
      }

      final Long rate = out.getExpression().evaluate();
      actorImplem.append(MEMCPY_TEXT + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + SIZEOF_TEXT + out.getFifo().getType() + ")" + ");\n");
    }

    return actorImplem;
  }

  /**
   * This method processes a ForkActor by generating the implementation code for its data output ports based on the
   * provided actor and repetition count. It calculates scaling factors and buffer names for input and output ports, and
   * constructs memcpy operations to copy data from input to output buffers.
   *
   * @param frk
   *          The ForkActor instance to process.
   * @param repetition
   *          The number of repetitions for the broadcast actor.
   * @return A StringBuilder containing the implementation code for the broadcast actor.
   */
  private StringBuilder processForkActor(ForkActor frk, int repetition) {

    final StringBuilder actorImplem = new StringBuilder();
    Long scaleIn = 1L;
    String inBuffName = "";

    if (frk.getDataInputPorts().get(0).getFifo().getSource() instanceof final DataInputInterface din) {
      scaleIn = din.getDataPort().getExpression().evaluate() / repetition;
      inBuffName = din.getName();
    } else {
      final String srcActor = ((AbstractVertex) frk.getDataInputPorts().get(0).getFifo().getSource()).getName() + "_"
          + frk.getDataInputPorts().get(0).getFifo().getSourcePort().getName();
      final String snkActor = frk.getName() + "_" + frk.getDataInputPorts().get(0).getName();
      inBuffName = srcActor + "__" + snkActor;
    }
    int ret = 0;
    for (final DataOutputPort out : frk.getDataOutputPorts()) {

      String outBuffName = "";
      String iterOut = "0";
      String iterIn;
      Long scaleOut = 1L;

      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        outBuffName = dout.getName();
        scaleOut = dout.getDataPort().getExpression().evaluate() / repetition;
      } else {
        outBuffName = out.getName();
      }
      iterIn = String.valueOf(ret);
      if (repetition > 1) {
        iterOut = " " + INDEX + frk.getName() + "*" + scaleOut;
        iterIn = " " + INDEX + frk.getName() + "*" + scaleIn + ret;

      }
      final Long rate = out.getExpression().evaluate();
      actorImplem.append(MEMCPY_TEXT + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + SIZEOF_TEXT + out.getFifo().getType() + ")" + ");\n");
      ret += rate;
    }

    return actorImplem;
  }

  /**
   * This method processes a JoinActor by generating the implementation code for its data output ports based on the
   * provided actor and repetition count. It calculates scaling factors and buffer names for input and output ports, and
   * constructs memcpy operations to copy data from input to output buffers.
   *
   * @param join
   *          The JoinActor instance to process.
   * @param repetition
   *          The number of repetitions for the broadcast actor.
   * @return A StringBuilder containing the implementation code for the broadcast actor.
   */
  private StringBuilder processJoinActor(JoinActor join, int repetition) {

    final StringBuilder actorImplem = new StringBuilder();
    Long scaleIn = 1L;
    String outBuffName = "";

    if (join.getDataOutputPorts().get(0).getFifo().getTarget() instanceof final DataOutputInterface dout) {
      scaleIn = dout.getDataPort().getExpression().evaluate() / repetition;
      outBuffName = dout.getName();
    } else {
      final String srcActor = join.getName() + "_" + join.getDataOutputPorts().get(0).getName();
      final String snkActor = ((AbstractVertex) join.getDataOutputPorts().get(0).getFifo().getTarget()).getName() + "_"
          + join.getDataOutputPorts().get(0).getFifo().getTargetPort().getName();
      outBuffName = srcActor + "__" + snkActor;
    }

    int ret = 0;
    for (final DataInputPort in : join.getDataInputPorts()) {

      final Long scaleOut = 1L;
      String inBuffName = "";
      String iterOut;
      String iterIn = "0";

      if (in.getFifo().getSource() instanceof final DataInputInterface din) {
        inBuffName = din.getName();
        scaleIn = din.getDataPort().getExpression().evaluate() / repetition;
      } else {
        inBuffName = ((AbstractVertex) in.getFifo().getSource()).getName() + "_"
            + in.getFifo().getSourcePort().getName() + "__" + join.getName() + "_" + in.getName();
      }

      iterOut = String.valueOf(ret);
      if (repetition > 1) {
        iterOut = " " + INDEX + join.getName() + "*" + scaleOut + ret;
        iterIn = " " + INDEX + join.getName() + "*" + scaleIn;
      }
      final Long rate = in.getExpression().evaluate();
      actorImplem.append(MEMCPY_TEXT + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + SIZEOF_TEXT + in.getFifo().getType() + ")" + ");\n");
      ret += rate;
    }

    return actorImplem;
  }

  /**
   * This method processes clustered delays for a given schedule step (ScapeSchedule). It constructs memcpy operations
   * for data output ports that have delays without persistence level, appending them to a StringBuilder.
   *
   * @param sc
   *          The ScapeSchedule representing the schedule step to be processed.
   * @return A String containing memcpy operations for clustered delays.
   */
  private String processClusteredDelay(ScapeSchedule sc) {
    final StringBuilder memcpy = new StringBuilder();
    for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
      if (out.getFifo().isHasADelay() && out.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        final Delay delay = out.getFifo().getDelay();
        memcpy.append(MEMCPY_TEXT + delay.getActor().getSetterActor().getName() + ","
            + delay.getActor().getGetterActor().getName() + "," + out.getFifo().getDelay().getExpression().evaluate()
            + ");\n");
      }
    }

    return memcpy.toString();
  }

  /**
   * This method processes an executable actor for CPU execution based on the given schedule step (ScapeSchedule). It
   * generates the implementation code for calling the actor's function, including handling function prototypes,
   * arguments, and data ports.
   *
   * @param sc
   *          The ScapeSchedule representing the schedule step to be processed.
   * @return A StringBuilder containing the implementation code for the CPU execution of the actor.
   */
  private StringBuilder processActorCPU(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();

    // Check if the actor is an instance of ExecutableActor
    if (sc.getActor() instanceof ExecutableActor) {
      String funcName = sc.getActor().getName();
      FunctionPrototype loopPrototype = null;

      // Check if the actor has a refinement and a loop prototype
      if (((Actor) sc.getActor()).getRefinement() != null
          && ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype() != null) {
        loopPrototype = ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype();
        funcName = ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype().getName();

      }

      actorImplem.append(funcName + "(");

      final int nbArg = sc.getActor().getConfigInputPorts().size() + sc.getActor().getDataInputPorts().size()
          + sc.getActor().getDataOutputPorts().size();

      // Handle case where there are no argument
      if (nbArg == 0) {
        actorImplem.append(");\n");
        return actorImplem;
      }

      // Append input configuration parameters if there is a loop prototype
      if (loopPrototype != null) {
        for (final FunctionArgument arg : loopPrototype.getInputConfigParameters()) {
          actorImplem.append(arg.getName());
          actorImplem.append(",");
        }
      }

      // Append processed data input and output ports
      actorImplem.append(processActorDataInputPorts(sc));
      actorImplem.append(processActorDataOutputPorts(sc));

      actorImplem.deleteCharAt(actorImplem.length() - 1);

      actorImplem.append(");\n");
    }
    return actorImplem;
  }

  private StringBuilder processActorDataInputPorts(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();

    boolean isGPU = false;
    for (final AbstractActor prout : sc.getActor().getContainingPiGraph().getContainingPiGraph().getActors()) {
      if (prout.isOnGPU()) {
        isGPU = true;
      }
    }

    for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
      String buffname = "";

      // EWEN TESTING

      if (((AbstractActor) in.getFifo().getSource()).isOnGPU()) {
        buffname += "d_";
      }
      // END EWEN TESTING

      Long scale = 1L;

      if (in.getFifo().getSource() instanceof final DataInputInterface din) {
        scale = din.getDataPort().getExpression().evaluate() / sc.getRepetition();
        buffname += din.getName();
      } else if (in.getFifo().isHasADelay() && in.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        final Delay delay = in.getFifo().getDelay();
        buffname += delay.getActor().getSetterActor().getName();
      } else {
        buffname += ((AbstractVertex) in.getFifo().getSource()).getName() + "_" + in.getFifo().getSourcePort().getName()
            + "__" + sc.getActor().getName() + "_" + in.getName();
      }

      if ((sc.isLoopPrec() || sc.isBeginLoop() || sc.isEndLoop()) && !isGPU) {
        buffname += " + index" + sc.getActor().getName() + "*" + scale;
      }

      actorImplem.append(buffname);
      actorImplem.append(",");
    }

    return actorImplem;
  }

  private StringBuilder processActorDataOutputPorts(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();

    for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
      String buffname = "";
      // EWEN TESTING
      if (((AbstractActor) out.getFifo().getTarget()).isOnGPU()) {
        buffname += "d_";
      } else if (out.getFifo().getTarget() instanceof final BroadcastActor bc) {
        final AtomicBoolean isNextGPU = new AtomicBoolean(false);
        bc.getDataOutputPorts().forEach(outPort -> {
          if (((AbstractActor) outPort.getFifo().getTarget()).isOnGPU()) {
            isNextGPU.set(true);
          }
        });
        if (isNextGPU.get()) {
          buffname += "d_";
        }
      }
      // END EWEN TESTING
      Long scale = 1L;

      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        scale = dout.getDataPort().getExpression().evaluate() / sc.getRepetition();
        buffname += dout.getName();
      } else if (out.getFifo().isHasADelay() && out.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        final Delay delay = out.getFifo().getDelay();
        buffname += delay.getActor().getGetterActor().getName();
      } else {
        buffname += sc.getActor().getName() + "_" + out.getName() + "__"
            + ((AbstractVertex) out.getFifo().getTarget()).getName() + "_" + out.getFifo().getTargetPort().getName();
      }

      if (sc.isLoopPrec() || sc.isBeginLoop() || sc.isEndLoop()) {
        buffname += " + index" + sc.getActor().getName() + "*" + scale;
      }

      actorImplem.append(buffname);
      actorImplem.append(",");
    }

    return actorImplem;
  }

  /**
   * Translate the subgraph into string C function declaration.
   *
   * @param subgraph
   *          Graph to consider.
   * @return The string content of the loopFunction.
   */
  private String loopFunction(PiGraph subGraph) {
    final StringBuilder funcLoop = new StringBuilder();
    if (subGraph.getContainingPiGraph() == null || !subGraph.getContainingPiGraph().getName().contains("sub")) {
      funcLoop.append("void " + subGraph.getName() + "(");
    } else {
      funcLoop
          .append("void " + "Cluster_" + subGraph.getContainingPiGraph().getName() + "_" + subGraph.getName() + "(");
    }

    final int nbArg = subGraph.getParameters().size() + subGraph.getDataInterfaces().size();

    if (nbArg == 0) {
      funcLoop.append(")");
      return funcLoop.toString();
    }

    for (final Parameter param : subGraph.getParameters()) {
      funcLoop.append("int " + param.getName());
      funcLoop.append(",");
    }

    for (final InterfaceActor dInterface : subGraph.getDataInterfaces()) {
      funcLoop.append(dInterface.getDataPort().getFifo().getType() + " *" + dInterface.getName());
      funcLoop.append(",");
    }

    // Removing trailing comma
    funcLoop.deleteCharAt(funcLoop.length() - 1);

    funcLoop.append(")");
    return funcLoop.toString();
  }

  private void processGPUBuffer(PiGraph subGraph, ScapeBuilder build) {

    build.getBuffer().add("// GPU Input Buffer Declaration \n");
    processInputGPUBuffer(subGraph, build);

    build.getBuffer().add("// GPU Broadcast Buffer Declaration \\n");
    processBroadcastGPUBuffer(subGraph, build);

    build.getBuffer().add("// GPU Output Buffer Declaration \n");
    processOutputGPUBuffer(subGraph, build);

  }

  private void processBroadcastGPUBuffer(PiGraph subGraph, ScapeBuilder build) {
    subGraph.getExecutableActors().forEach(actor -> {
      if (actor instanceof BroadcastActor) {
        // Output Ports
        final boolean localMem = true;
        actor.getDataOutputPorts().forEach(outPort -> {
          final DataInputPort inPort = actor.getDataInputPorts().get(0);
          if (inPort.getFifo().getSource() instanceof ActorImpl) {
            String outBuffname = "";
            final Long nbExecOut = outPort.getExpression().evaluate();
            if (outPort.getFifo().getTarget() instanceof final ActorImpl target) {
              final String targetPortName = outPort.getFifo().getTargetPort().getName();
              final String bcOutputPortName = outPort.getName();
              outBuffname = "d_" + actor.getName() + "_" + bcOutputPortName + "__" + target.getName() + "_"
                  + targetPortName;
            }
            if (!outBuffname.equals("")) {

              final String buffer = outPort.getOutgoingFifo().getType() + " *" + outBuffname + " = NULL;";

              final String cudaFreeBuffers = "cudaFree(" + outBuffname + ");\n";

              build.getBuffer().add(buffer);
              build.getBuffer().add(processCudaMallocBuffer(outBuffname, outPort, nbExecOut, localMem));
              build.getFreeBuffer().add(cudaFreeBuffers);
            }
          }
        });
      }
    });
  }

  private void processOutputGPUBuffer(PiGraph subGraph, ScapeBuilder build) {

    final boolean localMem = true;

    // final LinkedHashMap<AbstractActor, Boolean> actorsList = new LinkedHashMap<>();
    //
    // subGraph.getExecutableActors().forEach(actor -> actorsList.put(actor, actor.isOnGPU()));

    subGraph.getExecutableActors().forEach(actor -> actor.getDataOutputPorts().forEach(dout -> {
      String buffname = "";
      final Long nbExec = dout.getExpression().evaluate();
      if (dout.getFifo().getTarget() instanceof final DataOutputInterface dataOutputInterface) {
        buffname = "d_" + dataOutputInterface.getName();
        String cudaToCPU = "";
        if (localMem) {
          cudaToCPU = "cudaMemcpy(" + dataOutputInterface.getName() + ", " + buffname + ", sizeof("
              + dout.getOutgoingFifo().getType() + ") *" + nbExec + ", cudaMemcpyDeviceToHost);\n";
          cudaToCPU += "cudaDeviceSynchronize(); \n";
        } else {
          cudaToCPU = MEMCPY_TEXT + dataOutputInterface.getName() + ", " + buffname + ", sizeof("
              + dout.getOutgoingFifo().getType() + ") *" + nbExec + ");\n";
        }
        build.getOffloadBuffer().add(cudaToCPU);
      } else if (dout.getFifo().isHasADelay()) {
        final Delay delay = dout.getFifo().getDelay();
        buffname = "d_" + delay.getActor().getGetterActor().getName();
      } else if (!(actor instanceof BroadcastActor) && !(actor.getName().equals("single_source"))) {
        buffname = "d_" + actor.getName() + "_" + dout.getName() + "__"
            + ((AbstractVertex) dout.getFifo().getTarget()).getName() + "_" + dout.getFifo().getTargetPort().getName();
      }

      if (!buffname.equals("")) {
        final String buffer = dout.getOutgoingFifo().getType() + " *" + buffname + " = NULL;";

        final String cudaFreeBuffers = "cudaFree(" + buffname + ");\n";

        build.getBuffer().add(buffer);
        build.getBuffer().add(processCudaMallocBuffer(buffname, dout, nbExec, localMem));
        build.getFreeBuffer().add(cudaFreeBuffers);
      }
    }));
  }

  private void processInputGPUBuffer(PiGraph subGraph, ScapeBuilder build) {

    final boolean localMem = true;
    subGraph.getExecutableActors().forEach(actor -> actor.getDataInputPorts().forEach(din -> {
      final Long nbExec = din.getExpression().evaluate();
      String buffname = "";
      if (din.getFifo().getSource() instanceof final DataInputInterface inputInterface
          && !(actor instanceof BroadcastActor)) {
        buffname = "d_" + din.getName();
        // buffname = "d_" + ((AbstractActor) din.getFifo().getTarget()).getName() + "_" + inputInterface.getName();
        // buffname = "d_" + ((AbstractActor) din.getFifo().getTarget()).getName() + "_" + inputInterface.getName();
      } else if (din.getFifo().isHasADelay() && !(actor instanceof BroadcastActor)) {
        final Delay delay = din.getFifo().getDelay();
        buffname = "d_" + delay.getActor().getSetterActor().getName();
      } else if (actor instanceof BroadcastActor
          && din.getFifo().getSource() instanceof final DataInputInterface inputInterface) {
        buffname = "d_" + inputInterface.getName();
      }

      if (!buffname.equals("")) {

        final String buffer = din.getIncomingFifo().getType() + " *" + buffname + " = NULL;";

        final String cudaFreeBuffers = "cudaFree(" + buffname + ");\n";

        build.getBuffer().add(buffer);
        build.getBuffer().add(processCudaMallocBuffer(buffname, din, nbExec, localMem));
        build.getFreeBuffer().add(cudaFreeBuffers);

        if (din.getFifo().getSource() instanceof final DataInputInterface inputInterface) {
          String cudaMemcpyBuffers = "";
          final String src = inputInterface.getName();
          if (localMem) {
            cudaMemcpyBuffers = "cudaMemcpy(" + buffname + ", " + src + ", sizeof(" + din.getIncomingFifo().getType()
                + ") *" + nbExec + ", cudaMemcpyHostToDevice);\n";
          } else {
            cudaMemcpyBuffers = MEMCPY_TEXT + buffname + ", " + src + ", sizeof(" + din.getIncomingFifo().getType()
                + ") *" + nbExec + ");\n";
          }

          build.getBuffer().add(cudaMemcpyBuffers);
        }
      }
    }));
  }

  private String processCudaMallocBuffer(String buffname, DataPort dataPort, Long nbExec, boolean localMem) {
    String cudaMallocBuffers = "";
    if (localMem) {
      cudaMallocBuffers = "cudaMalloc(&" + buffname + ", sizeof(" + dataPort.getFifo().getType() + ") *" + nbExec
          + "); \n";
    } else {
      cudaMallocBuffers = "cudaMallocManaged(&" + buffname + ", sizeof(" + dataPort.getFifo().getType() + ") *" + nbExec
          + ");";
    }
    return cudaMallocBuffers;
  }

  private StringBuilder processBroadcastActorGPU(BroadcastActor brd, int repetition) {

    final StringBuilder actorImplem = new StringBuilder();
    if (!(brd.getDataInputPorts().get(0).getFifo().getSource() instanceof DataInputInterface)
        && !(brd.getDataInputPorts().get(0).getFifo().getSource() instanceof BroadcastActor)) {
      final Long scaleIn = 1L;
      String inBuffName = "";

      if (!(brd.getDataInputPorts().get(0).getFifo().getSource() instanceof final DataInputInterface din)) {
        final String srcActor = ((AbstractVertex) brd.getDataInputPorts().get(0).getFifo().getSource()).getName() + "_"
            + brd.getDataInputPorts().get(0).getFifo().getSourcePort().getName();
        final String snkActor = brd.getName() + "_" + brd.getDataInputPorts().get(0).getName();
        inBuffName = "d_" + srcActor + "__" + snkActor;
      }

      for (final DataOutputPort out : brd.getDataOutputPorts()) {

        Long scaleOut = 1L;
        String outBuffName = "";
        String iterOut = "0";
        String iterIn = "0";

        if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
          outBuffName = dout.getName();
          scaleOut = dout.getDataPort().getExpression().evaluate() / repetition;
        } else {
          final String targetActorName = ((AbstractActor) out.getFifo().getTarget()).getName();
          final String targetActorPortName = out.getFifo().getTargetPort().getName();
          outBuffName = "d_" + out.getContainingActor().getName() + "_" + out.getName() + "__" + targetActorName + "_"
              + targetActorPortName;
        }

        if (repetition > 1) {
          iterOut = " " + INDEX + brd.getName() + "*" + scaleOut;
          iterIn = " " + INDEX + brd.getName() + "*" + scaleIn;
        }

        final Long rate = out.getExpression().evaluate();
        actorImplem.append("cudaMemcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + ","
            + rate + SIZEOF_TEXT + out.getFifo().getType() + ")" + ", cudaMemcpyDeviceToDevice);\n");
      }
    }
    return actorImplem;
  }

  private StringBuilder processActorGPU(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();

    // Compute rates for inputs and outputs
    final Map<String, Long> rateActor = new HashMap<>();
    sc.getActor().getDataInputPorts()
        .forEach(in -> rateActor.put(sc.getActor().getName(), in.getExpression().evaluate()));
    sc.getActor().getDataOutputPorts().forEach(out -> {
      rateActor.put(((AbstractVertex) out.getFifo().getTarget()).getName(), out.getExpression().evaluate());
      if (rateActor.get(sc.getActor().getName()) == null
          || rateActor.get(sc.getActor().getName()) < out.getExpression().evaluate()) {
        rateActor.put(sc.getActor().getName(), out.getExpression().evaluate());
      }
    });

    // Determine block dimensions
    final int blockDim = (int) Math.min(1024, rateActor.get(sc.getActor().getName()));

    // Determine function name
    String funcName = sc.getActor().getName();
    if (((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype() != null) {
      funcName = ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype().getName();
    }

    // Append block size and dimensions
    actorImplem.append(" int block_size_" + sc.getActor().getName() + " = " + blockDim + "; \n");

    actorImplem.append(" dim3 block_dim_" + sc.getActor().getName() + " (block_size_" + sc.getActor().getName() + "); "
        + "\n dim3 grid_dim_" + sc.getActor().getName() + " ((" + rateActor.get(sc.getActor().getName())
        + "+ block_size_" + sc.getActor().getName() + " - 1 ) / block_size_" + sc.getActor().getName() + "); \n");

    actorImplem.append(
        funcName + "<<<grid_dim_" + sc.getActor().getName() + ", block_dim_" + sc.getActor().getName() + ">>>(");

    // Append loop prototype arguments
    final FunctionPrototype loopPrototype = ((Actor) sc.getActor())
        .getRefinement() instanceof final CHeaderRefinement cheaderrefinement ? cheaderrefinement.getLoopPrototype()
            : null;
    if (loopPrototype != null) {
      loopPrototype.getInputConfigParameters().forEach(arg -> {
        actorImplem.append(arg.getName() + ",");
      });
    }

    // Append input buffer names
    sc.getActor().getDataInputPorts().forEach(in -> {
      String buffname = "";

      if (in.getFifo().getSource() instanceof final DataInputInterface
          & !(in.getFifo().getSource() instanceof final BroadcastActor)) {
        buffname = "d_" + in.getName();
      } else if (in.getFifo().isHasADelay()) {
        final Delay delay = in.getFifo().getDelay();
        buffname = "d_" + delay.getActor().getSetterActor().getName();
      } else if (in.getFifo().getSource() instanceof final BroadcastActor bc) {
        if (bc.getDataInputPorts().get(0).getFifo().getSource() instanceof final DataInputInterface inbc) {
          buffname = "d_" + inbc.getName();
        } else if (bc.getDataInputPorts().get(0).getFifo().getSource() instanceof final BroadcastActor intbc) {
          buffname = "d_" + ((DataInputInterface) intbc.getDataInputPorts().get(0).getFifo().getSource()).getName();
        } else {
          buffname = "d_" + bc.getName() + "_" + in.getFifo().getSourcePort().getName() + "__" + sc.getActor().getName()
              + "_" + in.getName();
        }

      } else {
        buffname = "d_" + ((AbstractVertex) in.getFifo().getSource()).getName() + "_"
            + in.getFifo().getSourcePort().getName() + "__" + sc.getActor().getName() + "_" + in.getName();
      }
      actorImplem.append(buffname + ",");
    });

    // Append output buffer names
    sc.getActor().getDataOutputPorts().forEach(out -> {
      String buffname = "";
      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        buffname = "d_" + dout.getName();
      } else if (out.getFifo().isHasADelay()) {
        final Delay delay = out.getFifo().getDelay();
        buffname = "d_" + delay.getActor().getGetterActor().getName();
      } else {
        buffname = "d_" + sc.getActor().getName() + "_" + out.getName() + "__"
            + ((AbstractVertex) out.getFifo().getTarget()).getName() + "_" + out.getFifo().getTargetPort().getName();
      }
      actorImplem.append(buffname + ",");
    });

    actorImplem.deleteCharAt(actorImplem.length() - 1);
    actorImplem.append("); \n\n");
    return actorImplem;
  }

}
