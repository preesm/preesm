package org.preesm.algorithm.mparameters;

import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysisMainTask;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysisMainTask.AnalysisResultFPGA;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;

public class ConfigurationSchedulerFPGA extends AbstractConfigurationScheduler {

  @Override
  public boolean supportsMemoryEstimation() {
    return true;
  }

  @Override
  public boolean supportsExtraDelayCuts() {
    return false;
  }

  @Override
  public DSEpointIR runConfiguration(Scenario scenario, PiGraph graph, Design architecture) {
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);

    AnalysisResultFPGA res = null;
    try {
      res = FpgaAnalysisMainTask.checkAndAnalyze(graph, architecture, scenario);
    } catch (PreesmRuntimeException e) {
      // put back all messages
      PreesmLogger.getLogger().setLevel(backupLevel);
      PreesmLogger.getLogger().log(Level.WARNING, "Scheduling was impossible.", e);
      return new DSEpointIR(Long.MAX_VALUE, 0, Long.MAX_VALUE, Long.MAX_VALUE, 0, 0, null, false);
    }

    // be careful, here the latency is stored as the number of operators
    // (i.e. one FPGA with a pipeline depth being latency)
    final int latency = res.statGenerator.getNbUsedOperators();
    // and the final time is actually the graph durationII
    lastEndTime = res.statGenerator.getFinalTime();
    long memory = architecture.getOperatorComponentInstances().stream().filter(FPGA.class::isInstance)
        .collect(Collectors.summingLong(res.statGenerator::getMem));

    // put back all messages
    PreesmLogger.getLogger().setLevel(backupLevel);

    return new DSEpointIR(0L, latency, lastEndTime, memory, 0, 0, null, true);
  }

}
