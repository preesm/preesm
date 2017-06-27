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
   * returns the maximum number of allowed executions of an actor to complete its iteration
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
        if (n < maxExecutions) {
          maxExecutions = n;
          if (maxExecutions == 0) {
            return 0;
          }
        }
        if (newStartDate > maxStartDate) {
          maxStartDate = newStartDate;
        }
      }
      actorInfo.get(actor).startDate = maxStartDate;
      return maxExecutions;
    }
  }

  /**
   * Returns the maximum possible number of executions.
   * 
   * @param actor
   *          SDF Actor
   * @return executions number
   */

  public int maxNbOfExecutions(SDFAbstractVertex actor) {
    int maxExecutions = Integer.MAX_VALUE; // initialize the counter with a max value
    for (SDFInterfaceVertex input : actor.getSources()) {
      SDFEdge edge = actor.getAssociatedEdge(input);
      int n = (int) Math.floor(edge.getDelay().intValue() / edge.getCons().intValue());
      // if n = 0, it means that the actor is not ready to be fired
      if (n < maxExecutions) {
        maxExecutions = n;
        if (maxExecutions == 0) {
          return 0;
        }
      }
    }
    return maxExecutions;
  }

  /**
   * Executes an actor n times.</br>
   * Before calling this method, you should verify if the actor is ready to be fired n times. In case of insufficient data tokens on its input edges, it will
   * results to a negative delays on the edges.</br>
   * 
   * if n < 0 it will cancel n executions == remove data tokens from the output edges and restore them in the input edges.
   * 
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   * 
   */
  public void execute(SDFAbstractVertex actor, int n) {
    if (n != 0) {
      // consume n times data tokens from the input edges of the actor
      this.consume(actor, n);
      // produce n times data tokens on the output edges of the actor
      this.produce(actor, n);
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

      // decrement the counter by n
      actorInfo.get(actor).executionsCounter -= n;
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
   * restore the edges delay to the initial marking
   */
  public void restoreInitialMarking() {
    for (SDFEdge edge : graph.edgeSet()) {
      edge.setDelay(initialMarking.get(edge));
    }
  }

}
