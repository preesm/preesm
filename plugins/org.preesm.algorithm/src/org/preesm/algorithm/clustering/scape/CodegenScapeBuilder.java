package org.preesm.algorithm.clustering.scape;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.schedule.model.ScapeBuilder;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
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
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class CodegenScapeBuilder {
  public CodegenScapeBuilder(ScapeBuilder build, String clusterSchedule, PiGraph subGraph, Long stackSize) {
    final Map<AbstractVertex, Long> brv = PiBRV.compute(subGraph, BRVMethod.LCM);
    // build initial function
    final String funcI = " void " + subGraph.getName() + "Init(";
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
    final List<ScapeSchedule> cs = schedule(clusterSchedule, subGraph);
    final String body = bodyFunction(cs);
    build.setBody(body);

  }

  private String bodyFunction(List<ScapeSchedule> cs) {
    final StringConcatenation body = new StringConcatenation();
    for (final ScapeSchedule sc : cs) {
      if (sc.isBeginLoop()) {
        final String bodyLine = "for(int index = 0; index <" + sc.getIterator() + ";index++){\n";
        body.append(bodyLine, "");

      }
      final StringConcatenation actor = new StringConcatenation();
      actor.append(sc.getActor().getName() + "(");
      for (final ConfigInputPort cfg : sc.getActor().getConfigInputPorts()) {
        actor.append("int " + ((AbstractVertex) cfg.getIncomingDependency().getSource()).getName() + ";");
      }
      for (final DataInputPort in : sc.getActor().getDataInputPorts()) {
        actor.append(in.getFifo().getType() + " " + in.getName() + ";");
      }
      for (final DataOutputPort out : sc.getActor().getDataOutputPorts()) {
        actor.append(out.getFifo().getType() + " " + out.getName() + ";");
      }
      body.append(actor, "");
      for (int i = 0; i < sc.getEndLoopNb(); i++) {
        body.append("}");
      }
    }
    return body.toString();
  }

  private String loopFunction(PiGraph subGraph) {
    final StringConcatenation funcLoop = new StringConcatenation();
    funcLoop.append("void" + subGraph.getName() + "(", "");
    for (final Parameter param : subGraph.getParameters()) {
      funcLoop.append("int " + param.getName() + ",");
    }
    for (final DataInputInterface input : subGraph.getDataInputInterfaces()) {
      funcLoop.append(input.getDataPort().getFifo().getType() + " " + input.getName() + ",");
    }
    for (final DataOutputInterface output : subGraph.getDataOutputInterfaces()) {
      funcLoop.append(output.getDataPort().getFifo().getType() + " " + output.getName() + ",");
    }
    String funcL = funcLoop.toString().substring(0, funcLoop.length() - 1);
    funcL += "{\n";
    return funcL;
  }

  private List<ScapeSchedule> schedule(String clusterSchedule, PiGraph subGraph) {
    final List<ScapeSchedule> cs = new LinkedList<>();
    final List<AbstractActor> actorList = subGraph.getExecutableActors();
    final String scheduleMonoCore = clusterSchedule.replace("/", ""); // doesn't deal with parallelism
    final String[] splitActor = scheduleMonoCore.split("\\*");
    String[] splitRate;
    int openLoopCounter = 0;

    for (int i = 0; i < splitActor.length; i++) {
      final ScapeSchedule sc = ScheduleFactory.eINSTANCE.createScapeSchedule();
      if (splitActor[i].contains("(")) {
        sc.setBeginLoop(true);
        openLoopCounter++;
        splitRate = splitActor[i].split("\\(");
        sc.setIterator(Integer.parseInt(splitRate[0]));
        splitActor[i] = splitRate[1];
      } else {
        sc.setBeginLoop(false);
        sc.setIterator(1);
      }
      if (splitActor[i].contains(")")) {
        openLoopCounter--;
        sc.setEndLoop(true);
        sc.setEndLoopNb(compterOccurrences(splitActor[i], ')'));
        splitActor[i] = splitActor[i].replace("\\)", "");

      } else {
        sc.setEndLoop(false);
      }
      sc.setActor(PiMMUserFactory.instance.createActor());
      sc.getActor().setName(splitActor[i]);

      sc.setLoopPrec((openLoopCounter >= 1 && !sc.isBeginLoop()));

      cs.add(sc);

    }
    for (final ScapeSchedule element : cs) {
      for (final AbstractActor element2 : actorList) {
        if (element.getActor().getName().equals(element2.getName())) {
          element.setActor(element2);
        }
      }
    }

    return cs;
  }

  public static int compterOccurrences(String maChaine, char recherche) {
    int nb = 0;
    for (int i = 0; i < maChaine.length(); i++) {
      if (maChaine.charAt(i) == recherche) {
        nb++;
      }
    }
    return nb;
  }
}
