package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * This class computes the schedule of the cluster using the authentic APGAN method, which relies on Repetition Count
 * computation, specifically the Greatest Common Divisor (GCD) of the repetition vectors (RVs) of a pair of connected
 * actors. The fundamental principle of APGAN involves iteratively clustering pairs of actors until a single entity is
 * obtained. Initiating the process by clustering pairs with the maximum repetition count has been demonstrated to
 * result in a schedule with minimal memory requirements. The resulting schedule consists of nested looped schedules,
 * designed to make the behavior of the cluster sequential.
 *
 * @see "https://apps.dtic.mil/sti/pdfs/ADA455067.pdf"
 * @author orenaud
 *
 */
public class ScheduleScape {
  /**
   * Input graph.
   */
  private final PiGraph graph;

  public ScheduleScape(PiGraph graph) {
    this.graph = graph;
  }

  public List<ScapeSchedule> execute() {
    final String scheduleStr = pisdf2str();
    PreesmLogger.getLogger().log(Level.INFO, "APGAN schedule: " + scheduleStr);

    return str2schedule(scheduleStr);

  }

  /**
   * Translate String into a structure for the dedicated cluster code generation
   *
   * @param scheduleStr
   *          The string schedule to translate
   */
  private List<ScapeSchedule> str2schedule(String scheduleStr) {
    final List<ScapeSchedule> cs = new LinkedList<>();
    final List<AbstractActor> actorList = graph.getExecutableActors();
    final String scheduleMonoCore = scheduleStr.replace("/", ""); // doesn't deal with parallelism
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
        sc.setEndLoopNb(occurrency(splitActor[i], ')'));
        splitActor[i] = splitActor[i].replace(")", "");
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

  public static int occurrency(String str, char seek) {
    int nb = 0;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == seek) {
        nb++;
      }
    }
    return nb;
  }

  /**
   * Used to compute the schedule of the cluster with APGAN method
   *
   */
  private String pisdf2str() {
    final Map<AbstractVertex, Long> rv = PiBRV.compute(graph, BRVMethod.LCM);
    // Schedule subgraph

    StringBuilder result = new StringBuilder();
    List<AbstractActor> actorList = graph.getAllExecutableActors();

    if (rv.size() == 1) {
      result.append(rv.get(actorList.get(0)) + "(" + actorList.get(0).getName() + ")");
      return result.toString();

    }
    // Compute BRV

    final Map<AbstractVertex, Long> repetitionVector = rv;

    // compute repetition count (gcd(q(a),q(b)))
    final Map<AbstractVertex, Map<AbstractVertex, Long>> repetitionCount = new LinkedHashMap<>();

    for (final AbstractActor element : graph.getAllExecutableActors()) {

      for (final DataOutputPort element2 : element.getDataOutputPorts()) {
        if (!(element2.getFifo().getTarget() instanceof DataOutputInterface) && !element2.getFifo().isHasADelay()
            && repetitionVector.containsKey(element) && repetitionVector.containsKey(element2.getFifo().getTarget())) {
          final Long rep = gcd(repetitionVector.get(element), repetitionVector.get(element2.getFifo().getTarget()));

          final Map<AbstractVertex, Long> map = new LinkedHashMap<>();
          map.put((AbstractVertex) element2.getFifo().getTarget(), rep);
          if (!repetitionCount.containsKey(element)) {
            if (!element2.getFifo().isHasADelay()) {
              repetitionCount.put(element, map);
            }
          } else if (!element2.getFifo().isHasADelay()) {
            repetitionCount.get(element).put((AbstractVertex) element2.getFifo().getTarget(), rep);
          }
        }
      }
    }

    // find actors in order of succession

    int totalsize = graph.getAllExecutableActors().size();// 5
    final List<AbstractActor> actorListOrdered = new ArrayList<>();
    int flag = 0;
    if (!graph.getDataInputInterfaces().isEmpty()) {
      for (int i = 0; i < graph.getActors().size(); i++) {
        if (graph.getActors().get(i) instanceof DataInputInterface) {
          flag = 0;
          final AbstractActor curentActor = (AbstractActor) graph.getActors().get(i).getDataOutputPorts().get(0)
              .getFifo().getTarget();
          if (!actorListOrdered.contains(curentActor)) {

            for (int iii = 0; iii < curentActor.getDataInputPorts().size(); iii++) {
              if (actorListOrdered.contains(curentActor.getDataInputPorts().get(iii).getFifo().getSource())
                  || curentActor.getDataInputPorts().get(iii).getFifo().getSource() instanceof DataInputInterface
                  || curentActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
                flag++;
              }
            }
            if (flag == curentActor.getDataInputPorts().size()) {

              actorListOrdered.add(curentActor);// fill a
              totalsize--;// 4
            }
          }
        }
      }
      for (final AbstractActor a : actorList) {
        flag = 0;
        if (a.getDataInputPorts().isEmpty() && (!actorListOrdered.contains(a))) {
          for (int i = 0; i < a.getDataInputPorts().size(); i++) {
            if (actorListOrdered.contains(a.getDataInputPorts().get(i).getFifo().getSource())) {
              flag++;
            }
          }
          if (flag == a.getDataInputPorts().size()) {
            actorListOrdered.add(a);
            totalsize--;
          }

        }
      }
    } else {
      for (final AbstractActor a : actorList) {
        if (a.getDataInputPorts().isEmpty() && (!actorListOrdered.contains(a))) {
          actorListOrdered.add(a);
          totalsize--;

        }
      }
    }
    int curentSize = actorListOrdered.size();// 1

    for (int i = 0; i < curentSize; i++) {
      for (int ii = 0; ii < actorListOrdered.get(i).getDataOutputPorts().size(); ii++) {
        flag = 0;
        final AbstractActor precActor = (AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo()
            .getTarget();
        if (!actorListOrdered.contains(precActor)) {
          for (int iii = 0; iii < precActor.getDataInputPorts().size(); iii++) {
            if (actorListOrdered.contains(precActor.getDataInputPorts().get(iii).getFifo().getSource())
                || precActor.getDataInputPorts().get(iii).getFifo().getSource() instanceof DataInputInterface
                || precActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
              flag++;
            }
          }
          if (flag == ((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget())
              .getDataInputPorts().size()) {
            actorListOrdered
                .add((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget());// fill

            totalsize--;// 3
          }

        }

      }
    }
    flag = 0;
    int precSize = curentSize;
    curentSize = actorListOrdered.size();// 2
    while (totalsize > 0) {
      for (int i = precSize; i < curentSize; i++) {
        for (int ii = 0; ii < actorListOrdered.get(i).getDataOutputPorts().size(); ii++) {
          flag = 0;
          final AbstractActor curentActor = (AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii)
              .getFifo().getTarget();
          if (!actorListOrdered.contains(curentActor)) {
            for (int iii = 0; iii < curentActor.getDataInputPorts().size(); iii++) {
              final AbstractActor precActor = (AbstractActor) curentActor.getDataInputPorts().get(iii).getFifo()
                  .getSource();
              if (actorListOrdered.contains(precActor) || precActor instanceof DataInputInterface
                  || curentActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
                flag++;
              }
            }
            if (flag == curentActor.getDataInputPorts().size()) {
              actorListOrdered
                  .add((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget());

              totalsize--;// 2
            }

          }
        }
      }
      precSize = curentSize;
      curentSize = actorListOrdered.size();// 3++
    }
    actorList = actorListOrdered;
    Long lastIndex = (long) 0;

    AbstractActor maxLeft = null;
    AbstractActor maxRight = null;
    Long max = (long) 0;
    while (repetitionVector.size() > 1) {
      boolean removeRight = false;

      // find pair with the biggest repetition count

      if (result.length() == 0) {
        maxLeft = null;
        maxRight = null;
        max = (long) 0;
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;

          if (repetitionCount.containsKey(actorList.get(rang))) {
            for (final AbstractVertex a : repetitionCount.get(actorList.get(rang)).keySet()) {
              if (repetitionCount.get(actorList.get(rang)).get(a) > max) {

                max = repetitionCount.get(actorList.get(rang)).get(a);
                maxLeft = actorList.get(rang);
                maxRight = (AbstractActor) a;

              }
            }
          }
        }

      }
      if (repetitionCount.isEmpty()) {
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;
          if (actorList.get(rang) instanceof Actor || actorList.get(rang) instanceof SpecialActor) {
            result.append("*" + actorList.get(rang).getName());
          }
        }
        return result.toString();
      }
      if (repetitionVector.get(maxLeft) == null) {
        boolean hasPredecessor = false;
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;
          if (repetitionCount.containsKey(actorList.get(rang))) {
            if (repetitionCount.get(actorList.get(rang)).containsKey(maxRight) && !hasPredecessor) {
              max = repetitionCount.get(actorList.get(rang)).get(maxRight);
              maxLeft = actorList.get(rang);
              hasPredecessor = true;

            }

          }
        }
        if (!hasPredecessor) {
          for (final AbstractActor element : actorList) {

            if (repetitionCount.containsKey(maxRight)) {
              if (repetitionCount.get(maxRight).containsKey(element) && !hasPredecessor) {
                max = repetitionCount.get(maxRight).get(element);
                maxLeft = maxRight;
                maxRight = element;
                hasPredecessor = true;
              }
            }
          }
        }
      } else if (repetitionVector.get(maxRight) == null) {
        boolean hasSuccessor = false;
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;
          if (repetitionCount.containsKey(actorList.get(rang))) { // if maxLeft to right
            if (repetitionCount.get(actorList.get(rang)).containsKey(maxLeft) && !hasSuccessor) {
              max = repetitionCount.get(actorList.get(rang)).get(maxLeft);
              maxRight = maxLeft;
              maxLeft = actorList.get(rang);

              hasSuccessor = true;
            }

          }
        }
        if (!hasSuccessor) {
          for (final AbstractActor element : actorList) {
            if (repetitionCount.containsKey(maxLeft)) { // if maxLeft to left
              if (repetitionCount.get(maxLeft).containsKey(element) && !hasSuccessor) {
                max = repetitionCount.get(maxLeft).get(element);
                maxRight = element;
                hasSuccessor = true;
              }

            }
          }
        }
      }

      // compute String schedule
      if (result.length() == 0) {
        if (max == 1) {
          result.append(maxLeft.getName() + "*" + maxRight.getName());
        } else {
          result.append(String.valueOf(max) + "(" + maxLeft.getName() + "*" + maxRight.getName() + ")");
        }
        lastIndex = max;
        if (maxRight.getDataInputPorts().size() > maxLeft.getDataOutputPorts().size()) {
          removeRight = false;
        } else {
          removeRight = true;
        }
      } else {
        if (repetitionVector.get(maxRight) == null || repetitionVector.get(maxLeft) == null) {
          for (final AbstractVertex a : repetitionVector.keySet()) {
            if (!result.toString().contains(a.getName())) {
              result.append("*" + a.getName());
            }
          }
          return result.toString();
        }
        if (repetitionVector.get(maxRight) == 1 && repetitionVector.get(maxLeft) == 1) {
          // if rv = 1*
          if (maxRight != null) {
            if (!result.toString().contains(maxRight.getName())) {
              result.append("*" + maxRight.getName());
              removeRight = true;
            } else {
              result.insert(0, maxLeft.getName() + "*");
            }
          }
          lastIndex = (long) 1;

        } else if (repetitionVector.get(maxRight) > 1 || repetitionVector.get(maxLeft) > 1) {
          // if same loop
          if (!result.toString().contains(maxRight.getName())) { // ajout de maxRight
            // add loop
            if (Objects.equals(repetitionVector.get(maxRight), lastIndex)) {
              if (repetitionVector.get(maxRight) > 1) {
                if (result.toString().contains(repetitionVector.get(maxRight) + "(")
                    && result.indexOf(String.valueOf(repetitionVector.get(maxRight))) == 0) {
                  final String temp = result.toString().replace(repetitionVector.get(maxRight) + "(", "");
                  final StringBuilder temp2 = new StringBuilder(temp.replaceFirst("\\)", ""));
                  result = temp2;
                  result.insert(0, repetitionVector.get(maxRight) + "(");
                  result.append("*" + maxRight.getName() + ")");
                } else if (result.toString().contains(repetitionVector.get(maxRight) + "(")
                    && result.indexOf(String.valueOf(repetitionVector.get(maxRight))) != 0) {
                  final char[] temp = result.toString().toCharArray();
                  String tempi = "";
                  for (int i = 0; i < temp.length - 2; i++) {
                    tempi = tempi + temp[i];
                  }
                  result = new StringBuilder(tempi + "*" + maxRight.getName() + ")");
                }

                lastIndex = repetitionVector.get(maxRight);

              } else {
                result.append("*" + maxRight.getName());
                lastIndex = (long) 1;
              }
            } else {
              // add into prec loop
              if (Long.valueOf(result.charAt(result.lastIndexOf("(") - 1)).equals(repetitionVector.get(maxRight))) {
                result.toString().replace(repetitionVector.get(maxRight) + "(", "");

              }
              result.append("*" + repetitionVector.get(maxRight) + "(" + maxRight.getName() + ")");
              lastIndex = repetitionVector.get(maxRight);
            }
            removeRight = true;
          } else if (!result.toString().contains(maxLeft.getName())) { // ajout de maxLeft
            if (Objects.equals(repetitionVector.get(maxLeft), lastIndex)) {
              if (repetitionVector.get(maxLeft) > 1) {
                if (result.toString().contains(repetitionVector.get(maxLeft) + "(")) {
                  final StringBuilder temp = new StringBuilder(
                      result.toString().replace(repetitionVector.get(maxLeft) + "(", ""));
                  result = temp;
                }
                result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName() + "*");
                lastIndex = repetitionVector.get(maxLeft);
              } else {
                result.insert(0, maxLeft.getName() + "*");
                lastIndex = (long) 1;
              }
            } else {
              final String[] temp = result.toString().split("\\(");
              if (temp[0].equals(repetitionVector.get(maxLeft))) {

                final StringBuilder tempo = new StringBuilder(
                    result.toString().replace(repetitionVector.get(maxLeft) + "(", ""));
                result = tempo;
                result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName());
                lastIndex = repetitionVector.get(maxLeft);
              } else if (repetitionVector.get(maxLeft) > 1) {
                result.insert(0, ")" + "*");
                result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName());
                lastIndex = repetitionVector.get(maxLeft);
              } else {
                result.insert(0, maxLeft.getName() + "*");
                lastIndex = (long) 1;
              }

            }

          }

        } else if (!result.toString().contains(maxRight.getName())) {
          if (Long.valueOf(result.charAt(result.lastIndexOf("(") - 1)).equals(repetitionVector.get(maxRight))) {

            result.toString().replace(repetitionVector.get(maxRight) + "(", "");

          } else {
            result.insert(0, ")" + "*");
          }
          result.append("*" + repetitionVector.get(maxRight) + "(" + maxRight.getName() + ")");
          removeRight = true;
        } else {
          if (result.toString().contains(repetitionVector.get(maxLeft) + "(")) {
            final StringBuilder temp = new StringBuilder(
                result.toString().replace(repetitionVector.get(maxLeft) + "(", ""));
            result = temp;
          } else {
            result.insert(0, ")" + "*");
          }
          result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName());
        }

      }

      // remove/replace clustered actor
      if (!removeRight) {
        repetitionVector.remove(maxLeft);
        for (final AbstractActor a : graph.getAllExecutableActors()) {
          if (repetitionCount.containsKey(a)) {
            if (repetitionCount.get(a).containsKey(maxLeft)) {
              for (final AbstractVertex aa : repetitionCount.get(maxLeft).keySet()) {
                if (!a.equals(aa)) {
                  repetitionCount.get(a).put(aa, repetitionCount.get(maxLeft).get(aa));
                }
              }
              repetitionCount.get(a).remove(maxLeft);
            }
          }

        }
        repetitionCount.remove(maxLeft);
        if (repetitionCount.containsKey(maxLeft)) {
          repetitionCount.remove(maxLeft);
        }

      } else {
        repetitionVector.remove(maxRight);
        for (final AbstractActor a : graph.getAllExecutableActors()) {
          if (repetitionCount.containsKey(a)) {
            if (repetitionCount.get(a).containsKey(maxRight)) {
              if (repetitionCount.containsKey(maxRight)) {
                if (!repetitionCount.get(maxRight).entrySet().isEmpty()) {
                  for (final AbstractVertex aa : repetitionCount.get(maxRight).keySet()) {
                    if (!a.equals(aa)) {
                      repetitionCount.get(a).put(aa, repetitionCount.get(maxRight).get(aa));
                    }
                  }
                  repetitionCount.get(a).remove(maxRight);
                }
              }
            }
          }

        }
        repetitionCount.remove(maxRight);
      }
    }

    return result.toString();
  }

  /**
   * Used to compute the greatest common denominator between 2 long values
   *
   * @param long1
   *          long value 1
   * @param long2
   *          long value 2
   */
  private Long gcd(Long long1, Long long2) {
    if (long2 == 0) {
      return long1;
    }
    return gcd(long2, long1 % long2);
  }

}
