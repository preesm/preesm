package org.preesm.algorithm.clustering.scape;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.schedule.model.ScapeBuilder;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;

/**
 * This class fill the clustering SCAPE structure
 *
 * @author orenaud
 *
 */
public class CodegenScapeBuilder {

  private static final String INDEX       = "index";
  private static final String SIZEOF_TEXT = "*sizeof(";

  public CodegenScapeBuilder(ScapeBuilder build, List<ScapeSchedule> cs, PiGraph subGraph, Long stackSize) {
    final Map<AbstractVertex, Long> brv = PiBRV.compute(subGraph, BRVMethod.LCM);
    // build initial function
    String funcI = " void " + subGraph.getName() + "Init()";
    if (subGraph.getContainingPiGraph().getName().contains("sub")) {
      funcI = " void " + "Cluster_" + subGraph.getContainingPiGraph().getName() + "_" + subGraph.getName() + "Init()";
    }
    build.setInitFunc(funcI);
    // build loop function
    final String funcL = loopFunction(subGraph);
    build.setLoopFunc(funcL);

    // build buffer
    Long count = 0L;

    final Set<String> GPUFree = new LinkedHashSet<>();
    boolean synchro = false;

    for (final AbstractActor actor : subGraph.getExecutableActors()) {
      for (final DataOutputPort dout : actor.getDataOutputPorts()) {
        String buff = "";
        if (!(dout.getOutgoingFifo().getTarget() instanceof DataOutputInterface) && !dout.getFifo().isHasADelay()) {
          // Classic CPU
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

          // GPU test
          // dout.getContainingActor().getName() = vectorAdd
          // + "_"
          // + dout.getName() // name of output port : c
          // + "__"
          // +((AbstractVertex) dout.getFifo().getTarget()).getName() // name of other func : displayvect
          // + "_"
          // + dout.getFifo().getTargetPort().getName(); // same as above
          final Set<String> buffGPU = new LinkedHashSet<>();

          for (final ScapeSchedule sc : cs) {
            if (sc.isOnGPU()) {
              final int nbExec = 10; // TODO
              for (final DataInputPort in : actor.getDataInputPorts()) {

                String buffname = "";
                if (in.getFifo().getSource() instanceof final DataInputInterface din) {
                  buffname = "d_" + in.getName();
                } else if (in.getFifo().isHasADelay()) {
                  final Delay delay = in.getFifo().getDelay();
                  buffname = "d_" + delay.getActor().getSetterActor().getName();
                } else {

                  buffname = "d_" + ((AbstractVertex) in.getFifo().getSource()).getName() + "_"
                      + in.getFifo().getSourcePort().getName() + "__" + sc.getActor().getName() + "_" + in.getName();
                }

                final String buffer = in.getIncomingFifo().getType() + " *" + buffname + " = NULL;";
                final String cudaMallocBuffers = "cudaMalloc(&" + buffname + ", sizeof("
                    + in.getIncomingFifo().getType() + ") *" + nbExec + ");";
                final String cudaFreeBuffers = "cudaFree(" + buffname + ");\n";

                buffGPU.add(buffer);
                buffGPU.add(cudaMallocBuffers);
                GPUFree.add(cudaFreeBuffers);

                final String cudaMemcpyBuffers = "cudaMemcpy(" + buffname + ", " + in.getName() + ", sizeof("
                    + in.getIncomingFifo().getType() + ") *" + nbExec + ", cudaMemcpyHostToDevice);\n";

                buffGPU.add(cudaMemcpyBuffers);
                synchro = true;
              }

              for (final DataOutputPort out2 : sc.getActor().getDataOutputPorts()) {
                String buffname = "";
                if (out2.getFifo().getTarget() instanceof final DataOutputInterface dout2) {
                  buffname = "d_" + dout2.getName();
                } else if (out2.getFifo().isHasADelay()) {
                  final Delay delay = out2.getFifo().getDelay();
                  buffname = "d_" + delay.getActor().getGetterActor().getName();
                } else {
                  buffname = "d_" + sc.getActor().getName() + "_" + out2.getName() + "__"
                      + ((AbstractVertex) out2.getFifo().getTarget()).getName() + "_"
                      + out2.getFifo().getTargetPort().getName();
                }

                final String buffer = dout.getOutgoingFifo().getType() + " *" + buffname + " = NULL;";
                final String cudaMallocBuffers = "cudaMalloc(&" + buffname + ", sizeof("
                    + dout.getOutgoingFifo().getType() + ") *" + nbExec + ");";
                final String cudaFreeBuffers = "cudaFree(" + buffname + ");\n";

                buffGPU.add(buffer);
                buffGPU.add(cudaMallocBuffers);
                GPUFree.add(cudaFreeBuffers);
                synchro = true;
              }
            }
          }

          for (final String i : buffGPU) {
            build.getBuffer().add(i);

          }

          build.getBuffer().add(buff);
        }
      }
    }

    if (synchro) {
      build.getBuffer().add("cudaDeviceSynchronize();");
    }

    // build body
    String body = bodyFunction(cs);

    for (final String free : GPUFree) {
      body += free;
    }

    build.setBody(body);
  }

  /**
   * Translate the schedule firing of actor into string C code.
   *
   * @param cs
   *          Schedule structure of the cluster
   * @return The string content of the bodyFunction.
   */
  private String bodyFunction(List<ScapeSchedule> cs) {

    final StringConcatenation bodyGPU = new StringConcatenation();
    boolean synchro = false;

    final StringBuilder body = new StringBuilder();

    for (final ScapeSchedule sc : cs) {

      if (!sc.getActor().getName().isEmpty()) {

        if (sc.isOnGPU()) {

          final StringConcatenation actor = new StringConcatenation();
          final int nbArg = sc.getActor().getConfigInputPorts().size() + sc.getActor().getDataInputPorts().size()
              + sc.getActor().getDataOutputPorts().size();
          int countArg = 1;

          final int blockDim = 1;
          final int gridDim = 10;
          actor.append(sc.getActor().getName() + "<<<" + blockDim + ", " + gridDim + ">>>(");

          for (final ConfigInputPort cfg : sc.getActor().getConfigInputPorts()) {
            actor.append(((AbstractVertex) cfg.getIncomingDependency().getSource()).getName());
            if (countArg < nbArg) {
              actor.append(",");
            }
            countArg++;
          }

          for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
            String buffname = "";
            if (in.getFifo().getSource() instanceof final DataInputInterface din) {

              buffname = "d_" + in.getName();
            } else if (in.getFifo().isHasADelay()) {
              final Delay delay = in.getFifo().getDelay();
              buffname = "d_" + delay.getActor().getSetterActor().getName();
            } else {

              buffname = "d_" + ((AbstractVertex) in.getFifo().getSource()).getName() + "_"
                  + in.getFifo().getSourcePort().getName() + "__" + sc.getActor().getName() + "_" + in.getName();
            }
            actor.append(buffname);
            if (countArg < nbArg) {
              actor.append(",");
            }
            countArg++;
          }
          String memcpy = "";
          for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
            String buffname = "";
            if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
              buffname = "d_" + dout.getName();
            } else if (out.getFifo().isHasADelay()) {
              final Delay delay = out.getFifo().getDelay();
              buffname = "d_" + delay.getActor().getGetterActor().getName();
            } else {
              buffname = "d_" + sc.getActor().getName() + "_" + out.getName() + "__"
                  + ((AbstractVertex) out.getFifo().getTarget()).getName() + "_"
                  + out.getFifo().getTargetPort().getName();
            }
            actor.append(buffname);
            if (countArg < nbArg) {
              actor.append(",");
            }
            countArg++;
            if (out.getFifo().isHasADelay()) {
              final Delay delay = out.getFifo().getDelay();
              memcpy += "memcpy(" + delay.getActor().getSetterActor().getName() + ","
                  + delay.getActor().getGetterActor().getName() + ","
                  + out.getFifo().getDelay().getExpression().evaluate() + "); \n";

            }
          }
          actor.append("); \n");
          bodyGPU.append(actor, "");
          bodyGPU.append(memcpy);
          synchro = true;

        } else {
          if (sc.isBeginLoop()) {
            final String bodyLine = "for(int index" + sc.getActor().getName() + " = 0; index" + sc.getActor().getName()
                + "<" + sc.getRepetition() + ";index" + sc.getActor().getName() + "++){\n";
            body.append(bodyLine);

          }

          StringBuilder actor = new StringBuilder();
          String memcpy = "";

          if (sc.getActor() instanceof Actor) {
            actor = processActor(sc);
            memcpy = processClusteredDelay(sc);
          }
          if (sc.getActor() instanceof SpecialActor) {
            actor = processSpecialActor(sc);
          }

          body.append(actor);
          body.append(memcpy);
          for (int i = 0; i < sc.getEndLoopNb(); i++) {
            body.append("\n }");
          }
        }
      }
      if (synchro) {
        body.append("cudaDeviceSynchronize(); \n");
        return bodyGPU.toString();
      }

      return body.toString();
    }
  }

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
        outBuffName = out.getName();
      }

      if (repetition > 1) {
        iterOut = " " + INDEX + brd.getName() + "*" + scaleOut;
        iterIn = " " + INDEX + brd.getName() + "*" + scaleIn;
      }

      final Long rate = out.getExpression().evaluate();
      actorImplem.append("memcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + SIZEOF_TEXT + out.getFifo().getType() + ")" + ");\n");
    }

    return actorImplem;
  }

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
      actorImplem.append("memcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + SIZEOF_TEXT + out.getFifo().getType() + ")" + ");\n");
      ret += rate;
    }

    return actorImplem;
  }

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
            + in.getFifo().getSourcePort().getName() + "__" + join + "_" + in.getName();
      }

      iterOut = String.valueOf(ret);
      if (repetition > 1) {
        iterOut = " " + INDEX + join.getName() + "*" + scaleOut + ret;
        iterIn = " " + INDEX + join.getName() + "*" + scaleIn;
      }
      final Long rate = in.getExpression().evaluate();
      actorImplem.append("memcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
          + SIZEOF_TEXT + in.getFifo().getType() + ")" + ");\n");
      ret += rate;
    }

    return actorImplem;
  }

  private String processClusteredDelay(ScapeSchedule sc) {
    final StringBuilder memcpy = new StringBuilder();
    for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
      if (out.getFifo().isHasADelay()) {
        final Delay delay = out.getFifo().getDelay();
        memcpy.append(
            "memcpy(" + delay.getActor().getSetterActor().getName() + "," + delay.getActor().getGetterActor().getName()
                + "," + out.getFifo().getDelay().getExpression().evaluate() + ");\n");
      }
    }

    return memcpy.toString();
  }

  private StringBuilder processActor(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();
    String funcName = sc.getActor().getName();
    FunctionPrototype loopPrototype = null;
    if (((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype() != null) {
      loopPrototype = ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype();
      funcName = ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype().getName();
    }

    actorImplem.append(funcName + "(");

    final int nbArg = sc.getActor().getConfigInputPorts().size() + sc.getActor().getDataInputPorts().size()
        + sc.getActor().getDataOutputPorts().size();

    if (nbArg == 0) {
      actorImplem.append(");\n");
      return actorImplem;
    }

    if (loopPrototype != null) {
      for (final FunctionArgument arg : loopPrototype.getInputConfigParameters()) {
        actorImplem.append(arg.getName());
        actorImplem.append(",");
      }
    }

    actorImplem.append(processActorDataInputPorts(sc));
    actorImplem.append(processActorDataOutputPorts(sc));

    actorImplem.deleteCharAt(actorImplem.length() - 1);

    actorImplem.append(");\n");
    return actorImplem;
  }

  private StringBuilder processActorDataInputPorts(ScapeSchedule sc) {

    final StringBuilder actorImplem = new StringBuilder();

    for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
      String buffname = "";
      Long scale = 1L;

      if (in.getFifo().getSource() instanceof final DataInputInterface din) {
        scale = din.getDataPort().getExpression().evaluate() / sc.getRepetition();
        buffname = din.getName();
      } else if (in.getFifo().isHasADelay()) {
        final Delay delay = in.getFifo().getDelay();
        buffname = delay.getActor().getSetterActor().getName();
      } else {
        buffname = ((AbstractVertex) in.getFifo().getSource()).getName() + "_" + in.getFifo().getSourcePort().getName()
            + "__" + sc.getActor().getName() + "_" + in.getName();
      }

      if (sc.isLoopPrec() || sc.isBeginLoop() || sc.isEndLoop()) {
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
      Long scale = 1L;

      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        scale = dout.getDataPort().getExpression().evaluate() / sc.getRepetition();
        buffname = dout.getName();
      } else if (out.getFifo().isHasADelay()) {
        final Delay delay = out.getFifo().getDelay();
        buffname = delay.getActor().getGetterActor().getName();
      } else {
        buffname = sc.getActor().getName() + "_" + out.getName() + "__"
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
    if (!subGraph.getContainingPiGraph().getName().contains("sub")) {
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
