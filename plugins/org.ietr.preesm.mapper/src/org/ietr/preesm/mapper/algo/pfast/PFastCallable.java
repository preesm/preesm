/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper.algo.pfast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.params.FastAlgoParameters;

// TODO: Auto-generated Javadoc
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

  /** The is display solutions. */
  // True if we want to display the best found solutions
  private final boolean isDisplaySolutions;

  /** The already mapped. */
  // Variables to know if we have to do the initial scheduling or not
  private final boolean alreadyMapped;

  /** The scenario. */
  private final PreesmScenario scenario;

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
   * @param isDisplaySolutions
   *          the is display solutions
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
      final Set<String> blockingNodeNames, final boolean isDisplaySolutions, final boolean alreadyMapped,
      final AbcParameters abcParams, final FastAlgoParameters fastParams, final PreesmScenario scenario) {
    this.threadName = name;
    this.inputDAG = inputDAG;
    this.inputArchi = inputArchi;
    this.blockingNodeNames = blockingNodeNames;
    this.fastParams = fastParams;
    this.alreadyMapped = alreadyMapped;
    this.abcParams = abcParams;
    this.isDisplaySolutions = isDisplaySolutions;
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
      callableDAG = this.inputDAG.clone();
    }

    synchronized (this.inputArchi) {
      callableArchi = this.inputArchi;
    }

    synchronized (this.blockingNodeNames) {
      callableBlockingNodes.addAll(callableDAG.getVertexSet(this.blockingNodeNames));
    }

    // Create the CPN Dominant Sequence
    final IAbc IHsimu = new InfiniteHomogeneousAbc(this.abcParams, callableDAG.clone(), callableArchi, this.scenario);
    final InitialLists initialLists = new InitialLists();
    initialLists.constructInitialLists(callableDAG, IHsimu);

    final TopologicalTaskSched taskSched = new TopologicalTaskSched(IHsimu.getTotalOrder());
    IHsimu.resetDAG();

    // performing the fast algorithm
    final FastAlgorithm algo = new FastAlgorithm(initialLists, this.scenario);
    final MapperDAG outputDAG = algo.map(this.threadName, this.abcParams, this.fastParams, callableDAG, callableArchi,
        this.alreadyMapped, true, this.isDisplaySolutions, null, initialLists.getCpnDominant(), callableBlockingNodes,
        initialLists.getCriticalpath(), taskSched);

    // Saving best total order for future display
    outputDAG.getPropertyBean().setValue("bestTotalOrder", algo.getBestTotalOrder());

    return outputDAG;

  }
}
