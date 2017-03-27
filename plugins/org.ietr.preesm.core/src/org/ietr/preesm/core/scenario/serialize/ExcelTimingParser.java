/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

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
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Refinement;

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

	public void parse(String url, Set<String> opDefIds)
			throws InvalidModelException, FileNotFoundException {
		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"Importing timings from an excel sheet. Non precised timings are kept unmodified.");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		Activator.updateWorkspace();

		Path path = new Path(url);
		IFile file = workspace.getRoot().getFile(path);
		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			// Warnings are displayed once for each missing operator or vertex
			// in the excel sheet
			Set<String> missingVertices = new HashSet<String>();
			Set<String> missingOperatorTypes = new HashSet<String>();

			parseTimings(w, opDefIds, missingVertices, missingOperatorTypes);

		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void parseTimings(Workbook w, Set<String> opDefIds,
			Set<String> missingVertices, Set<String> missingOperatorTypes)
			throws FileNotFoundException, InvalidModelException, CoreException {
		// Depending on the type of SDF graph we process (IBSDF or PISDF), call
		// one or the other method
		if (scenario.isIBSDFScenario()) {
			SDFGraph currentGraph = ScenarioParser.getSDFGraph(scenario
					.getAlgorithmURL());
			parseTimingsForIBSDFGraph(w, currentGraph, opDefIds,
					missingVertices, missingOperatorTypes);
		}

		else if (scenario.isPISDFScenario()) {
			PiGraph currentGraph = ScenarioParser.getPiGraph(scenario
					.getAlgorithmURL());
			parseTimingsForPISDFGraph(w, currentGraph, opDefIds,
					missingVertices, missingOperatorTypes);
		}

	}

	private void parseTimingsForPISDFGraph(Workbook w, PiGraph currentGraph,
			Set<String> opDefIds, Set<String> missingVertices,
			Set<String> missingOperatorTypes) {
		// Each of the vertices of the graph is either itself a graph
		// (hierarchical vertex), in which case we call recursively this method;
		// a standard actor, in which case we parser its timing; or a special
		// vertex, in which case we do nothing
		for (AbstractActor vertex : currentGraph.getVertices()) {
			// Handle connected graphs from hierarchical vertices
			if (vertex instanceof PiGraph) {
				parseTimingsForPISDFGraph(w, (PiGraph) vertex, opDefIds,
						missingVertices, missingOperatorTypes);
			} else if (vertex instanceof Actor) {
				Actor actor = (Actor) vertex;

				// Handle unconnected graphs from hierarchical vertices
				Refinement refinement = actor.getRefinement();
				AbstractActor subgraph = null;
				if (refinement != null)
					subgraph = refinement.getAbstractActor();

				if (subgraph != null && subgraph instanceof PiGraph) {
					parseTimingsForPISDFGraph(w, (PiGraph) subgraph, opDefIds,
							missingVertices, missingOperatorTypes);
				}
				// If the actor is not hierarchical, parse its timing
				else {
					parseTimingForVertex(w, vertex.getName(), opDefIds,
							missingVertices, missingOperatorTypes);
				}
			}
		}

	}

	private void parseTimingForVertex(Workbook w, String vertexName,
			Set<String> opDefIds, Set<String> missingVertices,
			Set<String> missingOperatorTypes) {
		// For each kind of processing elements, we look for a timing for given
		// vertex
		for (String opDefId : opDefIds) {
			if (!opDefId.isEmpty() && !vertexName.isEmpty()) {
				// Get row and column for the timing we are looking for
				Cell vertexCell = w.getSheet(0).findCell(vertexName);
				Cell operatorCell = w.getSheet(0).findCell(opDefId);

				if (vertexCell != null && operatorCell != null) {
					// Get the cell containing the timing
					Cell timingCell = w.getSheet(0).getCell(
							operatorCell.getColumn(), vertexCell.getRow());

					if (timingCell.getType().equals(CellType.NUMBER)
							|| timingCell.getType().equals(
									CellType.NUMBER_FORMULA)) {

						String stringTiming = timingCell.getContents();
						// Removing useless characters (spaces...)
						stringTiming = stringTiming.replaceAll(" ", "");

						try {
							Timing timing = new Timing(opDefId, vertexName,
									Long.valueOf(timingCell.getContents()));

							scenario.getTimingManager().addTiming(timing);

							WorkflowLogger.getLogger().log(Level.INFO,
									"Importing timing: " + timing.toString());
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

	private void parseTimingsForIBSDFGraph(Workbook w, SDFGraph currentGraph,
			Set<String> opDefIds, Set<String> missingVertices,
			Set<String> missingOperatorTypes) {
		// Each of the vertices of the graph is either itself a graph
		// (hierarchical vertex), in which case we call recursively this method;
		// a standard vertex, in which case we parser its timing; or another
		// kind of vertex, in which case we do nothing
		for (SDFAbstractVertex vertex : currentGraph.vertexSet()) {
			if (vertex.getGraphDescription() != null) {
				parseTimingsForIBSDFGraph(w,
						(SDFGraph) vertex.getGraphDescription(), opDefIds,
						missingVertices, missingOperatorTypes);
			} else if (vertex.getKind().equalsIgnoreCase("vertex")) {
				parseTimingForVertex(w, vertex.getName(), opDefIds,
						missingVertices, missingOperatorTypes);
			}
		}
	}
}
