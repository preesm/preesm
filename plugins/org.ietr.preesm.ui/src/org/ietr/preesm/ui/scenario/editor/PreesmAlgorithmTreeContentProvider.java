/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.ui.scenario.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Refinement;

/**
 * This class provides the elements displayed in {@link SDFTreeSection}. Each
 * element is a vertex. This tree is used in scenario editor to edit the
 * constraints
 * 
 * @author mpelcat
 */
public class PreesmAlgorithmTreeContentProvider implements ITreeContentProvider {

	private SDFGraph currentIBSDFGraph = null;

	private PiGraph currentPISDFGraph = null;

	private PreesmScenario scenario;

	/**
	 * This map keeps the VertexWithPath used as a tree content for each vertex.
	 */
	private Map<String, HierarchicalSDFVertex> correspondingVertexWithMap = null;

	public PreesmAlgorithmTreeContentProvider(CheckboxTreeViewer treeViewer) {
		super();
		correspondingVertexWithMap = new HashMap<String, HierarchicalSDFVertex>();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object table[] = null;

		if (scenario.isIBSDFScenario()) {
			if (parentElement instanceof SDFGraph) {
				SDFGraph graph = (SDFGraph) parentElement;

				// Some types of vertices are ignored in the constraints view
				table = filterIBSDFChildren(graph.vertexSet()).toArray();
			} else if (parentElement instanceof HierarchicalSDFVertex) {
				HierarchicalSDFVertex vertex = (HierarchicalSDFVertex) parentElement;
				IRefinement refinement = vertex.getStoredVertex()
						.getRefinement();

				if (refinement != null && refinement instanceof SDFGraph) {
					SDFGraph graph = (SDFGraph) refinement;
					table = filterIBSDFChildren(graph.vertexSet()).toArray();
				}
			}
		} else if (scenario.isPISDFScenario()) {
			if (parentElement instanceof PiGraph) {
				PiGraph graph = (PiGraph) parentElement;
				// Some types of vertices are ignored in the constraints view
				table = filterPISDFChildren(graph.getVertices()).toArray();
			} else if (parentElement instanceof Actor) {
				Actor actor = (Actor) parentElement;
				Refinement refinement = actor.getRefinement();

				if (refinement != null) {
					AbstractActor subgraph = refinement.getAbstractActor();
					if (subgraph instanceof PiGraph) {
						PiGraph graph = (PiGraph) subgraph;
						table = filterPISDFChildren(graph.getVertices())
								.toArray();
					}
				}

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

		if (scenario.isIBSDFScenario()) {
			if (element instanceof SDFGraph) {
				SDFGraph graph = (SDFGraph) element;
				hasChildren = !graph.vertexSet().isEmpty();
			} else if (element instanceof HierarchicalSDFVertex) {
				SDFAbstractVertex sdfVertex = ((HierarchicalSDFVertex) element)
						.getStoredVertex();
				if (sdfVertex instanceof SDFVertex) {
					SDFVertex vertex = (SDFVertex) sdfVertex;
					hasChildren = vertex.getRefinement() != null;
				}
			}
		} else if (scenario.isPISDFScenario()) {
			if (element instanceof PiGraph) {
				PiGraph graph = (PiGraph) element;
				hasChildren = !graph.getVertices().isEmpty();
			} else if (element instanceof Actor) {
				Actor actor = (Actor) element;
				hasChildren = actor.getRefinement() != null;
			}
		}

		return hasChildren;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] table = new Object[1];

		if (inputElement instanceof PreesmScenario) {
			scenario = (PreesmScenario) inputElement;
			// Opening algorithm from file
			if (scenario.isIBSDFScenario()) {
				try {
					currentIBSDFGraph = ScenarioParser.getSDFGraph(scenario
							.getAlgorithmURL());
				} catch (Exception e) {
					e.printStackTrace();
				}
				table[0] = currentIBSDFGraph;
			} else if (scenario.isPISDFScenario()) {
				try {
					currentPISDFGraph = ScenarioParser.getPiGraph(scenario
							.getAlgorithmURL());
				} catch (Exception e) {
					e.printStackTrace();
				}
				table[0] = currentPISDFGraph;
			}
		}
		return table;
	}

	public SDFGraph getIBSDFCurrentGraph() {
		return currentIBSDFGraph;
	}
	
	public PiGraph getPISDFCurrentGraph() {
		return currentPISDFGraph;
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
	public Set<AbstractActor> filterPISDFChildren(
			EList<AbstractActor> vertices) {
		Set<AbstractActor> result = new HashSet<AbstractActor>();
		for (AbstractActor actor : vertices) {
			// TODO: Filter if needed
			result.add(actor);
		}
		return result;
	}
	
	public Set<HierarchicalSDFVertex> filterIBSDFChildren(
			Set<SDFAbstractVertex> children) {

		ConcurrentSkipListSet<HierarchicalSDFVertex> appropriateChildren = new ConcurrentSkipListSet<HierarchicalSDFVertex>(
				new PathComparator());

		for (SDFAbstractVertex v : children) {
			if (v.getKind().equalsIgnoreCase("vertex")) {
				appropriateChildren.add(convertSDFChild(v));
			}
		}

		return appropriateChildren;
	}

	public HierarchicalSDFVertex convertSDFChild(SDFAbstractVertex child) {
		if (!correspondingVertexWithMap.containsKey(child.getInfo()))
			correspondingVertexWithMap.put(child.getInfo(),
					new HierarchicalSDFVertex(child));

		return correspondingVertexWithMap.get(child.getInfo());
	}

}
