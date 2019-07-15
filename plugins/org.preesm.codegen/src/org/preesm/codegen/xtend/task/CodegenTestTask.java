package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.codegen.model.Block;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * @author dgageot
 *
 */
@PreesmTask(id = "org.ietr.preesm.codegentesttask", name = "CodegenTest",

    inputs = { @Port(name = "schedules", type = Map.class), @Port(name = "PiMM", type = PiGraph.class),
        @Port(name = "scenario", type = Scenario.class), @Port(name = "architecture", type = Design.class) },
    description = "Workflow task responsible for codegen testing."

)
public class CodegenTestTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final PiGraph algorithm = (PiGraph) inputs.get("PiMM");
    final Map<AbstractActor, Schedule> mapper = (Map<AbstractActor, Schedule>) inputs.get("schedules");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design architecture = (Design) inputs.get("architecture");

    final String codegenPath = scenario.getCodegenDirectory() + File.separator;
    CodegenClusterModelGenerator modelGen = new CodegenClusterModelGenerator(architecture, algorithm, scenario,
        workflow, mapper);
    List<Block> blocks = modelGen.generate();
    CodegenEngine engine = new CodegenEngine(codegenPath, blocks, modelGen);
    engine.registerPrintersAndBlocks("C");
    engine.preprocessPrinters();
    engine.print();

    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "CodegenTest";
  }

}
