/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.scenario.serialize;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.preesm.algorithm.importer.InvalidModelException;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.utils.sdf.NameComparator;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.scenario.PreesmScenario;

// TODO: Auto-generated Javadoc
/**
 * Provides the elements contained in the timing editor.
 *
 * @author mpelcat
 */
public class PreesmAlgorithmListContentProvider implements IStructuredContentProvider {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    Object[] elementTable = null;

    if (inputElement instanceof PreesmScenario) {
      final PreesmScenario inputScenario = (PreesmScenario) inputElement;

      try {
        if (inputScenario.isIBSDFScenario()) {
          elementTable = getSortedIBSDFVertices(inputScenario).toArray();
        } else if (inputScenario.isPISDFScenario()) {
          elementTable = getSortedPISDFVertices(inputScenario).toArray();
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    return elementTable;
  }

  /**
   * Gets the sorted PISDF vertices.
   *
   * @param inputScenario
   *          the input scenario
   * @return the sorted PISDF vertices
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   */
  public Set<AbstractActor> getSortedPISDFVertices(final PreesmScenario inputScenario)
      throws InvalidModelException, CoreException {
    final PiGraph currentGraph = PiParser.getPiGraph(inputScenario.getAlgorithmURL());
    return filterVertices(currentGraph.getAllActors());
  }

  /**
   * Filter out special actors and hierarchical actors.
   *
   * @param vertices
   *          the set of AbstractActor to filter
   * @return a set of Actors, with none of them being a hierarchical actor
   */
  private Set<AbstractActor> filterVertices(final EList<AbstractActor> vertices) {
    return vertices.stream().filter(Actor.class::isInstance).map(Actor.class::cast).filter(a -> !a.isHierarchical())
        .collect(Collectors.toSet());
  }

  /**
   * Depending on the kind of vertex, timings are edited or not.
   *
   * @param vertices
   *          the vertices
   */
  public void filterVertices(final Set<SDFAbstractVertex> vertices) {

    final Iterator<SDFAbstractVertex> iterator = vertices.iterator();

    while (iterator.hasNext()) {
      final SDFAbstractVertex vertex = iterator.next();

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

  /**
   * Gets the sorted IBSDF vertices.
   *
   * @param inputScenario
   *          the input scenario
   * @return the sorted IBSDF vertices
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public Set<SDFAbstractVertex> getSortedIBSDFVertices(final PreesmScenario inputScenario)
      throws InvalidModelException, FileNotFoundException {
    Set<SDFAbstractVertex> sortedVertices = null;
    // Opening algorithm from file
    final SDFGraph currentGraph = ScenarioParser.getSDFGraph(inputScenario.getAlgorithmURL());

    // Displays the task names in alphabetical order
    if (currentGraph != null) {

      // lists the vertices in hierarchy
      final Set<SDFAbstractVertex> vertices = currentGraph.getHierarchicalVertexSet();

      // Filters the results
      filterVertices(vertices);

      sortedVertices = new ConcurrentSkipListSet<>(new NameComparator());
      sortedVertices.addAll(vertices);
    }

    return sortedVertices;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TODO Auto-generated method stub

  }

}
