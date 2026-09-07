package org.preesm.algorithm.clustering.heuristics;

import org.preesm.algorithm.clustering.balancing.BasicBalancing;
import org.preesm.algorithm.clustering.balancing.CompleteBalancing;
import org.preesm.algorithm.clustering.identification.SRVHeuristic;
import org.preesm.algorithm.clustering.identification.SimpleFPGAIdentifier;
import org.preesm.algorithm.clustering.identification.URCHeuristic;
import org.preesm.algorithm.clustering.synthesis.APGANSchedulingHeuristic;
import org.preesm.algorithm.clustering.synthesis.ClassicMappingHeuristic;
import org.preesm.algorithm.clustering.synthesis.SimpleAllocationHeuristic;
import org.preesm.algorithm.clustering.synthesis.SimpleMappingHeuristic;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * Helper class that helps retrieving an {@link Heuristic heuristic} according to its name. If a new {@link Heuristic
 * heuristic} is created, it has to be registered here. There is two actions to do to add a new heuristic: - First, add
 * it in the {@link #getHeuristic(String) getHeuristic} method. Then, you can add its name as a public static final
 * attribute of the class.
 *
 * @author rcazoulat
 */
public class HeuristicGetter {

  private HeuristicGetter() {
  }

  // Default heuristic names
  public static final String CLASSIC_MAPPER         = "classic mapper";
  public static final String SIMPLE_MAPPER          = "simple mapper";
  public static final String COMPLETE_BALANCING     = "complete balancing";
  public static final String BASIC_BALANCING        = "basic balancing";
  public static final String SIMPLE_ALLOCATION      = "simple allocation";
  public static final String APGAN_SCHEDULING       = "apgan scheduling";
  public static final String URC_IDENTIFIER         = "urc";
  public static final String SRV_IDENTIFIER         = "srv";
  public static final String SIMPLE_FPGA_IDENTIFIER = "simple FPGA arch";

  /**
   * Method used to retrieve any heuristic of any kind.
   *
   * @param heuristicName
   *          the name of the heuristic (preferably described by an public static final attribute)
   * @return the heuristic linked to the name in input
   */
  public static Heuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case null -> null;
      case SRV_IDENTIFIER -> new SRVHeuristic();
      case URC_IDENTIFIER -> new URCHeuristic();
      case SIMPLE_FPGA_IDENTIFIER -> new SimpleFPGAIdentifier();
      case CLASSIC_MAPPER -> new ClassicMappingHeuristic();
      case SIMPLE_MAPPER -> new SimpleMappingHeuristic();
      case SIMPLE_ALLOCATION -> new SimpleAllocationHeuristic();
      case COMPLETE_BALANCING -> new CompleteBalancing();
      case BASIC_BALANCING -> new BasicBalancing();
      case APGAN_SCHEDULING -> new APGANSchedulingHeuristic();
      default -> throw new PreesmRuntimeException(
          "heuristicName <" + heuristicName + "> is unkown, can't retrieve the wanted heuristic.");
    };
  }
}
