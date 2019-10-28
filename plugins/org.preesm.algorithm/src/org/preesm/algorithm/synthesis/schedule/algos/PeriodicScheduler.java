package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.logging.Level;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This scheduler handles the periods defined in the PiGraph and in its actors. However, it does not take into account
 * communication time.
 * 
 * @author ahonorat
 */
public class PeriodicScheduler extends AbstractScheduler {

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario) {

    if (slamDesign.getOperatorComponents().size() != 1) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous architecture, abandon.");
    }

    int nbCore = slamDesign.getOperatorComponents().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCore + " cores.");

    long graphPeriod = piGraph.getPeriod().evaluate();
    PreesmLogger.getLogger().log(Level.INFO, "Graph period is: " + graphPeriod);

    throw new PreesmRuntimeException("It stops here for now!");
  }

}
