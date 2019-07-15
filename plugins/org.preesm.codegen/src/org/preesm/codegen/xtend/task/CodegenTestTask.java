package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
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

    inputs = { @Port(name = "Schedules", type = Map.class), @Port(name = "PiMM", type = PiGraph.class),
        @Port(name = "Scenario", type = Scenario.class), @Port(name = "Architecture", type = Design.class) },
    description = "Workflow task responsible for codegen testing."

)
public class CodegenTestTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final PiGraph algorithm = (PiGraph) inputs.get("PiMM");
    final Map<AbstractActor, Schedule> mapper = (Map<AbstractActor, Schedule>) inputs.get("Schedules");
    final Scenario scenario = (Scenario) inputs.get("Scenario");
    final Design architecture = (Design) inputs.get("Architecture");

    CoreBlock cb = CodegenModelUserFactory.createCoreBlock();
    cb.setCoreID(0);
    cb.setName(scenario.getSimulationInfo().getMainOperator().getInstanceName());
    cb.setCoreType("x86");
    List<Block> block = new LinkedList<>();
    block.add(cb);

    final String codegenPath = scenario.getCodegenDirectory() + File.separator;
    CodegenClusterModelGenerator modelGen = new CodegenClusterModelGenerator(architecture, algorithm, scenario,
        workflow, mapper);
    modelGen.generate();
    CodegenEngine engine = new CodegenEngine(codegenPath, block, modelGen);
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
