/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.scenario.serialize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
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

/**
 * Importing timings in a scenario from an excel file. task names are rows while
 * operator types are columns
 * 
 * @author mpelcat
 */
public class ExcelTimingParser {

	private PreesmScenario scenario = null;

	public ExcelTimingParser(PreesmScenario scenario) {
		super();
		this.scenario = scenario;
	}

	public void parse(String url, Set<String> opDefIds) throws InvalidModelException,FileNotFoundException {
		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"Importing timings from an excel sheet. Non precised timings are kept unmodified.");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		Activator.updateWorkspace();

		SDFGraph currentGraph = ScenarioParser.getSDFGraph(scenario
				.getAlgorithmURL());

		Path path = new Path(url);
		IFile file = workspace.getRoot().getFile(path);
		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			// Warnings are displayed once for each missing operator or vertex
			// in the excel sheet
			Set<String> missingVertices = new HashSet<String>();
			Set<String> missingOperatorTypes = new HashSet<String>();

			parseTimings(w, currentGraph, opDefIds, missingVertices,
					missingOperatorTypes);

		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseTimings(Workbook w, SDFGraph currentGraph,
			Set<String> opDefIds, Set<String> missingVertices,
			Set<String> missingOperatorTypes) {

		for (SDFAbstractVertex vertex : currentGraph.vertexSet()) {

			if (vertex.getGraphDescription() != null) {
				parseTimings(w, (SDFGraph) vertex.getGraphDescription(),
						opDefIds, missingVertices, missingOperatorTypes);
			} else if (vertex.getKind().equalsIgnoreCase("vertex")) {
				for (String opDefId : opDefIds) {
					String vertexName = vertex.getName();

					if (!opDefId.isEmpty() && !vertexName.isEmpty()) {
						Cell vertexCell = w.getSheet(0).findCell(vertexName);
						Cell operatorCell = w.getSheet(0).findCell(opDefId);

						if (vertexCell != null && operatorCell != null) {
							Cell timingCell = w.getSheet(0).getCell(
									operatorCell.getColumn(),
									vertexCell.getRow());

							if (timingCell.getType().equals(CellType.NUMBER)
									|| timingCell.getType().equals(
											CellType.NUMBER_FORMULA)) {

								String stringTiming = timingCell.getContents();
								// Removing useless characters (spaces...)
								stringTiming = stringTiming.replaceAll(" ", "");

								try {
									Timing timing = new Timing(opDefId,
											vertex.getName(),
											Integer.valueOf(timingCell
													.getContents()));

									scenario.getTimingManager().addTiming(
											timing);

									WorkflowLogger.getLogger().log(
											Level.INFO,
											"Importing timing: "
													+ timing.toString());
								} catch (NumberFormatException e) {
									WorkflowLogger
											.getLogger()
											.log(Level.SEVERE,
													"Problem importing timing of "
															+ vertexName
															+ " on "
															+ opDefId
															+ ". Integer with no space or special character needed. Be careful on the special number formats.");
								}
							}
						} else {
							if (vertexCell == null
									&& !missingVertices.contains(vertexName)) {
								WorkflowLogger.getLogger().log(
										Level.WARNING,
										"No line found in excel sheet for vertex: "
												+ vertexName);
								missingVertices.add(vertexName);
							} else if (operatorCell == null
									&& !missingOperatorTypes.contains(opDefId)) {
								WorkflowLogger.getLogger().log(
										Level.WARNING,
										"No column found in excel sheet for operator type: "
												+ opDefId);
								missingOperatorTypes.add(opDefId);
							}
						}
					}
				}
			}
		}
	}
}
