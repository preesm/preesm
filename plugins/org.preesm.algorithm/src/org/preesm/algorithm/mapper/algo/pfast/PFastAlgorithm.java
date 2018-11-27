/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2016)
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
package org.preesm.algorithm.mapper.algo.pfast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import org.preesm.algorithm.mapper.PreesmMapperException;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.order.VertexOrderList;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.algo.fast.FastAlgorithm;
import org.preesm.algorithm.mapper.algo.list.InitialLists;
import org.preesm.algorithm.mapper.algo.list.KwokListScheduler;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.params.FastAlgoParameters;
import org.preesm.algorithm.mapper.params.PFastAlgoParameters;
import org.preesm.algorithm.mapper.ui.BestCostPlotter;
import org.preesm.algorithm.mapper.ui.bestcost.BestCostEditor;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.WorkflowException;

/**
 * Task scheduling FAST algorithm multithread.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class PFastAlgorithm extends Observable {

  /**
   * The scheduling (total order of tasks) for the best found solution.
   */
  private VertexOrderList bestTotalOrder = null;

  /**
   * FinalTimeComparator : comparator between two different implementation based on.
   */
  private class FinalTimeComparator implements Comparator<MapperDAG> {

    /**
     * Compare.
     *
     * @param o1
     *          the o 1
     * @param o2
     *          the o 2
     * @return : integer
     * @Override compare
     */
    @Override
    public int compare(final MapperDAG o1, final MapperDAG o2) {

      long difference = 0;

      difference = o1.getScheduleCost();

      difference -= o2.getScheduleCost();

      if (difference >= 0) {
        difference = 1;
      } else {
        difference = 0;
      }

      return (int) difference;
    }

    /**
     * Constructor : FinalTimeComparator.
     *
     * @param abcParams
     *          the abc params
     * @param dag
     *          the dag
     * @param archi
     *          the archi
     * @param scenario
     *          the scenario
     */
    public FinalTimeComparator(final AbcParameters abcParams, final MapperDAG dag, final Design archi,
        final PreesmScenario scenario) {
      super();
    }

  }

  /**
   * Constructor : PFastAlgorithm.
   */
  public PFastAlgorithm() {
    super();
  }

  /**
   * chooseNbCores : Determine how many processors will be used among the available ones and return the set of nodes on
   * which each processor will perform the fast algorithm.
   *
   * @param initialLists
   *          the initial lists
   * @param nboperator
   *          // number of available processor
   * @param nodesmin
   *          // number of nodes necessary for each thread
   * @param subSet
   *          // Set with the partial BlockingNodesLists
   * @return integer
   */
  public int chooseNbCores(final InitialLists initialLists, final int nboperator, final int nodesmin,
      final Set<Set<String>> subSet) {

    // initialization
    final List<MapperDAGVertex> blockingNodelist = new ArrayList<>();
    blockingNodelist.addAll(initialLists.getBlockingNodes());

    // find number of thread possible
    final int nbsubsets = setThreadNumber(blockingNodelist, nboperator, nodesmin);

    if (nbsubsets == 0) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Not enough nodes to execute PFAST. Try reducing nodesmin in workflow or use another mapper.");
    }

    // find number of nodes per thread
    final int nbnodes = nbnodes(blockingNodelist, nbsubsets, nodesmin);
    subSet.add(new LinkedHashSet<String>());

    // Put the nodes of the BlockingNodes List in the Sublist
    for (int i = 0; i < nbsubsets; i++) {
      subSet.add(new LinkedHashSet<String>());
      final Iterator<Set<String>> itera = subSet.iterator();
      final Set<String> tempSet = itera.next();
      for (int j = 0; j < nbnodes; j++) {
        if (!(subSet.containsAll(blockingNodelist))) {
          final Iterator<MapperDAGVertex> riter = blockingNodelist.iterator();
          final MapperDAGVertex currentvertex = riter.next();
          tempSet.add(currentvertex.getName());
          blockingNodelist.remove(currentvertex);

        }
      }
    }

    return nbsubsets;
  }

  /**
   * setnumber : return the number of processor(thread) necessary to process the PFastAlgorithm with the List of Vertex.
   *
   * @param bLlist
   *          // BlockingNodesList
   * @param nboperator
   *          // number of available processor
   * @param nodesmin
   *          // number of nodes necessary for each thread
   * @return integer
   */

  public int setThreadNumber(final List<MapperDAGVertex> bLlist, int nboperator, final int nodesmin) {

    final int nbnodes = bLlist.size();
    int nbsubsets;
    if (nboperator == 0) {
      nboperator = 1;
    }
    // verify if we have enough nodes to do the PFastAlgorithm
    if (nbnodes >= (nboperator * nodesmin)) {
      nbsubsets = nboperator;
    } else {
      nbsubsets = nbnodes / nodesmin;
    }
    return nbsubsets;
  }

  /**
   * nbnodes : return the number of nodes per partial BlockingNodesList.
   *
   * @param bLlist
   *          // BlockingNodesList
   * @param nbsubsets
   *          // Number of thread
   * @param nodesmin
   *          // Number of nodes per thread
   * @return integer
   */
  public int nbnodes(final List<MapperDAGVertex> bLlist, int nbsubsets, final int nodesmin) {

    final int nbnodes = bLlist.size();
    if (nbsubsets == 0) {
      nbsubsets = 1;
    }
    // Find the number of nodes for each thread
    int nbnodeset = nbnodes / nbsubsets;
    if ((nbnodes % nodesmin) != 0) {
      nbnodeset++;
    }
    return nbnodeset;
  }

  /**
   * mapcontain = verify if the vertex is already in the other partial lists.
   *
   * @param subSet
   *          the sub set
   * @param vertex
   *          the vertex
   * @return boolean
   */
  public boolean mapcontain(final Set<Set<MapperDAGVertex>> subSet, final MapperDAGVertex vertex) {

    final Iterator<Set<MapperDAGVertex>> itera = subSet.iterator();
    // Verify if the vertex is already in the subset
    while (itera.hasNext()) {
      final Set<MapperDAGVertex> temp = itera.next();
      if (temp.contains(vertex)) {
        return true;
      }
    }
    return false;
  }

  /**
   * map = perform the Pfast Algo (it is the main thread)
   *
   * <p>
   * // Determine if we want the Pfast solution or a population of good solution to perform another algorithm.
   * </p>
   *
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   * @param initialLists
   *          the initial lists
   * @param abcParams
   *          the abc params
   * @param pFastParams
   *          the fast params
   * @param population
   *          the population
   * @param populationsize
   *          // if we want a population this parameter determine how many individuals we want in the population
   * @param isDisplaySolutions
   *          the is display solutions
   * @param populationList
   *          // List of MapperDAG which are solution
   * @param taskSched
   *          the task sched
   * @return MapperDAG
   * @throws WorkflowException
   *           the workflow exception
   */
  public MapperDAG map(MapperDAG dag, final Design archi, final PreesmScenario scenario,
      final InitialLists initialLists, final AbcParameters abcParams, final PFastAlgoParameters pFastParams,
      final boolean population, int populationsize, final boolean isDisplaySolutions,
      final List<MapperDAG> populationList, final AbstractTaskSched taskSched) {

    int i = 0;
    if (populationsize < 1) {
      populationsize = 1;
    }
    int k = 0;

    final List<MapperDAGVertex> cpnDominantVector = new ArrayList<>(initialLists.getCpnDominant());
    final List<MapperDAGVertex> blockingnodeVector = new ArrayList<>(initialLists.getBlockingNodes());
    final List<MapperDAGVertex> fcpVector = new ArrayList<>(initialLists.getCriticalpath());
    MapperDAG dagfinal;
    final KwokListScheduler scheduler = new KwokListScheduler();
    final LatencyAbc archisimu = LatencyAbc.getInstance(abcParams, dag, archi, scenario);
    final Set<Set<String>> subSet = new LinkedHashSet<>();

    final FastAlgoParameters fastParams = new FastAlgoParameters(pFastParams.getFastTime(),
        pFastParams.getFastLocalSearchTime(), pFastParams.isDisplaySolutions());

    // if only one operator the fast must be used
    if (pFastParams.getProcNumber() == 0) {
      final FastAlgorithm algorithm = new FastAlgorithm(initialLists, scenario);

      dag = algorithm.map("Fast", abcParams, fastParams, dag, archi, false, false, false, null, cpnDominantVector,
          blockingnodeVector, fcpVector, taskSched);
      return dag;
    }

    // Data window set
    final Semaphore pauseSemaphore = new Semaphore(1);
    final BestCostPlotter costPlotter = new BestCostPlotter("PFast Algorithm", pauseSemaphore);

    if (!population) {
      costPlotter.setSUBPLOT_COUNT(1);
      BestCostEditor.createEditor(costPlotter);

      addObserver(costPlotter);
    }

    // step 1
    // step 2
    dagfinal = scheduler.schedule(dag, cpnDominantVector, archisimu, null, null).copy();

    this.bestTotalOrder = archisimu.getTotalOrder();
    archisimu.updateFinalCosts();
    long iBest = archisimu.getFinalCost();

    setChanged();
    notifyObservers(iBest);
    dagfinal.setScheduleCost(iBest);
    dag.setScheduleCost(iBest);
    // step 3/4
    final int nbCores = chooseNbCores(initialLists, pFastParams.getProcNumber(), pFastParams.getNodesmin(), subSet);

    final ConcurrentSkipListSet<MapperDAG> mappedDAGSet = new ConcurrentSkipListSet<>(
        new FinalTimeComparator(abcParams, dagfinal, archi, scenario));

    // step 5/7/8
    int totalsearchcount = 0;
    while (totalsearchcount < pFastParams.getFastNumber()) {

      // step 11

      // create ExecutorService to manage threads
      final Iterator<Set<String>> subiter = subSet.iterator();
      final Set<FutureTask<MapperDAG>> futureTasks = new LinkedHashSet<>();
      final ExecutorService es = Executors.newFixedThreadPool(nbCores);

      // step 6
      for (i = k; i < (nbCores + k); i++) {
        final String name = String.format("thread%d", i);

        // step 9/11
        final PFastCallable thread = new PFastCallable(name, dag, archi, subiter.next(), isDisplaySolutions, true,
            abcParams, fastParams, scenario);

        final FutureTask<MapperDAG> task = new FutureTask<>(thread);
        futureTasks.add(task);
        es.submit(task);

      }
      k = i;

      // step 10
      try {

        final Iterator<FutureTask<MapperDAG>> it = futureTasks.iterator();

        while (it.hasNext()) {

          final FutureTask<MapperDAG> task = it.next();

          final MapperDAG currentOutDAG = task.get();
          mappedDAGSet.add(currentOutDAG);

        }
        while (mappedDAGSet.size() > populationsize) {

          mappedDAGSet.pollLast();
        }

        // step 12
        if (!population) {
          dag = mappedDAGSet.first().copy();

          iBest = dag.getScheduleCost();
          setChanged();
          notifyObservers(iBest);

          // Mode Pause
          while (costPlotter.getActionType() == 2) {
            Thread.sleep(50);
          }

          // Mode stop
          if (costPlotter.getActionType() == 1) {
            break;
          }

        }

        es.shutdown();

      } catch (final InterruptedException | ExecutionException e) {
        throw new PreesmMapperException("Error in PFast", e);
      }
      // step 13
      totalsearchcount++;

    }

    if (population) {
      final Iterator<MapperDAG> ite = mappedDAGSet.iterator();
      while (ite.hasNext()) {
        MapperDAG currentdag;
        currentdag = ite.next();
        populationList.add(currentdag);
      }

    }

    this.bestTotalOrder = (VertexOrderList) mappedDAGSet.first().getPropertyBean().getValue("bestTotalOrder");
    dagfinal = mappedDAGSet.first().copy();

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
}
