/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.throughput.tools;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.algorithm.model.AbstractEdgePropertyType;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.types.LongEdgePropertyType;

/**
 * @author hderoui
 *
 *         A simulation helper.
 */
public class GraphSimulationHelper {
  private static final String NB_EXECUTION_PROPERTY = "nbExeution";

  private static final String START_DATE_PROPERTY = "startDate";

  private static final String FINISH_DATE_PROPERTY = "finishDate";

  private static final String EXECUTION_COUNTER_PROPERTY = "executionCounter";

  // SDF graph and scenario
  private final SDFGraph graph;

  // list of the initial marking to restore the graph
  private Map<SDFEdge, AbstractEdgePropertyType<?>> initialMarking;

  /**
   * @param graph
   *          SDF graph
   */
  public GraphSimulationHelper(final SDFGraph graph) {
    this.graph = graph;

    // save the initial marking of the graph
    saveInitialMarking();

    // add some simulation properties for actors
    prepareActors();

  }

  /**
   * returns the maximum number of allowed executions of an actor to complete its iteration
   *
   * @param actor
   *          SDF actor
   * @return number of allowed executions
   */
  public long maxExecToCompleteAnIteration(final SDFAbstractVertex actor) {
    // initialize the counter with a max value = RV - counter
    double maxStartDate = 0;
    long maxExecutions = actor.getNbRepeatAsLong()
        - (long) actor.getPropertyBean().getValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY);
    if (maxExecutions <= 0) {
      return 0;
    } else {
      for (final SDFInterfaceVertex input : actor.getSources()) {
        final SDFEdge edge = actor.getAssociatedEdge(input);
        // compute the max number of executions that edge delays allow
        final long n = edge.getDelay().longValue() / edge.getCons().longValue();
        final double newStartDate = (double) edge.getSource().getPropertyBean()
            .getValue(GraphSimulationHelper.FINISH_DATE_PROPERTY);
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
      actor.setPropertyValue(GraphSimulationHelper.START_DATE_PROPERTY, maxStartDate);
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

  private long maxNbOfExecutions(final SDFAbstractVertex actor) {
    long maxExecutions = Long.MAX_VALUE; // initialize the counter with a max value
    for (final SDFInterfaceVertex input : actor.getSources()) {
      final SDFEdge edge = actor.getAssociatedEdge(input);
      final long n = edge.getDelay().longValue() / edge.getCons().longValue();
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
   * Before calling this method, you should verify if the actor is ready to be fired n times. In case of insufficient
   * data tokens on its input edges, it will results to a negative delays on the edges.
   *
   * if n < 0 it will cancel n executions == remove data tokens from the output edges and restore them in the input
   * edges.
   *
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   */
  public void execute(final SDFAbstractVertex actor, final long n) {
    if (n != 0) {
      // consume n times data tokens from the input edges of the actor
      consume(actor, n);
      // produce n times data tokens on the output edges of the actor
      produce(actor, n);
    }
  }

  /**
   *
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   */
  public void consume(final SDFAbstractVertex actor, final long n) {
    if (n > 0) {
      // consume n data tokens on each input edge
      for (final SDFInterfaceVertex input : actor.getSources()) {
        final SDFEdge edge = actor.getAssociatedEdge(input);
        // e.delay -= n * e.cons
        final long newDelay = edge.getDelay().longValue() - (n * edge.getCons().longValue());
        edge.setDelay(new LongEdgePropertyType(newDelay));
      }

      // increment the counter by n
      final long oldN = (long) actor.getPropertyBean().getValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY);
      actor.setPropertyValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY, oldN + n);

    } else {
      // restore n data tokens on each input edge
      for (final SDFInterfaceVertex input : actor.getSources()) {
        final SDFEdge edge = actor.getAssociatedEdge(input);
        final long newDelay = edge.getDelay().longValue() + (n * edge.getCons().longValue());
        edge.setDelay(new LongEdgePropertyType(newDelay));
      }
    }
  }

  /**
   * @param actor
   *          SDF actor
   * @param n
   *          number of executions
   */
  public void produce(final SDFAbstractVertex actor, final long n) {
    if (n > 0) {
      // produce n data tokens on each output edge
      for (final SDFInterfaceVertex output : actor.getSinks()) {
        final SDFEdge edge = actor.getAssociatedEdge(output);
        final long newDelay = edge.getDelay().longValue() + (n * edge.getProd().longValue());
        edge.setDelay(new LongEdgePropertyType(newDelay));
      }
    } else {
      // remove n data tokens on each output edge
      for (final SDFInterfaceVertex output : actor.getSinks()) {
        final SDFEdge edge = actor.getAssociatedEdge(output);
        final long newDelay = edge.getDelay().longValue() - (n * edge.getProd().longValue());
        edge.setDelay(new LongEdgePropertyType(newDelay));
      }

      // decrement the counter by n
      final long oldN = (long) actor.getPropertyBean().getValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY);
      actor.setPropertyValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY, oldN - n);
    }
  }

  /**
   * return the list of ready actors to execute
   */
  public Map<SDFAbstractVertex, Long> getReadyActorsNbExecutions() {
    final Map<SDFAbstractVertex, Long> readyActors = new LinkedHashMap<>();
    // get the max number n of executions for each actor, if n>0 then add the actor to the list of ready actors
    for (final SDFAbstractVertex actor : this.graph.vertexSet()) {
      final long n = maxNbOfExecutions(actor);
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
  public double getStartDate(final SDFAbstractVertex actor) {
    return (double) actor.getPropertyBean().getValue(GraphSimulationHelper.START_DATE_PROPERTY);
  }

  /**
   * set the start date of the last execution of an actor
   *
   * @param actor
   *          SDF actor
   * @param date
   *          start date
   */
  public void setStartDate(final SDFAbstractVertex actor, final double date) {
    actor.setPropertyValue(GraphSimulationHelper.START_DATE_PROPERTY, date);
  }

  /**
   * return the finish date of the last execution of an actor
   *
   * @param actor
   *          SDF actor
   * @return finish date
   */
  public double getFinishDate(final SDFAbstractVertex actor) {
    return (double) actor.getPropertyBean().getValue(GraphSimulationHelper.FINISH_DATE_PROPERTY);
  }

  /**
   * @param actor
   *          SDF actor
   * @param date
   *          finish date
   */
  public void setfinishDate(final SDFAbstractVertex actor, final double date) {
    actor.setPropertyValue(GraphSimulationHelper.FINISH_DATE_PROPERTY, date);
  }

  /**
   * return the duration of an actor
   *
   * @param actor
   *          SDF actor
   * @return duration
   */
  public double getActorDuration(final SDFAbstractVertex actor) {
    return (double) actor.getPropertyBean().getValue("duration");
  }

  /**
   * get the execution counter
   *
   * @param actor
   *          actor
   * @return execution counter
   */
  public long getExecutionCounter(final SDFAbstractVertex actor) {
    return (long) actor.getPropertyBean().getValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY);
  }

  /**
   * check if the graph has completed an iteration
   *
   * @return true if the graph iteration is complete
   */
  public boolean isIterationCompleted() {
    // test if each actor was executed RV times
    for (final SDFAbstractVertex actor : this.graph.vertexSet()) {
      if ((long) actor.getPropertyBean().getValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY) < actor
          .getNbRepeatAsLong()) {
        return false;
      }
    }
    return true;
  }

  /**
   * prepare actors info
   */
  private void prepareActors() {
    for (final SDFAbstractVertex actor : this.graph.vertexSet()) {
      actor.setPropertyValue(GraphSimulationHelper.START_DATE_PROPERTY, 0.);
      actor.setPropertyValue(GraphSimulationHelper.FINISH_DATE_PROPERTY, 0.);
      actor.setPropertyValue(GraphSimulationHelper.NB_EXECUTION_PROPERTY, 0L);
      actor.setPropertyValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY, 0L);
    }
  }

  /**
   * Save the initial marking of the graph
   */
  private void saveInitialMarking() {
    this.initialMarking = new Hashtable<>(this.graph.edgeSet().size());
    for (final SDFEdge edge : this.graph.edgeSet()) {
      this.initialMarking.put(edge, edge.getDelay());
    }
  }

  /**
   * Resets actors execution counter.
   *
   */
  public void resetExecutionCounter() {
    for (final SDFAbstractVertex actor : this.graph.vertexSet()) {
      actor.setPropertyValue(GraphSimulationHelper.EXECUTION_COUNTER_PROPERTY, 0L);
    }
  }

}
