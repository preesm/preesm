package org.preesm.algorithm.clustering.identification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.clustering.ClusterCreationTask;
import org.preesm.algorithm.clustering.ClusterCreator;
import org.preesm.algorithm.clustering.ClusterHelper;
import org.preesm.algorithm.clustering.heuristics.BalancingHeuristic;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.heuristics.HorizontalHeuristic;
import org.preesm.algorithm.clustering.heuristics.MappingHeuristic;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesisHelper;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.serialize.PiSDFExporterTask;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The main method of this class is {@link #identify(PiGraph, Scenario, Design, Map, Workflow) identify}. Check its
 * description for more information.
 *
 * @author rcazoulat
 */
public class ClusterIdentifier {

  private ClusterIdentifier() {
  }

  /**
   * This method will (1) identify and create different clusters in {@link PiGraph algorithm} (in a
   * {@link VerticalHeuristic vertical} and in an {@link HorizontalHeuristic horizontal} way), (2) select a
   * {@link Component component} for each identified clusters and will update the constraints in the {@link Scenario
   * scenario}, and then (3) balance weights to adapt the the repetition value of the clusters according to a
   * {@link BalancingHeuristic balancing heuristic}.
   *
   * @param algorithm
   *          the top {@link PiGraph graph} that describes the algorithm.
   * @param scenario
   *          the {@link Scenario scenario} that links the algorithm with the {@link Design architecture}, describes the
   *          timings...
   * @param architecture
   *          the S-LAM {@link Design architecture} graph.
   * @param parameters
   *          the parameters of the {@link AbstractTaskImplementation task} calling this method.
   * @return A pair that contains (1)the modified algorithm, and (2) a map containing the clusters (that are
   *         {@link PiGraph sub-graphs} of the top graph} and a {@link Component component} type for each cluster. There
   *         is also the scenario as indirect output that has been updated with the clusters'constraints.
   */
  public static Pair<PiGraph, Map<PiGraph, Component>> identify(PiGraph algorithm, Scenario scenario,
      Design architecture, Map<String, String> parameters, Workflow workflow) {

    // Getting parameters
    final boolean verbose = "true".equalsIgnoreCase(parameters.get(ClusterCreationTask.PARAM_VERBOSE));
    final String vertiIdentifierName = parameters.getOrDefault(ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, "")
        .toLowerCase();
    final String horizIdentifierName = parameters.getOrDefault(ClusterCreationTask.PARAM_HORIZONTAL_HEURISTIC, "")
        .toLowerCase();
    final String mapperName = parameters.getOrDefault(ClusterCreationTask.PARAM_MAPPING_HEURISTIC, "").toLowerCase();
    final String balancerName = parameters.getOrDefault(ClusterCreationTask.PARAM_BALANCING_HEURISTIC, "")
        .toLowerCase();
    final boolean debug = "true".equalsIgnoreCase(parameters.get(ClusterCreationTask.PARAM_DEBUG));

    final boolean verticalEnabled = !"".equals(vertiIdentifierName);

    // Retrieving heuristics
    final HorizontalHeuristic horizIdentifier = (HorizontalHeuristic) HeuristicGetter.getHeuristic(horizIdentifierName);
    final MappingHeuristic clusterMapper = (MappingHeuristic) HeuristicGetter.getHeuristic(mapperName);
    final BalancingHeuristic clusterBalancer = (BalancingHeuristic) HeuristicGetter.getHeuristic(balancerName);

    // ---------------------------------------------------------------------------------------------- //
    // Vertical clusters
    // ---------------------------------------------------------------------------------------------- //
    VerticalHeuristic vertiIdentifier = null;
    if (verticalEnabled) {
      vertiIdentifier = (VerticalHeuristic) HeuristicGetter.getHeuristic(vertiIdentifierName);
      vertiIdentifier.initHeuristicParameters(algorithm, scenario, architecture, parameters);
    }

    algorithm = buildVerticalClusters(algorithm, vertiIdentifier);

    // Adding special actors for every hierarchical level of algorithm.
    // Can be useful to unlock memory reuse without passing by a SrDAG
    ClusterSynthesisHelper.addAllSpecialActors(algorithm);

    if (verbose) {
      PreesmLogger.getLogger().info("Clustering Id: building vertical clusters done.");
    }

    // Debug
    if (debug) {
      Map<String, Object> exportInputs = null;
      Map<String, String> exportParameters;
      exportInputs = new HashMap<>();
      exportInputs.put("PiMM", algorithm);
      exportParameters = new HashMap<>();
      exportParameters.put("path", "/Algo/generated/clustering/debug/vertical/");
      exportParameters.put("hierarchical", "true");

      new PiSDFExporterTask().execute(exportInputs, exportParameters, null, null, workflow);
    }

    // ---------------------------------------------------------------------------------------------- //
    // Horizontal clusters
    // ---------------------------------------------------------------------------------------------- //
    // It will modify the algorithm graph & return the created subgraphs list

    horizIdentifier.initHeuristicParameters(algorithm, scenario, architecture, parameters);
    clusterMapper.initHeuristicParameters(algorithm, scenario, architecture, parameters);

    final List<PiGraph> clustersList = buildHorizontalClusters(algorithm /* will be modified */, architecture, scenario,
        horizIdentifier, verbose);

    if (verbose) {
      PreesmLogger.getLogger()
          .info("Clustering Id: building horizontal clusters with " + horizIdentifier + " heuristic done.");
    }

    // ---------------------------------------------------------------------------------------------- //
    // Clusters constraints
    // ---------------------------------------------------------------------------------------------- //
    // It will modify the scenario by adding constraints.

    // Create Refinement link in advance
    clustersList.parallelStream().forEach(cluster -> buildClusterRefinement(cluster, scenario));

    // Init cluster balancer after the identification (once the algorithm is updated with clusters)
    clusterBalancer.initHeuristicParameters(algorithm, scenario, architecture, parameters);

    Map<PiGraph, Component> result = new HashMap<>();
    for (final PiGraph cluster : clustersList) {

      // Picking the component type of cluster thanks to the mapping heuristic
      final Component clusterComponent = clusterMapper.selectComponent(cluster);
      result.put(cluster, clusterComponent);

      // Adding the constraints of all the component instances for the given cluster (works in a mono-node only)
      for (final ComponentInstance ci : architecture.getComponentInstancesOfType(clusterComponent)) {
        scenario.getConstraints().addConstraint(ci, cluster);
      }
    }

    if (verbose) {
      PreesmLogger.getLogger().info("Clustering Id: clusters constraints Id done");
    }

    // ---------------------------------------------------------------------------------------------- //
    // Balancing
    // ---------------------------------------------------------------------------------------------- //
    // It will modify cluster lists and clusters' input and output weights
    final Map<PiGraph, Component> newResult = new HashMap<>();
    final long nPEs = ClusterHelper.computeSingleNodeCoreEquivalent(scenario);

    for (final Entry<PiGraph, Component> entry : result.entrySet()) {
      final PiGraph cluster = entry.getKey();
      final Component clusterComponent = entry.getValue();
      clusterBalancer.balanceFirings(algorithm /* will be modified */, cluster, nPEs).stream().forEach(c -> {
        newResult.put(c, clusterComponent);
      });
    }

    result = newResult;

    if (verbose) {
      PreesmLogger.getLogger().info("Clustering Id: partitioning clusters done");
    }

    return new Pair<>(algorithm, result);

  }

  /**
   * This function will regroup (or extend ?) vertically the algorithm, following a given heuristic. For each sub-graph,
   * it will execute the {@link VerticalHeuristic vertical heuristic} function. If the vertical clustering heuristic is
   * set to null, the current graph will just be flattened.
   *
   * @param graph
   *          the whole application.
   * @param heuristic
   *          the vertical clustering heuristic, used to merge different graphs.
   */
  public static PiGraph buildVerticalClusters(PiGraph graph, VerticalHeuristic heuristic) {
    if (heuristic == null) {
      return PiSDFFlattener.flatten(graph, true);
    }
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    buildVerticalClustersRec(null, graphCopy, heuristic);
    return graphCopy;

  }

  /**
   * Recursive function that will process every hierarchical level of the graph.
   *
   * @param parentGraph
   *          the parent graph containing the current graph. If set to null, it means that the current graph is the top
   *          graph of the algorithm.
   * @param graph
   *          the current graph.
   * @param heuristic
   *          the vertical clustering heuristic, used to merge different graphs.
   */
  private static void buildVerticalClustersRec(PiGraph parentGraph, PiGraph graph, VerticalHeuristic heuristic) {
    heuristic.assessFlatteningBefore(parentGraph, graph);

    for (final PiGraph subgraph : graph.getChildrenGraphs()) {
      buildVerticalClustersRec(graph, subgraph, heuristic);
    }

    heuristic.assessFlatteningAfter(parentGraph, graph);
  }

  /***
   * This function will regroup horizontally the {@link PiGraph graph}, following a given {@link HorizontallyHeuristic
   * heuristic}.
   *
   * @param graph
   *          the current graph where to seek horizontal clusters. It can be the top graph, or a sub-graph. It is
   *          supposed to be impactless.
   * @param arch
   *          the S-LAM {@link Design architecture} graph
   * @param heuristic
   *          the {@link HorizontallyHeuristic heuristic} to cluster horizontally the graph.
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
     * Exploring the children graphs, in a recursive way
     */

    for (final PiGraph subGraph : graph.getChildrenGraphs()) {
      final var subClusterList = buildHorizontalClusters(subGraph, arch, scenario, heuristic, verbose);
      listClusters.addAll(subClusterList);
    }

    // ---------------------------------------
    // STEP 2
    // ---------------------------------------

    // Seek for clusters within the graph. The first step is to choose an actor to start the cluster from : the "seed".
    // The second step is to merge (un)direct neighbors of the seed, respecting the heuristic.

    // Actors of the graph
    final List<AbstractActor> actors = graph.getActors().stream()
        .filter(a -> !(a instanceof DataInterface || a instanceof DelayActor)).toList();

    // This map keeps track of all identified seeds and merged actors, so we don't iterate over them twice.
    final Set<AbstractActor> identifiedActors = new HashSet<>();

    String seedName = ""; // Only for log and cluster name
    int i = 0; // The index to iterate on actors list
    boolean graphIsFullySearched = false; // boolean that allows us to quit the two while loops.

    // **First while loop** : Visit all actors of current graph
    while (!graphIsFullySearched) {
      boolean seedFound = false;
      AbstractActor potentialSeed = null;

      // ---------------------------------------
      // SUB-STEP 1 : identify a seed
      // ---------------------------------------

      // **Nested while loop** : Try to find a valid seed
      while (!seedFound && !graphIsFullySearched) {

        // All actor have been explored, end of the 2 while loops
        potentialSeed = actors.get(i++);

        // The graph is fully searched, we can't find more clusters
        if (i == actors.size()) {
          graphIsFullySearched = true;
        }

        // Current actor has already been visited.
        if (identifiedActors.contains(potentialSeed)) {
          continue;
        }

        // If a seed is found, end of the **Nested while loop**
        if (heuristic.assesSeedable(potentialSeed)) {
          seedFound = true;
          seedName = potentialSeed.getName();
        }
      }

      // If the nested while loop has ended, but no seed has been found,
      // it means that the graph is fully searched. End of the First while loop.
      if (!seedFound) {
        PreesmLogger.getLogger().info("No seed has been found, end of cluster search");
        continue;
      }

      /* ----- SUB-STEP 2 : identify mergeable actors according to the seed. */

      // Now we have a seed, let's build a list of all the actors we want to merge.
      // They will all be (un)direct neighbors of the seed.
      final Set<AbstractActor> visited = new HashSet<>();
      final Set<AbstractActor> actorsToMerge = buildMergeList(potentialSeed, visited, identifiedActors, heuristic);

      // If the created cluster is not valid according the used heuristic, we continue the iteration, without adding the
      // cluster to the graph.
      if (!heuristic.validateCluster(actorsToMerge)) {
        continue;
      }

      // ---------------------------------------
      // STEP 4
      // ---------------------------------------
      // Merging of identified actors

      // Mark the seed and the merged actors as identified
      identifiedActors.addAll(actorsToMerge);
      identifiedActors.add(potentialSeed);

      // Set cluster name
      final String clusterName = heuristic.getPrefix() + "_" + seedName;

      // Log
      if (verbose) {
        final String info = "> Clustering actors " + actorsToMerge.stream().map(a -> a.getName()).toList()
            + " into cluster " + clusterName;
        PreesmLogger.getLogger().log(Level.INFO, info);

      }

      // Creating the cluster
      final PiGraph cluster = ClusterCreator.create(graph, actorsToMerge, clusterName);

      // Checking modified graph (with the new cluster) consistency
      final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker();
      pgcc.check(graph);

      listClusters.add(cluster);

    }

    return listClusters;
  }

  /**
   * This function will build a list of {@link AbstractActor actors} that can be merged with the seed actor. If an actor
   * can be merged with the seed actor, then this method can be executed again, with the mergeable actor as seed. The
   * cluster will be the concatenation of all the executions of this method.
   *
   * @param seed
   *          Starting actor of the clustering.
   * @param identifiedActors
   *          actors already part of a cluster (the seeds and the merged ones)
   * @return a cluster of actors that can be merge
   */
  public static Set<AbstractActor> buildMergeList(AbstractActor seed, Set<AbstractActor> visited,
      Set<AbstractActor> identifiedActors, HorizontalHeuristic heuristic) {

    // The set that will be returned
    final Set<AbstractActor> actorsToMerge = new HashSet<>();

    // adding the seed
    actorsToMerge.add(seed);
    visited.add(seed);

    // Getting all seed neighbors
    final List<
        AbstractActor> seedSucc = seed.getDataOutputPorts().stream().map(dop -> dop.getFifo().getTarget()).toList();
    final List<
        AbstractActor> seedPred = seed.getDataInputPorts().stream().map(dop -> dop.getFifo().getSource()).toList();

    final List<AbstractActor> seedNeighbors = new ArrayList<>(seedSucc);
    seedNeighbors.addAll(seedPred);

    // Iterating on direct neighbors
    for (final AbstractActor actor : seedNeighbors) {

      // If already visited or already identified in a cluster, this actor is not eligible
      if (identifiedActors.contains(actor) || visited.contains(actor)) {
        continue;
      }
      final boolean mergeable = heuristic.assessMergeable(seed, actor);
      if (mergeable) {

        // If actor is mergeable, exploring the neighbors of the current actor as a seed
        final Set<AbstractActor> successorList = buildMergeList(actor, visited, identifiedActors, heuristic);
        actorsToMerge.addAll(successorList);
      }
    }
    return actorsToMerge;
  }

  /**
   * A loop and an init function will be generated for each cluster. A cluster need a refinement so that the top graph
   * that is scheduled in a more conventional way can consider the cluster as a classic actor. For now, the cluster can
   * only have a {@link CHeaderRefinement}. It has to be changed if multiple languages (Other than C and its derivatives
   * like C++, OpenMP, or CUDA) are supported by PREESM.
   *
   * @param cluster
   *          The given cluster. A refinement will be added to it.
   * @param scenario
   *          The given scenario
   */
  public static void buildClusterRefinement(PiGraph cluster, Scenario scenario) {

    // extract function's arguments
    final CHeaderRefinement clusterHeader = PiMMUserFactory.instance.createCHeaderRefinement();
    clusterHeader.setFilePath(scenario.getCodegenDirectory() + File.separator + cluster.getName() + ".h");

    final FunctionPrototype initPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    initPrototype.setName(ClusterHelper.getInitPrototypeName(cluster));
    final FunctionPrototype loopPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    loopPrototype.setName(ClusterHelper.getLoopPrototypeName(cluster));
    clusterHeader.setLoopPrototype(loopPrototype);
    clusterHeader.setInitPrototype(initPrototype);

    final List<Port> clusterInputsOutputs = new ArrayList<>();
    clusterInputsOutputs.addAll(cluster.getConfigInputPorts());
    clusterInputsOutputs.addAll(cluster.getAllDataPorts());

    final List<FunctionArgument> initArgs = new ArrayList<>();
    final List<FunctionArgument> loopArgs = new ArrayList<>();

    for (int i = 0; i < clusterInputsOutputs.size(); i++) {
      final Port port = clusterInputsOutputs.get(i);
      final FunctionArgument loopArg = PiMMUserFactory.instance.createFunctionArgument();
      loopArg.setDirection(port instanceof DataOutputPort ? Direction.OUT : Direction.IN);
      loopArg.setIsConfigurationParameter(port instanceof ConfigInputPort);
      loopArg.setIsPassedByReference(port instanceof DataPort);
      loopArg.setName(clusterInputsOutputs.get(i).getName());

      if (port instanceof ConfigInputPort) {
        loopArg.setType("int");

        // We have to recreate a function argument for init, otherwise it can't be in two lists at a time
        final FunctionArgument initArg = PiMMUserFactory.instance.createFunctionArgument();
        initArg.setDirection(loopArg.getDirection());
        initArg.setIsConfigurationParameter(true);
        initArg.setIsPassedByReference(false);
        initArg.setName(port.getName());
        initArg.setType("int");
        initArgs.add(initArg);

      } else if (port instanceof final DataInputPort dip) {
        loopArg.setType(dip.getFifo().getType());
      } else if (port instanceof final DataOutputPort dop) {
        loopArg.setType(dop.getFifo().getType());
      } else {
        throw new PreesmRuntimeException("Port" + port.getName() + " is neither config nor data input/output.");
      }
      loopArgs.add(loopArg);
    }
    loopPrototype.getArguments().addAll(loopArgs);
    initPrototype.getArguments().addAll(initArgs);

    cluster.setRefinement(clusterHeader);
  }

}
