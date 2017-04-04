/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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

package org.ietr.preesm.core.scenario.serialize;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.utils.sdf.NameComparator;

/**
 * Provides the elements contained in the timing editor
 * 
 * @author mpelcat
 */
public class PreesmAlgorithmListContentProvider implements
		IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		Object[] elementTable = null;

		if (inputElement instanceof PreesmScenario) {
			PreesmScenario inputScenario = (PreesmScenario) inputElement;

			try {
				if (inputScenario.isIBSDFScenario())
					elementTable = getSortedIBSDFVertices(inputScenario)
							.toArray();
				else if (inputScenario.isPISDFScenario())
					elementTable = getSortedPISDFVertices(inputScenario)
							.toArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return elementTable;
	}

	public Set<AbstractActor> getSortedPISDFVertices(
			PreesmScenario inputScenario) throws InvalidModelException,
			CoreException {
		PiGraph currentGraph = ScenarioParser.getPiGraph(inputScenario
				.getAlgorithmURL());
		return filterVertices(currentGraph.getAllVertices());
	}

	/**
	 * Filter out special actors and hierarchical actors
	 * 
	 * @param vertices
	 *            the set of AbstractActor to filter
	 * @return a set of Actors, with none of them being a hierarchical actor
	 */
	private Set<AbstractActor> filterVertices(EList<AbstractActor> vertices) {
		Set<AbstractActor> result = new ConcurrentSkipListSet<AbstractActor>(new Comparator<AbstractActor>() {
			@Override
			public int compare(AbstractActor o1, AbstractActor o2) {
				return o1.getName().compareTo(o2.getName());
			}			
		});
		for (AbstractActor vertex : vertices) {
			if (vertex instanceof Actor) {
				Refinement refinement = ((Actor) vertex).getRefinement();
				if (refinement == null) result.add(vertex);
				else {
					AbstractActor subGraph = refinement.getAbstractActor();
					if (subGraph == null) result.add(vertex);
					else if (!(subGraph instanceof PiGraph)) result.add(vertex);
				}
			}
		}
		return result;
	}

	public Set<SDFAbstractVertex> getSortedIBSDFVertices(
			PreesmScenario inputScenario) throws InvalidModelException,
			FileNotFoundException {
		Set<SDFAbstractVertex> sortedVertices = null;
		// Opening algorithm from file
		SDFGraph currentGraph = ScenarioParser.getSDFGraph(inputScenario
				.getAlgorithmURL());

		// Displays the task names in alphabetical order
		if (currentGraph != null) {

			// lists the vertices in hierarchy
			Set<SDFAbstractVertex> vertices = currentGraph
					.getHierarchicalVertexSet();

			// Filters the results
			filterVertices(vertices);

			sortedVertices = new ConcurrentSkipListSet<SDFAbstractVertex>(
					new NameComparator());
			sortedVertices.addAll(vertices);
		}

		return sortedVertices;
	}

	/**
	 * Depending on the kind of vertex, timings are edited or not
	 */
	public void filterVertices(Set<SDFAbstractVertex> vertices) {

		Iterator<SDFAbstractVertex> iterator = vertices.iterator();

		while (iterator.hasNext()) {
			SDFAbstractVertex vertex = iterator.next();

			if (vertex.getKind().equalsIgnoreCase("Broadcast")) {
				iterator.remove();
			} else if (vertex.getKind().equalsIgnoreCase("port")) {
				iterator.remove();
			} else if (vertex.getGraphDescription() != null) {
				// Timings of vertices with graph description are deduced and
				// not entered in scenario
				iterator.remove();
			}
		}
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
