/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.preesm.algorithm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.params.FastAlgoParameters;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * One thread of Task scheduling FAST algorithm multithread.
 *
 * @author pmenuet
 */
class PFastCallable implements Callable<MapperDAG> {

  /** The abc params. */
  // Simulator chosen
  private final AbcParameters abcParams;

  /** The thread name. */
  // thread named by threadName
  private final String threadName;

  /** The input DAG. */
  // DAG given by the main thread
  private final MapperDAG inputDAG;

  /** The input archi. */
  // Architecture used to implement the DAG
  private final Design inputArchi;

  /** The blocking node names. */
  // Set of the nodes upon which we used fast algorithm in the thread
  private final Set<String> blockingNodeNames;

  /** The fast params. */
  // parameters for the fast algorithm
  private final FastAlgoParameters fastParams;

  /** The already mapped. */
  // Variables to know if we have to do the initial scheduling or not
  private final boolean alreadyMapped;

  /** The scenario. */
  private final Scenario scenario;

  /**
   * Constructor.
   *
   * @param name
   *          the name
   * @param inputDAG
   *          the input DAG
   * @param inputArchi
   *          the input archi
   * @param blockingNodeNames
   *          the blocking node names
   * @param alreadyMapped
   *          the already mapped
   * @param abcParams
   *          the abc params
   * @param fastParams
   *          the fast params
   * @param scenario
   *          the scenario
   */
  public PFastCallable(final String name, final MapperDAG inputDAG, final Design inputArchi,
      final Set<String> blockingNodeNames, final boolean alreadyMapped, final AbcParameters abcParams,
      final FastAlgoParameters fastParams, final Scenario scenario) {
    this.threadName = name;
    this.inputDAG = inputDAG;
    this.inputArchi = inputArchi;
    this.blockingNodeNames = blockingNodeNames;
    this.fastParams = fastParams;
    this.alreadyMapped = alreadyMapped;
    this.abcParams = abcParams;
    this.scenario = scenario;
  }

  /**
   * Call.
   *
   * @return : MapperDAG
   * @throws Exception
   *           the exception
   * @Override call():
   */
  @Override
  public MapperDAG call() throws Exception {

    // intern variables
    MapperDAG callableDAG;
    Design callableArchi;
    final List<MapperDAGVertex> callableBlockingNodes = new ArrayList<>();

    // Critical sections where the data from the main thread are copied for
    // this thread
    synchronized (this.inputDAG) {
      callableDAG = this.inputDAG.copy();
    }

    synchronized (this.inputArchi) {
      callableArchi = this.inputArchi;
    }

    synchronized (this.blockingNodeNames) {
      callableBlockingNodes.addAll(callableDAG.getVertexSet(this.blockingNodeNames));
    }

    // Create the CPN Dominant Sequence
    final LatencyAbc iHsimu = new InfiniteHomogeneousAbc(this.abcParams, callableDAG.copy(), callableArchi,
        this.scenario);
    final InitialLists initialLists = new InitialLists();
    initialLists.constructInitialLists(callableDAG, iHsimu);

    final TopologicalTaskSched taskSched = new TopologicalTaskSched(iHsimu.getTotalOrder());
    iHsimu.resetDAG();

    // performing the fast algorithm
    final FastAlgorithm algo = new FastAlgorithm(initialLists, this.scenario);
    final MapperDAG outputDAG = algo.map(this.threadName, this.abcParams, this.fastParams, callableDAG, callableArchi,
        this.alreadyMapped, null, initialLists.getCpnDominant(), callableBlockingNodes, initialLists.getCriticalpath(),
        taskSched);

    // Saving best total order for future display
    outputDAG.getPropertyBean().setValue("bestTotalOrder", algo.getBestTotalOrder());

    return outputDAG;
  }
}
