package org.preesm.algorithm.clustering.scape;

import java.util.List;
import java.util.Map;
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
  public CodegenScapeBuilder(ScapeBuilder build, List<ScapeSchedule> cs, PiGraph subGraph, Long stackSize) {
    final Map<AbstractVertex, Long> brv = PiBRV.compute(subGraph, BRVMethod.LCM);
    // build initial function
    final String funcI = " void " + subGraph.getName() + "Init()";
    build.setInitFunc(funcI);
    // build loop function
    final String funcL = loopFunction(subGraph);
    build.setLoopFunc(funcL);
    // build buffer
    Long count = 0L;
    for (final AbstractActor actor : subGraph.getExecutableActors()) {
      for (final DataOutputPort dout : actor.getDataOutputPorts()) {
        String buff = "";
        if (!(dout.getOutgoingFifo().getTarget() instanceof DataOutputInterface) && !dout.getFifo().isHasADelay()) {
          final String buffName = dout.getContainingActor().getName() + "_" + dout.getName() + "__"
              + ((AbstractVertex) dout.getFifo().getTarget()).getName() + "_"
              + dout.getFifo().getTargetPort().getName();
          final Long nbToken = dout.getExpression().evaluate() * brv.get(dout.getContainingActor());
          if (count < stackSize) {
            buff = dout.getOutgoingFifo().getType() + " " + buffName + "[" + nbToken + "];\n";
          } else {
            buff = dout.getOutgoingFifo().getType() + " *" + buffName + " = (" + dout.getOutgoingFifo().getType()
                + " *)malloc(" + nbToken + "*sizeof(" + dout.getOutgoingFifo().getType() + "));\n";
            build.getDynmicBuffer().add(buffName);
          }
          count += nbToken;
          build.getBuffer().add(buff);
        }
      }
    }
    // build body
    final String body = bodyFunction(cs);
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
    final StringConcatenation body = new StringConcatenation();
    for (final ScapeSchedule sc : cs) {

      if (!sc.getActor().getName().isEmpty()) {
        if (sc.isBeginLoop()) {
          final String bodyLine = "for(int index" + sc.getActor().getName() + " = 0; index" + sc.getActor().getName()
              + "<" + sc.getIterator() + ";index" + sc.getActor().getName() + "++){\n";
          body.append(bodyLine, "");

        }

        StringConcatenation actor = new StringConcatenation();
        String memcpy = "";

        if (sc.getActor() instanceof Actor) {
          actor = processActor(sc);
          memcpy = processClusteredDelay(sc);
        }
        if (sc.getActor() instanceof SpecialActor) {
          actor = processSpecialActor(sc);
        }

        body.append(actor, "");
        body.append(memcpy);
        for (int i = 0; i < sc.getEndLoopNb(); i++) {
          body.append("\n }");
        }
      }
    }
    return body.toString();
  }

  private StringConcatenation processSpecialActor(ScapeSchedule sc) {
    final StringConcatenation actorImplem = new StringConcatenation();
    Long scaleOut = 1L;
    Long scaleIn = 1L;
    String inBuffName = "";
    String outBuffName = "";
    String iterOut = "0";
    String iterIn = "0";
    actorImplem.append("//" + sc.getActor().getName() + "\n");

    if (sc.getActor() instanceof final BroadcastActor brd) {

      if (sc.getActor().getDataInputPorts().get(0).getFifo().getSource() instanceof final DataInputInterface din) {
        scaleIn = din.getDataPort().getExpression().evaluate() / sc.getIterator();
        inBuffName = din.getName();
      } else {
        final String srcActor = ((AbstractVertex) sc.getActor().getDataInputPorts().get(0).getFifo().getSource())
            .getName() + "_" + sc.getActor().getDataInputPorts().get(0).getFifo().getSourcePort().getName();
        final String snkActor = sc.getActor().getName() + "_" + sc.getActor().getDataInputPorts().get(0).getName();
        inBuffName = srcActor + "__" + snkActor;
      }

      for (final DataOutputPort out : brd.getDataOutputPorts()) {

        if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
          outBuffName = dout.getName();
          scaleOut = dout.getDataPort().getExpression().evaluate() / sc.getIterator();
        } else {
          outBuffName = out.getName();
        }
        if (sc.getIterator() > 1) {
          iterOut = " index" + sc.getActor().getName() + "*" + scaleOut;
          iterIn = " index" + sc.getActor().getName() + "*" + scaleIn;

        }
        final Long rate = out.getExpression().evaluate();
        actorImplem.append("memcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
            + "*sizeof(" + out.getFifo().getType() + ")" + ");\n");
      }
    }

    if (sc.getActor() instanceof final ForkActor frk) {
      if (sc.getActor().getDataInputPorts().get(0).getFifo().getSource() instanceof final DataInputInterface din) {
        scaleIn = din.getDataPort().getExpression().evaluate() / sc.getIterator();
        inBuffName = din.getName();
      } else {
        final String srcActor = ((AbstractVertex) sc.getActor().getDataInputPorts().get(0).getFifo().getSource())
            .getName() + "_" + sc.getActor().getDataInputPorts().get(0).getFifo().getSourcePort().getName();
        final String snkActor = sc.getActor().getName() + "_" + sc.getActor().getDataInputPorts().get(0).getName();
        inBuffName = srcActor + "__" + snkActor;
      }
      int ret = 0;
      for (final DataOutputPort out : frk.getDataOutputPorts()) {

        if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
          outBuffName = dout.getName();
          scaleOut = dout.getDataPort().getExpression().evaluate() / sc.getIterator();
        } else {
          outBuffName = out.getName();
        }
        iterIn = String.valueOf(ret);
        if (sc.getIterator() > 1) {
          iterOut = " index" + sc.getActor().getName() + "*" + scaleOut;
          iterIn = " index" + sc.getActor().getName() + "*" + scaleIn + ret;

        }
        final Long rate = out.getExpression().evaluate();
        actorImplem.append("memcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
            + "*sizeof(" + out.getFifo().getType() + ")" + ");\n");
        ret += rate;
      }

    }

    if (sc.getActor() instanceof final JoinActor jn) {
      if (sc.getActor().getDataOutputPorts().get(0).getFifo().getTarget() instanceof final DataOutputInterface dout) {
        scaleIn = dout.getDataPort().getExpression().evaluate() / sc.getIterator();
        outBuffName = dout.getName();
      } else {
        final String srcActor = sc.getActor().getName() + "_" + sc.getActor().getDataOutputPorts().get(0).getName();
        final String snkActor = ((AbstractVertex) sc.getActor().getDataOutputPorts().get(0).getFifo().getTarget())
            .getName() + "_" + sc.getActor().getDataOutputPorts().get(0).getFifo().getTargetPort().getName();
        outBuffName = srcActor + "__" + snkActor;
      }
      int ret = 0;
      for (final DataInputPort in : jn.getDataInputPorts()) {

        if (in.getFifo().getSource() instanceof final DataInputInterface din) {
          inBuffName = din.getName();
          scaleIn = din.getDataPort().getExpression().evaluate() / sc.getIterator();
        } else {

          inBuffName = ((AbstractVertex) in.getFifo().getSource()).getName() + "_"
              + in.getFifo().getSourcePort().getName() + "__" + sc.getActor() + "_" + in.getName();
        }
        iterOut = String.valueOf(ret);
        if (sc.getIterator() > 1) {
          iterOut = " index" + sc.getActor().getName() + "*" + scaleOut + ret;
          iterIn = " index" + sc.getActor().getName() + "*" + scaleIn;
        }
        final Long rate = in.getExpression().evaluate();
        actorImplem.append("memcpy(" + outBuffName + " + " + iterOut + "," + inBuffName + " + " + iterIn + "," + rate
            + "*sizeof(" + in.getFifo().getType() + ")" + ");\n");
        ret += rate;
      }

    }

    return actorImplem;
  }

  private String processClusteredDelay(ScapeSchedule sc) {
    String memcpy = "";
    for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
      if (out.getFifo().isHasADelay()) {
        final Delay delay = out.getFifo().getDelay();
        memcpy += "memcpy(" + delay.getActor().getSetterActor().getName() + ","
            + delay.getActor().getGetterActor().getName() + "," + out.getFifo().getDelay().getExpression().evaluate()
            + "); \n";

      }
    }
    return memcpy;
  }

  private StringConcatenation processActor(ScapeSchedule sc) {
    Long scale = 1L;
    final StringConcatenation actorImplem = new StringConcatenation();
    final String funcName = ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getLoopPrototype().getName();

    actorImplem.append(funcName + "(");
    final int nbArg = sc.getActor().getConfigInputPorts().size() + sc.getActor().getDataInputPorts().size()
        + sc.getActor().getDataOutputPorts().size();
    int countArg = 1;
    for (final ConfigInputPort cfg : sc.getActor().getConfigInputPorts()) {
      actorImplem.append(((AbstractVertex) cfg.getIncomingDependency().getSource()).getName());
      if (countArg < nbArg) {
        actorImplem.append(",");
      }
      countArg++;
    }
    for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
      String buffname = "";
      scale = 1L;
      if (in.getFifo().getSource() instanceof final DataInputInterface din) {
        scale = din.getDataPort().getExpression().evaluate() / sc.getIterator();
        buffname = din.getName();
      } else if (in.getFifo().isHasADelay()) {
        final Delay delay = in.getFifo().getDelay();
        buffname = delay.getActor().getSetterActor().getName();
      } else {

        buffname = ((AbstractVertex) in.getFifo().getSource()).getName() + "_" + in.getFifo().getSourcePort().getName()
            + "__" + sc.getActor() + "_" + in.getName();
      }
      if (sc.isLoopPrec() || sc.isBeginLoop() || sc.isEndLoop()) {

        buffname += " + index" + sc.getActor().getName() + "*" + scale;
      }
      actorImplem.append(buffname);
      if (countArg < nbArg) {
        actorImplem.append(",");
      }
      countArg++;
    }
    for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
      String buffname = "";
      if (out.getFifo().getTarget() instanceof final DataOutputInterface dout) {
        scale = dout.getDataPort().getExpression().evaluate() / sc.getIterator();
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
      if (countArg < nbArg) {
        actorImplem.append(",");
      }
      countArg++;

    }
    actorImplem.append("); \n");
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
    final StringConcatenation funcLoop = new StringConcatenation();
    funcLoop.append("void " + subGraph.getName() + "(", "");
    final int nbArg = subGraph.getParameters().size() + subGraph.getDataInputInterfaces().size()
        + subGraph.getDataOutputInterfaces().size();
    int i = 1;
    for (final Parameter param : subGraph.getParameters()) {
      funcLoop.append("int " + param.getName());
      if (i < nbArg) {
        funcLoop.append(",");
      }
      i++;
    }
    for (final DataInputInterface input : subGraph.getDataInputInterfaces()) {
      funcLoop.append(input.getDataPort().getFifo().getType() + " *" + input.getName());
      if (i < nbArg) {
        funcLoop.append(",");
      }
      i++;
    }
    for (final DataOutputInterface output : subGraph.getDataOutputInterfaces()) {
      funcLoop.append(output.getDataPort().getFifo().getType() + " *" + output.getName());
      if (i < nbArg) {
        funcLoop.append(",");
      }
      i++;
    }
    funcLoop.append(")");
    return funcLoop.toString();
  }

}
