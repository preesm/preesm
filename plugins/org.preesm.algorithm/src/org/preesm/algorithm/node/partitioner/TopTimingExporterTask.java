package org.preesm.algorithm.node.partitioner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.generator.ScenariosGenerator;
import org.preesm.model.scenario.serialize.ScenarioParser;
import org.preesm.model.slam.Component;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class fills the csv for the timing of topgraph tasks
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "TopTimingExporterTask.identifier", name = "Top Timing Exporter", category = "CSV exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) },

    shortDescription = "This task exports scheduling results as a *.csv file .")
public class TopTimingExporterTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {

    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);
    final String scenarioPath = "/" + workflow.getProjectName() + "/Scenarios/generated/";
    final String fileName = "top_tim.csv";
    final String filePath = scenarioPath;
    final StringConcatenation content = new StringConcatenation();
    final ScenarioParser scenarioParser = new ScenarioParser();
    // if the file exists, we write to it otherwise we create the template
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + fileName));

    if (!iFile.exists()) {
      content.append("Actors;node;\n");
    } else {

      InputStream inputStream;
      try {
        inputStream = iFile.getContents();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line + "\n");
        }
        inputStream.close();

      } catch (CoreException | IOException e) {
        throw new PreesmRuntimeException("Could not generate source file for " + fileName, e);
      }
    }
    content.append("top/" + abc.getScenario().getAlgorithm().getName() + ";" + abc.getFinalLatency() + "; \n");

    PreesmIOHelper.getInstance().print(scenarioPath, fileName, content);
    final String scenarioName = "top_top.scenario";
    final IFile iFileScenario = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + scenarioName));

    Scenario scenario;
    try {
      scenario = scenarioParser.parseXmlFile(iFileScenario);
      for (final Component opId : scenario.getDesign().getProcessingElements()) {
        for (final AbstractActor actor : scenario.getAlgorithm().getExecutableActors()) {
          if (actor.getName().equals(abc.getScenario().getAlgorithm().getName())) {
            scenario.getTimings().setExecutionTime(actor, opId, abc.getFinalLatency());
          }
        }
      }
      final ScenariosGenerator s = new ScenariosGenerator(iFileScenario.getProject());
      s.saveScenario(scenario, iFileScenario);
    } catch (final FileNotFoundException | CoreException e) {
      throw new PreesmRuntimeException("Could not generate source file for " + scenarioName, e);
    }

    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generate the topgraph timing file.";
  }

}
