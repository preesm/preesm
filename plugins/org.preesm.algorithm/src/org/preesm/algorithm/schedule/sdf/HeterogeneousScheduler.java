package org.preesm.algorithm.schedule.sdf;

import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.ListSchedulingMappingFromPiMM;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.LegacyListScheduler;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;

public class HeterogeneousScheduler {
  // old API
  public static Map<String, Object> schedule(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final ListSchedulingMappingFromPiMM listSchedulingMappingFromPiMM = new ListSchedulingMappingFromPiMM();
    return listSchedulingMappingFromPiMM.execute(inputs, parameters, monitor, nodeName, workflow);
  }

  // new API
  public static SynthesisResult schedule(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {
    final var listScheduler = new LegacyListScheduler();
    return listScheduler.scheduleAndMap(piGraph, slamDesign, scenario);
  }

}
