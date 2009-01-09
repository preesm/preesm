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
 
package org.ietr.preesm.core.scenario.editor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.tools.NameComparator;
import org.sdf4j.model.IRefinement;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;
import org.sdf4j.model.sdf.esdf.SDFBroadcastVertex;

/**
 * This class provides the elements displayed in {@link SDFTreeSection}.
 * Each element is a vertex. This tree is used in scenario editor to
 * edit the constraints
 * 
 * @author mpelcat
 */
public class SDFTreeContentProvider implements ITreeContentProvider {
	
	private SDFGraph currentGraph = null;

	public SDFTreeContentProvider(CheckboxTreeViewer treeViewer) {
		super();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		Object table[] = null;
		
		if(parentElement instanceof SDFGraph){
			SDFGraph graph = (SDFGraph)parentElement;
			
			// Some types of vertices are ignored in the constraints view
			table = keepAppropriateChildren(graph.vertexSet()).toArray();
		}
		else if(parentElement instanceof SDFVertex){
			SDFVertex vertex = (SDFVertex)parentElement;
			IRefinement refinement = vertex.getRefinement();
			
			if(refinement != null && refinement instanceof SDFGraph){
				SDFGraph graph = (SDFGraph)refinement;
				table = keepAppropriateChildren(graph.vertexSet()).toArray();
			}
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
		else if(element instanceof SDFBroadcastVertex){
			//SDFAbstractVertex vertex = (SDFAbstractVertex)element;
			hasChildren = false;
		}
		else if(element instanceof SDFVertex){
			SDFVertex vertex = (SDFVertex)element;
			IRefinement refinement = vertex.getRefinement();
			
			hasChildren = (refinement != null);
		}
		
		return hasChildren;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] table = new Object[1];

		if(inputElement instanceof Scenario){
			Scenario inputScenario = (Scenario)inputElement;
			
			// Opening algorithm from file
			currentGraph = ScenarioParser.getAlgorithm(inputScenario.getAlgorithmURL());
			table[0] = currentGraph;
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

	/**
	 * Filters the children to display in the tree
	 */
	static public Set<SDFAbstractVertex> keepAppropriateChildren(Set<SDFAbstractVertex> children) {
		
		ConcurrentSkipListSet<SDFAbstractVertex> appropriateChildren = new ConcurrentSkipListSet<SDFAbstractVertex>(new NameComparator());
		
		for(SDFAbstractVertex v : children){
			if(v.getKind() == "vertex")
				appropriateChildren.add(v);
		}
		
		return appropriateChildren;
	}

}
