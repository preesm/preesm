/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2025) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2025)
 * Ophelie-Renaud [ophelie.renaud@insa-rennes.fr] (2025)
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

package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * This class fill the clustering SCAPE structure
 *
 * @author orenaud
 * @author emichel
 *
 */
public class CodegenScapeBuilder {

  private static final String INDEX       = "index";
  private static final String SIZEOF_TEXT = "sizeof(";
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
    processCPUBuffer(subGraph, build, stackSize);

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

        if (!(dout.getOutgoingFifo().getTarget() instanceof DataOutputInterface) && !dout.getFifo().isDelayPresent()
            && !dout.getContainingActor().getName().equals("single_source")) {

          final String buffName = dout.getContainingActor().getName() + "_" + dout.getName() + "__"
              + dout.getFifo().getTarget().getName() + "_" + dout.getFifo().getTargetPort().getName();
          final Long nbToken = dout.getExpression().evaluateAsLong() * brv.get(dout.getContainingActor());
          if (count < stackSize) {
            buff = dout.getOutgoingFifo().getType() + " " + buffName + "[" + nbToken + "];\n";
          } else {
            buff = dout.getOutgoingFifo().getType() + " *" + buffName + " = (" + dout.getOutgoingFifo().getType()
                + " *)malloc(" + nbToken + "*" + SIZEOF_TEXT + dout.getOutgoingFifo().getType() + "));\n";
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

    cs.forEach(sc -> {
      // Skip if the actor's name is empty
      if (sc.getActor().getName().isEmpty()) {
        return;
      }

      // Add a loop structure if the schedule step begins a loop
      processLoopStart(subGraph, sc, body);

      final StringBuilder actor = new StringBuilder();
      String memcpy = "";

      // Process regular actors
      if (sc.getActor() instanceof Actor && !processedActors.containsValue(sc.getActor())) {
        actor.append(processActorCPU(sc));

        memcpy = processClusteredDelay(sc);

      }

      // Process special actors
      processSpecialActorBody(sc, subGraph, actor);

      body.append(actor);
      body.append(memcpy);

      // Close the loop structure
      closeLoops(subGraph, sc, body);

      processedActors.put(sc.getActor().getName(), sc.getActor());

    });

    return body.toString();
  }

  private void processLoopStart(PiGraph subGraph, ScapeSchedule sc, StringBuilder body) {
    if (sc.isBeginLoop()) {
      body.append("for(int index" + sc.getActor().getName() + " = 0; index" + sc.getActor().getName() + " < "
          + sc.getRepetition() + "; index" + sc.getActor().getName() + "++){\n");
    }
  }

  private void processSpecialActorBody(ScapeSchedule sc, PiGraph subGraph, StringBuilder actor) {
    if (sc.getActor() instanceof SpecialActor) {
      actor.append(processSpecialActor(sc));
    }
  }

  private void closeLoops(PiGraph subGraph, ScapeSchedule sc, StringBuilder body) {
    IntStream.range(0, sc.getEndLoopNb()).forEach(i -> body.append("\n}"));
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
      scaleIn = din.getDataPort().getExpression().evaluateAsLong() / repetition;
      inBuffName = din.getName();
    } else {
      final String srcActor = brd.getDataInputPorts().get(0).getFifo().getSource().getName() + "_"
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
        scaleOut = dout.getDataPort().getExpression().evaluateAsLong() / repetition;
      } else {
        final String targetActorName = out.getFifo().getTarget().getName();
        final String targetActorPortName = out.getFifo().getTargetPort().getName();
        outBuffName = out.getContainingActor().getName() + "_" + out.getName() + "__" + targetActorName + "_"
            + targetActorPortName;
      }

      if (repetition > 1) {
        iterOut = " " + INDEX + brd.getName() + "*" + scaleOut;
        iterIn = " " + INDEX + brd.getName() + "*" + scaleIn;
      }

      final Long rate = out.getExpression().evaluateAsLong();
      actorImplem.append(MEMCPY_TEXT + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + "*" + SIZEOF_TEXT + out.getFifo().getType() + ")" + ");\n");
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
      scaleIn = din.getDataPort().getExpression().evaluateAsLong() / repetition;
      inBuffName = din.getName();
    } else {
      final String srcActor = frk.getDataInputPorts().get(0).getFifo().getSource().getName() + "_"
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
        scaleOut = dout.getDataPort().getExpression().evaluateAsLong() / repetition;
      } else {
        outBuffName = out.getName();
      }
      iterIn = String.valueOf(ret);
      if (repetition > 1) {
        iterOut = " " + INDEX + frk.getName() + "*" + scaleOut;
        iterIn = " " + INDEX + frk.getName() + "*" + scaleIn + ret;

      }
      final Long rate = out.getExpression().evaluateAsLong();
      actorImplem.append(MEMCPY_TEXT + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + "*" + SIZEOF_TEXT + out.getFifo().getType() + ")" + ");\n");
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
      scaleIn = dout.getDataPort().getExpression().evaluateAsLong() / repetition;
      outBuffName = dout.getName();
    } else {
      final String srcActor = join.getName() + "_" + join.getDataOutputPorts().get(0).getName();
      final String snkActor = join.getDataOutputPorts().get(0).getFifo().getTarget().getName() + "_"
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
        scaleIn = din.getDataPort().getExpression().evaluateAsLong() / repetition;
      } else {
        inBuffName = in.getFifo().getSource().getName() + "_" + in.getFifo().getSourcePort().getName() + "__"
            + join.getName() + "_" + in.getName();
      }

      iterOut = String.valueOf(ret);
      if (repetition > 1) {
        iterOut = " " + INDEX + join.getName() + "*" + scaleOut + ret;
        iterIn = " " + INDEX + join.getName() + "*" + scaleIn;
      }
      final Long rate = in.getExpression().evaluateAsLong();
      actorImplem.append(MEMCPY_TEXT + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + "*" + SIZEOF_TEXT + in.getFifo().getType() + ")" + ");\n");
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
      if (out.getFifo().isDelayPresent() && out.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        final Delay delay = out.getFifo().getDelay();
        memcpy.append(MEMCPY_TEXT + delay.getDelayActor().getSetterActor().getName() + ","
            + delay.getDelayActor().getGetterActor().getName() + ","
            + out.getFifo().getDelay().getExpression().evaluateAsLong() + ");\n");
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

    for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
      String buffname = "";

      Long scale = 1L;

      if (in.getFifo().getSource() instanceof final DataInputInterface din) {
        scale = din.getDataPort().getExpression().evaluateAsLong() / sc.getRepetition();
        buffname += din.getName();
      } else if (in.getFifo().isDelayPresent() && in.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        final Delay delay = in.getFifo().getDelay();
        buffname += delay.getDelayActor().getSetterActor().getName();
      } else {

        buffname += in.getFifo().getSource().getName() + "_" + in.getFifo().getSourcePort().getName() + "__"
            + sc.getActor().getName() + "_" + in.getName();

      }

      if ((sc.isLoopPrec() || sc.isBeginLoop() || sc.isEndLoop())) {
        buffname += " + index" + sc.getActor().getName() + "*" + scale;
      }

      actorImplem.append(buffname);
      actorImplem.append(",");
    }

    return actorImplem;
  }

  private StringBuilder processActorDataOutputPorts(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();

    sc.getActor().getDataOutputPorts().stream().forEach(out -> {
      String buffname = "";

      Long scale = 1L;
      // Handle DataOutputInterface
      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        scale = dout.getDataPort().getExpression().evaluateAsLong() / sc.getRepetition();
        buffname += dout.getName();

        // Handle Delays
      } else if (out.getFifo().isDelayPresent() && out.getFifo().getDelay().getLevel().equals(PersistenceLevel.NONE)) {
        final Delay delay = out.getFifo().getDelay();
        buffname += delay.getDelayActor().getGetterActor().getName();

        // Default case
      } else {
        buffname += sc.getActor().getName() + "_" + out.getName() + "__" + out.getFifo().getTarget().getName() + "_"
            + out.getFifo().getTargetPort().getName();
      }

      // Adjust for loop presence
      if (sc.isLoopPrec() || sc.isBeginLoop() || sc.isEndLoop()) {
        buffname += " + index" + sc.getActor().getName() + "*" + scale;
      }

      actorImplem.append(buffname);
      actorImplem.append(",");
    });

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

}
