package org.preesm.algorithm.node.partitioner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.serialize.PiWriter;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.ui.utils.FileUtils;

/**
 * This class aims to generate time-balanced subgraphs, each associated with one node.
 *
 * @author orenaud
 *
 */
public class IntranodeBuilder {
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;

  private final Design                                 archi;
  private final PiGraph                                graph;
  private PiGraph                                      topGraph       = null;
  private final Map<AbstractVertex, Long>              brv;
  private final Map<Integer, Long>                     equivalentTimings;
  private final List<Design>                           archiList;
  private final Map<Long, List<AbstractActor>>         topoOrderASAP;
  private final Map<Integer, Map<AbstractActor, Long>> subs           = new HashMap<>();
  Map<Integer, Map<AbstractActor, Long>>               subsCopy       = new HashMap<>();
  List<PiGraph>                                        sublist        = new ArrayList<>();
  private String                                       codegenPath    = "";
  private String                                       graphPath      = "";
  static String                                        fileError      = "Error occurred during file generation: ";
  private String                                       scenariiPath   = "";
  private static final String                          INTERFACE_PATH = "interface/sub";

  public IntranodeBuilder(Scenario scenario, Map<AbstractVertex, Long> brv, Map<Integer, Long> timeEq,
      List<Design> archiList, Map<Long, List<AbstractActor>> topoOrderASAP) {

    this.scenario = scenario;
    this.graph = scenario.getAlgorithm();
    this.archi = scenario.getDesign();
    this.brv = brv;
    this.equivalentTimings = timeEq;
    this.archiList = archiList;
    this.topoOrderASAP = topoOrderASAP;
  }

  public List<PiGraph> execute() {
    initPath();

    computeSubs();

    computeSplits();

    constructSubs();

    exportSubs();
    sublist.add(topGraph);
    return sublist;

  }

  private void initPath() {
    final String[] uriString = graph.getUrl().split("/");
    graphPath = "/" + uriString[1] + "/" + uriString[2] + "/generated/";
    scenariiPath = "/" + uriString[1] + "/Scenarios/generated/";
    codegenPath = uriString[1] + "/Code/generated/";

  }

  /**
   * Export .pi and .scenario for each subgraph
   *
   */
  private void exportSubs() {
    for (final PiGraph subgraph : sublist) {
      graphExporter(subgraph);
      final String indexStr = subgraph.getName().replace("sub", "");
      final Long indexL = Long.decode(indexStr);
      final Design subArchi = archiList.get(indexL.intValue());
      new ScenarioBuilder(subgraph, subArchi, scenariiPath, codegenPath + subgraph.getName(), this.scenario)
          .subExecute();
    }

  }

  /**
   * Computes splits in the graph by generating subgraphs for specific actors based on their instances. 1. Splits tasks
   * if required by generating subgraphs and updating actor instances. 2. Generates subgraphs for each split and stores
   * them in the 'sublist'. 3. Stores the top graph to preserve inter-subgraph connections.
   */
  private void computeSplits() {
    // Step 1: Split tasks if required
    for (final Entry<Integer, Map<AbstractActor, Long>> entry : subs.entrySet()) {
      final Map<AbstractActor, Long> newValue = new HashMap<>(entry.getValue());
      subsCopy.put(entry.getKey(), newValue);
    }

    for (Integer subRank = 0; subRank < subs.size(); subRank++) {
      for (final Entry<AbstractActor, Long> entry : subs.get(subRank).entrySet()) {
        // If instances are less than required, split the actor and update instances
        if (entry.getValue() < brv.get(entry.getKey())) {
          brv.replace(entry.getKey(), split(entry.getKey(), entry.getValue(), brv.get(entry.getKey()), subRank));
        }
      }
    }

    // Step 2: Generate subgraphs
    for (Integer subRank = 0; subRank < subs.size(); subRank++) {
      final List<AbstractActor> list = new ArrayList<>();
      for (final AbstractActor actor : subsCopy.get(subRank).keySet()) {
        list.add(actor);
      }
      final PiGraph subgraph = new PiSDFSubgraphBuilder(graph, list, "sub" + subRank).build();
      sublist.add(subgraph);
    }

    // Step 3: Store the top graph to preserve inter-subgraph connections
    this.topGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
  }

  /**
   * Constructs interfaces for the generated subgraphs.
   *
   * Step 1: Free subs (Interface --> sink; container) - Iterate over sublist and create source and sink actors for each
   * DataInputInterface and DataOutputInterface. - Generate CHeaderRefinement and associated files for each actor. - Set
   * execution time for the interface actors.
   *
   * Step 2: Merge CFG - Rename dependencies and ports for inter-subgraph connections.
   *
   * Step 3: Remove Duplicate Parameters - Eliminate duplicate parameters and merge their dependencies.
   *
   * Step 4: Convert ConfigInputPorts to Parameters - Replace ConfigInputPorts that are not Parameters with equivalent
   * Parameter instances.
   *
   * Step 5: Remove Empty FIFOs and Interface Actors - Remove FIFOs with null source or target ports. - Remove
   * DataInputInterface and DataOutputInterface actors.
   *
   * Step 6: Compute BRV for Subgraph - Compute the Buffer Read Volume (BRV) for the subgraph using the LCM method. -
   * Set the containing graph of the subgraph to null.
   */
  private void constructSubs() {
    // Step 1: Free subs (Interface --> sink; container)
    for (final PiGraph subgraph : sublist) {
      final int index = Integer.parseInt(subgraph.getName().replace("sub", ""));

      // Create source actors for DataInputInterfaces
      for (final DataInputInterface in : subgraph.getDataInputInterfaces()) {
        final Actor src = createSourceActor(in, index);
        setExecutionTimeForInterface(src);
      }

      // Create sink actors for DataOutputInterfaces
      for (final DataOutputInterface out : subgraph.getDataOutputInterfaces()) {
        final Actor snk = createSinkActor(out, index);
        setExecutionTimeForInterface(snk);
      }

      // Step 2: Merge CFG (rename dependencies and ports for inter-subgraph connections)
      mergeCFG(subgraph);

      // Step 3: Remove Duplicate Parameters
      removeDuplicateParameters(subgraph);

      // Step 4: Convert ConfigInputPorts to Parameters
      convertConfigInputPortsToParameters(subgraph);

      // Step 5: Remove Empty FIFOs and Interface Actors
      removeEmptyFIFOsAndInterfaceActors(subgraph);

      // Step 6: Compute BRV for Subgraph
      PiBRV.compute(subgraph, BRVMethod.LCM);
      subgraph.setContainingGraph(null);
    }
  }

  // Helper method to create a source actor for a DataInputInterface
  private Actor createSourceActor(DataInputInterface in, int index) {
    final Actor src = PiMMUserFactory.instance.createActor();
    src.setName("src_" + in.getName());
    final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
    src.setRefinement(refinement);
    final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (src).getRefinement();
    final Prototype oEmptyPrototype = new Prototype();
    oEmptyPrototype.setIsStandardC(true);

    cHeaderRefinement.setFilePath(codegenPath + INTERFACE_PATH + index + "/" + src.getName() + ".h");
    final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    cHeaderRefinement.setLoopPrototype(functionPrototype);
    functionPrototype.setName(src.getName());

    src.setContainingGraph(in.getDirectSuccessors().get(0).getContainingGraph());
    final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
    src.getDataOutputPorts().add(dout);
    dout.setName("out");
    dout.setExpression(in.getDataPort().getExpression());
    in.getDataOutputPorts().get(0).getFifo().setSourcePort(dout);
    final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
    functionArgument.setName(src.getAllPorts().get(0).getName());
    functionArgument.setType(dout.getFifo().getType());
    functionArgument.setDirection(Direction.OUT);
    functionPrototype.getArguments().add(functionArgument);
    generateFileH(src, index);

    // Set 1 because it's an interface
    for (final Component opId : archi.getProcessingElements()) {
      scenario.getTimings().setExecutionTime(src, opId, 1L);
    }

    return src;
  }

  // Helper method to create a sink actor for a DataOutputInterface
  private Actor createSinkActor(DataOutputInterface out, int index) {
    final Actor snk = PiMMUserFactory.instance.createActor();
    snk.setName("snk_" + out.getName());
    final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
    snk.setRefinement(refinement);
    final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (snk.getRefinement());
    final Prototype oEmptyPrototype = new Prototype();
    oEmptyPrototype.setIsStandardC(true);

    cHeaderRefinement.setFilePath(codegenPath + INTERFACE_PATH + index + "/" + snk.getName() + ".h");
    final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    cHeaderRefinement.setLoopPrototype(functionPrototype);
    functionPrototype.setName(snk.getName());

    snk.setContainingGraph(out.getContainingGraph());
    final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
    snk.getDataInputPorts().add(din);
    din.setName("in");
    din.setExpression(out.getDataPort().getExpression());
    out.getDataInputPorts().get(0).getFifo().setTargetPort(din);
    final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
    functionArgument.setName(snk.getAllPorts().get(0).getName());
    functionArgument.setType(din.getFifo().getType());
    functionArgument.setDirection(Direction.IN);
    functionPrototype.getArguments().add(functionArgument);
    generateFileH(snk, index);

    // Set 1 because it's an interface
    for (final Component opId : archi.getProcessingElements()) {
      scenario.getTimings().setExecutionTime(snk, opId, 1L);
    }

    return snk;
  }

  // Helper method to set execution time for interface actors
  private void setExecutionTimeForInterface(Actor actor) {
    // Set execution time for interface actors (set to 1 as they are interfaces)
    for (final Component opId : archi.getProcessingElements()) {
      scenario.getTimings().setExecutionTime(actor, opId, 1L);
    }
  }

  // Helper method to merge CFG (rename dependencies and ports for inter-subgraph connections)
  private void mergeCFG(PiGraph subgraph) {
    // Iterate through dependencies and rename setter names to getter names
    for (final Dependency dep : subgraph.getDependencies()) {
      ((AbstractVertex) dep.getSetter()).setName(dep.getGetter().getName());
      ((ConfigInputInterface) dep.getSetter()).getGraphPort().setName(dep.getGetter().getName());
    }
  }

  // Helper method to remove duplicate parameters
  private void removeDuplicateParameters(PiGraph subgraph) {
    // Implementation to eliminate duplicate parameters and merge their dependencies
    int paramSize = subgraph.getParameters().size();
    for (int i = 0; i < paramSize; i++) {
      for (int j = i + 1; j < paramSize; j++) {
        if (subgraph.getParameters().get(i).getName().equals(subgraph.getParameters().get(j).getName())) {
          subgraph.getParameters().get(i).getOutgoingDependencies()
              .add(subgraph.getParameters().get(j).getOutgoingDependencies().get(0));
          subgraph.removeParameter(subgraph.getParameters().get(j));
          paramSize--;
          j--;
        }
      }
    }
  }

  // Helper method to convert ConfigInputPorts to Parameters
  private void convertConfigInputPortsToParameters(PiGraph subgraph) {
    // Implementation to replace ConfigInputPorts that are not Parameters with equivalent Parameter instances
    for (int i = 0; i < subgraph.getConfigInputPorts().size(); i++) {
      if (!(subgraph.getConfigInputPorts().get(i) instanceof Parameter)) {
        // Convert ConfigInputPort to Parameter and set necessary properties
        final Parameter parameter = PiMMUserFactory.instance.createParameter(subgraph.getParameters().get(i).getName(),
            subgraph.getParameters().get(i).getExpression().evaluate());

        // Set the expression and add outgoing dependencies
        final Long exp = ((ExpressionHolder) subgraph.getConfigInputPorts().get(i).getIncomingDependency().getSetter())
            .getExpression().evaluate();
        for (int j = 0; j < subgraph.getParameters().get(i).getOutgoingDependencies().size(); j++) {
          parameter.getOutgoingDependencies().add(subgraph.getParameters().get(i).getOutgoingDependencies().get(j));
          j--;
        }
        parameter.setExpression(exp);

        // Remove the original ConfigInputPort and add the new Parameter
        subgraph.addParameter(parameter);
        subgraph.removeParameter(subgraph.getParameters().get(i));
        i--;
      }
    }
  }

  // Helper method to remove empty FIFOs and interface actors
  private void removeEmptyFIFOsAndInterfaceActors(PiGraph subgraph) {
    // Implementation to remove FIFOs with null source or target ports
    subgraph.getFifos().stream().filter(x -> x.getSourcePort() == null).forEach(subgraph::removeFifo);
    subgraph.getFifos().stream().filter(x -> x.getTargetPort() == null).forEach(subgraph::removeFifo);
    subgraph.getAllActors().stream().filter(x -> x instanceof DataInputInterface || x instanceof DataOutputInterface)
        .forEach(subgraph::removeActor);

  }

  /**
   * Export subgraph
   *
   * @param printgraph
   *          Graph to consider.
   */
  private void graphExporter(PiGraph printgraph) {
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
    final String message = "sub print in : " + graphPath;
    PreesmLogger.getLogger().log(Level.INFO, message);
    WorkspaceUtils.updateWorkspace();
  }

  /**
   * Split an actor into two actor and dispatch data tokens
   *
   * @param key
   *          Actor to split
   * @param rv1
   *          desire repetition vector
   * @param rv2
   *          genuine repetition vector to split
   */
  private Long split(AbstractActor key, Long rv1, Long rv2, Integer subRank) {
    // if data pattern
    // copy instance
    final AbstractActor copy = PiMMUserFactory.instance.copy(key);
    copy.setContainingGraph(key.getContainingGraph());
    int index = 0;
    for (final DataInputPort in : key.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_" + key.getName() + index);
        frk.setContainingGraph(key.getContainingGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        final Long dt = in.getFifo().getSourcePort().getExpression().evaluate() * brv.get(in.getFifo().getSource());
        final Long rt = in.getExpression().evaluate();
        din.setExpression(dt);

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(key.getContainingGraph());

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        dout.setExpression(dt - (rv1 * rt));
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        fout.setContainingGraph(key.getContainingGraph());

        // connect fork to duplicated actors
        final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
        doutn.setName("out_" + 1);
        doutn.setExpression(dt - ((rv2 - rv1) * rt));
        frk.getDataOutputPorts().add(doutn);
        final Fifo foutn = PiMMUserFactory.instance.createFifo();
        foutn.setType(fin.getType());
        foutn.setSourcePort(doutn);
        foutn.setContainingGraph(key.getContainingGraph());
        copy.getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
            .forEach(x -> x.setIncomingFifo(foutn));

        subsCopy.get(subRank).put(frk, 1L);
        brv.put(frk, 1L);
        index++;
      } else {
        PreesmLogger.getLogger().log(Level.INFO, "global delay not yet handle");
      }
    }
    index = 0;
    for (final DataOutputPort out : key.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_" + key.getName() + index);
        jn.setContainingGraph(key.getContainingGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();

        dout.setName("out");
        final Long dt = out.getFifo().getTargetPort().getExpression().evaluate() * brv.get(out.getFifo().getTarget());
        final Long rt = out.getExpression().evaluate();
        dout.setExpression(dt);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(out.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(key.getContainingGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        din.setExpression(dt - (rv1 * rt));
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(key.getContainingGraph());
        out.getFifo().setType(fout.getType());

        // connect duplicated actors to Join
        final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
        dinn.setName("in_" + 1);
        dinn.setExpression(dt - (rv2 - rv1) * out.getFifo().getSourcePort().getExpression().evaluate());
        jn.getDataInputPorts().add(dinn);
        final Fifo finn = PiMMUserFactory.instance.createFifo();
        finn.setType(fout.getType());
        finn.setTargetPort(dinn);
        finn.setContainingGraph(key.getContainingGraph());
        copy.getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
            .forEach(x -> x.setOutgoingFifo(finn));
        brv.put(jn, 1L);
        updateSubsCopy(subRank, key, jn, rv1, rv2, copy);

        index++;
      } else {

        // connect oEmpty delayed output to 1st duplicated actor
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        fdin.setSourcePort(out);
        copy.getDataInputPorts().stream().filter(x -> x.getFifo() == null).forEach(x -> x.setIncomingFifo(fdin));
        fdin.setContainingGraph(key.getContainingGraph());

      }
    }
    for (final ConfigInputPort cfg : key.getConfigInputPorts()) {
      copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
      copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
          .forEach(x -> x.getIncomingDependency().setContainingGraph(cfg.getIncomingDependency().getContainingGraph()));

    }

    setExecutionTimeForDuplicatedActor(key, copy);
    removeDelayAndEmptyFIFOs(key);

    return rv2 - rv1;

  }

  // Helper method to update subsCopy based on subRank and subRank+1
  private void updateSubsCopy(Integer subRank, AbstractActor key, JoinActor jn, Long rv1, Long rv2,
      AbstractActor copy) {
    if (subsCopy.get(subRank + 1) == null) {
      subsCopy.get(subRank).put(jn, 1L);
      subsCopy.get(subRank).remove(key);
      subsCopy.get(subRank).put(copy, rv2 - rv1);
    } else {
      subsCopy.get(subRank + 1).put(jn, 1L);
      subsCopy.get(subRank + 1).remove(key);
      subsCopy.get(subRank + 1).put(copy, rv2 - rv1);
    }
  }

  // Helper method to set execution time for the duplicated actor (copy)
  private void setExecutionTimeForDuplicatedActor(AbstractActor key, AbstractActor copy) {
    // Set 1 because it's an interface
    for (final Component opId : archi.getProcessingElements()) {
      scenario.getTimings().setExecutionTime(copy, opId, scenario.getTimings().getExecutionTimeOrDefault(key, opId));
    }
  }

  // Helper method to remove delay and empty FIFOs from the containing graph
  private void removeDelayAndEmptyFIFOs(AbstractActor key) {
    // Remove delay
    ((PiGraph) key.getContainingGraph()).getDelays().stream().filter(x -> x.getContainingFifo().getSourcePort() == null)
        .forEach(((PiGraph) key.getContainingGraph())::removeDelay);

    // Remove empty FIFOs
    ((PiGraph) key.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(((PiGraph) key.getContainingGraph())::removeFifo);

    ((PiGraph) key.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(((PiGraph) key.getContainingGraph())::removeFifo);
  }

  /**
   * Generates the C header file for an interface actor.
   *
   * This method creates a C header file for the specified interface actor, including function prototype, input/output
   * ports, and their types. The generated file is printed to the designated code generation path under the "interface"
   * directory for the specified subgraph index.
   *
   * @param actor
   *          The interface actor for which the C header file is generated.
   * @param index
   *          The index of the subgraph to which the actor belongs.
   */
  private void generateFileH(Actor actor, int index) {
    final StringConcatenation content = new StringConcatenation();
    content.append("// Interface actor file \n #ifndef " + actor.getName().toUpperCase() + "_H \n #define "
        + actor.getName().toUpperCase() + "_H \n void " + actor.getName() + "(");

    for (int i = 0; i < actor.getAllDataPorts().size(); i++) {
      final DataPort dp = actor.getAllDataPorts().get(i);
      content.append(dp.getFifo().getType() + " " + dp.getName());
      if (i == actor.getAllDataPorts().size() - 1) {
        content.append(",");
      }
    }
    content.append("); \n #endif");

    PreesmIOHelper.getInstance().print(codegenPath + INTERFACE_PATH + index + "/", actor.getName() + ".h", content);
    final String message = "interface file print in : " + codegenPath + INTERFACE_PATH + index + "/";
    PreesmLogger.getLogger().log(Level.INFO, message);
  }

  /**
   * Computes the sub-timings for each node based on the topological order and equivalent timings. The function iterates
   * over actors in topological ASAP order, adding instances to reach target times.
   */
  private void computeSubs() {
    Integer nodeID = 0;
    final Integer LastNodeID = equivalentTimings.size() - 1;
    Long subTimings = 0L;
    Map<AbstractActor, Long> list = new HashMap<>();
    // add actor in topological ASAP order
    for (final Entry<Long, List<AbstractActor>> entry : topoOrderASAP.entrySet()) {
      // add actor instance by instance
      for (final AbstractActor a : entry.getValue()) {

        // compute actor timing on slowest core
        final Long slow = (a instanceof Actor) ? NodePartitioner.slowestTime(a, scenario) : 0L;
        // compute number of instance to add to reach target time
        final Long nodeCapacity = (a instanceof Actor) && slow > 0L
            ? (equivalentTimings.get(nodeID) - subTimings) / slow
            : brv.get(a);
        // add all instance if last node or node has the capacity
        if (Objects.equals(nodeID, LastNodeID) || brv.get(a) <= nodeCapacity) {
          list.put(a, brv.get(a));
          subTimings += slow * brv.get(a);
        } else {
          // add actor until reach node capacity
          list = processActorList(list, a, nodeCapacity);

          subs.put(nodeID, list);
          // fill the next node
          nodeID++;
          list = new HashMap<>();
          // Fill the list with remaining instances
          final Long remainingInstance = brv.get(a) - nodeCapacity;
          list.put(a, remainingInstance);
          subTimings = remainingInstance * slow;
        }

      }
    }
    // Put the final node and its sub-timings into the map
    subs.put(nodeID, list);
  }

  /**
   * Processes the actor based on the given node capacity, updating the list if the capacity is greater than zero.
   *
   * @param currentList
   *          The current list of actors and their instances.
   * @param actor
   *          The actor to be processed.
   * @param nodeCapacity
   *          The computed node capacity.
   * @return The updated list after processing the actor.
   */
  private Map<AbstractActor, Long> processActorList(Map<AbstractActor, Long> list, AbstractActor a, Long nodeCapacity) {

    return Optional.ofNullable(nodeCapacity).filter(capacity -> capacity > 0).map(capacity -> {
      list.put(a, capacity);
      return list;
    }).orElse(list);
  }

}
