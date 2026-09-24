package org.preesm.algorithm.synthesis.memalloc;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "pi.alloc", name = "Memory Allocation from PiSDF", category = "Allocators",

    inputs = { @Port(name = "Schedule", type = Schedule.class), @Port(name = "scenario", type = Scenario.class), },

    outputs = { @Port(name = "Allocation", type = Allocation.class) },

    parameters = { @Parameter(name = "Alloc type", values = {}) })

public class SrDAGLessMemoryAllocationTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Schedule schedule = (Schedule) inputs.get("Schedule");

    final String allocType = parameters.get("Alloc type");
    Allocation result;

    if ("simple".equals(allocType)) {
      result = new SrDAGLessMemoryAllocationScheduleSwitch().allocateMemory(null, null, scenario, schedule, null);
    } else if ("passive".equals(allocType)) {
      result = new PassiveMemoryAllocationScheduleSwitch().allocateMemory(null, null, scenario, schedule, null);
    } else {
      throw new PreesmRuntimeException("pi Alloc type " + allocType + " is not supported.");
    }

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put("Allocation", result);

    return outputs;

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
