package org.preesm.codegen.xtend.spider2;

import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * The Class Spider2Codegen.
 */
public class Spider2Codegen {
  /** The scenario. */
  private final Scenario scenario;

  /** The architecture */
  final Design architecture;

  /** The application graph */
  final PiGraph applicationGraph;

  /**
   * Instantiates a new spider2 codegen.
   *
   * @param scenario
   *          the scenario
   * @param architecture
   *          the architecture
   */
  public Spider2Codegen(final Scenario scenario, final Design architecture, final PiGraph applicationGraph) {
    this.scenario = scenario;
    this.architecture = architecture;
    this.applicationGraph = applicationGraph;
    /** Calls the pre-processor */
  }

}
