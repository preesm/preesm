/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021 - 2022)
 * Mickael Dardaillon [mickael.dardaillon@insa-rennes.fr] (2022 - 2024)
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

package org.preesm.algorithm.mparameters;

import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.AsapFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysis;
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
      res = FpgaAnalysis.checkAndAnalyzeAlgorithm(graph, scenario, AsapFpgaFifoEvaluator.FIFO_EVALUATOR_AVG);
    } catch (final PreesmRuntimeException e) {
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
    final long memory = architecture.getOperatorComponentInstances().stream()
        .filter(x -> (x.getComponent() instanceof FPGA)).collect(Collectors.summingLong(res.statGenerator::getMem));

    // put back all messages
    PreesmLogger.getLogger().setLevel(backupLevel);

    return new DSEpointIR(0L, latency, lastEndTime, memory, 0, 0, null, true);
  }

}
