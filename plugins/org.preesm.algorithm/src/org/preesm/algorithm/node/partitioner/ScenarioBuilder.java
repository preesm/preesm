package org.preesm.algorithm.node.partitioner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.generator.ScenariosGenerator;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

public class ScenarioBuilder {
  PiGraph  graph;
  Design   archi;
  String   scenariiPath;
  String   codegenPath;
  Scenario originalScenario;

  public ScenarioBuilder(PiGraph graph, Design archi, String scenariiPath, String codegenPath,
      Scenario genuineScenario) {
    this.graph = graph;
    this.archi = archi;
    this.scenariiPath = scenariiPath;
    this.codegenPath = codegenPath;
    this.originalScenario = genuineScenario;

  }

  public void topExecute() {
    final Scenario topScenario = ScenarioUserFactory.createScenario();
    defaultInfo(topScenario);
    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    // for dedicated node, allow mapping on it
    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor aa : graph.getAllActors()) {
        if (aa instanceof Actor
            && coreId.getInstanceName().replace("Node", "").equals(aa.getName().replace("sub", ""))) {

          topScenario.getConstraints().addConstraint(coreId, aa);
        }

      }
      topScenario.getSimulationInfo().addSpecialVertexOperator(coreId);
    }
    // file timing
    topScenario.getTimings().setExcelFileURL(scenariiPath + "top_tim.csv");

    scenarioExporter(topScenario);
  }

  public void subExecute() {

    if (graph.getActors().isEmpty()) {

    }
  }

  // store timing in case
  csvGenerator(final subScenario, graph, archi);

      final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
      // for all different type of cores, allow mapping on it
      final ComponentInstance mainOp = coreIds.get(0);
      for (final ComponentInstance coreId : coreIds) {
        for (final AbstractActor aa : graph.getAllActors()) {
          if ((aa.getName().contains("src_") || aa.getName().contains("snk_")) && coreId != mainOp) {
            continue;
          }
          subScenario.getConstraints().addConstraint(coreId, aa);

        }
        // Add special actors operator id (all cores can execute special
        // actors)
        subScenario.getSimulationInfo().addSpecialVertexOperator(coreId);
      }

      scenarioExporter(subScenario);

    }

  final Scenario subScenario = ScenarioUserFactory.createScenario();

  defaultInfo(subScenario);

    // for all different type of cores, add default timing
    for (final Component opId : archi.getProcessingElements()) {
      for (final AbstractActor aa : graph.getAllActors()) {
        subScenario.getTimings().setExecutionTime(aa, opId, originalScenario.getTimings().getExecutionTimeOrDefault(aa,
            originalScenario.getDesign().getProcessingElement(opId.getVlnv().getName())));
      }
    }

    // store timing in case
    csvGenerator(subScenario, graph, archi);

    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    // for all different type of cores, allow mapping on it
    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor aa : graph.getAllActors()) {
        subScenario.getConstraints().addConstraint(coreId, aa);
      }
      // Add special actors operator id (all cores can execute special actors)
      subScenario.getSimulationInfo().addSpecialVertexOperator(coreId);
    }

    scenarioExporter(subScenario);
  }

  private void defaultInfo(Scenario scenario) {
    scenario.setDesign(archi);

    scenario.setAlgorithm(graph);
    // Get com nodes and cores names
    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    final List<ComponentInstance> comNodeIds = archi.getCommunicationComponentInstances();

    // Add a main core (first of the list)
    if (!coreIds.isEmpty()) {
      scenario.getSimulationInfo().setMainOperator(coreIds.get(0));
    }
    if (!comNodeIds.isEmpty()) {
      scenario.getSimulationInfo().setMainComNode(comNodeIds.get(0));
    }
    // Add a average transfer size
    scenario.getSimulationInfo().setAverageDataSize(originalScenario.getSimulationInfo().getAverageDataSize());

    scenario.getSimulationInfo().getDataTypes().addAll(originalScenario.getSimulationInfo().getDataTypes());

    scenario.setSizesAreInBit(true);
    scenario.setCodegenDirectory(codegenPath);
    scenario.setScenarioURL(scenariiPath + scenario.getScenarioName() + ".scenario");

  }

  private void scenarioExporter(Scenario subScenario) {

    final ScenariosGenerator s = new ScenariosGenerator(iproject(scenariiPath));
    final IFolder scenarioDir = iproject(scenariiPath).getFolder("Scenarios/generated");
    final Set<Scenario> scenarios = new HashSet<>();
    scenarios.add(subScenario);
    try {
      s.saveScenarios(scenarios, scenarioDir);
    } catch (final CoreException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, () -> "Error occurred during file generation: " + e.getMessage());
    }
    PreesmLogger.getLogger().log(Level.INFO, () -> "scenario print in : " + scenariiPath);

  }

  public static IProject iproject(String path) {
    final IPath fromPortableString = Path.fromPortableString(path);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    return file.getProject();
  }

  /**
   * export CSV file containing subgraph actor timing
   *
   * @param scenar
   *          scenario to consider.
   * @param pigraph
   *          Graph to consider.
   * @param architecture
   *          architecture to consider.
   *
   */
  private void csvGenerator(Scenario scenar, PiGraph pigraph, Design architecture) {
    // Create the list of core_types
    final List<Component> coreTypes = new ArrayList<>(architecture.getProcessingElements()); // List of core_types

    // Create the CSV header
    final StringBuilder content = new StringBuilder();
    content.append("Actors;");
    for (final Component coreType : coreTypes) {
      coreType.getVlnv().getName();
      content.append(coreType.getVlnv().getName() + ";");
    }

    content.append("\n");

    // Create data rows
    for (final AbstractActor actor : pigraph.getActors()) {
      if (actor instanceof Actor && !(actor instanceof SpecialActor)) {
        content.append(actor.getVertexPath().replace(graph.getName() + "/", "") + ";");

        for (final Component coreType : coreTypes) {
          final String executionTime = scenar.getTimings().getExecutionTimeOrDefault(actor, coreType);
          content.append(executionTime + ";");
        }

        content.append("\n");
      }
    }

    scenar.getTimings().setExcelFileURL(scenariiPath + pigraph.getName() + ".csv");
    PreesmIOHelper.getInstance().print(scenariiPath, pigraph.getName() + ".csv", content);
    final String message = "sub csv print in : " + scenariiPath;
    PreesmLogger.getLogger().log(Level.INFO, message);

  }

}
