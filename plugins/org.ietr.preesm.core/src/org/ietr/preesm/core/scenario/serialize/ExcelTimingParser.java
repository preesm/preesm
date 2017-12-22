/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2016)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008 - 2009)
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
package org.ietr.preesm.core.scenario.serialize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;

/**
 * Importing timings in a scenario from an excel file. task names are rows while operator types are columns
 *
 * @author mpelcat
 */
public class ExcelTimingParser {

  /** The scenario. */
  private PreesmScenario scenario = null;

  /**
   * Instantiates a new excel timing parser.
   *
   * @param scenario
   *          the scenario
   */
  public ExcelTimingParser(final PreesmScenario scenario) {
    super();
    this.scenario = scenario;
  }

  /**
   * Parses the.
   *
   * @param url
   *          the url
   * @param opDefIds
   *          the op def ids
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public void parse(final String url, final Set<String> opDefIds) throws InvalidModelException, FileNotFoundException {
    WorkflowLogger.getLogger().log(Level.INFO, "Importing timings from an excel sheet. Non precised timings are kept unmodified.");

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    Activator.updateWorkspace();

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);
    try {
      final Workbook w = Workbook.getWorkbook(file.getContents());

      // Warnings are displayed once for each missing operator or vertex
      // in the excel sheet
      final Set<String> missingVertices = new LinkedHashSet<>();
      final Set<String> missingOperatorTypes = new LinkedHashSet<>();

      parseTimings(w, opDefIds, missingVertices, missingOperatorTypes);

    } catch (final BiffException | IOException | CoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses the timings.
   *
   * @param w
   *          the w
   * @param opDefIds
   *          the op def ids
   * @param missingVertices
   *          the missing vertices
   * @param missingOperatorTypes
   *          the missing operator types
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   */
  private void parseTimings(final Workbook w, final Set<String> opDefIds, final Set<String> missingVertices, final Set<String> missingOperatorTypes)
      throws InvalidModelException, CoreException {
    // Depending on the type of SDF graph we process (IBSDF or PISDF), call
    // one or the other method
    if (this.scenario.isIBSDFScenario()) {
      final SDFGraph currentGraph = ScenarioParser.getSDFGraph(this.scenario.getAlgorithmURL());
      parseTimingsForIBSDFGraph(w, currentGraph, opDefIds, missingVertices, missingOperatorTypes);
    } else if (this.scenario.isPISDFScenario()) {
      final PiGraph currentGraph = PiParser.getPiGraph(this.scenario.getAlgorithmURL());
      parseTimingsForPISDFGraph(w, currentGraph, opDefIds, missingVertices, missingOperatorTypes);
    }

  }

  /**
   * Parses the timings for PISDF graph.
   *
   * @param w
   *          the w
   * @param currentGraph
   *          the current graph
   * @param opDefIds
   *          the op def ids
   * @param missingVertices
   *          the missing vertices
   * @param missingOperatorTypes
   *          the missing operator types
   */
  private void parseTimingsForPISDFGraph(final Workbook w, final PiGraph currentGraph, final Set<String> opDefIds, final Set<String> missingVertices,
      final Set<String> missingOperatorTypes) {

    currentGraph.getActorsWithRefinement().stream().filter(a -> !a.isHierarchical())
        .forEach(a -> parseTimingForVertex(w, a.getName(), opDefIds, missingVertices, missingOperatorTypes));

    currentGraph.getChildrenGraphs().stream().forEach(g -> parseTimingsForPISDFGraph(w, g, opDefIds, missingVertices, missingOperatorTypes));

  }

  /**
   * Parses the timing for vertex.
   *
   * @param w
   *          the w
   * @param vertexName
   *          the vertex name
   * @param opDefIds
   *          the op def ids
   * @param missingVertices
   *          the missing vertices
   * @param missingOperatorTypes
   *          the missing operator types
   */
  private void parseTimingForVertex(final Workbook w, final String vertexName, final Set<String> opDefIds, final Set<String> missingVertices,
      final Set<String> missingOperatorTypes) {
    // For each kind of processing elements, we look for a timing for given
    // vertex
    for (final String opDefId : opDefIds) {
      if (!opDefId.isEmpty() && !vertexName.isEmpty()) {
        // Get row and column for the timing we are looking for
        final Cell vertexCell = w.getSheet(0).findCell(vertexName);
        final Cell operatorCell = w.getSheet(0).findCell(opDefId);

        if ((vertexCell != null) && (operatorCell != null)) {
          // Get the cell containing the timing
          final Cell timingCell = w.getSheet(0).getCell(operatorCell.getColumn(), vertexCell.getRow());

          if (timingCell.getType().equals(CellType.NUMBER) || timingCell.getType().equals(CellType.NUMBER_FORMULA)) {

            String stringTiming = timingCell.getContents();
            // Removing useless characters (spaces...)
            stringTiming = stringTiming.replaceAll(" ", "");

            try {
              final Timing timing = new Timing(opDefId, vertexName, Long.valueOf(timingCell.getContents()));

              this.scenario.getTimingManager().addTiming(timing);

              WorkflowLogger.getLogger().log(Level.INFO, "Importing timing: " + timing.toString());
            } catch (final NumberFormatException e) {
              WorkflowLogger.getLogger().log(Level.SEVERE, "Problem importing timing of " + vertexName + " on " + opDefId
                  + ". Integer with no space or special character needed. Be careful on the special number formats.");
            }
          }
        } else {
          if ((vertexCell == null) && !missingVertices.contains(vertexName)) {
            WorkflowLogger.getLogger().log(Level.WARNING, "No line found in excel sheet for vertex: " + vertexName);
            missingVertices.add(vertexName);
          } else if ((operatorCell == null) && !missingOperatorTypes.contains(opDefId)) {
            WorkflowLogger.getLogger().log(Level.WARNING, "No column found in excel sheet for operator type: " + opDefId);
            missingOperatorTypes.add(opDefId);
          }
        }
      }
    }
  }

  /**
   * Parses the timings for IBSDF graph.
   *
   * @param w
   *          the w
   * @param currentGraph
   *          the current graph
   * @param opDefIds
   *          the op def ids
   * @param missingVertices
   *          the missing vertices
   * @param missingOperatorTypes
   *          the missing operator types
   */
  private void parseTimingsForIBSDFGraph(final Workbook w, final SDFGraph currentGraph, final Set<String> opDefIds, final Set<String> missingVertices,
      final Set<String> missingOperatorTypes) {
    // Each of the vertices of the graph is either itself a graph
    // (hierarchical vertex), in which case we call recursively this method;
    // a standard vertex, in which case we parser its timing; or another
    // kind of vertex, in which case we do nothing
    for (final SDFAbstractVertex vertex : currentGraph.vertexSet()) {
      if (vertex.getGraphDescription() != null) {
        parseTimingsForIBSDFGraph(w, (SDFGraph) vertex.getGraphDescription(), opDefIds, missingVertices, missingOperatorTypes);
      } else if (vertex.getKind().equalsIgnoreCase("vertex")) {
        parseTimingForVertex(w, vertex.getName(), opDefIds, missingVertices, missingOperatorTypes);
      }
    }
  }
}
