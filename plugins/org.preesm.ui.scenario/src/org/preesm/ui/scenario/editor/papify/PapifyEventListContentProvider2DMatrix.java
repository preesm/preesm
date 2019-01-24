/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
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
package org.preesm.ui.scenario.editor.papify;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.preesm.commons.exceptions.PreesmFrameworkException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.scenario.PreesmScenario;

// TODO: Auto-generated Javadoc
/**
 * Provides the elements contained in the papify editor.
 *
 * @author dmadronal
 */
public class PapifyEventListContentProvider2DMatrix implements ITreeContentProvider {

  /** Currently edited scenario. */
  private PreesmScenario   scenario           = null;
  PapifyCheckStateListener checkStateListener = null;

  /** The current PISDF graph. */
  private PiGraph currentPISDFGraph = null;
  /**
   * This map keeps the VertexWithPath used as a tree content for each vertex.
   */

  /**
   * This map keeps the VertexWithPath used as a tree content for each vertex.
   */
  // private Set<PapifyEventListTreeElement> papifyEventListElements;
  // private Map<SDFAbstractVertex, HierarchicalSDFVertex> correspondingVertexWithMap = null;

  /**
   * Instantiates a new preesm algorithm tree content provider for PAPIFY.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyEventListContentProvider2DMatrix(PreesmScenario scenario) {
    super();
    this.scenario = scenario;
    // this.papifyEventListElements = new LinkedHashSet<>();
  }

  /**
   *
   */
  public void addCheckStateListener(PapifyCheckStateListener checkStateListener) {
    this.checkStateListener = checkStateListener;
  }

  /**
   *
   */
  /*
   * public void setInput() {
   *
   * Set<PapifyConfigActor> papiConfigs = this.scenario.getPapifyConfigManager().getPapifyConfigGroupsActors();
   *
   * for (PapifyConfigActor papiConfig : papiConfigs) { String actorId = papiConfig.getActorId(); for (String compName :
   * papiConfig.getPAPIEvents().keySet()) { for (PapiEvent event : papiConfig.getPAPIEvents().get(compName)) { for
   * (PapifyListTreeElement treeElement : this.elementList) { if (treeElement.label.equals(event.getName())) { final
   * Map<String, PAPIStatus> statuses = treeElement.PAPIStatuses; statuses.put(actorId, PAPIStatus.YES); } } } } } for
   * (PapifyActorListContentProvider2DMatrixES viewer : this.editingSupports) { for (PapifyListTreeElement treeElement :
   * this.elementList) { viewer.getViewer().update(treeElement, null); } } }
   */
  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    Object[] table = null;

    if (parentElement instanceof PapifyEventListTreeElement) {
      Object algorithmElement = ((PapifyEventListTreeElement) parentElement).getAlgorithmElement();
      if (this.scenario.isIBSDFScenario()) {
        throw new PreesmFrameworkException("IBSDF is not supported anymore");
      } else if (this.scenario.isPISDFScenario()) {
        if (algorithmElement instanceof PiGraph) {
          final PiGraph graph = (PiGraph) algorithmElement;
          // Some types of vertices are ignored in the constraints view
          table = filterPISDFChildren(graph.getActors()).toArray();
        } else if (algorithmElement instanceof Actor) {
          final Actor actor = (Actor) algorithmElement;
          if (actor.isHierarchical()) {
            final PiGraph subGraph = actor.getSubGraph();
            table = filterPISDFChildren(subGraph.getActors()).toArray();
          }
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
    // TODO Auto-generated method stub
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

    if (element instanceof PapifyEventListTreeElement) {
      Object algorithmElement = ((PapifyEventListTreeElement) element).getAlgorithmElement();
      if (this.scenario.isIBSDFScenario()) {
        throw new PreesmFrameworkException("IBSDF is not supported anymore");
      } else if (this.scenario.isPISDFScenario()) {
        if (algorithmElement instanceof PiGraph) {
          final PiGraph graph = (PiGraph) algorithmElement;
          hasChildren = !graph.getActors().isEmpty();
        } else if (algorithmElement instanceof Actor) {
          final Actor actor = (Actor) algorithmElement;
          hasChildren = actor.getRefinement() != null;
        }
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
        throw new PreesmFrameworkException("IBSDF is not supported anymore");
      } else if (this.scenario.isPISDFScenario()) {
        try {
          this.currentPISDFGraph = PiParser.getPiGraphWithReconnection(this.scenario.getAlgorithmURL());
        } catch (final Exception e) {
          e.printStackTrace();
        }
        final PapifyEventListTreeElement element = new PapifyEventListTreeElement(this.currentPISDFGraph);
        this.checkStateListener.addEventListTreeElement(element);
        table[0] = element;
      }
    }
    return table;
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

  /**
   * Filters the children to display in the tree.
   *
   * @param vertices
   *          the vertices
   * @return the sets the
   */
  public Set<PapifyEventListTreeElement> filterPISDFChildren(final EList<AbstractActor> vertices) {
    final Set<AbstractActor> result = new LinkedHashSet<>();
    final Set<PapifyEventListTreeElement> papifyTreeElements = new LinkedHashSet<>();
    for (final AbstractActor actor : vertices) {
      // TODO: Filter if needed
      if (!(actor instanceof DataInputInterface) && !(actor instanceof DataOutputInterface)
          && !(actor instanceof BroadcastActor) && !(actor instanceof JoinActor) && !(actor instanceof ForkActor)
          && !(actor instanceof RoundBufferActor) && !(actor instanceof DelayActor)) {
        result.add(actor);
        final PapifyEventListTreeElement elementActor = new PapifyEventListTreeElement(actor);
        this.checkStateListener.addEventListTreeElement(elementActor);
        papifyTreeElements.add(elementActor);

      }
    }
    return papifyTreeElements;
  }

}
