/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2014)
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
package org.preesm.algorithm.mapper.algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.order.VertexOrderList;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.abc.taskscheduling.TaskSwitcher;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.params.FastAlgoParameters;
import org.preesm.algorithm.mapper.tools.RandomIterator;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Fast Algorithm.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class FastAlgorithm {

  /**
   * The scheduling (total order of tasks) for the best found solution.
   */
  private VertexOrderList bestTotalOrder = null;

  /** The initial lists. */
  private InitialLists initialLists = null;

  /** The scenario. */
  private Scenario scenario = null;

  private long fastLocalSearchTime;
  private long fastGlobalSearchTime;

  /**
   * Constructor.
   *
   * @param initialLists
   *          the initial lists
   * @param scenario
   *          the scenario
   */
  public FastAlgorithm(final InitialLists initialLists, final Scenario scenario) {
    this.initialLists = initialLists;
    this.scenario = scenario;
  }

  /**
   * Map.
   *
   * @param threadName
   *          the thread name
   * @param abcParams
   *          the abc params
   * @param fastParams
   *          the fast params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param alreadyMapped
   *          the already mapped
   * @param pfastused
   *          the pfastused
   * @param displaySolutions
   *          the display solutions
   * @param monitor
   *          the monitor
   * @param taskSched
   *          the task sched
   * @return the mapper DAG
   * @throws PreesmException
   *           the workflow exception
   */
  public MapperDAG map(final String threadName, final AbcParameters abcParams, final FastAlgoParameters fastParams,
      final MapperDAG dag, final Design archi, final boolean alreadyMapped, final boolean pfastused,
      final boolean displaySolutions, final IProgressMonitor monitor, final AbstractTaskSched taskSched) {

    final List<MapperDAGVertex> cpnDominantList = this.initialLists.getCpnDominant();
    final List<MapperDAGVertex> blockingNodesList = this.initialLists.getBlockingNodes();
    final List<MapperDAGVertex> finalcriticalpathList = this.initialLists.getCriticalpath();

    return map(threadName, abcParams, fastParams, dag, archi, alreadyMapped, pfastused, displaySolutions, monitor,
        cpnDominantList, blockingNodesList, finalcriticalpathList, taskSched);
  }

  /**
   * map : do the FAST algorithm by Kwok without the initialization of the list which must be done before this
   * algorithm.
   *
   * @param threadName
   *          the thread name
   * @param abcParams
   *          the abc params
   * @param fastParams
   *          the fast params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param alreadyMapped
   *          the already mapped
   * @param pfastused
   *          the pfastused
   * @param displaySolutions
   *          the display solutions
   * @param monitor
   *          the monitor
   * @param cpnDominantList
   *          the cpn dominant list
   * @param blockingNodesList
   *          the blocking nodes list
   * @param finalcriticalpathList
   *          the finalcriticalpath list
   * @param taskSched
   *          the task sched
   * @return the mapper DAG
   * @throws PreesmException
   *           the workflow exception
   */
  public MapperDAG map(final String threadName, final AbcParameters abcParams, final FastAlgoParameters fastParams,
      final MapperDAG dag, final Design archi, final boolean alreadyMapped, final boolean pfastused,
      final boolean displaySolutions, final IProgressMonitor monitor, final List<MapperDAGVertex> cpnDominantList,
      final List<MapperDAGVertex> blockingNodesList, final List<MapperDAGVertex> finalcriticalpathList,
      final AbstractTaskSched taskSched) {

    final Random randomGenerator = new Random(System.nanoTime());

    // Variables
    final LatencyAbc simulator = LatencyAbc.getInstance(abcParams, dag, archi, this.scenario);

    // A topological task scheduler is chosen for the list scheduling.
    // It schedules the tasks in topological order and, if they are on
    // the same level, in alphabetical name order
    simulator.setTaskScheduler(taskSched);

    final Iterator<MapperDAGVertex> vertexiter = new RandomIterator<>(blockingNodesList);
    final Iterator<MapperDAGVertex> iter = new RandomIterator<>(finalcriticalpathList);

    MapperDAGVertex currentvertex = null;
    MapperDAGVertex fcpvertex = null;
    ComponentInstance operatortest;
    ComponentInstance operatorfcp;
    ComponentInstance operatorprec;

    final Logger logger = PreesmLogger.getLogger();

    // these steps are linked to the description of the FAST algorithm to
    // understand the steps, please refer to the Kwok thesis

    // step 3

    final KwokListScheduler listscheduler = new KwokListScheduler();

    // step 1
    if (!alreadyMapped) {
      listscheduler.schedule(dag, cpnDominantList, simulator, null, null);
    } else {
      simulator.setDAG(dag);
    }
    // display initial time after the list scheduling
    simulator.updateFinalCosts();
    final long initial = simulator.getFinalCost();

    this.bestTotalOrder = simulator.getTotalOrder();

    final String msg = "Found List solution; Cost:" + initial;
    PreesmLogger.getLogger().log(Level.INFO, msg);

    final String msg2 = "InitialSP " + initial;
    logger.log(Level.FINE, msg2);

    dag.setScheduleCost(initial);
    if (blockingNodesList.size() < 2) {
      return simulator.getDAG().copy();
    }
    long bestSL = initial;

    MapperDAG dagfinal = simulator.getDAG().copy();
    dagfinal.setScheduleCost(bestSL);

    // A switcher task scheduler is chosen for the fast refinement
    simulator.setTaskScheduler(new TaskSwitcher());

    // FAST parameters
    // FAST is stopped after a time given in seconds
    setGlobalSearchTimeout(fastParams.getFastTime());
    // the number of local solutions searched in a neighbourhood is the size of the graph
    final int maxStep = dag.vertexSet().size() * archi.getOperatorComponentInstances().size();
    // the number of better solutions found in a neighbourhood is limited
    final int margin = Math.max(maxStep / 10, 1);

    // step 4/17
    // Stopping after the given time in seconds is reached
    while ((fastParams.getFastTime() < 0) || !isGlobalSearchTimeout()) {

      // step 5
      int searchStep = 0;
      int localCounter = 0;
      simulator.updateFinalCosts();

      // FAST local search is stopped after a time given in seconds
      setLocalSearchTimeout(fastParams.getFastLocalSearchTime());

      // step 6 : neighbourhood search
      while ((searchStep < maxStep) && (localCounter < margin) && !isLocalSearchTimeout()) {

        // step 7
        // Selecting random vertex with operator set of size > 1
        List<ComponentInstance> operatorList = new ArrayList<>();
        int nonBlockingIndex = 0;

        while (vertexiter.hasNext() && (operatorList.size() < 2) && (nonBlockingIndex < 100)) {
          nonBlockingIndex++;
          currentvertex = vertexiter.next();
          operatorList = simulator.getCandidateOperators(currentvertex, false);
        }

        if (operatorList.size() < 2) {
          break;
        }

        final long sl = simulator.getFinalCost();

        // step 8

        // The mapping can reaffect the same operator as before,
        // refining the edge scheduling
        final int randomIndex = randomGenerator.nextInt(operatorList.size());
        operatortest = (ComponentInstance) operatorList.toArray()[randomIndex];

        operatorprec = simulator.getEffectiveComponent(currentvertex);

        final String msg3 = "FAST algorithm has difficulties to find a valid component for vertex: " + currentvertex;
        if (operatortest == null) {
          PreesmLogger.getLogger().log(Level.SEVERE, msg3);
        }

        // step 9
        simulator.map(currentvertex, operatortest, false, true);

        if (!currentvertex.hasEffectiveComponent()) {
          PreesmLogger.getLogger().log(Level.SEVERE, msg3);
        }

        // step 10
        simulator.updateFinalCosts();
        final long newSL = simulator.getFinalCost();

        if (newSL >= sl) {
          // TODO: check if ok to use mapWithGroup
          // simulator.map(currentvertex, operatorprec, false);
          simulator.map(currentvertex, operatorprec, false, true);
          simulator.updateFinalCosts();
          localCounter++;
        } else {
          localCounter = 0;
        }

        searchStep++;
        // step 11
      }

      // step 12
      simulator.updateFinalCosts();

      if (bestSL > simulator.getFinalCost()) {

        // step 13
        dagfinal = simulator.getDAG().copy();
        // step 14

        bestSL = simulator.getFinalCost();

        this.bestTotalOrder = simulator.getTotalOrder();

        final String msg3 = "Found Fast solution; Cost:" + bestSL;
        PreesmLogger.getLogger().log(Level.INFO, msg3);

        dagfinal.setScheduleCost(bestSL);
      }

      // step 16
      // Choosing a vertex in critical path with an operator set of more than 1 element
      List<ComponentInstance> operatorList = new ArrayList<>();
      int nonBlockingIndex = 0;

      while (iter.hasNext() && (operatorList.size() < 2) && (nonBlockingIndex < 100)) {
        nonBlockingIndex++;
        fcpvertex = iter.next();
        operatorList = simulator.getCandidateOperators(fcpvertex, false);
      }

      if (operatorList.size() < 2) {
        break;
      }

      // Choosing an operator different from the current vertex operator
      final ComponentInstance currentOp = dagfinal.getMapperDAGVertex(fcpvertex.getName()).getEffectiveOperator();

      do {
        final int randomIndex = randomGenerator.nextInt(operatorList.size());
        operatorfcp = (ComponentInstance) operatorList.toArray()[randomIndex];
      } while (operatorfcp.getInstanceName().equals(currentOp.getInstanceName()) && (operatorList.size() > 1));

      simulator.resetDAG();

      // Reschedule the whole dag
      listscheduler.schedule(dag, cpnDominantList, simulator, operatorfcp, fcpvertex);
    }

    return dagfinal;
  }

  private void setLocalSearchTimeout(int fastLocalSearchTime) {
    this.fastLocalSearchTime = System.currentTimeMillis() + (1000 * fastLocalSearchTime);
  }

  private void setGlobalSearchTimeout(int fastGlobalSearchTime) {
    this.fastGlobalSearchTime = System.currentTimeMillis() + (1000 * fastGlobalSearchTime);
  }

  private boolean isLocalSearchTimeout() {
    return System.currentTimeMillis() > fastLocalSearchTime;
  }

  private boolean isGlobalSearchTimeout() {
    return System.currentTimeMillis() > fastGlobalSearchTime;
  }

  /**
   * Gets the best total order.
   *
   * @return the best total order
   */
  public VertexOrderList getBestTotalOrder() {
    return this.bestTotalOrder;
  }

}
