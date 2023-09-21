package org.preesm.algorithm.clustering.scape;

import java.util.List;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.schedule.model.ScapeBuilder;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
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
        if (!(dout.getOutgoingFifo().getTarget() instanceof DataOutputInterface)) {
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
      if (sc.isBeginLoop()) {
        final String bodyLine = "for(int index = 0; index <" + sc.getIterator() + ";index++){\n";
        body.append(bodyLine, "");

      }
      final StringConcatenation actor = new StringConcatenation();
      actor.append(sc.getActor().getName() + "(");
      final int nbArg = sc.getActor().getConfigInputPorts().size() + sc.getActor().getDataInputPorts().size()
          + sc.getActor().getDataOutputPorts().size();
      int countArg = 1;
      for (final ConfigInputPort cfg : sc.getActor().getConfigInputPorts()) {
        actor.append(((AbstractVertex) cfg.getIncomingDependency().getSource()).getName());
        if (countArg < nbArg) {
          actor.append(",");
        }
        countArg++;
      }
      for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
        actor.append(in.getName());
        if (countArg < nbArg) {
          actor.append(",");
        }
        countArg++;
      }
      for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
        actor.append(out.getName());
        if (countArg < nbArg) {
          actor.append(",");
        }
        countArg++;
      }
      actor.append(");");
      body.append(actor, "");
      for (int i = 0; i < sc.getEndLoopNb(); i++) {
        body.append("\n }");
      }
    }
    return body.toString();
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
      funcLoop.append(input.getDataPort().getFifo().getType() + " " + input.getName());
      if (i < nbArg) {
        funcLoop.append(",");
      }
      i++;
    }
    for (final DataOutputInterface output : subGraph.getDataOutputInterfaces()) {
      funcLoop.append(output.getDataPort().getFifo().getType() + " " + output.getName());
      if (i < nbArg) {
        funcLoop.append(",");
      }
      i++;
    }
    funcLoop.append(")");
    return funcLoop.toString();
  }

}
