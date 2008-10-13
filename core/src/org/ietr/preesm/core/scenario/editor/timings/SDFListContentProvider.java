/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.timings;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Provides the elements contained in the timing editor
 * 
 * @author mpelcat
 */
public class SDFListContentProvider implements IStructuredContentProvider{

	
	private Scenario scenario = null;
	
	private SDFGraph currentGraph = null;

	Object[] elementTable = null;
	
	@Override
	public Object[] getElements(Object inputElement) {


		if(inputElement instanceof Scenario){
			Scenario inputScenario = (Scenario)inputElement;
			
			// Opening algorithm from file
			if(inputScenario != scenario){
				scenario = inputScenario;
				currentGraph = ScenarioParser.getAlgorithm(inputScenario.getAlgorithmURL());
				elementTable = currentGraph.vertexSet().toArray();
			}
		}
		return elementTable;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

}
