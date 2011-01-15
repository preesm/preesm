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

package org.ietr.preesm.core.scenario.editor.simu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.tools.PathComparator;
import org.sdf4j.model.IRefinement;

/**
 * This class provides the elements displayed in {@link OperatorTreeSection}.
 * Each element is a vertex. This tree is used in scenario editor to edit the
 * constraints
 * 
 * @author mpelcat
 */
public class OperatorTreeContentProvider implements ITreeContentProvider {

	private MultiCoreArchitecture currentArchi = null;

	/**
	 * This map keeps the VertexWithPath used as a tree content for each vertex.
	 */
	private Map<String, HierarchicalArchiCmp> correspondingCmpWithPath = null;

	public OperatorTreeContentProvider(CheckboxTreeViewer treeViewer) {
		super();
		correspondingCmpWithPath = new HashMap<String, HierarchicalArchiCmp>();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object table[] = null;

		if (parentElement instanceof MultiCoreArchitecture) {
			MultiCoreArchitecture archi = (MultiCoreArchitecture) parentElement;

			// Some types of vertices are ignored in the constraints view
			table = convertChildren(archi.vertexSet()).toArray();
		} else if (parentElement instanceof HierarchicalArchiCmp) {
			HierarchicalArchiCmp vertex = (HierarchicalArchiCmp) parentElement;
			IRefinement refinement = vertex.getStoredVertex().getRefinement();

			if (refinement != null
					&& refinement instanceof MultiCoreArchitecture) {
				MultiCoreArchitecture archi = (MultiCoreArchitecture) refinement;
				table = convertChildren(archi.vertexSet()).toArray();
			}
		}

		return table;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		boolean hasChildren = false;

		if (element instanceof MultiCoreArchitecture) {
			MultiCoreArchitecture graph = (MultiCoreArchitecture) element;
			hasChildren = !graph.vertexSet().isEmpty();
		} else if (element instanceof HierarchicalArchiCmp) {
			ArchitectureComponent op = ((HierarchicalArchiCmp) element)
					.getStoredVertex();

			IRefinement refinement = op.getRefinement();
			hasChildren = (refinement != null);
		}

		return hasChildren;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] table = new Object[1];

		if (inputElement instanceof PreesmScenario) {
			PreesmScenario inputScenario = (PreesmScenario) inputElement;

			// Opening algorithm from file
			currentArchi = ScenarioParser.getArchitecture(inputScenario
					.getArchitectureURL());
			table[0] = currentArchi;
		}
		return table;
	}

	public MultiCoreArchitecture getCurrentArchi() {
		return currentArchi;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public HierarchicalArchiCmp convertChild(ArchitectureComponent child) {

		if (!correspondingCmpWithPath.containsKey(child.getInfo()))
			correspondingCmpWithPath.put(child.getInfo(),
					new HierarchicalArchiCmp(child));

		return correspondingCmpWithPath.get(child.getInfo());
	}

	/**
	 * Returns the children to display in the tree
	 */
	public Set<HierarchicalArchiCmp> convertChildren(
			Set<ArchitectureComponent> children) {

		ConcurrentSkipListSet<HierarchicalArchiCmp> appropriateChildren = new ConcurrentSkipListSet<HierarchicalArchiCmp>(
				new PathComparator());

		for (ArchitectureComponent v : children) {
			if (v instanceof Operator) {
				appropriateChildren.add(convertChild((Operator) v));
			}
		}

		return appropriateChildren;
	}

}
