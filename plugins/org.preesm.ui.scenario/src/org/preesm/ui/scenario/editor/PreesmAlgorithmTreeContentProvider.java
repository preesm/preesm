/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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
package org.preesm.ui.scenario.editor;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.scenario.PreesmScenario;

/**
 * This class provides the elements displayed in {@link SDFTreeSection}. Each element is a vertex. This tree is used in
 * scenario editor to edit the constraints
 *
 * @author mpelcat
 */
public class PreesmAlgorithmTreeContentProvider implements ITreeContentProvider {

  /** The current PISDF graph. */
  private PiGraph currentPISDFGraph = null;

  /** The scenario. */
  private PreesmScenario scenario;

  /**
   * Instantiates a new preesm algorithm tree content provider.
   *
   * @param treeViewer
   *          the tree viewer
   */
  public PreesmAlgorithmTreeContentProvider(final CheckboxTreeViewer treeViewer) {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    Object[] table = null;

    if (this.scenario.isIBSDFScenario()) {
      throw new PreesmException("IBSDF is not supported anymore");
    } else if (this.scenario.isPISDFScenario()) {
      if (parentElement instanceof PiGraph) {
        final PiGraph graph = (PiGraph) parentElement;
        // Some types of vertices are ignored in the constraints view
        table = filterPISDFChildren(graph.getActors()).toArray();
      } else if (parentElement instanceof Actor) {
        final Actor actor = (Actor) parentElement;
        if (actor.isHierarchical()) {
          final PiGraph subGraph = actor.getSubGraph();
          table = filterPISDFChildren(subGraph.getActors()).toArray();
        }
      }
    }

    return table;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
   */
  @Override
  public Object getParent(final Object element) {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    boolean hasChildren = false;

    if (this.scenario.isIBSDFScenario()) {
      throw new PreesmException("IBSDF is not supported anymore");
    } else if (this.scenario.isPISDFScenario()) {
      if (element instanceof PiGraph) {
        final PiGraph graph = (PiGraph) element;
        hasChildren = !graph.getActors().isEmpty();
      } else if (element instanceof Actor) {
        final Actor actor = (Actor) element;
        hasChildren = actor.getRefinement() != null;
      }
    }

    return hasChildren;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    final Object[] table = new Object[1];

    if (inputElement instanceof PreesmScenario) {
      this.scenario = (PreesmScenario) inputElement;
      // Opening algorithm from file
      if (this.scenario.isIBSDFScenario()) {
        throw new PreesmException("IBSDF is not supported anymore");
      } else if (this.scenario.isPISDFScenario()) {
        try {
          this.currentPISDFGraph = PiParser.getPiGraphWithReconnection(this.scenario.getAlgorithmURL());
        } catch (final Exception e) {
          e.printStackTrace();
        }
        table[0] = this.currentPISDFGraph;
      }
    }
    return table;
  }

  /**
   * Gets the PISDF current graph.
   *
   * @return the PISDF current graph
   */
  public PiGraph getPISDFCurrentGraph() {
    return this.currentPISDFGraph;
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

  }

  /**
   * Filters the children to display in the tree.
   *
   * @param vertices
   *          the vertices
   * @return the sets the
   */
  public Set<AbstractActor> filterPISDFChildren(final EList<AbstractActor> vertices) {
    final Set<AbstractActor> result = new LinkedHashSet<>();
    for (final AbstractActor actor : vertices) {
      // TODO: Filter if needed
      result.add(actor);
    }
    return result;
  }

}
