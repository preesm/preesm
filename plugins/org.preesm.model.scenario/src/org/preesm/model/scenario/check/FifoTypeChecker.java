package org.preesm.model.scenario.check;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.DefaultTypeSizes;

/**
 * This class check if all fifos of the algorithm have a known data type size.
 *
 * @author ahonorat
 */
public class FifoTypeChecker {

  private FifoTypeChecker() {
    // forbid instantiation
  }

  /**
   * Get all the fifo types for which the size is not specified in the scenario.
   *
   * @param scenario
   *          To be checked.
   * @return A list of missing fifo type sizes. Empty list if no algorithm specified.
   */
  public static Set<String> getMissingFifoTypeSizes(final Scenario scenario) {
    final Set<String> result = new LinkedHashSet<>();
    final PiGraph graph = scenario.getAlgorithm();
    if (graph == null) {
      return result;
    }
    for (final Fifo f : graph.getAllFifos()) {
      final String typeName = f.getType();
      // Search for typeName in the Scenario

      // If typeName is known in the Scenario
      if (scenario.getSimulationInfo().getDataTypes().containsKey(typeName)) {
        continue;
      }

      final long typeSize = DefaultTypeSizes.getInstance().getTypeSize(typeName);

      if (typeSize != DefaultTypeSizes.UNKNOWN_TYPE) {
        // If typeName matches a default known type
        scenario.getSimulationInfo().getDataTypes().put(typeName, typeSize);
        PreesmLogger.getLogger().info(() -> "A default size of " + typeSize + " bits was used for '" + typeName
            + "' in fifo '" + f.getId() + "' for this Workflow execution.");
      } else {
        // If typeName is completely unknown
        result.add("'" + typeName + "'");
        PreesmLogger.getLogger().warning(() -> "Unknown type: " + typeName + ", in Fifo: " + f.getId());
      }
    }
    return result;
  }

  /**
   * Check if all the fifo types have a size defined in the scenario.
   *
   * @param scenario
   *          To be checked.
   * @throws PreesmRuntimeException
   *           If it is not the case.
   */
  public static void checkMissingFifoTypeSizes(final Scenario scenario) {
    final Set<String> missingDataTypeSizes = getMissingFifoTypeSizes(scenario);
    if (!missingDataTypeSizes.isEmpty()) {
      final String formattedMissingDataTypeSizes = missingDataTypeSizes.stream().collect(Collectors.joining(", "));
      throw new PreesmRuntimeException(
          "Cannot find the size of the following fifo types: " + formattedMissingDataTypeSizes + ".");
    }
  }
}
