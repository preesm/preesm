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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * Importing constraints in a scenario from an excel file. The existing timings
 * mean that the task can be mapped on the given operator. Task names are rows
 * while operator types are columns.
 * 
 * @author mpelcat
 */
public class ExcelConstraintsParser {

	private PreesmScenario scenario = null;

	public ExcelConstraintsParser(PreesmScenario scenario) {
		super();
		this.scenario = scenario;
	}

	public void parse(String url, Set<String> allOperatorIds) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		try {
			workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SDFGraph currentGraph = ScenarioParser.getAlgorithm(scenario
				.getAlgorithmURL());

		Path path = new Path(url);
		IFile file = workspace.getRoot().getFile(path);

		scenario.getConstraintGroupManager().removeAll();
		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"Importing constraints from an excel sheet. Previously defined constraints are discarded.");

		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			// Warnings are displayed once for each missing operator or vertex
			// in the excel sheet
			Set<String> missingVertices = new HashSet<String>();
			Set<String> missingOperators = new HashSet<String>();

			for (SDFAbstractVertex vertex : currentGraph
					.getHierarchicalVertexSet()) {

				if (vertex.getKind() == "vertex") {
					for (String operatorId : allOperatorIds) {
						checkOpConstraint(w, operatorId, vertex,
								missingVertices, missingOperators);
					}
				}
			}

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

	/**
	 * Importing constraints from component names
	 */
	private void checkOpConstraint(Workbook w, String operatorId,
			SDFAbstractVertex vertex, Set<String> missingVertices,
			Set<String> missingOperators) {
		String vertexName = vertex.getName();

		if (!operatorId.isEmpty() && !vertexName.isEmpty()) {
			Cell vertexCell = w.getSheet(0).findCell(vertexName);
			Cell operatorCell = w.getSheet(0).findCell(operatorId);

			if (vertexCell != null && operatorCell != null) {
				Cell timingCell = w.getSheet(0).getCell(
						operatorCell.getColumn(), vertexCell.getRow());

				if (timingCell.getType().equals(CellType.NUMBER)
						|| timingCell.getType().equals(CellType.NUMBER_FORMULA)) {

					scenario.getConstraintGroupManager().addConstraint(
							operatorId, vertex);

					WorkflowLogger.getLogger().log(
							Level.FINE,
							"Importing constraint: {" + operatorId + ","
									+ vertex + ",yes}");

				} else {
					WorkflowLogger.getLogger().log(
							Level.FINE,
							"Importing constraint: {" + operatorId + ","
									+ vertex + ",no}");
				}
			} else {
				if (vertexCell == null && !missingVertices.contains(vertexName)) {
					if (vertex.getGraphDescription() != null) {
						WorkflowLogger.getLogger().log(
								Level.WARNING,
								"No line found in excel sheet for hierarchical vertex: "
										+ vertexName);
					} else {
						WorkflowLogger.getLogger().log(
								Level.SEVERE,
								"No line found in excel sheet for atomic vertex: "
										+ vertexName);
					}
					missingVertices.add(vertexName);
				} else if (operatorCell == null
						&& !missingOperators.contains(operatorId)) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"No column found in excel sheet for operator: "
									+ operatorId);
					missingOperators.add(operatorId);
				}
			}
		}
	}
}
