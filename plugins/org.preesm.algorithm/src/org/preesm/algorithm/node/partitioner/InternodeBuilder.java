package org.preesm.algorithm.node.partitioner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
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
import org.preesm.model.slam.Design;
import org.preesm.model.slam.generator.ArchitecturesGenerator;
import org.preesm.ui.utils.FileUtils;

public class InternodeBuilder {
  private final Scenario          scenario;
  private final List<PiGraph>     subGraphs;
  int                             nodeIndex = 0;
  private final List<NodeMapping> hierarchicalArchitecture;

  private String codegenPath  = "";
  private String graphPath    = "";
  private String scenarioPath = "";
  private String archiPath    = "";

  public InternodeBuilder(Scenario scenario, List<PiGraph> subGraphs, List<NodeMapping> hierarchicalArchitecture) {
    this.subGraphs = subGraphs;
    this.scenario = scenario;
    this.hierarchicalArchitecture = hierarchicalArchitecture;
  }

  public PiGraph execute() {
    initPath();
    final PiGraph topGraph = constructTop();

    graphExporter(topGraph, graphPath);
    graphExporter(topGraph, graphPath + "top/");// top folder used by SimGrid
    scenarioExporter(topGraph);
    return topGraph;

  }

  private void initPath() {

    final String graphFilePath = scenario.getAlgorithm().getUrl();
    graphPath = graphFilePath.substring(0, graphFilePath.lastIndexOf("/") + 1) + "generated/";

    final String scenarioFilePath = scenario.getScenarioURL();
    scenarioPath = scenarioFilePath.substring(0, scenarioFilePath.lastIndexOf("/") + 1) + "generated/";

    codegenPath = scenario.getCodegenDirectory() + "/top/";

    final String archiFilePath = scenario.getDesign().getUrl();
    archiPath = archiFilePath.substring(0, archiFilePath.lastIndexOf("/") + 1);
  }

  private PiGraph constructTop() {

    final PiGraph topGraph = subGraphs.stream().filter(sub -> !sub.getName().contains("sub")).findAny()
        .orElseThrow(PreesmRuntimeException::new);

    topGraph.setName("top");
    emptyTop(topGraph);
    // 2. insert delay
    pipelineTop(topGraph);

    // remove extra parameter
    for (final AbstractActor a : topGraph.getExecutableActors()) {
      final List<String> cfgOccur = new ArrayList<>();

      final Iterator<ConfigInputPort> iter = a.getConfigInputPorts().iterator();
      while (iter.hasNext()) {

        final ConfigInputPort cin = iter.next();

        cin.setName(((AbstractVertex) cin.getIncomingDependency().getSetter()).getName());
        final String name = cin.getName();

        if (cfgOccur.contains(cin.getName())) {
          topGraph.removeDependency(cin.getIncomingDependency());
          iter.remove();
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

    topGraph.setUrl(graphPath + topGraph.getName() + ".pi");
    for (final AbstractActor pi : topGraph.getActors()) {
      if (!(pi instanceof PiGraph)) {
        continue;
      }

      final Actor aEmpty = PiMMUserFactory.instance.createActor();
      aEmpty.setName(pi.getName());

      for (final DataInputPort din : pi.getDataInputPorts()) {
        final DataInputPort inputPort = PiMMUserFactory.instance.copy(din);
        aEmpty.getDataInputPorts().add(inputPort);
      }

      for (final DataOutputPort dout : pi.getDataOutputPorts()) {
        final DataOutputPort outputPort = PiMMUserFactory.instance.copy(dout);
        aEmpty.getDataOutputPorts().add(outputPort);
      }

      for (final ConfigInputPort cin : pi.getConfigInputPorts()) {
        final ConfigInputPort cfgInputPort = PiMMUserFactory.instance.copy(cin);
        aEmpty.getConfigInputPorts().add(cfgInputPort);
      }

      topGraph.replaceActor(pi, aEmpty);
      nodeIndex++;
    }
    return nodeIndex;
  }

  private void scenarioExporter(PiGraph topGraph) {

    final IPath fromPortableString = Path.fromPortableString(archiPath);
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

    new ScenarioBuilder(topGraph, topArchi, scenarioPath, codegenPath + topGraph.getName(), this.scenario).topExecute();

  }

  private void graphExporter(PiGraph printgraph, String graphPath) {
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

    PreesmLogger.getLogger().log(Level.INFO, () -> "top print in : " + graphPath);
    WorkspaceUtils.updateWorkspace();
  }

}
