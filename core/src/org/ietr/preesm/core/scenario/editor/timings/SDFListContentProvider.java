/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.timings;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Provides the elements contained in the timing editor
 * 
 * @author mpelcat
 */
public class SDFListContentProvider implements IStructuredContentProvider{

	
	private Scenario scenario = null;
	
	private SDFGraph currentGraph = null;
	
	@Override
	public Object[] getElements(Object inputElement) {

		Object[] table = new Object[1];

		if(inputElement instanceof Scenario){
			Scenario inputScenario = (Scenario)inputElement;
			
			// Opening algorithm from file
			if(inputScenario != scenario){
				scenario = inputScenario;
				currentGraph = ScenarioParser.getAlgorithm(inputScenario.getAlgorithmURL());
				table = currentGraph.vertexSet().toArray();
			}
		}
		return table;
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
