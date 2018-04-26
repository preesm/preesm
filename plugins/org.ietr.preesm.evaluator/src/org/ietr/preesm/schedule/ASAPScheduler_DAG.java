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
package org.ietr.preesm.schedule;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.throughput.tools.helpers.GraphSimulationHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public class ASAPScheduler_DAG {
  public GraphSimulationHelper         simulator;       // simulator helper
  public Double                        dur1Iter;        // duration of one iteration of a graph
  private ArrayList<SDFAbstractVertex> actorsToExecute; // list of actors to execute

  /**
   * Schedule the graph using an ASAP schedule and return the duration of the graph iteration
   *
   * @param graph
   *          SDF graph
   * @return the duration of a graph iteration
   */
  public double schedule(final SDFGraph graph) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // initialize the simulator and the list of actor to execute
    this.simulator = new GraphSimulationHelper(graph);
    this.actorsToExecute = new ArrayList<>();
    initialzeList(graph);
    this.dur1Iter = 0.;

    while (!this.actorsToExecute.isEmpty()) {
      // execute the first actor of the list
      final SDFAbstractVertex currentActor = this.actorsToExecute.get(0);
      this.simulator.produce(currentActor, 1);

      // update the duration of the iteration
      if (this.dur1Iter < this.simulator.getFinishDate(currentActor)) {
        this.dur1Iter = this.simulator.getFinishDate(currentActor);
      }

      // verify the target actors of the executed actor if they are ready to be executed
      for (final SDFInterfaceVertex output : currentActor.getSinks()) {

        // execute 1 time the target actor if it is ready
        final SDFAbstractVertex targetActor = currentActor.getAssociatedEdge(output).getTarget();
        if (isReady(targetActor)) {
          // consume 1 time
          this.simulator.consume(targetActor, 1);

          // set the finish date
          final double finishDate = this.simulator.getStartDate(targetActor) + this.simulator.getActorDuration(targetActor);
          this.simulator.setfinishDate(targetActor, finishDate);

          // add the execution to the list
          this.actorsToExecute.add(targetActor);
        }
      }

      // remove the current actor from the list
      this.actorsToExecute.remove(0);
    }

    // check if the simulation is completed
    // if (this.simulator.isIterationCompleted()) {
    // System.out.println("Iteration complete !!");
    // } else {
    // System.err.println("Iteration not complete !!");
    // }

    timer.stop();
    System.out.println("SDF Graph Scheduled in " + timer.toString());
    return this.dur1Iter;
  }

  /**
   * Initialize the list of ready executions
   *
   * @param g
   *          SDF graph
   */
  private void initialzeList(final SDFGraph g) {
    // loop actors
    for (final SDFAbstractVertex actor : g.vertexSet()) {
      // if ready
      if (isReady(actor)) {
        // consume N data tokens
        this.simulator.consume(actor, 1);
        // set the finish date
        final double finishDate = this.simulator.getStartDate(actor) + this.simulator.getActorDuration(actor);
        this.simulator.setfinishDate(actor, finishDate);
        // add the execution to the list
        this.actorsToExecute.add(actor);
      }
    }
  }

  /**
   * verify if the actor is ready to be executed
   *
   * @param actor
   *          actor to verify
   * @return true if it is ready
   */
  private boolean isReady(final SDFAbstractVertex actor) {
    double maxStartDate = 0;
    if (this.simulator.getExecutionCounter(actor) > 0) {
      return false;
    } else {
      boolean ready = true;
      for (final SDFInterfaceVertex input : actor.getSources()) {
        final SDFEdge edge = actor.getAssociatedEdge(input);
        if (edge.getDelay().intValue() == 0) {
          ready = false;
          break;
        } else {
          if (this.simulator.getFinishDate(edge.getSource()) > maxStartDate) {
            maxStartDate = this.simulator.getFinishDate(edge.getSource());
          }
        }
      }
      this.simulator.setStartDate(actor, maxStartDate);
      return ready;
    }
  }

}
