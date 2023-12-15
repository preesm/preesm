package org.preesm.algorithm.node.partitioner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.preesm.algorithm.mapping.model.NodeMapping;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.serialize.PiWriter;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.generator.ScenariosGenerator;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.generator.ArchitecturesGenerator;
import org.preesm.ui.utils.FileUtils;

public class InternodeBuilder {
  private final Scenario          scenario;
  private final List<PiGraph>     subGraphs;
  int                             nodeIndex = 0;
  private final List<NodeMapping> hierarchicalArchitecture;
  static String                   fileError = "Error occurred during file generation: ";

  public InternodeBuilder(Scenario scenario, List<PiGraph> subGraphs, List<NodeMapping> hierarchicalArchitecture) {
    this.subGraphs = subGraphs;
    this.scenario = scenario;
    this.hierarchicalArchitecture = hierarchicalArchitecture;
  }

  public PiGraph execute() {
    final String[] uriString = scenario.getAlgorithm().getUrl().split("/");
    final String projectPath = "/" + uriString[1] + "/" + uriString[2];// projectName/Algo
    final PiGraph topGraph = constructTop();

    graphExporter(topGraph, projectPath, "/generated/");
    graphExporter(topGraph, projectPath, "/generated/top/");// top folder used by SimGrid
    scenarioExporter(topGraph);
    return topGraph;

  }

  private PiGraph constructTop() {
    PiGraph topGraph = PiMMUserFactory.instance.createPiGraph();

    for (final PiGraph sub : subGraphs) {
      if (!sub.getName().contains("sub")) {
        topGraph = sub;
      }
    }
    topGraph.setName("top");
    emptyTop(topGraph);
    // 2. insert delay
    pipelineTop(topGraph);

    // remove extra parameter
    for (final AbstractActor a : topGraph.getExecutableActors()) {
      final List<String> cfgOccur = new ArrayList<>();
      for (int i = 0; i < a.getConfigInputPorts().size(); i++) {
        a.getConfigInputPorts().get(i)
            .setName(((AbstractVertex) a.getConfigInputPorts().get(i).getIncomingDependency().getSetter()).getName());
        final String name = a.getConfigInputPorts().get(i).getName();

        if (cfgOccur.contains(a.getConfigInputPorts().get(i).getName())) {
          topGraph.removeDependency(a.getConfigInputPorts().get(i).getIncomingDependency());
          a.getConfigInputPorts().remove(a.getConfigInputPorts().get(i));
          i--;
        }
        cfgOccur.add(name);
      }
    }
    for (final Parameter param : topGraph.getAllParameters()) {
      for (final Dependency element : param.getOutgoingDependencies()) {
        if (element.getContainingGraph() != topGraph) {
          element.setContainingGraph(topGraph);
        }
      }
    }
    for (final Dependency i : topGraph.getAllDependencies()) {

      final boolean getterContained = i.getGetter().getConfigurable() != null;
      if (!getterContained) {
        i.getSetter().getOutgoingDependencies().remove(i);
        topGraph.removeDependency(i);
      }
    }

    return topGraph;
  }

  private void pipelineTop(PiGraph topGraph) {
    int index = 0;
    for (final Fifo f : topGraph.getFifos()) {
      final Delay d = PiMMUserFactory.instance.createDelay();
      d.setName(((AbstractActor) f.getSource()).getName() + "_out_" + ((AbstractActor) f.getTarget()).getName() + "_in_"
          + index);
      d.setLevel(PersistenceLevel.PERMANENT);
      d.setExpression(f.getSourcePort().getExpression().evaluate());
      d.setContainingGraph(f.getContainingGraph());
      f.assignDelay(d);
      d.getActor().setContainingGraph(f.getContainingGraph());
      index++;
    }
  }

  private int emptyTop(PiGraph topGraph) {

    final String[] uriString = scenario.getAlgorithm().getUrl().split("/");
    final String graphPath = "/" + uriString[1] + "/" + uriString[2] + "/generated/";
    topGraph.setUrl(graphPath + topGraph.getName() + ".pi");
    for (final AbstractActor pi : topGraph.getActors()) {
      if (pi instanceof PiGraph) {
        final Actor aEmpty = PiMMUserFactory.instance.createActor();
        aEmpty.setName(pi.getName());
        for (int i = 0; i < pi.getDataInputPorts().size(); i++) {
          final DataInputPort inputPort = PiMMUserFactory.instance.copy(pi.getDataInputPorts().get(i));
          aEmpty.getDataInputPorts().add(inputPort);
        }
        for (int i = 0; i < pi.getDataOutputPorts().size(); i++) {
          final DataOutputPort outputPort = PiMMUserFactory.instance.copy(pi.getDataOutputPorts().get(i));
          aEmpty.getDataOutputPorts().add(outputPort);
        }
        for (int i = 0; i < pi.getConfigInputPorts().size(); i++) {
          final ConfigInputPort cfgInputPort = PiMMUserFactory.instance.copy(pi.getConfigInputPorts().get(i));
          aEmpty.getConfigInputPorts().add(cfgInputPort);
        }
        topGraph.replaceActor(pi, aEmpty);
        nodeIndex++;
      }
    }
    return nodeIndex;
  }

  private void scenarioExporter(PiGraph topGraph) {
    final String[] uriString = scenario.getAlgorithm().getUrl().split("/");
    final String scenariiPath = "/" + uriString[1] + "/Scenarios/generated/";
    final String archiPath = "/" + uriString[1] + "/Archi/";
    final IPath fromPortableString = Path.fromPortableString(scenariiPath);
    final IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    final IProject iProject = file2.getProject();
    final ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
    final Map<String, Integer> nodeList = new HashMap<>();
    nodeList.put("node", nodeIndex);
    final Design topArchi = ArchitecturesGenerator.generateArchitecture(nodeList, "top",
        hierarchicalArchitecture.get(0).getNodeCommunicationRate());
    a.saveArchitecture(topArchi);
    topArchi.setUrl(archiPath + "top.slam");
    // 4. generate scenario
    final Scenario topScenario = ScenarioUserFactory.createScenario();
    topScenario.setAlgorithm(topGraph);
    topScenario.setDesign(topArchi);
    final List<ComponentInstance> coreIds = new ArrayList<>(topArchi.getOperatorComponentInstances());
    final List<ComponentInstance> comNodeIds = topArchi.getCommunicationComponentInstances();

    // Add a main core (first of the list)
    if (!coreIds.isEmpty()) {
      topScenario.getSimulationInfo().setMainOperator(coreIds.get(0));
    }
    if (!comNodeIds.isEmpty()) {
      topScenario.getSimulationInfo().setMainComNode(comNodeIds.get(0));
    }

    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor aa : topGraph.getAllActors()) {

        if (aa instanceof Actor) {
          if (coreId.getInstanceName().replace("Node", "").equals(aa.getName().replace("sub", ""))) {

            topScenario.getConstraints().addConstraint(coreId, aa);
          }

        }
      }
      topScenario.getSimulationInfo().addSpecialVertexOperator(coreId);
    }
    // Add a average transfer size
    topScenario.getSimulationInfo().setAverageDataSize(scenario.getSimulationInfo().getAverageDataSize());
    // Set the default data type sizes
    // for (final Fifo f : topScenario.getAlgorithm().getAllFifos()) {
    // final String typeName = f.getType();
    // topScenario.getSimulationInfo().getDataTypes().put(typeName,
    // DefaultTypeSizes.getInstance().getTypeSize(typeName));
    // }
    for (final Entry<String, Long> element : scenario.getSimulationInfo().getDataTypes()) {
      final String key = element.getKey();
      final Long value = element.getValue();
      topScenario.getSimulationInfo().getDataTypes().put(key, value);
    }

    topScenario.setCodegenDirectory(scenario.getCodegenDirectory() + "/top");
    topScenario.setSizesAreInBit(true);
    topScenario.setScenarioURL(scenariiPath + topScenario.getScenarioName() + ".scenario");
    topScenario.getTimings().setExcelFileURL(scenariiPath + "top_tim.csv");
    for (final Component opId : topArchi.getProcessingElements()) {
      // topScenario.getTimings().getMemTimings().put(opId, scenario.getTimings().getMemTimings().get(0).getValue());
    }

    final ScenariosGenerator s = new ScenariosGenerator(iProject);
    final IFolder scenarioDir = iProject.getFolder("Scenarios/generated");
    final Set<Scenario> scenarios = new HashSet<>();
    scenarios.add(topScenario);
    try {
      s.saveScenarios(scenarios, scenarioDir);
    } catch (final CoreException e) {
      final String errorMessage = fileError + e.getMessage();
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
    PreesmLogger.getLogger().log(Level.INFO, "top scenario print in : " + scenariiPath);

  }

  // private void graphExporter(PiGraph printgraph) {
  // final String[] uriString = scenario.getAlgorithm().getUrl().split("/");
  // final String graphPath = "/" + uriString[1] + "/" + uriString[2] + "/generated/";
  // printgraph.setUrl(graphPath + printgraph.getName() + ".pi");
  // PiBRV.compute(printgraph, BRVMethod.LCM);
  //
  // final IPath fromPortableString = Path.fromPortableString(graphPath);
  // final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
  // final IProject iProject = file.getProject();
  // final String fileName = printgraph.getName() + "" + ".pi";
  // final URI uri = FileUtils.getPathToFileInFolder(iProject, fromPortableString, fileName);
  //
  // // Get the project
  // final String platformString = uri.toPlatformString(true);
  // final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
  // final String osString = documentFile.getLocation().toOSString();
  // try (final OutputStream outStream = new FileOutputStream(osString);) {
  // // Write the Graph to the OutputStream using the Pi format
  // new PiWriter(uri).write(printgraph, outStream);
  // } catch (final IOException e) {
  // throw new PreesmRuntimeException("Could not open outputstream file " + uri.toPlatformString(false));
  // }
  //
  // PreesmLogger.getLogger().log(Level.INFO, "top print in : " + graphPath);
  // WorkspaceUtils.updateWorkspace();
  //
  // }
  private void graphExporter(PiGraph printgraph, String projectPath, String folderPath) {
    // final String[] uriString = scenario.getAlgorithm().getUrl().split("/");
    final String graphPath = projectPath + folderPath;
    printgraph.setUrl(graphPath + printgraph.getName() + ".pi");
    PiBRV.compute(printgraph, BRVMethod.LCM);

    final IPath fromPortableString = Path.fromPortableString(graphPath);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    final IProject iProject = file.getProject();
    final String fileName = printgraph.getName() + "" + ".pi";
    final URI uri = FileUtils.getPathToFileInFolder(iProject, fromPortableString, fileName);

    // Get the project
    final String platformString = uri.toPlatformString(true);
    final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
    final String osString = documentFile.getLocation().toOSString();
    try (final OutputStream outStream = new FileOutputStream(osString);) {
      // Write the Graph to the OutputStream using the Pi format
      new PiWriter(uri).write(printgraph, outStream);
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not open outputstream file " + uri.toPlatformString(false));
    }

    PreesmLogger.getLogger().log(Level.INFO, "top print in : " + graphPath);
    WorkspaceUtils.updateWorkspace();

  }
}
