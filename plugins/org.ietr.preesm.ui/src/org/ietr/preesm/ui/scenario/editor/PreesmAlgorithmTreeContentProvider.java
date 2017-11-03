/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.ui.scenario.editor;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;

// TODO: Auto-generated Javadoc
/**
 * This class provides the elements displayed in {@link SDFTreeSection}. Each element is a vertex. This tree is used in scenario editor to edit the constraints
 *
 * @author mpelcat
 */
public class PreesmAlgorithmTreeContentProvider implements ITreeContentProvider {

  /** The current IBSDF graph. */
  private SDFGraph currentIBSDFGraph = null;

  /** The current PISDF graph. */
  private PiGraph currentPISDFGraph = null;

  /** The scenario. */
  private PreesmScenario scenario;

  /**
   * This map keeps the VertexWithPath used as a tree content for each vertex.
   */
  private Map<String, HierarchicalSDFVertex> correspondingVertexWithMap = null;

  /**
   * Instantiates a new preesm algorithm tree content provider.
   *
   * @param treeViewer
   *          the tree viewer
   */
  public PreesmAlgorithmTreeContentProvider(final CheckboxTreeViewer treeViewer) {
    super();
    this.correspondingVertexWithMap = new LinkedHashMap<>();
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
      if (parentElement instanceof SDFGraph) {
        final SDFGraph graph = (SDFGraph) parentElement;

        // Some types of vertices are ignored in the constraints view
        table = filterIBSDFChildren(graph.vertexSet()).toArray();
      } else if (parentElement instanceof HierarchicalSDFVertex) {
        final HierarchicalSDFVertex vertex = (HierarchicalSDFVertex) parentElement;
        final IRefinement refinement = vertex.getStoredVertex().getRefinement();

        if ((refinement != null) && (refinement instanceof SDFGraph)) {
          final SDFGraph graph = (SDFGraph) refinement;
          table = filterIBSDFChildren(graph.vertexSet()).toArray();
        }
      }
    } else if (this.scenario.isPISDFScenario()) {
      if (parentElement instanceof PiGraph) {
        final PiGraph graph = (PiGraph) parentElement;
        // Some types of vertices are ignored in the constraints view
        table = filterPISDFChildren(graph.getVertices()).toArray();
      } else if (parentElement instanceof Actor) {
        final Actor actor = (Actor) parentElement;
        final Refinement refinement = actor.getRefinement();

        if (refinement != null) {
          final AbstractActor subgraph = refinement.getAbstractActor();
          if (subgraph instanceof PiGraph) {
            final PiGraph graph = (PiGraph) subgraph;
            table = filterPISDFChildren(graph.getVertices()).toArray();
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

    if (this.scenario.isIBSDFScenario()) {
      if (element instanceof SDFGraph) {
        final SDFGraph graph = (SDFGraph) element;
        hasChildren = !graph.vertexSet().isEmpty();
      } else if (element instanceof HierarchicalSDFVertex) {
        final SDFAbstractVertex sdfVertex = ((HierarchicalSDFVertex) element).getStoredVertex();
        if (sdfVertex instanceof SDFVertex) {
          final SDFVertex vertex = (SDFVertex) sdfVertex;
          hasChildren = vertex.getRefinement() != null;
        }
      }
    } else if (this.scenario.isPISDFScenario()) {
      if (element instanceof PiGraph) {
        final PiGraph graph = (PiGraph) element;
        hasChildren = !graph.getVertices().isEmpty();
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
        try {
          this.currentIBSDFGraph = ScenarioParser.getSDFGraph(this.scenario.getAlgorithmURL());
        } catch (final Exception e) {
          e.printStackTrace();
        }
        table[0] = this.currentIBSDFGraph;
      } else if (this.scenario.isPISDFScenario()) {
        try {
          this.currentPISDFGraph = PiParser.getPiGraph(this.scenario.getAlgorithmURL());
        } catch (final Exception e) {
          e.printStackTrace();
        }
        table[0] = this.currentPISDFGraph;
      }
    }
    return table;
  }

  /**
   * Gets the IBSDF current graph.
   *
   * @return the IBSDF current graph
   */
  public SDFGraph getIBSDFCurrentGraph() {
    return this.currentIBSDFGraph;
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
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
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

  /**
   * Filter IBSDF children.
   *
   * @param children
   *          the children
   * @return the sets the
   */
  public Set<HierarchicalSDFVertex> filterIBSDFChildren(final Set<SDFAbstractVertex> children) {

    final ConcurrentSkipListSet<HierarchicalSDFVertex> appropriateChildren = new ConcurrentSkipListSet<>(new PathComparator());

    for (final SDFAbstractVertex v : children) {
      if (v.getKind().equalsIgnoreCase("vertex")) {
        appropriateChildren.add(convertSDFChild(v));
      }
    }

    return appropriateChildren;
  }

  /**
   * Convert SDF child.
   *
   * @param child
   *          the child
   * @return the hierarchical SDF vertex
   */
  public HierarchicalSDFVertex convertSDFChild(final SDFAbstractVertex child) {
    if (!this.correspondingVertexWithMap.containsKey(child.getInfo())) {
      this.correspondingVertexWithMap.put(child.getInfo(), new HierarchicalSDFVertex(child));
    }

    return this.correspondingVertexWithMap.get(child.getInfo());
  }

}
