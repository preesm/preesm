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

package org.ietr.preesm.core.scenario.editor.variables;

import java.io.IOException;
import java.util.logging.Level;

import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Importing variables in a scenario from an excel file.
 * 
 * @author mpelcat
 */
public class ExcelVariablesParser {

	private PreesmScenario scenario = null;

	public ExcelVariablesParser(PreesmScenario scenario) {
		super();
		this.scenario = scenario;
	}

	public void parse(String url) {
		AbstractWorkflowLogger.getLogger().log(Level.INFO,
				"Importing variables from an excel sheet.");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		try {
			workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		SDFGraph currentGraph = ScenarioParser.getAlgorithm(scenario
				.getAlgorithmURL());

		Path path = new Path(url);
		IFile file = workspace.getRoot().getFile(path);
		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			parseVariables(w, currentGraph);

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

	private void parseVariables(Workbook w, SDFGraph currentGraph) {

		for (String varName : currentGraph.getVariables().keySet()) {

			Cell varCell = w.getSheet(0).findCell(varName);

			if (varCell != null) {
				Cell valueCell = w.getSheet(0).getCell(varCell.getColumn() + 1,
						varCell.getRow());

				if (valueCell.getType().equals(CellType.NUMBER)
						|| valueCell.getType().equals(CellType.NUMBER_FORMULA)) {

					String value = valueCell.getContents();
					value = value.replaceAll(" ", "");

					scenario.getVariablesManager().setVariable(varName, value);

					AbstractWorkflowLogger.getLogger().log(Level.INFO,
							"Importing variable: " + varName);
				}
			} else {
				AbstractWorkflowLogger.getLogger()
						.log(Level.WARNING,
								"No cell found in excel sheet for variable: "
										+ varName);
			}

		}
	}
}
