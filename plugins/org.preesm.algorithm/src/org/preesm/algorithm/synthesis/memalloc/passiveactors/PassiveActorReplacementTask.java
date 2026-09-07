package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "passiveactor", name = "Passive Actor", category = "Memory Optimization",

    inputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Input PiGraph algorithm") },

    outputs = { @Port(name = "PiMM", type = PiGraph.class, description = """
        Output PiGraph algorithm, with passive actors instead of special actors and classic actors
        with passive scripts paths""") },

    parameters = { @Parameter(name = "alignment", values = {}) },

    description = "",

    shortDescription = "",

    seeAlso = ""

)
public class PassiveActorReplacementTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph graph = (PiGraph) inputs.get("PiMM");

    final long alignment = Long.parseLong(parameters.get("alignment"));

    final PassiveActorEngine engine = new PassiveActorEngine(graph, alignment);
    engine.findAndComputePassiveActors();
    // engine.composePassiveActors();

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put("PiMM", graph);
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
