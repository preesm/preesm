/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.moa.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.preesm.algorithm.mapper.abc.SpecialVertexManager;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.ContainersManager;
import org.preesm.commons.files.PathTools;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.scenario.types.ImplementationPropertyNames;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.component.Component;
import org.preesm.model.slam.route.AbstractRouteStep;
import org.preesm.model.slam.route.MessageRouteStep;
import org.preesm.model.slam.route.Route;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Exports activity information in terms of quanta based on individual quanta information on actors set in an xls file
 *
 * @author mpelcat
 *
 */
public class CustomQuantaExporter extends AbstractTaskImplementation {

  /**
   * Tag for storing the path of the input file with individual quanta information
   */
  static final String INPUT_XLS_FILE = "xls_file";

  /**
   * Tag for storing the path of the file to store data of per core information
   */
  static final String PATH = "path";

  /**
   * Tag for storing the information on whether to display the component names or not
   */
  static final String HUMAN_READABLE = "human_readable";

  /**
   * Storing the graph vertices already explored
   */
  private Set<MapperDAGVertex> visited = new LinkedHashSet<>();

  /**
   * Currently retrieved activity
   */
  private Activity activity = new Activity();

  /**
   * Number of custom quanta per actor and operator
   */
  private CustomQuanta customQuanta = new CustomQuanta();

  /**
   * Exporting in a CSV file custom quanta information
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    final Logger logger = PreesmLogger.getLogger();

    final String filePath = parameters.get(CustomQuantaExporter.PATH);
    final boolean human_readable = (parameters.get(CustomQuantaExporter.HUMAN_READABLE) == "Yes");
    // The abc contains all information on the implemented system
    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);

    String inputXLSFile = parameters.get(CustomQuantaExporter.INPUT_XLS_FILE);

    if (abc != null) {
      // The pattern $SCENARIO$ in the input excel file name is replaced by the scenario name
      final PreesmScenario scenario = abc.getScenario();
      final String scenarioURL = scenario.getScenarioURL();
      final String scenarioName = Paths.get(scenarioURL).getFileName().toString().replace(".scenario", "");
      inputXLSFile = inputXLSFile.replace("$SCENARIO$", scenarioName);
      inputXLSFile = PathTools.getAbsolutePath(inputXLSFile, workflow.getProjectName());

      // parsing individual quanta values from an excel file
      parseQuantaInputFile(inputXLSFile, abc.getScenario());

      writeActivity(abc, filePath, workflow, human_readable);
    } else {
      logger.log(Level.SEVERE, "Not a valid set of ABCs for ActivityExporter.");
    }

    final String msg = "Activity: " + this.activity;
    logger.log(Level.INFO, msg);

    return new HashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> defaultParams = new HashMap<>();
    defaultParams.put(CustomQuantaExporter.INPUT_XLS_FILE, "stats/mat/custom_quanta_in/quanta_in_$SCENARIO$.xls");
    defaultParams.put(CustomQuantaExporter.PATH, "stats/mat/activity");
    defaultParams.put(CustomQuantaExporter.HUMAN_READABLE, "Yes");
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Exporting implementation activity in terms of custom quanta.";
  }

  /**
   * Generating a class with activity information, then exporting it in a text file
   */
  private void writeActivity(final LatencyAbc abc, final String path, final Workflow workflow,
      final boolean human_readable) {

    // Clearing the history
    this.visited.clear();
    this.activity.clear();

    // Initializing all PE and CN information to 0
    final Design architecture = abc.getArchitecture();
    for (final ComponentInstance vertex : architecture.getComponentInstances()) {
      this.activity.addTokenNumber(vertex.getInstanceName(), 0);
      this.activity.addQuantaNumber(vertex.getInstanceName(), 0);
    }

    // Generating activity information
    for (final MapperDAGVertex vertex : abc.getImplementation().getSources()) {
      addVertexAndSuccessors(vertex, abc);
      vertex.getSuccessors(true);
    }

    // Writing the activity csv file
    CustomQuantaExporter.writeString(this.activity.quantaString(human_readable), "custom_quanta", path, workflow);
  }

  /**
   * Recursive function to scan the whole application for extracting activity
   */
  private void addVertexAndSuccessors(final MapperDAGVertex vertex, final LatencyAbc abc) {
    // add a vertex and its successors to activity information
    visitVertex(vertex);
    this.visited.add(vertex); // avoiding multiple visits of a vertex
    for (final MapperDAGVertex succ : vertex.getSuccessors(true).keySet()) {
      final MapperDAGEdge edge = vertex.getSuccessors(true).get(succ);
      visitEdge(edge, abc); // Visiting edge even if the successor vertex was already visited
      if (!this.visited.contains(succ)) {
        addVertexAndSuccessors(succ, abc);
      }
    }
  }

  /**
   * Visiting one vertex and extracting activity Activity is computed from time information and from custom quanta
   * information
   */
  private void visitVertex(final MapperDAGVertex vertex) {
    final String duration = vertex.getPropertyStringValue(ImplementationPropertyNames.Task_duration);
    final Component operator = vertex.getEffectiveComponent().getComponent();
    final String cquanta = this.customQuanta.getQuanta(vertex.getName(), operator.getVlnv().getName());

    if (!cquanta.isEmpty()) {
      // Resolving the value as a String expression of t
      final JEP jep = new JEP();
      jep.addVariable("t", Double.valueOf(duration));

      try {
        final Node node = jep.parse(cquanta);
        final Double result = (Double) jep.evaluate(node);

        this.activity.addQuantaNumber(vertex.getEffectiveComponent().getInstanceName(), result.longValue());

        final String msg = "Custom quanta set to " + result.longValue() + " by solving " + cquanta + " with t="
            + duration + " for " + vertex.getName();
        PreesmLogger.getLogger().log(Level.INFO, msg);
      } catch (final ParseException exc) {
        throw new PreesmRuntimeException(exc);
      }
    } else if (SpecialVertexManager.isBroadCast(vertex)) {
      // Broadcasts have a fix ponderation of their custom quanta compared to timing
      double correctedDuration = Double.parseDouble(duration);
      correctedDuration = correctedDuration * 0.720;
      this.activity.addQuantaNumber(vertex.getEffectiveComponent().getInstanceName(), (long) correctedDuration);

      final String msg = "Broadcast custom quanta set to " + ((long) correctedDuration)
          + " by applying constant factor 0.72 to " + duration + " for " + vertex.getName();
      PreesmLogger.getLogger().log(Level.INFO, msg);

    } else {
      this.activity.addQuantaNumber(vertex.getEffectiveComponent().getInstanceName(), Long.valueOf(duration));
    }
  }

  /**
   * Visiting one edge and extracting activity
   */
  private void visitEdge(final MapperDAGEdge edge, final LatencyAbc abc) {
    final MapperDAGVertex source = (MapperDAGVertex) edge.getSource();
    final MapperDAGVertex target = (MapperDAGVertex) edge.getTarget();
    if (!(source.getEffectiveComponent()).equals(target.getEffectiveComponent())) {
      final long size = edge.getInit().getDataSize();
      final Route route = abc.getComRouter().getRoute(edge);

      // Counting tokens and quanta for each elements in the route between 2 processors for an edge
      for (final AbstractRouteStep step : route) {
        if (step instanceof MessageRouteStep) {
          // a step is internally composed of several communication nodes
          final MessageRouteStep mstep = (MessageRouteStep) step;
          for (final ComponentInstance node : mstep.getNodes()) {
            this.activity.addQuantaNumber(node.getInstanceName(), size);
          }
        }
      }
    }

  }

  /**
   * Writing CSV text containing the activity description in fileName located in stringPath.
   */
  static void writeString(final String text, final String fileName, final String stringPath, final Workflow workflow) {

    final String sPath = PathTools.getAbsolutePath(stringPath, workflow.getProjectName());
    IPath path = new Path(sPath);
    path = path.append(fileName + ".csv");

    // Get a complete valid path with all folders existing
    try {
      if (path.getFileExtension() != null) {
        ContainersManager.createMissingFolders(path.removeFileExtension().removeLastSegments(1));
      } else {
        ContainersManager.createMissingFolders(path);
      }
    } catch (final CoreException e) {
      throw new PreesmRuntimeException("Path " + path + " is not a valid path for export.");
    }

    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    try {
      if (!iFile.exists()) {
        iFile.create(null, false, new NullProgressMonitor());
        iFile.setContents(new ByteArrayInputStream(text.getBytes()), true, false, new NullProgressMonitor());
      } else {
        iFile.setContents(new ByteArrayInputStream(text.getBytes()), true, false, new NullProgressMonitor());
      }
    } catch (final CoreException ex) {
      throw new PreesmRuntimeException(ex);
    }

  }

  /**
   * Reading individual quanta information from an excel file.
   */
  void parseQuantaInputFile(final String fileName, final PreesmScenario scenario) {

    final IPath path = new Path(fileName);
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

    if (!iFile.exists()) {
      final String msg = "The custom quanta input file " + fileName + " does not exist.";
      PreesmLogger.getLogger().log(Level.SEVERE, msg);
    } else {
      final String msg = "Reading custom quanta from file " + fileName + ".";
      PreesmLogger.getLogger().log(Level.INFO, msg);
      try {
        final Workbook w = Workbook.getWorkbook(iFile.getContents());

        if (scenario.isPISDFScenario()) {
          final PiGraph currentGraph = PiParser.getPiGraphWithReconnection(scenario.getAlgorithmURL());
          final Set<String> operators = scenario.getOperatorDefinitionIds();
          parseQuantaForPISDFGraph(w, currentGraph, operators);
        } else {
          PreesmLogger.getLogger().log(Level.SEVERE, "Only PiSDF graphs are supported for custom quanta export.");
        }

        // Warnings are displayed once for each missing operator or vertex
        // in the excel sheet
      } catch (IOException | CoreException | PreesmException | BiffException e) {
        throw new PreesmRuntimeException(e);
      }
    }

  }

  /**
   * Reading individual quanta information from an excel file. Recursive method.
   */
  void parseQuantaForPISDFGraph(final Workbook w, final PiGraph appli, final Set<String> operators) {
    // Each of the vertices of the graph is either itself a graph
    // (hierarchical vertex), in which case we call recursively this method
    // we parse quanta for standard and special vertices
    for (final AbstractActor vertex : appli.getActors()) {
      // Handle connected graphs from hierarchical vertices
      if (vertex instanceof PiGraph) {
        parseQuantaForPISDFGraph(w, (PiGraph) vertex, operators);
      } else if (vertex instanceof Actor) {
        final Actor actor = (Actor) vertex;

        // Handle unconnected graphs from hierarchical vertices
        final Refinement refinement = actor.getRefinement();
        if (refinement != null) {
          final AbstractActor subgraph = refinement.getAbstractActor();

          if (subgraph instanceof PiGraph) {
            parseQuantaForPISDFGraph(w, (PiGraph) subgraph, operators);
          } else {
            // If the actor is not hierarchical, parse its timing
            parseQuantaForVertex(w, vertex.getName(), operators);
          }
        }
      } else {
        // case of special actors
        parseQuantaForVertex(w, vertex.getName(), operators);

      }
    }
  }

  /**
   * Reading individual quanta information from an excel file. Recursive method.
   */
  void parseQuantaForVertex(final Workbook w, final String vertexName, final Set<String> operators) {

    // Test excel reader, to be continued
    for (final String opDefId : operators) {
      final Cell vertexCell = w.getSheet(0).findCell(vertexName);
      final Cell operatorCell = w.getSheet(0).findCell(opDefId);

      if ((vertexCell != null) && (operatorCell != null)) {
        // Get the cell containing the timing
        final Cell timingCell = w.getSheet(0).getCell(operatorCell.getColumn(), vertexCell.getRow());

        if (timingCell.getType().equals(CellType.NUMBER) || timingCell.getType().equals(CellType.NUMBER_FORMULA)) {

          String stringQuanta = timingCell.getContents();
          // Removing useless characters (spaces...)
          stringQuanta = stringQuanta.replaceAll(" ", "");

          try {
            // Testing the validity of the value as a Long number.
            final long quantaValue = Long.parseLong(timingCell.getContents());

            final String msg = "Importing custom quantum: " + vertexName + " on " + opDefId + ": " + quantaValue;
            PreesmLogger.getLogger().log(Level.INFO, msg);
            this.customQuanta.addQuantaExpression(vertexName, opDefId, timingCell.getContents());

          } catch (final NumberFormatException e) {
            PreesmLogger.getLogger().log(Level.SEVERE, "Problem importing quanta of " + vertexName + " on " + opDefId
                + ". Integer with no space or special character needed. Be careful on the special number formats.");
          }
        } else {

          try {
            // Case of a string formula
            final String msg = "Detected formula: " + timingCell.getContents() + " for " + vertexName + " on "
                + opDefId;
            PreesmLogger.getLogger().log(Level.INFO, msg);

            // Testing the validity of the value as a String expression of t
            final JEP jep = new JEP();
            jep.addVariable("t", 1);
            final Node node = jep.parse(timingCell.getContents());
            jep.evaluate(node);

            this.customQuanta.addQuantaExpression(vertexName, opDefId, timingCell.getContents());
          } catch (final ParseException e) {
            PreesmLogger.getLogger().log(Level.SEVERE, "Problem evaluating quanta expression of " + vertexName + " on "
                + opDefId + ": " + timingCell.getContents());
          }

        }
      } else {
        if (vertexCell == null) {
          final String msg = "No line found in custom quanta excel sheet for vertex: " + vertexName;
          PreesmLogger.getLogger().log(Level.WARNING, msg);
        } else if (operatorCell == null) {
          final String msg = "No column found in custom quanta excel sheet for operator type: " + opDefId;
          PreesmLogger.getLogger().log(Level.WARNING, msg);
        }
      }
    }
  }

}
