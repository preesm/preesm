package org.preesm.algorithm.mapper.ui.stats;

import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Generates stats for the mapper UI (abstract)
 * 
 * @author ahonorat
 */
public abstract class AbstractStatGenerator implements IStatGenerator {

  protected final Design   architecture;
  protected final Scenario scenario;

  public AbstractStatGenerator(final Design architecture, final Scenario scenario) {
    this.architecture = architecture;
    this.scenario = scenario;
  }

  /**
   * Returns the number of operators with main type.
   *
   * @return the nb main type operators
   */
  public int getNbMainTypeOperators() {
    int nbMainTypeOperators = 0;
    final ComponentInstance mainOp = scenario.getSimulationInfo().getMainOperator();
    nbMainTypeOperators = architecture.getComponentInstancesOfType(mainOp.getComponent()).size();
    return nbMainTypeOperators;
  }

  public Design getDesign() {
    return architecture;
  }

  public Scenario getScenario() {
    return scenario;
  }

}
