/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.timings;

import java.io.File;
import java.io.IOException;

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
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * @author mpelcat
 * 
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

			for (SDFAbstractVertex vertex : currentGraph.vertexSet()) {
				for (ArchitectureComponentDefinition operatorDef : currentArchi
						.getComponentDefinitions(ArchitectureComponentType.operator)) {

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
}
