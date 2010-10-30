/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.core.scenario.editor.constraints;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Importing constraints in a scenario from an excel file. The existing timings
 * mean that the task can be mapped on the given operator. Task names are rows
 * while operator types are columns.
 * 
 * @author mpelcat
 */
public class ExcelConstraintsParser {

	private Scenario scenario = null;

	public ExcelConstraintsParser(Scenario scenario) {
		super();
		this.scenario = scenario;
	}

	public void parse(String url) {

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
		MultiCoreArchitecture currentArchi = ScenarioParser
				.getArchitecture(scenario.getArchitectureURL());

		Path path = new Path(url);
		IFile file = workspace.getRoot().getFile(path);

		scenario.getConstraintGroupManager().removeAll();
		PreesmLogger
				.getLogger()
				.log(
						Level.INFO,
						"Importing constraints from an excel sheet. Previously defined constraints are discarded.");

		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			// Warnings are displayed once for each missing operator or vertex in the excel sheet
			Set<String> missingVertices = new HashSet<String>();
			Set<String> missingOperators = new HashSet<String>();

			for (SDFAbstractVertex vertex : currentGraph
					.getHierarchicalVertexSet()) {

				if (vertex.getKind() == "vertex") {
					for (ArchitectureComponent operator : currentArchi
							.getComponents(ArchitectureComponentType.operator)) {
						checkOpConstraint(w, (Operator) operator, currentArchi,
								vertex, missingVertices, missingOperators);
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
	private void checkOpConstraint(Workbook w, Operator operator,
			MultiCoreArchitecture archi, SDFAbstractVertex vertex,
			Set<String> missingVertices, Set<String> missingOperators) {
		String operatorName = operator.getName();
		String vertexName = vertex.getName();

		if (!operatorName.isEmpty() && !vertexName.isEmpty()) {
			Cell vertexCell = w.getSheet(0).findCell(vertexName);
			Cell operatorCell = w.getSheet(0).findCell(operatorName);

			if (vertexCell != null && operatorCell != null) {
				Cell timingCell = w.getSheet(0).getCell(
						operatorCell.getColumn(), vertexCell.getRow());

				if (timingCell.getType().equals(CellType.NUMBER)
						|| timingCell.getType().equals(CellType.NUMBER_FORMULA)) {

					scenario.getConstraintGroupManager().addConstraint(
							(Operator) operator, vertex);

					PreesmLogger.getLogger().log(
							Level.FINE,
							"Importing constraint: {" + operator.getName()
									+ "," + vertex + ",yes}");

				} else {
					PreesmLogger.getLogger().log(
							Level.FINE,
							"Importing constraint: {" + operator.getName()
									+ "," + vertex + ",no}");
				}
			} else {
				if (vertexCell == null && !missingVertices.contains(vertexName)) {
					if (vertex.getGraphDescription() != null){
						PreesmLogger.getLogger().log(
								Level.WARNING,
								"No line found in excel sheet for hierarchical vertex: "
										+ vertexName);
					}
					else{
						PreesmLogger.getLogger().log(
								Level.SEVERE,
								"No line found in excel sheet for atomic vertex: "
										+ vertexName);
					}
					missingVertices.add(vertexName);
				} else if (operatorCell == null
						&& !missingOperators.contains(operatorName)) {
					PreesmLogger.getLogger().log(
							Level.SEVERE,
							"No column found in excel sheet for operator: "
									+ operatorName);
					missingOperators.add(operatorName);
				}
			}
		}
	}
}
