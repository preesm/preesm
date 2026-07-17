package org.preesm.algorithm.clustering.heuristics;

import org.preesm.algorithm.clustering.identifier.SRVHeuristic;
import org.preesm.algorithm.clustering.identifier.SimpleHeteroArchClusteringHeuristic;
import org.preesm.algorithm.clustering.identifier.SimplePartitionerHeuristic;
import org.preesm.algorithm.clustering.identifier.URCHeuristic;
import org.preesm.algorithm.clustering.synthesis.APGANSchedulingHeuristic;
import org.preesm.algorithm.clustering.synthesis.SimpleAllocationHeuristic;
import org.preesm.algorithm.clustering.synthesis.SimpleMappingHeuristic;
import org.preesm.algorithm.clustering.synthesis.SmartAllocationHeuristic;

public class HeuristicGetter {

  private HeuristicGetter() {
    /* This utility class should not be instantiated */
  }

  public static final String DEFAULT_MAPPER      = "simple mapper";
  public static final String DEFAULT_PARTITIONER = "simple partitioner";
  public static final String DEFAULT_ALLOCATION  = "simple allocation";
  public static final String DEFAULT_SCHEDULING  = "apgan scheduling";
  public static final String DEFAULT_IDENTIFIER  = "urc";

  public static Heuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case null -> null;
      case "srv" -> new SRVHeuristic();
      case DEFAULT_IDENTIFIER -> new URCHeuristic();
      case "heterogeneous" -> new SimpleHeteroArchClusteringHeuristic();
      case DEFAULT_MAPPER -> new SimpleMappingHeuristic();
      case DEFAULT_ALLOCATION -> new SimpleAllocationHeuristic();
      case DEFAULT_PARTITIONER -> new SimplePartitionerHeuristic();
      case DEFAULT_SCHEDULING -> new APGANSchedulingHeuristic();
      case "smart allocation" -> new SmartAllocationHeuristic();

      default -> null;
    };
  }
}
