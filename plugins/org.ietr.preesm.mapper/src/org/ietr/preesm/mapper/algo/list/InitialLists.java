/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
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
package org.ietr.preesm.mapper.algo.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.tools.BLevelIterator;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.NeighborCache;

/**
 * Creates the CPN dominant list.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class InitialLists implements Cloneable {

  /** The blocking nodes. */
  // List of the nodes with a link with the critical path
  protected List<MapperDAGVertex> blockingNodes;

  /** The cpn dominant. */
  // List of the nodes which are ordered by the CPNDominant Sequence List
  protected List<MapperDAGVertex> cpnDominant;

  /** The critical path. */
  // List of the nodes of the critical path
  protected List<MapperDAGVertex> criticalPath;

  /**
   * constructors.
   */

  public InitialLists() {
    super();
    this.cpnDominant = new ArrayList<>();
    this.blockingNodes = new ArrayList<>();
    this.criticalPath = new ArrayList<>();

  }

  /**
   * clone: Clone the initial lists.
   *
   * @return : InitialLists
   */

  @Override
  public InitialLists clone() {

    // variables
    final InitialLists initialLists = new InitialLists();
    final List<MapperDAGVertex> newlist = new ArrayList<>();

    // retrieve and clone the cpnDominantList
    Iterator<MapperDAGVertex> iter = this.cpnDominant.listIterator();
    while (iter.hasNext()) {
      final MapperDAGVertex temp = iter.next().clone();
      if (temp != null) {
        newlist.add(temp);
      }
    }
    initialLists.setCpnDominantList(newlist);

    // retrieve and clone the blockingNodesList
    final List<MapperDAGVertex> newlist2 = new ArrayList<>();
    iter = this.blockingNodes.iterator();
    while (iter.hasNext()) {
      final MapperDAGVertex temp = iter.next().clone();
      if (temp != null) {
        newlist2.add(temp);
      }
    }
    initialLists.setBlockingNodesList(newlist2);

    // retrieve and clone the finalcriticalpathList
    final List<MapperDAGVertex> newlist3 = new ArrayList<>();
    iter = this.criticalPath.iterator();
    while (iter.hasNext()) {
      final MapperDAGVertex temp = iter.next().clone();
      if (temp != null) {
        newlist3.add(temp);
      }
    }
    initialLists.setFinalcriticalpathList(newlist3);

    return initialLists;

  }

  /**
   * checkpredecessor: Choose the vertex necessary to continue the CPNdominantlist and add it in the CPN dominant list.
   *
   * @param dag
   *          the dag
   * @param currentvertex
   *          the currentvertex
   * @param orderlist
   *          the orderlist
   * @param abc
   *          the abc
   * @return : true if a vertex was found
   */
  private boolean choosePredecessor(final MapperDAG dag, final MapperDAGVertex currentvertex, final List<MapperDAGVertex> orderlist, final LatencyAbc abc) {

    MapperDAGVertex cpnvertex = null;

    final Graph<DAGVertex, DAGEdge> castDag = dag;
    final NeighborCache<DAGVertex, DAGEdge> neighborindex = new NeighborCache<>(castDag);

    final Set<DAGVertex> predset = new LinkedHashSet<>();

    // check the parents of the current vertex
    cpnvertex = currentvertex;
    predset.addAll(neighborindex.predecessorsOf(currentvertex));

    // Run backward in the DAG to find the node with its parents in the
    // CPNdominantlist and with the maximum b-level
    while (!(orderlist.containsAll(predset))) {
      cpnvertex = ibnChoice(dag, predset, orderlist, abc);
      predset.clear();

      if (cpnvertex != null) {
        predset.addAll(neighborindex.predecessorsOf(cpnvertex));
      } else {
        WorkflowLogger.getLogger().log(Level.SEVERE, "Predecessor not found");
        return false;
      }

    }

    orderlist.add(cpnvertex);
    return true;

  }

  /**
   * ibnChoice: Chooses among the node's predecessors, the vertex necessary to continue the algorithm.
   *
   * @param dag
   *          the dag
   * @param predset
   *          the predset
   * @param orderlist
   *          the orderlist
   * @param archi
   *          the archi
   * @return : MapperDAGVertex
   */
  private MapperDAGVertex ibnChoice(final MapperDAG dag, final Set<DAGVertex> predset, final List<MapperDAGVertex> orderlist, final LatencyAbc archi) {

    final Iterator<DAGVertex> iter = predset.iterator();
    MapperDAGVertex currentvertex = null;
    MapperDAGVertex vertexresult = null;
    long blevelmax = 0;
    long tlevelmax = Long.MAX_VALUE;

    // Check into the predecessor list the one with the biggest b-level or
    // if they have the same with the smallest t-level
    while (iter.hasNext()) {
      currentvertex = (MapperDAGVertex) iter.next();
      final long bLevel = archi.getBLevel(currentvertex, false);
      final long tLevel = archi.getTLevel(currentvertex, false);

      if ((bLevel == blevelmax) && !(orderlist.contains(currentvertex))) {
        if (tLevel < tlevelmax) {
          tlevelmax = tLevel;
          vertexresult = currentvertex;
        }
      } else if ((bLevel > blevelmax) && !(orderlist.contains(currentvertex))) {
        vertexresult = currentvertex;
        blevelmax = bLevel;
        tlevelmax = tLevel;
      } else if (bLevel == -1) {
        WorkflowLogger.getLogger().log(Level.SEVERE, "CPN list construction: b-level can not be computed for vertex " + currentvertex);
      }

    }

    return vertexresult;

  }

  /**
   * constructCPN : Critical Path implemented in the CPN-DominantList (Critical Path Nodes= CPN) and the FCP-list (Final Critical Path = FCP). See YK Kwok
   * thesis p.59
   *
   * @param dag
   *          the dag
   * @param cpnDominant
   *          the cpn dominant
   * @param criticalPath
   *          the critical path
   * @param abc
   *          the abc
   * @return true, if successful
   */
  public boolean constructCPN(final MapperDAG dag, final List<MapperDAGVertex> cpnDominant, final List<MapperDAGVertex> criticalPath, final LatencyAbc abc) {

    WorkflowLogger.getLogger().log(Level.INFO, "Starting to build CPN list");

    // variables
    MapperDAGVertex currentvertex;
    MapperDAGVertex tempvertex = null;
    long commax = 0;

    // Sets the t and b levels
    abc.updateTimings();

    // This step takes time because the whole graph b levels are calculated.
    final BLevelIterator iterator = new BLevelIterator(abc, dag, false);

    // The DAG is entirely read in b-level order by the iterator to find the
    // Critical Path
    currentvertex = iterator.next();
    while (!(currentvertex.incomingEdges().isEmpty())) {
      currentvertex = iterator.next();
    }

    final Graph<DAGVertex, DAGEdge> castDag = dag;
    final NeighborCache<DAGVertex, DAGEdge> neighborindex = new NeighborCache<>(castDag);

    final Set<DAGVertex> succset = new LinkedHashSet<>();

    // the first CPNdominant is found
    // put it in the order list and the FCP list
    cpnDominant.add(currentvertex);
    criticalPath.add(currentvertex);
    MapperDAGVertex cpnvertex = currentvertex;
    final MapperDAG base = (MapperDAG) currentvertex.getBase();

    // Find the successor of the first CPN (Critical Path Node)
    succset.addAll(neighborindex.successorsOf(cpnvertex));

    WorkflowLogger.getLogger().log(Level.INFO, "Building CPN list.");

    /* Do the process while the vertex is not a leaf */
    while (!(succset.isEmpty())) {
      final Iterator<DAGVertex> iter = succset.iterator();

      // the successors are read to find the next
      // level to process (biggest communication, smallest t-level
      commax = -1;
      while (iter.hasNext()) {

        currentvertex = (MapperDAGVertex) iter.next();

        final long edgeCost = abc.getCost((MapperDAGEdge) base.getEdge(cpnvertex, currentvertex));

        if (edgeCost > commax) {
          commax = edgeCost;
          tempvertex = currentvertex;
        } else if (edgeCost == commax) {
          if ((tempvertex == null) || (abc.getTLevel(currentvertex, false) < abc.getTLevel(tempvertex, false))) {
            tempvertex = currentvertex;
          }
        }
      }

      cpnvertex = tempvertex;
      currentvertex = tempvertex;
      criticalPath.add(currentvertex);
      succset.clear();
      succset.addAll(neighborindex.successorsOf(currentvertex));
      /*
       * Search for the predecessor of the final critical path nodes because they must be mapped before their successors
       */
      while (!(cpnDominant.contains(currentvertex))) {
        // If no predecessor was found
        if (!choosePredecessor(dag, currentvertex, cpnDominant, abc)) {
          WorkflowLogger.getLogger().log(Level.SEVERE, "No predecessor was found for vertex: " + currentvertex.getName());
          return false;
        }
      }

    }

    return true;

  }

  /**
   * constructCPNobn: Add to the CPN dominant list and the Blocking Node list all the remaining nodes in a decreasing order of b-level.
   *
   * @param dag
   *          the dag
   * @param orderlist
   *          the orderlist
   * @param abc
   *          the abc
   * @return : void
   */
  private void addCPNobn(final MapperDAG dag, final List<MapperDAGVertex> orderlist, final IAbc abc) {

    // Variables
    MapperDAGVertex currentvertex = null;
    final BLevelIterator iterator = new BLevelIterator(abc, dag, false);

    // Class the OBN into the lists (CPN and Blocking node)
    while (iterator.hasNext()) {
      currentvertex = iterator.next();
      if (!orderlist.contains(currentvertex)) {
        orderlist.add(currentvertex);

      }
    }

  }

  /**
   * constructCPNDominantlist: Construct the CPN dominant List and the other lists necessary for the initial scheduler
   *
   * <p>
   * A CPN is a node included in a critical path. An IBN is a node from which there is a path reaching a CPN. An OBN is a node which is neither a CPN nor an
   * IBN.
   * </p>
   *
   * @param dag
   *          the dag
   * @param simu
   *          the simu
   * @return : true if the initial lists were constructed
   */
  public boolean constructInitialLists(final MapperDAG dag, final IAbc simu) {

    this.cpnDominant.clear();
    this.blockingNodes.clear();
    this.criticalPath.clear();

    if (simu instanceof LatencyAbc) {
      // construction of critical path and CPN dominant list with CPN and
      // IBN actors
      if (!constructCPN(dag, this.cpnDominant, this.criticalPath, (LatencyAbc) simu)) {
        WorkflowLogger.getLogger().log(Level.SEVERE, "Problem with initial list construction");
        return false;
      }
    } else {
      WorkflowLogger.getLogger().log(Level.SEVERE, "To construct initial lists, a latency ABC is needed.");
      return false;
    }

    WorkflowLogger.getLogger().log(Level.INFO, "Adding OBN actors to CPN and IBN actors in CPN dominant list");
    addCPNobn(dag, this.cpnDominant, simu);

    for (final DAGVertex v : dag.vertexSet()) {
      if (!(this.criticalPath.contains(v))) {
        this.blockingNodes.add((MapperDAGVertex) v);
      }
    }

    simu.resetImplementation();

    return true;
  }

  /**
   * orderlistdisplay: Display a List with the logger.
   *
   * @param threadName
   *          the thread name
   * @param tempset
   *          the tempset
   */
  public void orderlistdisplay(final String threadName, final List<MapperDAGVertex> tempset) {

    // Variables
    final Iterator<MapperDAGVertex> iter = tempset.iterator();
    MapperDAGVertex currentvertex;
    final Logger logger = WorkflowLogger.getLogger();
    // check all the list
    while (iter.hasNext()) {
      currentvertex = iter.next();

      logger.log(Level.FINEST, threadName + "Vertex " + currentvertex.getName());

    }
  }

  /**
   * orderlistdisplay: Display a List with the logger.
   *
   * @param tempset
   *          the tempset
   */
  public void orderlistdisplay(final List<MapperDAGVertex> tempset) {

    orderlistdisplay("", tempset);
  }

  /**
   * getters and setters.
   *
   * @return the blocking nodes
   */

  public List<MapperDAGVertex> getBlockingNodes() {
    return this.blockingNodes;
  }

  /**
   * Gets the cpn dominant.
   *
   * @return the cpn dominant
   */
  public List<MapperDAGVertex> getCpnDominant() {
    return this.cpnDominant;
  }

  /**
   * Gets the criticalpath.
   *
   * @return the criticalpath
   */
  public List<MapperDAGVertex> getCriticalpath() {
    return this.criticalPath;
  }

  /**
   * Sets the blocking nodes list.
   *
   * @param blockingNodesList
   *          the new blocking nodes list
   */
  public void setBlockingNodesList(final List<MapperDAGVertex> blockingNodesList) {
    this.blockingNodes = blockingNodesList;
  }

  /**
   * Sets the cpn dominant list.
   *
   * @param cpnDominantList
   *          the new cpn dominant list
   */
  public void setCpnDominantList(final List<MapperDAGVertex> cpnDominantList) {
    this.cpnDominant = cpnDominantList;
  }

  /**
   * Sets the finalcriticalpath list.
   *
   * @param finalcriticalpathList
   *          the new finalcriticalpath list
   */
  public void setFinalcriticalpathList(final List<MapperDAGVertex> finalcriticalpathList) {
    this.criticalPath = finalcriticalpathList;
  }
}
