package org.preesm.algorithm.clustering.scape;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.commons.graph.Vertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.PiGraph;
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
  private final PiGraph           graph;
  final Map<AbstractVertex, Long> brv;
  final Map<String, Long>         brvStr;
  Map<String, Map<String, Long>>  rcStr;

  public ScheduleScape(PiGraph graph) {
    this.graph = graph;
    this.brv = PiBRV.compute(graph, BRVMethod.LCM);
    this.brvStr = initRepetitionVectorStr();
    this.rcStr = initRepetitionCountStr();
  }

  /**
   * Initialize the repetition count which is the greatest common divisor between the repetition vector coefficient of
   * two linked actors
   *
   * @return repetitionCountStr the repetition count map
   */
  private Map<String, Map<String, Long>> initRepetitionCountStr() {
    final Map<String, Map<String, Long>> repetitionCountStr = new LinkedHashMap<>();

    for (final AbstractActor actor : graph.getAllExecutableActors()) {
      for (final Vertex successor : actor.getDirectSuccessors()) {
        if (!(successor instanceof DataOutputInterface)) {
          final Long rep = gcd(brv.get(actor), brv.get(successor));

          repetitionCountStr.computeIfAbsent(actor.getName(), k -> new LinkedHashMap<>())
              .put(((AbstractVertex) successor).getName(), rep);
        }
      }
    }
    return repetitionCountStr;
  }

  /**
   * Initialize the repetition vector coefficient to link actor's name to the computed value
   *
   * @return repetitionVectorStr the repetition vector map
   */
  private Map<String, Long> initRepetitionVectorStr() {
    final Map<String, Long> repetitionVectorStr = new LinkedHashMap<>();

    for (final Entry<AbstractVertex, Long> entry : brv.entrySet()) {
      repetitionVectorStr.put(entry.getKey().getName(), entry.getValue());
    }
    return repetitionVectorStr;
  }

  public List<ScapeSchedule> execute() {
    if (brv.size() > 2 && graph.getDelayIndex() > 0) {
      PreesmLogger.getLogger().log(Level.SEVERE, "APGAN doesn't handle cycle except cycle of 1 actor");
    }
    final String scheduleStr = scheduleStr();
    final String message = "APGAN schedule: " + scheduleStr;
    PreesmLogger.getLogger().log(Level.INFO, message);

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
        sc.setEndLoopNb(occurrence(splitActor[i], ')'));
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

  /**
   * Count the number of time a char occur in a string
   *
   * @param str
   *          the character sequence
   * @param seek
   *          the character to seek
   * @return result the occurrence count
   *
   */
  public static int occurrence(String str, char seek) {
    int result = 0;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == seek) {
        result++;
      }
    }
    return result;
  }

  /**
   * Compute the APGAN schedule and transform it as a string
   *
   * @return combineName the APGAN schedule translation
   */
  private String scheduleStr() {
    // final Map<AbstractVertex, Long> rv = PiBRV.compute(graph, BRVMethod.LCM);
    if (graph.getExecutableActors().size() == 1) {
      return brv.get(graph.getExecutableActors().get(0)) + "(" + graph.getExecutableActors().get(0).getName() + ")";
    }

    final int iter = brv.size();
    String combineName = "";
    for (int i = 1; i < iter; i++) {
      final Pair<String, String> pairMax = findMaxPair();
      combineName = combineName(pairMax);

    }

    return combineName;
  }

  /**
   * The method iteratively combine the of actor's name and update the repetition count et the repetition vector
   * coefficient of the combine actor
   *
   * @return combineName the combine name
   */
  private String combineName(Pair<String, String> pairMax) {

    final String maxLeft = pairMax.getFirst();
    final String maxRight = pairMax.getSecond();
    final Long rv = gcd(brvStr.get(maxLeft), brvStr.get(maxRight));

    String newName = "";
    if (brvStr.get(maxLeft) / rv > 1) {
      newName += brvStr.get(maxLeft) / rv + "(" + maxLeft + ")";
    } else {
      newName += maxLeft;
    }
    if (brvStr.get(maxRight) / rv > 1) {
      newName += "*" + brvStr.get(maxRight) / rv + "(" + maxRight + ")";
    } else {
      newName += "*" + maxRight;
    }
    // update repetition vector with merge actors
    brvStr.remove(maxLeft);
    brvStr.remove(maxRight);
    brvStr.put(newName, rv);

    // update repetition count with merge actors
    rcStr.get(maxLeft).remove(maxRight);
    Map<String, Long> omega = new LinkedHashMap<>();
    if (!rcStr.get(maxLeft).isEmpty()) {
      omega = rcStr.get(maxLeft);
    }
    if (rcStr.containsKey(maxRight)) {
      omega.putAll(rcStr.get(maxRight));
    }
    rcStr.remove(maxRight);
    rcStr.remove(maxLeft);
    if (!omega.isEmpty()) {
      rcStr.put(newName, omega);
    }

    for (final Entry<String, Map<String, Long>> entry : rcStr.entrySet()) {
      if (entry.getValue().containsKey(maxRight)) {
        final Long save2 = entry.getValue().get(maxRight);
        entry.getValue().remove(maxRight);
        entry.getValue().put(newName, save2);
      }
      if (entry.getValue().containsKey(maxLeft)) {
        final Long save2 = entry.getValue().get(maxLeft);
        entry.getValue().remove(maxLeft);
        entry.getValue().put(newName, save2);
      }
    }

    return newName;
  }

  /**
   * The method iteratively identify the pair of actor with the greatest repetition count in order to be cluster first.
   *
   * @return maxPair the identified pair of actor
   */
  private Pair<String, String> findMaxPair() {
    Pair<String, String> maxPair = null;
    Long maxValue = Long.MIN_VALUE;

    for (final Map.Entry<String, Map<String, Long>> outerEntry : rcStr.entrySet()) {
      final String outerKey = outerEntry.getKey();
      final Map<String, Long> innerMap = outerEntry.getValue();

      for (final Map.Entry<String, Long> innerEntry : innerMap.entrySet()) {
        final String innerKey = innerEntry.getKey();
        final Long value = innerEntry.getValue();

        if (value > maxValue) {
          maxValue = value;
          maxPair = Tuples.create(outerKey, innerKey);
        }
      }
    }
    return maxPair;

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
