package org.ietr.preesm.schedule;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * @author hderoui
 * 
 * 
 */
public class Simulator {
  // SDF graph and scenario
  public SDFGraph       graph;
  public PreesmScenario scenario;
  // list of actors duration
  public Hashtable<String, Integer> actorsDuration;
  // list of actors execution counter
  // list of actors startDate
  // list of actors finish date
  // list of the initial marking to restore the graph

  public Simulator(SDFGraph graph, PreesmScenario scenario) {
    this.graph = graph;
    this.scenario = scenario;

    // prepare the lists
  }

  /**
   * return the maximum number of allowed executions of an actor to complete the iteration of the graph
   * 
   * @param actor
   *          SDF actor
   * @return number of allowed executions
   */
  public int maxExecToCompleteAnIteration(SDFAbstractVertex actor) {
    return 0;
  }

  /**
   * 
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   * @return true if the actor is executed correctly
   */
  public boolean consume(SDFAbstractVertex actor, int n) {
    return true;
  }

  /**
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   * @return true if the actor is executed correctly
   */
  public boolean produce(SDFAbstractVertex actor, int n) {
    return true;
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
    return 0;
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
  }

  /**
   * return the finish date of the last execution of an actor
   * 
   * @param actor
   *          SDF actor
   * @return finish date
   */
  public double getFinishDate(SDFAbstractVertex actor) {
    return 0;
  }

  /**
   * @param actor
   *          SDF actor
   * @param date
   *          finish date
   */
  public void setfinishDate(SDFAbstractVertex actor, double date) {
  }

  /**
   * return the duration of an actor
   * 
   * @param actor
   *          SDF actor
   * @return duration
   */
  public double getActorDuration(SDFAbstractVertex actor) {
    return 0;
  }

  /**
   * check if the graph has completed an iteration
   * 
   * @return
   */
  public boolean isIterationCompleted() {
    // test if each actor was executed rv times
    for (SDFAbstractVertex actor : this.graph.vertexSet())
      // if (a.executionCounter < a.repetitionFactor)
      return false;
    return true;
  }

  /*
   * functions:
   * 
   * consume(actor, n) : consume n*cons_rate of data tokens from its input edges
   * 
   * produce(actor, n) : produce n*prod_rate of data tokens in its output edges
   * 
   * getReadyActors() : return the list of ready actors
   * 
   * 
   * 
   */

}
