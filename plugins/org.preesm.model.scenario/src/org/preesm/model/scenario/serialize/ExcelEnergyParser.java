/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2016)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
 * Pengcheng Mu [pengcheng.mu@insa-rennes.fr] (2008 - 2009)
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
package org.preesm.model.scenario.serialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import jxl.Cell;
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
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ProcessingElement;

/**
 * Importing timings in a scenario from an excel file. task names are rows while operator types are columns
 *
 * @author mpelcat
 */
public class ExcelEnergyParser {

  /** The scenario. */
  private Scenario scenario = null;

  /**
   * Instantiates a new excel timing parser.
   *
   * @param scenario
   *          the scenario
   */
  public ExcelEnergyParser(final Scenario scenario) {
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
   */
  public void parse(final String url, final List<ProcessingElement> opDefIds) {
    PreesmLogger.getLogger().log(Level.INFO,
        "Importing energy from an excel sheet. Non precised energies are kept unmodified.");

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    WorkspaceUtils.updateWorkspace();

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);
    try {
      final Workbook w = Workbook.getWorkbook(file.getContents());

      // Warnings are displayed once for each missing operator or vertex
      // in the excel sheet
      final List<AbstractVertex> missingVertices = new ArrayList<>();
      final List<Component> missingOperatorTypes = new ArrayList<>();

      parseEnergy(w, opDefIds, missingVertices, missingOperatorTypes);

    } catch (final BiffException | IOException | CoreException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not parse energy file", e);
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
   * @throws CoreException
   *           the core exception
   */
  private void parseEnergy(final Workbook w, final List<ProcessingElement> opDefIds,
      final List<AbstractVertex> missingVertices, final List<Component> missingOperatorTypes) {
    // Depending on the type of SDF graph we process (IBSDF or PISDF), call
    // one or the other method
    final PiGraph currentGraph = scenario.getAlgorithm();
    parseEnergyForPISDFGraph(w, currentGraph, opDefIds, missingVertices, missingOperatorTypes);
  }

  /**
   * Parses the energy for PISDF graph.
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
  private void parseEnergyForPISDFGraph(final Workbook w, final PiGraph currentGraph,
      final List<ProcessingElement> opDefIds, final List<AbstractVertex> missingVertices,
      final List<Component> missingOperatorTypes) {

    currentGraph.getActorsWithRefinement().stream().filter(a -> !a.isHierarchical())
        .forEach(a -> parseEnergyForVertex(w, a, opDefIds, missingVertices, missingOperatorTypes));

    currentGraph.getChildrenGraphs().stream()
        .forEach(g -> parseEnergyForPISDFGraph(w, g, opDefIds, missingVertices, missingOperatorTypes));

  }

  /**
   * Parses the energy for vertex.
   *
   * @param w
   *          the w
   * @param actor
   *          the vertex name
   * @param opDefIds
   *          the op def ids
   * @param missingVertices
   *          the missing vertices
   * @param missingOperatorTypes
   *          the missing operator types
   */
  private void parseEnergyForVertex(final Workbook w, final AbstractActor actor, final List<ProcessingElement> opDefIds,
      final List<AbstractVertex> missingVertices, final List<Component> missingOperatorTypes) {
    // For each kind of processing elements, we look for an energy for given vertex
    for (final Component component : opDefIds) {
      if (component == null || actor == null) {
        return;
      }

      // Get row and column for the energy we are looking for
      final Cell vertexCell = w.getSheet(0).findCell(actor.getVertexPath());
      final Cell operatorCell = w.getSheet(0).findCell(component.getVlnv().getName());

      if ((vertexCell != null) && (operatorCell != null)) {
        // Get the cell containing the timing
        final Cell energyCell = w.getSheet(0).getCell(operatorCell.getColumn(), vertexCell.getRow());

        final String expression = energyCell.getContents();

        try {
          this.scenario.getEnergyConfig().setActorPeEnergy(actor, component, expression);
          final String msg = "Importing Energy: " + actor.getVertexPath() + " on " + component.getVlnv().getName()
              + " takes " + expression;
          PreesmLogger.getLogger().log(Level.INFO, msg);
        } catch (final NumberFormatException e) {
          final String message = "Problem importing energy of " + actor + " on " + component
              + ". Double with no space or special character needed. Be careful on the special number formats.";
          throw new PreesmRuntimeException(message);

        }
      } else if ((vertexCell == null) && !missingVertices.contains(actor)) {
        PreesmLogger.getLogger().warning(() -> "No line found in excel sheet for vertex: " + actor.getVertexPath());
        missingVertices.add(actor);
      } else if ((operatorCell == null) && !missingOperatorTypes.contains(component)) {
        PreesmLogger.getLogger().warning(() -> "No column found in excel sheet for operator type: " + component);
        missingOperatorTypes.add(component);
      }
    }
  }

}
