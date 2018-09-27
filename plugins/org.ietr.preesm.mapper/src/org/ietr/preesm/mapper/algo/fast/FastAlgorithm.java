/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
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
package org.ietr.preesm.mapper.algo.fast;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.PreesmMapperException;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSwitcher;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.algo.list.KwokListScheduler;
import org.ietr.preesm.mapper.gantt.GanttData;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.params.FastAlgoParameters;
import org.ietr.preesm.mapper.tools.RandomIterator;
import org.ietr.preesm.mapper.ui.BestCostPlotter;
import org.ietr.preesm.mapper.ui.bestcost.BestCostEditor;
import org.ietr.preesm.mapper.ui.gantt.GanttEditorRunnable;

/**
 * Fast Algorithm.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class FastAlgorithm extends Observable {

  /**
   * The scheduling (total order of tasks) for the best found solution.
   */
  private VertexOrderList bestTotalOrder = null;

  /** The initial lists. */
  private InitialLists initialLists = null;

  /** The scenario. */
  private PreesmScenario scenario = null;

  /**
   * Constructor.
   *
   * @param initialLists
   *          the initial lists
   * @param scenario
   *          the scenario
   */
  public FastAlgorithm(final InitialLists initialLists, final PreesmScenario scenario) {
    super();
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
   * @throws WorkflowException
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
   * @throws WorkflowException
   *           the workflow exception
   */
  public MapperDAG map(final String threadName, final AbcParameters abcParams, final FastAlgoParameters fastParams,
      final MapperDAG dag, final Design archi, final boolean alreadyMapped, final boolean pfastused,
      final boolean displaySolutions, final IProgressMonitor monitor, final List<MapperDAGVertex> cpnDominantList,
      final List<MapperDAGVertex> blockingNodesList, final List<MapperDAGVertex> finalcriticalpathList,
      final AbstractTaskSched taskSched) {

    final Random randomGenerator = new Random(System.nanoTime());

    final Semaphore pauseSemaphore = new Semaphore(1);
    final BestCostPlotter costPlotter = new BestCostPlotter("FastAlgorithm", pauseSemaphore);

    // initialing the data window if this is necessary
    if (!pfastused) {

      costPlotter.setSUBPLOT_COUNT(1);
      BestCostEditor.createEditor(costPlotter);

      addObserver(costPlotter);
    }

    // Variables
    final LatencyAbc simulator = LatencyAbc.getInstance(abcParams, dag, archi, this.scenario);

    // A topological task scheduler is chosen for the list scheduling.
    // It schedules the tasks in topological order and, if they are on
    // the same level, in alphabetical name order
    simulator.setTaskScheduler(taskSched);

    final Iterator<MapperDAGVertex> vertexiter = new RandomIterator<>(blockingNodesList, randomGenerator);

    final RandomIterator<MapperDAGVertex> iter = new RandomIterator<>(finalcriticalpathList, randomGenerator);
    MapperDAGVertex currentvertex = null;
    MapperDAGVertex fcpvertex = null;
    ComponentInstance operatortest;
    ComponentInstance operatorfcp;
    ComponentInstance operatorprec;
    final Logger logger = WorkflowLogger.getLogger();

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

    if (displaySolutions) {
      final GanttData ganttData = simulator.getGanttData();
      launchEditor(ganttData, "Cost:" + initial + " List");
    }

    final String msg = "Found List solution; Cost:" + initial;
    WorkflowLogger.getLogger().log(Level.INFO, msg);

    final String msg2 = "InitialSP " + initial;
    logger.log(Level.FINE, msg2);

    long SL = initial;
    dag.setScheduleCost(initial);
    if (blockingNodesList.size() < 2) {
      return simulator.getDAG().clone();
    }
    long bestSL = initial;
    Long iBest;
    MapperDAG dagfinal = simulator.getDAG().clone();
    dagfinal.setScheduleCost(bestSL);

    // A switcher task scheduler is chosen for the fast refinement
    simulator.setTaskScheduler(new TaskSwitcher());

    // FAST parameters
    // FAST is stopped after a time given in seconds
    final long fastStopTime = System.currentTimeMillis() + (1000 * fastParams.getFastTime());
    // the number of local solutions searched in a neighborhood is the size
    // of the graph
    final int maxStep = dag.vertexSet().size() * DesignTools.getNumberOfOperatorInstances(archi);
    // the number of better solutions found in a neighborhood is limited
    final int margin = Math.max(maxStep / 10, 1);

    // step 4/17
    // Stopping after the given time in seconds is reached
    while ((fastParams.getFastTime() < 0) || (System.currentTimeMillis() < fastStopTime)) {

      // Notifying display
      iBest = bestSL;
      setChanged();
      notifyObservers(iBest);

      // step 5
      int searchStep = 0;
      int localCounter = 0;
      simulator.updateFinalCosts();

      // FAST local search is stopped after a time given in seconds
      final long fastLocalSearchStopTime = System.currentTimeMillis() + (1000 * fastParams.getFastLocalSearchTime());

      // step 6 : neighborhood search
      do {
        // Mode stop
        if ((costPlotter.getActionType() == 1) || ((monitor != null) && monitor.isCanceled())) {

          return dagfinal.clone();
        } else if (costPlotter.getActionType() == 2) {
          // Mode Pause
          try {
            pauseSemaphore.acquire();
            pauseSemaphore.release();
          } catch (final InterruptedException e) {
            throw new PreesmMapperException("Semaphore issue", e);
          }
        }

        // step 7
        // Selecting random vertex with operator set of size > 1
        List<ComponentInstance> operatorList = null;
        int nonBlockingIndex = 0;

        do {
          nonBlockingIndex++;
          currentvertex = vertexiter.next();
          operatorList = simulator.getCandidateOperators(currentvertex, false);
        } while ((operatorList.size() < 2) && (nonBlockingIndex < 100));

        SL = simulator.getFinalCost();

        // step 8

        // The mapping can reaffect the same operator as before,
        // refining the edge scheduling
        final int randomIndex = randomGenerator.nextInt(operatorList.size());
        operatortest = (ComponentInstance) operatorList.toArray()[randomIndex];

        operatorprec = simulator.getEffectiveComponent(currentvertex);

        if (operatortest == null) {
          WorkflowLogger.getLogger().log(Level.SEVERE,
              "FAST algorithm has difficulties to find a valid component for vertex: " + currentvertex);
        }

        // step 9
        simulator.map(currentvertex, operatortest, false, true);

        if (!currentvertex.hasEffectiveComponent()) {
          WorkflowLogger.getLogger().log(Level.SEVERE,
              "FAST algorithm has difficulties to find a valid component for vertex: " + currentvertex);
        }

        // step 10
        simulator.updateFinalCosts();
        final long newSL = simulator.getFinalCost();

        if (newSL >= SL) {
          // TODO: check if ok to use mapWithGroup
          // simulator.map(currentvertex, operatorprec, false);
          simulator.map(currentvertex, operatorprec, false, true);
          simulator.updateFinalCosts();
          localCounter++;
        } else {
          localCounter = 0;
          SL = newSL;
        }

        searchStep++;
        // step 11
      } while ((searchStep < maxStep) && (localCounter < margin)
          && (System.currentTimeMillis() < fastLocalSearchStopTime));

      // step 12
      simulator.updateFinalCosts();

      if (bestSL > simulator.getFinalCost()) {

        // step 13
        dagfinal = simulator.getDAG().clone();
        // step 14

        bestSL = simulator.getFinalCost();

        this.bestTotalOrder = simulator.getTotalOrder();

        if (displaySolutions) {

          final GanttData ganttData = simulator.getGanttData();
          launchEditor(ganttData, "Cost:" + bestSL + " Fast");
        }

        WorkflowLogger.getLogger().log(Level.INFO, "Found Fast solution; Cost:" + bestSL);

        dagfinal.setScheduleCost(bestSL);
      }

      // step 16
      // Choosing a vertex in critical path with an operator set of more
      // than 1 element
      List<ComponentInstance> operatorList = null;
      int nonBlockingIndex = 0;

      do {
        nonBlockingIndex++;
        fcpvertex = iter.next();
        operatorList = simulator.getCandidateOperators(fcpvertex, false);
      } while ((operatorList.size() < 2) && (nonBlockingIndex < 100));

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

  /**
   * Gets the best total order.
   *
   * @return the best total order
   */
  public VertexOrderList getBestTotalOrder() {
    return this.bestTotalOrder;
  }

  /**
   * Launch editor.
   *
   * @param ganttData
   *          the gantt data
   * @param name
   *          the name
   * @throws WorkflowException
   *           the workflow exception
   */
  public void launchEditor(final GanttData ganttData, final String name) throws WorkflowException {

    GanttEditorRunnable.run(ganttData, name);

  }

}
