package org.preesm.algorithm.clustering.identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.clustering.heuristics.HorizontalHeuristic;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic.FlatteningOrder;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * @author rcazoulat
 */
public class ClusterBuilder {

  private ClusterBuilder() {
    /* This utility class should not be instantiated */
  }

  /**
   * This function will regroup vertically the graph, following a given heuristic. For now, it will cluster it in a
   * top-down way, starting from the top graph. At each level of hierarchy, it will execute the vertical clustering
   * heuristic function named assessFlattening. If the vertical clustering heuristic is set to null, the current graph
   * will just be flattened.
   *
   * @param parentGraph
   *          the graph containing the current graph. If set to null, it means that the current graph is the top graph
   *          of the algorithm.
   * @param graph
   *          the current graph.
   * @param heuristic
   *          the vertical clustering heuristic, used to merge different graphs.
   */
  public static void buildVerticalClusters(PiGraph parentGraph, PiGraph graph, VerticalHeuristic heuristic) {
    if (heuristic == null) {
      PiSDFFlattener.flatten(graph, true);
      return;
    }

    if (heuristic.getFlatteningOrder() == FlatteningOrder.TOP_DOWN) {
      heuristic.assessFlattening(parentGraph, graph);
    }

    for (final PiGraph subgraph : graph.getChildrenGraphs()) {
      buildVerticalClusters(graph, subgraph, heuristic);
    }

    if (heuristic.getFlatteningOrder() == FlatteningOrder.BOTTOM_UP) {
      heuristic.assessFlattening(parentGraph, graph);
    }
  }

  /***
   * This function will regroup horizontally the graph, following a given heuristic.
   *
   * @param graph
   *          the current graph where to seek horizontal clusters
   * @param arch
   *          the S-LAM architecture graph
   * @param heuristic
   *          the heuristic to cluster horizontally
   * @return the list of clusters that have been created. The graph will be modified if clusters have been detected and
   *         created.
   */
  public static List<PiGraph> buildHorizontalClusters(PiGraph graph, Design arch, Scenario scenario,
      final HorizontalHeuristic heuristic, final boolean verbose) {

    // The list that will be returned
    final List<PiGraph> listClusters = new ArrayList<>();

    /*---------------------------------------
     * STEP 1
     *---------------------------------------
     * This step is optional, and its purpose is only to accelerate the clustering. Check if the whole graph can be
     * clustered.
     */

    if (heuristic.assessGraph(graph)) {

      graph.setClusterValue(true);
      graph.setUrl("");

      // Log
      if (verbose) {
        final String info = " >>> Detected cluster " + graph.getName() + " with assessGraph method";
        PreesmLogger.getLogger().log(Level.INFO, info);
      }

      // Return
      listClusters.add(graph);
      return listClusters;
    }

    /*---------------------------------------
     * STEP 2
     *---------------------------------------
     * Exploring the children graphs, in a recursive way
     */

    for (final PiGraph subGraph : graph.getChildrenGraphs()) {
      final var subClusterList = buildHorizontalClusters(subGraph, arch, scenario, heuristic, verbose);
      listClusters.addAll(subClusterList);
    }

    /*---------------------------------------
     * STEP 3
     *---------------------------------------
     * Seek for clusters within the graph. The first step is to choose an actor to start the cluster from : the "seed".
     * The second step is to merge (un)direct neighbors of the seed, respecting the heuristic.
     */

    // Actors of the graph
    final List<AbstractActor> actors = graph.getActors();

    // This map keeps track of all identified seeds and merged actors, so we don't iterate over them twice.
    final Map<AbstractActor, Boolean> identifiedSeedAndMergedActors = actors.stream()
        .collect(Collectors.toMap(Function.identity(), v -> false));

    String seedName = ""; // Only for log and cluster name
    int i = 0; // The index to iterate on actors list
    boolean graphIsFullySearched = false; // boolean that allows us to quit the two while loops.

    /* **First while loop** : Visit all actors of current graph */
    while (!graphIsFullySearched) {

      boolean seedFound = false;

      // A bit dumb, as the seed value is set again the **Nested while loop**.
      // But it is mandatory so we don't get an error because seed is not initialized in the ** First while loop**.
      AbstractActor seed = actors.get(i);

      /* ----- SUB-STEP 1 : identify a seed */
      /* **Nested while loop** : Try to find a valid seed */
      while (!seedFound && !graphIsFullySearched) {

        // getting actor before incrementing i
        seed = actors.get(i++);

        // Current actor has already been visited.
        if (Boolean.TRUE.equals(identifiedSeedAndMergedActors.get(seed))) {
          continue;
        }

        // If a seed is found, end of the **Nested while loop**
        if (heuristic.assesSeedable(seed)) {
          seedFound = true;
          seedName = seed.getName();
        }

        // All actor have been explored, end of the 2 while loops
        if (i == actors.size()) {
          graphIsFullySearched = true;
        }
      }

      // If the nested while loop has ended, but no seed has been found,
      // it means that the graph is fully searched. End of the First while loop.
      if (!seedFound) {
        PreesmLogger.getLogger().info(" WARNING - NO SEED FOUND");

        continue;
      }

      /* ----- SUB-STEP 2 : identify mergeable actors according to the seed. */

      // Now we have a seed, let's build a list of all the actors we want to merge.
      // They will all be (un)direct neighbors of the seed.

      // This list will be used only in buildMergeList. Because it is a recursive function,
      // it has to be declared outside its scope
      final Set<AbstractActor> visitedActors = new HashSet<>();
      final Set<
          AbstractActor> actorsToMerge = buildMergeList(seed, visitedActors, identifiedSeedAndMergedActors, heuristic);

      // If the created cluster is not valid according the used heuristic, we continue the iteration, without adding the
      // cluster to the graph.
      if (!heuristic.validateCluster(actorsToMerge)) {
        continue;
      }

      /*---------------------------------------
       * STEP 4
       *---------------------------------------
       * Merging of identified actors
       */

      // Mark the seed and the merged actors as visited
      for (final AbstractActor a : actorsToMerge) {
        identifiedSeedAndMergedActors.put(a, true);
      }
      identifiedSeedAndMergedActors.put(seed, true);

      // Set cluster name
      final String clusterName = heuristic.getPrefix() + "_" + seedName;

      // Log
      if (verbose) {
        final String info = "> Clustering actors " + actorsToMerge.stream().map(a -> a.getName()).toList()
            + " into cluster " + clusterName;
        PreesmLogger.getLogger().log(Level.INFO, info);

      }

      // Creating the cluster
      final PiGraph cluster = ActorMerger.mergeActors(graph, actorsToMerge, clusterName);
      cluster.setClusterValue(true);
      cluster.setToFlatten(false);
      cluster.setToSrdag(false);

      // Checking modified graph (with the new cluster) consistency
      final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker();
      pgcc.check(graph);

      listClusters.add(cluster);

    }

    return listClusters;
  }

  /**
   * This function will build a list of actors that can be merged with the seed actor.
   *
   * @param seed
   *          Starting actor of the clustering
   * @param visitedActors
   *          the set of already visited actors for this specific seed (re-init for each seed), used to prevent infinite
   *          loops
   * @param seedAndMerged
   *          actors already part of a cluster (the seeds and the merged ones)
   * @return a cluster of actors that can be merge
   */
  public static Set<AbstractActor> buildMergeList(AbstractActor seed, Set<AbstractActor> visitedActors,
      Map<AbstractActor, Boolean> seedAndMerged, HorizontalHeuristic heuristic) {

    // The set that will be returned
    final Set<AbstractActor> actorsToMerge = new HashSet<>();

    // adding the seed
    actorsToMerge.add(seed);

    // Getting all seed neighbors
    final List<
        AbstractActor> seedSucc = seed.getDataOutputPorts().stream().map(dop -> dop.getFifo().getTarget()).toList();

    final List<
        AbstractActor> seedPred = seed.getDataInputPorts().stream().map(dop -> dop.getFifo().getSource()).toList();

    final List<AbstractActor> seedNeighbors = new ArrayList<>(seedSucc);
    seedNeighbors.addAll(seedPred);

    // Iterating on direct neighbors
    for (final AbstractActor actor : seedNeighbors) {
      if (visitedActors.contains(actor) || Boolean.TRUE.equals(seedAndMerged.get(actor))) {
        continue;
      }
      visitedActors.add(actor);

      // check if the actor can be added to the merge list
      final boolean mergeable = heuristic.assessMergeable(seed, actor);
      if (mergeable) {

        // If actor is mergeable, exploring the neighbors of the current actor as a seed
        final Set<AbstractActor> successorList = buildMergeList(actor, visitedActors, seedAndMerged, heuristic);
        actorsToMerge.addAll(successorList);
      }
    }
    return actorsToMerge;
  }

  public static void buildClusterRefinement(PiGraph cluster, Scenario scenario) {

    // 1 : extract function's arguments
    final CHeaderRefinement clusterHeader = PiMMUserFactory.instance.createCHeaderRefinement();
    clusterHeader.setFilePath(scenario.getCodegenDirectory() + "/clusters/" + cluster.getName() + ".h");

    final FunctionPrototype prototype = PiMMUserFactory.instance.createFunctionPrototype();
    prototype.setName(cluster.getName() + "_loop");
    clusterHeader.setLoopPrototype(prototype);

    final List<org.preesm.model.pisdf.Port> clusterInputsOutputs = new ArrayList<>();
    clusterInputsOutputs.addAll(cluster.getConfigInputPorts());
    clusterInputsOutputs.addAll(cluster.getAllDataPorts());

    final FunctionArgument[] args = new FunctionArgument[clusterInputsOutputs.size()];

    for (int i = 0; i < clusterInputsOutputs.size(); i++) {
      final org.preesm.model.pisdf.Port port = clusterInputsOutputs.get(i);

      args[i] = PiMMUserFactory.instance.createFunctionArgument();
      args[i].setDirection(port instanceof DataOutputPort ? Direction.OUT : Direction.IN);
      args[i].setIsConfigurationParameter(port instanceof ConfigInputPort);
      args[i].setIsPassedByReference(port instanceof DataPort);
      args[i].setName(clusterInputsOutputs.get(i).getName());

      if (port instanceof ConfigInputPort) {
        args[i].setType("int");
      } else if (port instanceof final DataInputPort dip) {
        args[i].setType(dip.getFifo().getType());
      } else if (port instanceof final DataOutputPort dop) {
        args[i].setType(dop.getFifo().getType());
      } else {
        throw new PreesmRuntimeException(
            "Port" + port.getName() + " is neither config nor data input/output, I don't know how to process it !");
      }

      args[i].setPosition(i);
    }

    prototype.getArguments().addAll(Arrays.asList(args));

    cluster.addRefinement(clusterHeader);

  }
}
