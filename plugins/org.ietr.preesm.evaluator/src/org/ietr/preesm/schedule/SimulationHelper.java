package org.ietr.preesm.schedule;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;

/**
 * @author hderoui
 * 
 *         A simulation helper.
 */
public class SimulationHelper {
  // SDF graph and scenario
  public SDFGraph       graph;
  public PreesmScenario scenario;

  // list of the initial marking to restore the graph
  public Hashtable<SDFEdge, AbstractEdgePropertyType<?>> initialMarking;

  // additional information for actors
  public Hashtable<SDFAbstractVertex, ActorExtendedInfo> actorInfo;

  /**
   * @param graph
   *          SDF graph
   * @param scenario
   *          contains actors duration
   */
  public SimulationHelper(SDFGraph graph, PreesmScenario scenario) {
    this.graph = graph;
    this.scenario = scenario;

    // save the initial marking
    initialMarking = new Hashtable<SDFEdge, AbstractEdgePropertyType<?>>(graph.edgeSet().size());
    for (SDFEdge edge : graph.edgeSet()) {
      initialMarking.put(edge, edge.getDelay());
    }

    // prepare the list of actors info
    actorInfo = new Hashtable<SDFAbstractVertex, ActorExtendedInfo>(graph.vertexSet().size());
    for (SDFAbstractVertex actor : graph.vertexSet()) {

      // get the actor duration from the scenario. If default set to 0;
      double dur = 0;
      Timing timing = scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86");
      if (timing.getVertexId() != "default") {
        dur = timing.getTime();
      }

      ActorExtendedInfo info = new ActorExtendedInfo(actor, dur, 0, 0, 0, 0);
      actorInfo.put(actor, info);
    }

  }

  /**
   * return the maximum number of allowed executions of an actor to complete the iteration of the graph
   * 
   * @param actor
   *          SDF actor
   * @return number of allowed executions
   */
  public int maxExecToCompleteAnIteration(SDFAbstractVertex actor) {
    // initialize the counter with a max value = RV - counter
    double maxStartDate = 0;
    int maxExecutions = actor.getNbRepeatAsInteger() - actorInfo.get(actor).executionsCounter;
    if (maxExecutions <= 0) {
      return 0;
    } else {
      for (SDFInterfaceVertex input : actor.getSources()) {
        SDFEdge edge = actor.getAssociatedEdge(input);
        // compute the max number of executions that edge delays allow
        int n = (int) Math.floor(edge.getDelay().intValue() / edge.getCons().intValue());
        double newStartDate = actorInfo.get(edge.getSource()).finishDate;
        // if n = 0, it means that the actor is not ready to be fired
        if (n != 0) {
          if (n < maxExecutions) {
            maxExecutions = n;
          }
          if (newStartDate > maxStartDate) {
            maxStartDate = newStartDate;
          }
        } else {
          return 0;
        }
      }
      actorInfo.get(actor).startDate = maxStartDate;
      return maxExecutions;
    }
  }

  /**
   * 
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   */
  public void consume(SDFAbstractVertex actor, int n) {
    if (n > 0) {
      // consume n data tokens on each input edge
      for (SDFInterfaceVertex input : actor.getSources()) {
        SDFEdge edge = actor.getAssociatedEdge(input);
        // e.delay -= n * e.cons
        int newDelay = edge.getDelay().intValue() - n * edge.getCons().intValue();
        edge.setDelay(new SDFIntEdgePropertyType(newDelay));
      }

      // increment the counter by n
      actorInfo.get(actor).executionsCounter += n;

    } else {
      // restore n data tokens on each input edge
      for (SDFInterfaceVertex input : actor.getSources()) {
        SDFEdge edge = actor.getAssociatedEdge(input);
        // e.delay += n * e.cons;
        int newDelay = edge.getDelay().intValue() + n * edge.getCons().intValue();
        edge.setDelay(new SDFIntEdgePropertyType(newDelay));
      }

      // TODO : add this line to produce() function
      // decrement the counter by n
      actorInfo.get(actor).executionsCounter -= n;
    }
  }

  /**
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   */
  public void produce(SDFAbstractVertex actor, int n) {
    if (n > 0) {
      // produce n data tokens on each output edge
      for (SDFInterfaceVertex output : actor.getSinks()) {
        SDFEdge edge = actor.getAssociatedEdge(output);
        // e.delay += n * e.prod;
        int newDelay = edge.getDelay().intValue() + n * edge.getProd().intValue();
        edge.setDelay(new SDFIntEdgePropertyType(newDelay));
      }
    } else {
      // remove n data tokens on each output edge
      for (SDFInterfaceVertex output : actor.getSinks()) {
        SDFEdge edge = actor.getAssociatedEdge(output);
        // e.delay -= n * e.prod;
        int newDelay = edge.getDelay().intValue() - n * edge.getProd().intValue();
        edge.setDelay(new SDFIntEdgePropertyType(newDelay));
      }
    }
  }

  /**
   * return the list of ready actors to execute
   */
  public void getReadyActors() {

  }

  /**
   * return the start Date of the last execution of an actor
   * 
   * @param actor
   *          SDF actor
   * @return start Date
   */
  public double getStartDate(SDFAbstractVertex actor) {
    return actorInfo.get(actor).startDate;
  }

  /**
   * set the start date of the last execution of an actor
   * 
   * @param actor
   *          SDF actor
   * @param date
   *          start date
   */
  public void setStartDate(SDFAbstractVertex actor, double date) {
    actorInfo.get(actor).startDate = date;
  }

  /**
   * return the finish date of the last execution of an actor
   * 
   * @param actor
   *          SDF actor
   * @return finish date
   */
  public double getFinishDate(SDFAbstractVertex actor) {
    return actorInfo.get(actor).finishDate;
  }

  /**
   * @param actor
   *          SDF actor
   * @param date
   *          finish date
   */
  public void setfinishDate(SDFAbstractVertex actor, double date) {
    actorInfo.get(actor).finishDate = date;
  }

  /**
   * return the duration of an actor
   * 
   * @param actor
   *          SDF actor
   * @return duration
   */
  public double getActorDuration(SDFAbstractVertex actor) {
    return actorInfo.get(actor).duration;
  }

  /**
   * check if the graph has completed an iteration
   * 
   * @return true if the graph iteration is complete
   */
  public boolean isIterationCompleted() {
    // test if each actor was executed RV times
    for (SDFAbstractVertex actor : this.graph.vertexSet()) {
      if (actorInfo.get(actor).executionsCounter < actor.getNbRepeatAsInteger()) {
        return false;
      }
    }
    return true;
  }

  /**
   * reset the edges delay to the initial marking
   */
  public void resetInitialMarking() {
    for (SDFEdge edge : graph.edgeSet()) {
      edge.setDelay(initialMarking.get(edge));
    }
  }

}
