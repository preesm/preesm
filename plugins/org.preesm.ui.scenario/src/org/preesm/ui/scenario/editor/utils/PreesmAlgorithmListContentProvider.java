/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
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
package org.preesm.ui.scenario.editor.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

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

    if (inputElement instanceof Scenario) {
      final Scenario inputScenario = (Scenario) inputElement;

      try {
        elementTable = getSortedPISDFVertices(inputScenario).toArray();
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
   * @throws CoreException
   *           the core exception
   */
  public static List<AbstractActor> getSortedPISDFVertices(final Scenario inputScenario) {
    final PiGraph currentGraph = inputScenario.getAlgorithm();
    return filterVertices(currentGraph.getAllActors());
  }

  /**
   * Filter out special actors and hierarchical actors.
   *
   * @param vertices
   *          the set of AbstractActor to filter
   * @return a set of Actors, with none of them being a hierarchical actor
   */
  private static List<AbstractActor> filterVertices(final EList<AbstractActor> vertices) {
    return vertices.stream().filter(Actor.class::isInstance).map(Actor.class::cast).filter(a -> !a.isHierarchical())
        .collect(Collectors.toList());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // no behavior by default
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // no behavior by default
  }

}
