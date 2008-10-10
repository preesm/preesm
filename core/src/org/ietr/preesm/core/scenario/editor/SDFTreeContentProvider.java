/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This class provides the elements displayed in {@link SDFTreeSection}.
 * Each element is a vertex.
 * 
 * @author mpelcat
 */
public class SDFTreeContentProvider implements ITreeContentProvider {
	
	private Scenario scenario = null;
	
	private SDFGraph currentGraph = null;

	public SDFTreeContentProvider(CheckboxTreeViewer treeViewer) {
		super();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		Object table[] = null;
		
		if(parentElement instanceof SDFGraph){
			SDFGraph graph = (SDFGraph)parentElement;
			table = graph.vertexSet().toArray();
		}
		else if(parentElement instanceof SDFAbstractVertex){
			SDFAbstractVertex vertex = (SDFAbstractVertex)parentElement;
			table = null;
		}
		
		return table;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		boolean hasChildren = false;
		
		if(element instanceof SDFGraph){
			SDFGraph graph = (SDFGraph)element;
			hasChildren = !graph.vertexSet().isEmpty();
		}
		else if(element instanceof SDFAbstractVertex){
			SDFAbstractVertex vertex = (SDFAbstractVertex)element;
			hasChildren = false;
		}
		
		return hasChildren;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] table = new Object[1];

		if(inputElement instanceof Scenario){
			Scenario inputScenario = (Scenario)inputElement;
			
			// Opening algorithm from file
			if(inputScenario != scenario){
				scenario = inputScenario;
				currentGraph = ScenarioParser.getAlgorithm(inputScenario.getAlgorithmURL());
				table[0] = currentGraph;
			}
		}
		return table;
	}

	public SDFGraph getCurrentGraph() {
		return currentGraph;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
