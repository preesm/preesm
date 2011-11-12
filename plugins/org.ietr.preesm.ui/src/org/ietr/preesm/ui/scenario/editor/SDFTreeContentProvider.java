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

package org.ietr.preesm.ui.scenario.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import net.sf.dftools.algorithm.model.IRefinement;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.algorithm.model.sdf.SDFVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;

/**
 * This class provides the elements displayed in {@link SDFTreeSection}. Each
 * element is a vertex. This tree is used in scenario editor to edit the
 * constraints
 * 
 * @author mpelcat
 */
public class SDFTreeContentProvider implements ITreeContentProvider {

	private SDFGraph currentGraph = null;

	/**
	 * This map keeps the VertexWithPath used as a tree content for each vertex.
	 */
	private Map<String, HierarchicalSDFVertex> correspondingVertexWithMap = null;

	public SDFTreeContentProvider(CheckboxTreeViewer treeViewer) {
		super();
		correspondingVertexWithMap = new HashMap<String, HierarchicalSDFVertex>();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object table[] = null;

		if (parentElement instanceof SDFGraph) {
			SDFGraph graph = (SDFGraph) parentElement;

			// Some types of vertices are ignored in the constraints view
			table = keepAndConvertAppropriateChildren(graph.vertexSet())
					.toArray();
		} else if (parentElement instanceof HierarchicalSDFVertex) {
			HierarchicalSDFVertex vertex = (HierarchicalSDFVertex) parentElement;
			IRefinement refinement = vertex.getStoredVertex().getRefinement();

			if (refinement != null && refinement instanceof SDFGraph) {
				SDFGraph graph = (SDFGraph) refinement;
				table = keepAndConvertAppropriateChildren(graph.vertexSet())
						.toArray();
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

		if (element instanceof SDFGraph) {
			SDFGraph graph = (SDFGraph) element;
			hasChildren = !graph.vertexSet().isEmpty();
		} else if (element instanceof HierarchicalSDFVertex) {
			SDFAbstractVertex sdfVertex = ((HierarchicalSDFVertex) element)
					.getStoredVertex();
			if (sdfVertex instanceof SDFBroadcastVertex) {
				// SDFAbstractVertex vertex = (SDFAbstractVertex)element;
				hasChildren = false;
			} else if (sdfVertex instanceof SDFVertex) {
				SDFVertex vertex = (SDFVertex) sdfVertex;
				IRefinement refinement = vertex.getRefinement();

				hasChildren = (refinement != null);
			}
		}

		return hasChildren;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] table = new Object[1];

		if (inputElement instanceof PreesmScenario) {
			PreesmScenario inputScenario = (PreesmScenario) inputElement;

			// Opening algorithm from file
			currentGraph = ScenarioParser.getAlgorithm(inputScenario
					.getAlgorithmURL());
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
	public Set<HierarchicalSDFVertex> keepAndConvertAppropriateChildren(
			Set<SDFAbstractVertex> children) {

		ConcurrentSkipListSet<HierarchicalSDFVertex> appropriateChildren = new ConcurrentSkipListSet<HierarchicalSDFVertex>(
				new PathComparator());

		for (SDFAbstractVertex v : children) {
			if (v.getKind() == "vertex") {
				appropriateChildren.add(convertChild(v));
			}
		}

		return appropriateChildren;
	}

	public HierarchicalSDFVertex convertChild(SDFAbstractVertex child) {
		if (!correspondingVertexWithMap.containsKey(child.getInfo()))
			correspondingVertexWithMap.put(child.getInfo(),
					new HierarchicalSDFVertex(child));

		return correspondingVertexWithMap.get(child.getInfo());
	}

}
