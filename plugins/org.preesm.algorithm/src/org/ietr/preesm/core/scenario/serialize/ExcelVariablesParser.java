/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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

import java.io.IOException;
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
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Importing variables in a scenario from an excel file.
 *
 * @author mpelcat
 */
public class ExcelVariablesParser {

  /** The scenario. */
  private PreesmScenario scenario = null;

  /**
   * Instantiates a new excel variables parser.
   *
   * @param scenario
   *          the scenario
   */
  public ExcelVariablesParser(final PreesmScenario scenario) {
    super();
    this.scenario = scenario;
  }

  /**
   * Parses the.
   *
   * @param url
   *          the url
   * @throws InvalidModelException
   *           the invalid model exception
   */
  public void parse(final String url) throws InvalidModelException {
    PreesmLogger.getLogger().log(Level.INFO, "Importing variables from an excel sheet.");

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    WorkspaceUtils.updateWorkspace();

    final SDFGraph currentGraph = ScenarioParser.getSDFGraph(this.scenario.getAlgorithmURL());

    final Path path = new Path(url);
    final IFile file = workspace.getRoot().getFile(path);
    try {
      final Workbook w = Workbook.getWorkbook(file.getContents());

      parseVariables(w, currentGraph);

    } catch (BiffException | IOException | CoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses the variables.
   *
   * @param w
   *          the w
   * @param currentGraph
   *          the current graph
   */
  private void parseVariables(final Workbook w, final SDFGraph currentGraph) {

    for (final String varName : currentGraph.getVariables().keySet()) {

      final Cell varCell = w.getSheet(0).findCell(varName);

      if (varCell != null) {
        final Cell valueCell = w.getSheet(0).getCell(varCell.getColumn() + 1, varCell.getRow());

        if (valueCell.getType().equals(CellType.NUMBER) || valueCell.getType().equals(CellType.NUMBER_FORMULA)) {

          String value = valueCell.getContents();
          value = value.replaceAll(" ", "");

          this.scenario.getVariablesManager().setVariable(varName, value);

          PreesmLogger.getLogger().log(Level.INFO, "Importing variable: " + varName);
        }
      } else {
        PreesmLogger.getLogger().log(Level.WARNING, "No cell found in excel sheet for variable: " + varName);
      }

    }
  }
}
