/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.scenario.editor.timings;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessorDefinition;
import org.ietr.preesm.core.architecture.advancedmodel.ProcessorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.Timing;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Importing timings in a scenario from an excel file. task names are rows while
 * operator types are columns
 * 
 * @author mpelcat
 */
public class ExcelTimingParser {

	private Scenario scenario = null;

	public ExcelTimingParser(Scenario scenario) {
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
		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			Set<ArchitectureComponentDefinition> opDefs = new HashSet<ArchitectureComponentDefinition>();

			opDefs
					.addAll(currentArchi
							.getComponentDefinitions(ArchitectureComponentType.operator));
			opDefs
					.addAll(currentArchi
							.getComponentDefinitions(ArchitectureComponentType.processor));
			opDefs
					.addAll(currentArchi
							.getComponentDefinitions(ArchitectureComponentType.ipCoprocessor));

			parseTimings(w, currentGraph, opDefs);

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
	

	private void parseTimings(Workbook w, SDFGraph currentGraph, Set<ArchitectureComponentDefinition> opDefs) {
		for (SDFAbstractVertex vertex : currentGraph.vertexSet()) {
			for (ArchitectureComponentDefinition operatorDef : opDefs) {

				String operatorId = ((OperatorDefinition) operatorDef)
						.getId();
				String vertexName = vertex.getName();

				if (!operatorId.isEmpty() && !vertexName.isEmpty()) {
					Cell vertexCell = w.getSheet(0).findCell(vertexName);
					Cell operatorCell = w.getSheet(0).findCell(operatorId);

					if (vertexCell != null && operatorCell != null) {
						Cell timingCell = w.getSheet(0).getCell(
								operatorCell.getColumn(),
								vertexCell.getRow());

						if (timingCell.getType().equals(CellType.NUMBER)
								|| timingCell.getType().equals(
										CellType.NUMBER_FORMULA)) {
							Timing timing = new Timing(
									((OperatorDefinition) operatorDef),
									vertex, Integer.valueOf(timingCell
											.getContents()));
							scenario.getTimingManager().addTiming(timing);
						}
					}
				}
			}
			
			if(vertex.getGraphDescription() != null){
				parseTimings(w, (SDFGraph)vertex.getGraphDescription(), opDefs);
			}
		}
	}
}
