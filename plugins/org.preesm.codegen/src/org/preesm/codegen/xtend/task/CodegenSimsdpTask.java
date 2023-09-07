package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.generator.CodegenModelGeneratorSimSDP;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

public class CodegenSimsdpTask extends AbstractTaskImplementation {
  public static final String PARAM_PRINTER    = "Printer";
  public static final String VALUE_PRINTER_IR = "IR";
  public static final String PARAM_NODE_NAME  = "NodeName";
  public static final String VALUE_NODE_NAME  = "Node0";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Design archi = (Design) inputs.get("architecture");
    final PiGraph topGraph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final String printer = parameters.get(CodegenSimsdpTask.PARAM_PRINTER);
    final String paramNamde = parameters.get(CodegenSimsdpTask.PARAM_NODE_NAME);
    // fill struct
    final Map<Long, String> nodeNames = new LinkedHashMap<>();

    final CodegenModelGeneratorSimSDP generator = new CodegenModelGeneratorSimSDP(archi, topGraph);
    final String apolloFlag = "false";
    final Collection<Block> codeBlocks = generator.generate(nodeNames);
    final String selectedPrinter = printer;
    final String codegenPath = scenario.getCodegenDirectory() + File.separator;
    final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, topGraph, archi, scenario);
    if (CodegenSimsdpTask.VALUE_PRINTER_IR.equals(selectedPrinter)) {
      engine.initializePrinterIR(codegenPath);
    }
    engine.registerApollo(apolloFlag);
    engine.registerPrintersAndBlocks(selectedPrinter);
    engine.preprocessPrinters();
    // engine.printMainSimSDP();

    return new LinkedHashMap<>();
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
