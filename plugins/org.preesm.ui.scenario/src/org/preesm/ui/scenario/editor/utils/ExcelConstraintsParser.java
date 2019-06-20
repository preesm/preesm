/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2015)
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
package org.preesm.ui.scenario.editor.utils;

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
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Importing constraints in a scenario from an excel file. The existing timings mean that the task can be mapped on the
 * given operator. Task names are rows while operator types are columns.
 *
 * @author mpelcat
 */
public class ExcelConstraintsParser {

  /** The scenario. */
  private Scenario scenario = null;

  /**
   * Instantiates a new excel constraints parser.
   *
   * @param scenario
   *          the scenario
   */
  public ExcelConstraintsParser(final Scenario scenario) {
    super();
    this.scenario = scenario;
  }

  /**
   * Parses the.
   *
   * @param url
   *          the url
   * @throws CoreException
   *           the core exception
   */
  public void parse(final String url) throws CoreException {

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    WorkspaceUtils.updateWorkspace();

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);

    this.scenario.getConstraints().getGroupConstraints().clear();
    PreesmLogger.getLogger().log(Level.INFO,
        "Importing constraints from an excel sheet. Previously defined constraints are discarded.");

    try {
      final Workbook w = Workbook.getWorkbook(file.getContents());

      // Warnings are displayed once for each missing operator or vertex
      // in the excel sheet
      final Set<AbstractActor> missingVertices = new LinkedHashSet<>();
      final Set<ComponentInstance> missingOperators = new LinkedHashSet<>();

      final PiGraph currentPiGraph = scenario.getAlgorithm();
      for (final AbstractActor vertex : currentPiGraph.getAllActors()) {
        if (vertex instanceof Actor) {
          final Design design = this.scenario.getDesign();
          for (final ComponentInstance operatorId : design.getOperatorComponentInstances()) {
            checkOpPiConstraint(w, operatorId, (Actor) vertex, missingVertices, missingOperators);
          }
        }
      }
    } catch (BiffException | IOException | CoreException e) {
      e.printStackTrace();
    }

  }

  /**
   * Importing constraints from component names.
   *
   * @param w
   *          the w
   * @param operatorId
   *          the operator id
   * @param vertex
   *          the vertex
   * @param missingVertices
   *          the missing vertices
   * @param missingOperators
   *          the missing operators
   */
  private void checkOpPiConstraint(final Workbook w, final ComponentInstance operatorId, final Actor vertex,
      final Set<AbstractActor> missingVertices, final Set<ComponentInstance> missingOperators) {
    final String vertexName = vertex.getName();

    if (operatorId != null && !vertexName.isEmpty()) {
      final Cell vertexCell = w.getSheet(0).findCell(vertexName);
      final Cell operatorCell = w.getSheet(0).findCell(operatorId.getInstanceName());

      if ((vertexCell != null) && (operatorCell != null)) {
        final Cell timingCell = w.getSheet(0).getCell(operatorCell.getColumn(), vertexCell.getRow());

        if (timingCell.getType().equals(CellType.NUMBER) || timingCell.getType().equals(CellType.NUMBER_FORMULA)) {

          this.scenario.getConstraints().addConstraint(operatorId, vertex);

          PreesmLogger.getLogger().log(Level.FINE, "Importing constraint: {" + operatorId + "," + vertex + ",yes}");

        } else {
          PreesmLogger.getLogger().log(Level.FINE, "Importing constraint: {" + operatorId + "," + vertex + ",no}");
        }
      } else {
        if ((vertexCell == null) && !missingVertices.contains(vertex)) {
          if (vertex.getRefinement() != null) {
            PreesmLogger.getLogger().log(Level.WARNING,
                "No line found in excel sheet for hierarchical vertex: " + vertexName);
          } else {
            final String message = "No line found in excel sheet for atomic vertex: " + vertexName;
            throw new PreesmRuntimeException(message);
          }
          missingVertices.add(vertex);
        } else if ((operatorCell == null) && !missingOperators.contains(operatorId)) {
          final String message = "No column found in excel sheet for operator: " + operatorId;
          throw new PreesmRuntimeException(message);
        }
      }
    }
  }

}
