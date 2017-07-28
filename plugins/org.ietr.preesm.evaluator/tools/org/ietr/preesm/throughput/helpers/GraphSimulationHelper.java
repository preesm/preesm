package org.ietr.preesm.throughput.helpers;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * @author hderoui
 * 
 *         A simulation helper.
 */
public class GraphSimulationHelper {
  // SDF graph and scenario
  public SDFGraph       graph;
  public PreesmScenario scenario;

  // list of the initial marking to restore the graph
  public Hashtable<SDFEdge, AbstractEdgePropertyType<?>> initialMarking;

  /**
   * @param graph
   *          SDF graph
   * @param scenario
   *          contains actors duration
   */
  public GraphSimulationHelper(SDFGraph graph, PreesmScenario scenario) {
    this.graph = graph;
    this.scenario = scenario;

    // save the initial marking of the graph
    this.saveInitialMarking();

    // add some simulation properties for actors
    this.prepareActors();

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
    int maxExecutions = actor.getNbRepeatAsInteger() - (int) actor.getPropertyBean().getValue("executionCounter");
    if (maxExecutions <= 0) {
      return 0;
    } else {
      for (SDFInterfaceVertex input : actor.getSources()) {
        SDFEdge edge = actor.getAssociatedEdge(input);
        // compute the max number of executions that edge delays allow
        int n = (int) Math.floor(edge.getDelay().intValue() / edge.getCons().intValue());
        double newStartDate = (double) edge.getSource().getPropertyBean().getValue("finishDate");
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
      actor.setPropertyValue("startDate", maxStartDate);
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
   * Executes an actor n times.
   * 
   * 
   * Before calling this method, you should verify if the actor is ready to be fired n times. In case of insufficient data tokens on its input edges, it will
   * results to a negative delays on the edges.
   * 
   * if n < 0 it will cancel n executions == remove data tokens from the output edges and restore them in the input edges.
   * 
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
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
   * Executes an actor as much as the data tokens in its input edges allow.
   * 
   * @param actor
   *          SDF actor
   * @return the number of executions
   */
  public int executeMax(SDFAbstractVertex actor) {
    // determine the max executions number
    int n = this.maxNbOfExecutions(actor);
    // execute the actor n times
    this.execute(actor, n);
    return n;
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
      int oldN = (int) actor.getPropertyBean().getValue("executionCounter");
      actor.setPropertyValue("executionCounter", oldN + n);

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
      int oldN = (int) actor.getPropertyBean().getValue("executionCounter");
      actor.setPropertyValue("executionCounter", oldN - n);
    }
  }

  /**
   * return the list of ready actors to execute
   */
  public Hashtable<SDFAbstractVertex, Integer> getReadyActorsNbExecutions() {
    Hashtable<SDFAbstractVertex, Integer> readyActors = new Hashtable<SDFAbstractVertex, Integer>();
    // get the max number n of executions for each actor, if n>0 then add the actor to the list of ready actors
    for (SDFAbstractVertex actor : this.graph.vertexSet()) {
      int n = this.maxNbOfExecutions(actor);
      if (n > 0) {
        readyActors.put(actor, n);
      }
    }
    return readyActors;
  }

  /**
   * return the start Date of the last execution of an actor
   * 
   * @param actor
   *          SDF actor
   * @return start Date
   */
  public double getStartDate(SDFAbstractVertex actor) {
    return (double) actor.getPropertyBean().getValue("startDate");
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
    actor.setPropertyValue("startDate", date);
  }

  /**
   * return the finish date of the last execution of an actor
   * 
   * @param actor
   *          SDF actor
   * @return finish date
   */
  public double getFinishDate(SDFAbstractVertex actor) {
    return (double) actor.getPropertyBean().getValue("finishDate");
  }

  /**
   * @param actor
   *          SDF actor
   * @param date
   *          finish date
   */
  public void setfinishDate(SDFAbstractVertex actor, double date) {
    actor.setPropertyValue("finishDate", date);
  }

  /**
   * return the duration of an actor
   * 
   * @param actor
   *          SDF actor
   * @return duration
   */
  public double getActorDuration(SDFAbstractVertex actor) {
    return (double) actor.getPropertyBean().getValue("duration");
  }

  /**
   * get the execution counter
   * 
   * @param actor
   *          actor
   * @return execution counter
   */
  public int getExecutionCounter(SDFAbstractVertex actor) {
    return (int) actor.getPropertyBean().getValue("executionCounter");
  }

  /**
   * Set the execution counters
   * 
   * @param actor
   *          actor
   * @param n
   *          number of execution to set
   */
  public void setExecutionCounter(SDFAbstractVertex actor, int n) {
    actor.setPropertyValue("executionCounter", n);
  }

  /**
   * check if the graph has completed an iteration
   * 
   * @return true if the graph iteration is complete
   */
  public boolean isIterationCompleted() {
    // test if each actor was executed RV times
    for (SDFAbstractVertex actor : this.graph.vertexSet()) {
      if ((int) actor.getPropertyBean().getValue("executionCounter") < actor.getNbRepeatAsInteger()) {
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

  /**
   * Resets the simulation by restoring the initial marking and reseting the actors execution counter.
   * 
   */
  public void resetSimulation() {
    this.restoreInitialMarking();
    this.prepareActors();
  }

  /**
   * prepare actors info
   */
  public void prepareActors() {
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      actor.setPropertyValue("startDate", 0.);
      actor.setPropertyValue("finishDate", 0.);
      actor.setPropertyValue("nbExeution", 0);
      actor.setPropertyValue("executionCounter", 0);
    }
  }

  /**
   * Save the initial marking of the graph
   */
  public void saveInitialMarking() {
    initialMarking = new Hashtable<SDFEdge, AbstractEdgePropertyType<?>>(graph.edgeSet().size());
    for (SDFEdge edge : graph.edgeSet()) {
      initialMarking.put(edge, edge.getDelay());
    }
  }

  /**
   * Resets actors execution counter.
   * 
   */
  public void resetExecutionCounter() {
    for (SDFAbstractVertex actor : this.graph.vertexSet()) {
      actor.setPropertyValue("executionsCounter", 0);
    }
  }

}
