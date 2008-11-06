/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.constraints;

import java.io.File;
import java.io.IOException;
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
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Importing constraints in a scenario from an excel file.
 * The existing timings mean that the task can be mapped on the given operator.
 * Task names are rows while operator types are columns.
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
		
		try {
			Workbook w = Workbook.getWorkbook(file.getContents());

			for (SDFAbstractVertex vertex : currentGraph.vertexSet()) {
				for (ArchitectureComponentDefinition operatorDef : currentArchi
						.getComponentDefinitions(ArchitectureComponentType.operator)) {

					String operatorDefId = ((OperatorDefinition) operatorDef)
							.getId();
					String vertexName = vertex.getName();

					if (!operatorDefId.isEmpty() && !vertexName.isEmpty()) {
						Cell vertexCell = w.getSheet(0).findCell(vertexName);
						Cell operatorCell = w.getSheet(0).findCell(operatorDefId);

						if (vertexCell != null && operatorCell != null) {
							Cell timingCell = w.getSheet(0).getCell(
									operatorCell.getColumn(),
									vertexCell.getRow());

							if (timingCell.getType().equals(CellType.NUMBER)
									|| timingCell.getType().equals(
											CellType.NUMBER_FORMULA)) {
								Set<ArchitectureComponent> operators = currentArchi.getComponents(ArchitectureComponentType.operator);
								
								for(ArchitectureComponent operator:operators){
									if(operator.getDefinition().getId().equalsIgnoreCase(operatorDefId))
										scenario.getConstraintGroupManager().addConstraint((Operator)operator, vertex);
								}
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
