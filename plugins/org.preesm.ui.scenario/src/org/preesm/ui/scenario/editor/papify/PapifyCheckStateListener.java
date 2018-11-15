/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
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
package org.preesm.ui.scenario.editor.papify;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapiEventInfo;
import org.ietr.preesm.core.scenario.papi.PapiEventModifier;
import org.ietr.preesm.core.scenario.papi.PapiEventSet;
import org.ietr.preesm.core.scenario.papi.PapifyConfigActor;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
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
import org.preesm.ui.scenario.editor.HierarchicalSDFVertex;
import org.preesm.ui.scenario.editor.ISDFCheckStateListener;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.PreesmAlgorithmTreeContentProvider;
import org.preesm.ui.scenario.editor.papify.PapifyEventListTreeElement.PAPIEventStatus;

// TODO: Auto-generated Javadoc
/**
 * Listener of the check state of both the PAPI component and the PAPI event tables but also of the selection
 * modification of the current core definition. It updates the check Papify configuration depending on the PapifyConf
 * groups in the scenario
 *
 * @author dmadronal
 */
public class PapifyCheckStateListener implements ISDFCheckStateListener {

  /** Currently edited scenario. */
  private PreesmScenario scenario = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  private Set<PapifyEventListContentProvider2DMatrixES> editingSupports = new LinkedHashSet<>();

  private Set<PapifyEventListTreeElement> elementList;

  /**
   * Instantiates a new constraints check state listener.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyCheckStateListener(final Composite container, final PreesmScenario scenario) {
    super();
    this.scenario = scenario;
    this.elementList = new LinkedHashSet<>();
  }

  /**
   * Add a new element to the tree.
   *
   */
  public void addEventListTreeElement(final PapifyEventListTreeElement element) {
    if (!this.elementList.contains(element)) {
      this.elementList.add(element);
    }
  }

  /**
   * Gets the Papi Component list.
   *
   * @return the Papi Component list
   */
  public Set<PapifyEventListTreeElement> getComponents() {
    return this.elementList;
  }

  /**
   * Clear the checkStateListener for the events table.
   *
   */
  public void clearEvents() {
    this.editingSupports = new LinkedHashSet<>();
    this.elementList = new LinkedHashSet<>();
  }

  /**
   * 
   */
  public void addEstatusSupport(PapifyEventListContentProvider2DMatrixES editingSupport) {
    if (editingSupport != null) {
      this.editingSupports.add(editingSupport);
    }
  }

  /**
   * 
   */
  public void removeEventfromActor(Object actorInstance, String eventName) {
    // The timing event
    PapiEvent timingEvent = new PapiEvent();
    ArrayList<PapiEventModifier> modifTimingList = new ArrayList<>();
    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    timingEvent.setModifiers(modifTimingList);
    boolean timing = false;

    String actorPath = "";

    if (actorInstance instanceof HierarchicalSDFVertex) {
      actorPath = ((HierarchicalSDFVertex) actorInstance).getName();
    } else if (actorInstance instanceof SDFGraph) {
      actorPath = ((SDFGraph) actorInstance).getName();
    } else if (actorInstance instanceof AbstractActor) {
      actorPath = ((AbstractActor) actorInstance).getVertexPath();
    }

    if (!actorPath.equals("") && !eventName.equals("")) {

      if (eventName.equals(timingEvent.getName())) {
        timing = true;
      }
      PapifyConfigActor papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(actorPath);
      PapiEventInfo papiData = this.scenario.getPapifyConfigManager().getPapifyData();
      String compName = "";
      PapiEvent event = null;
      boolean found = false;
      if (!timing) {
        for (PapiComponent comp : papiData.getComponents()) {
          for (PapiEventSet eventSet : comp.getEventSets()) {
            for (PapiEvent eventAux : eventSet.getEvents()) {
              if (eventAux.getModifiers().isEmpty() && eventAux.getName().equals(eventName)) {
                compName = comp.getId();
                event = eventAux;
                found = true;
                break;
              }
            }
          }
        }
      } else {
        found = true;
        event = timingEvent;
      }

      Map<String, PAPIEventStatus> statuses = new LinkedHashMap<>();
      for (PapifyEventListTreeElement treeElement : this.elementList) {
        if (treeElement.actorPath.equals(actorPath)) {
          statuses = treeElement.PAPIStatuses;
          break;
        }
      }
      if (found) {

        if (!timing) {
          papiConfig.removePAPIEvent(compName, event);
        } else {
          papiConfig.removePAPIEvent("Timing", event);
        }
        statuses.put(eventName, PAPIEventStatus.NO);
        if (hasChildren(actorInstance)) {
          final Set<String> actorPaths = getChildren(actorInstance);
          for (String oneActorPath : actorPaths) {
            for (PapifyEventListTreeElement treeElement : this.elementList) {
              if (treeElement.actorPath.equals(oneActorPath)) {
                treeElement.PAPIStatuses.put(eventName, PAPIEventStatus.NO);
                papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(oneActorPath);
                if (!timing) {
                  papiConfig.removePAPIEvent(compName, event);
                } else {
                  papiConfig.removePAPIEvent("Timing", event);
                }
                break;
              }
            }
          }
        }
      }
    }
  }

  /**
   * 
   */
  public void addEventtoActor(Object actorInstance, String eventName) {
    // The timing event
    PapiEvent timingEvent = new PapiEvent();
    ArrayList<PapiEventModifier> modifTimingList = new ArrayList<>();
    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    timingEvent.setModifiers(modifTimingList);
    boolean timing = false;

    String actorPath = "";

    if (actorInstance instanceof HierarchicalSDFVertex) {
      actorPath = ((HierarchicalSDFVertex) actorInstance).getName();
    } else if (actorInstance instanceof SDFGraph) {
      actorPath = ((SDFGraph) actorInstance).getName();
    } else if (actorInstance instanceof AbstractActor) {
      actorPath = ((AbstractActor) actorInstance).getVertexPath();
    }

    if (!actorPath.equals("") && !eventName.equals("")) {

      if (eventName.equals(timingEvent.getName())) {
        timing = true;
      }
      PapifyConfigActor papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(actorPath);
      PapiEventInfo papiData = this.scenario.getPapifyConfigManager().getPapifyData();
      String compName = "";
      PapiEvent event = null;
      boolean found = false;
      if (!timing) {
        for (PapiComponent comp : papiData.getComponents()) {
          for (PapiEventSet eventSet : comp.getEventSets()) {
            for (PapiEvent eventAux : eventSet.getEvents()) {
              if (eventAux.getModifiers().isEmpty() && eventAux.getName().equals(eventName)) {
                compName = comp.getId();
                event = eventAux;
                found = true;
                break;
              }
            }
          }
        }
      } else {
        found = true;
        event = timingEvent;
      }

      Map<String, PAPIEventStatus> statuses = new LinkedHashMap<>();
      for (PapifyEventListTreeElement treeElement : this.elementList) {
        if (treeElement.actorPath.equals(actorPath)) {
          statuses = treeElement.PAPIStatuses;
          break;
        }
      }
      if (found) {

        if (!timing) {
          papiConfig.addPAPIEvent(compName, event);
        } else {
          papiConfig.addPAPIEvent("Timing", event);
        }
        statuses.put(eventName, PAPIEventStatus.YES);
        if (hasChildren(actorInstance)) {
          final Set<String> actorPaths = getChildren(actorInstance);
          for (String oneActorPath : actorPaths) {
            for (PapifyEventListTreeElement treeElement : this.elementList) {
              if (treeElement.actorPath.equals(oneActorPath)) {
                treeElement.PAPIStatuses.put(eventName, PAPIEventStatus.YES);
                papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(oneActorPath);
                if (!timing) {
                  papiConfig.addPAPIEvent(compName, event);
                } else {
                  papiConfig.addPAPIEvent("Timing", event);
                }
                break;
              }
            }
          }
        }
      }
    }
  }

  /**
   * 
   */
  public void updateView(String event) {

    for (PapifyEventListContentProvider2DMatrixES viewer : this.editingSupports) {
      if (viewer.eventName.equals(event)) {
        for (PapifyEventListTreeElement treeElement : this.elementList) {
          viewer.getViewer().update(treeElement, null);
        }
        break;
      }
    }
  }

  /**
   * Sets the different necessary attributes.
   *
   * @param propertyListener
   *          the property listener
   */
  public void setComponentTreeViewer(final IPropertyListener propertyListener) {
    this.propertyListener = propertyListener;
  }

  /**
   * Sets the different necessary attributes.
   *
   * @param propertyListener
   *          the property listener
   */
  public void setEventTreeViewer(final IPropertyListener propertyListener) {
    this.propertyListener = propertyListener;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {
    // TODO Auto-generated method stub

  }

  /**
   * Adds a combo box for the core selection.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  @Override
  public void addComboBoxSelector(final Composite parent, final FormToolkit toolkit) {
    final Composite combocps = toolkit.createComposite(parent);
    combocps.setLayout(new FillLayout());
    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setVisibleItemCount(20);
    combo.setToolTipText(Messages.getString("Papify.coreSelectionTooltip"));
    comboDataInit(combo);
    combo.addFocusListener(new FocusListener() {

      @Override
      public void focusGained(final FocusEvent e) {
        comboDataInit((Combo) e.getSource());

      }

      @Override
      public void focusLost(final FocusEvent e) {
      }

    });

    combo.addSelectionListener(this);
  }

  /**
   * Combo data init.
   *
   * @param combo
   *          the combo
   */
  private void comboDataInit(final Combo combo) {

    combo.removeAll();
    final Set<String> result = new LinkedHashSet<>();
    String finalName;
    if (this.scenario.isPISDFScenario()) {
      final PiGraph graph = PiParser.getPiGraph(this.scenario.getAlgorithmURL());
      for (final AbstractActor vertex : graph.getAllActors()) {
        if (!(vertex instanceof PiGraph) && !(vertex instanceof DataInputInterface)
            && !(vertex instanceof DataOutputInterface)) {
          finalName = vertex.getVertexPath().substring(vertex.getVertexPath().indexOf('/') + 1);
          result.add(finalName);
        }
      }
    } else if (this.scenario.isIBSDFScenario()) {
      try {
        final SDFGraph graph = ScenarioParser.getSDFGraph(this.scenario.getAlgorithmURL());
        for (final SDFAbstractVertex vertex : graph.vertexSet()) {
          result.add(vertex.getName());
        }
      } catch (final InvalidModelException e) {
        e.printStackTrace();
      }
    }

    for (final String id : result) {
      combo.add(id);
    }
  }

  /**
   * 
   */
  public void updateEvents() {

    Set<PapifyConfigActor> papiConfigs = this.scenario.getPapifyConfigManager().getPapifyConfigGroupsActors();

    for (PapifyConfigActor papiConfig : papiConfigs) {
      String actorPath = papiConfig.getActorPath();
      Map<String, Set<PapiEvent>> actorEventMap = papiConfig.getPAPIEvents();

      for (PapifyEventListTreeElement treeElement : this.elementList) {
        if (treeElement.actorPath.equals(actorPath)) {
          for (String comp : actorEventMap.keySet()) {
            for (PapiEvent oneEvent : actorEventMap.get(comp)) {
              treeElement.PAPIStatuses.put(oneEvent.getName(), PAPIEventStatus.YES);
              updateView(oneEvent.getName());
            }
          }
          break;
        }
      }
    }
  }

  /**
   * 
   */
  public boolean hasChildren(Object actorInstance) {
    boolean hasChildren = false;
    if (this.scenario.isIBSDFScenario()) {
      if (actorInstance instanceof SDFGraph) {
        final SDFGraph graph = (SDFGraph) actorInstance;
        hasChildren = !graph.vertexSet().isEmpty();
      } else if (actorInstance instanceof HierarchicalSDFVertex) {
        final SDFAbstractVertex sdfVertex = ((HierarchicalSDFVertex) actorInstance).getStoredVertex();
        if (sdfVertex instanceof SDFVertex) {
          final SDFVertex vertex = (SDFVertex) sdfVertex;
          hasChildren = vertex.getRefinement() != null;
        }
      }
    } else if (this.scenario.isPISDFScenario()) {
      if (actorInstance instanceof PiGraph) {
        final PiGraph graph = (PiGraph) actorInstance;
        hasChildren = !graph.getActors().isEmpty();
      } else if (actorInstance instanceof Actor) {
        final Actor actor = (Actor) actorInstance;
        hasChildren = actor.getRefinement() != null;
      }
    }
    return hasChildren;
  }

  /**
   * 
   */
  public Set<String> getChildren(final Object parentElement) {
    Set<String> table = new LinkedHashSet<>();
    if (this.scenario.isIBSDFScenario()) {
      if (parentElement instanceof SDFGraph) {
        final SDFGraph graph = (SDFGraph) parentElement;

        // Some types of vertices are ignored in the constraints view
        table.addAll(filterIBSDFChildren(graph.vertexSet()));
      } else if (parentElement instanceof HierarchicalSDFVertex) {
        final HierarchicalSDFVertex vertex = (HierarchicalSDFVertex) parentElement;
        final IRefinement refinement = vertex.getStoredVertex().getRefinement();

        if ((refinement != null) && (refinement instanceof SDFGraph)) {
          final SDFGraph graph = (SDFGraph) refinement;
          table.addAll(filterIBSDFChildren(graph.vertexSet()));
        }
      }
    } else if (this.scenario.isPISDFScenario()) {
      if (parentElement instanceof PiGraph) {
        final PiGraph graph = (PiGraph) parentElement;
        // Some types of vertices are ignored in the constraints view
        table.addAll(filterPISDFChildren(graph.getAllActors()));
      } else if (parentElement instanceof Actor) {
        final Actor actor = (Actor) parentElement;
        if (actor.isHierarchical()) {
          final PiGraph subGraph = actor.getSubGraph();
          table.addAll(filterPISDFChildren(subGraph.getActors()));
        }
      }
    }

    return table;
  }

  /**
   * Filters the children to display in the tree.
   *
   * @param vertices
   *          the vertices
   * @return the sets the
   */
  public Set<String> filterPISDFChildren(final EList<AbstractActor> vertices) {
    final Set<String> actorPaths = new LinkedHashSet<>();
    for (final AbstractActor actor : vertices) {
      // TODO: Filter if needed
      if (!(actor instanceof DataInputInterface) && !(actor instanceof DataOutputInterface)
          && !(actor instanceof BroadcastActor) && !(actor instanceof JoinActor) && !(actor instanceof ForkActor)
          && !(actor instanceof RoundBufferActor) && !(actor instanceof DelayActor)) {
        final String elementActor = actor.getVertexPath();
        actorPaths.add(elementActor);

      }
    }
    return actorPaths;
  }

  /**
   * Filter IBSDF children.
   *
   * @param children
   *          the children
   * @return the sets the
   */
  public Set<String> filterIBSDFChildren(final Set<SDFAbstractVertex> children) {

    final Set<String> actorPaths = new LinkedHashSet<>();

    for (final SDFAbstractVertex v : children) {
      if (v.getKind().equalsIgnoreCase("vertex")) {
        actorPaths.add(v.getName());
      }
    }

    return actorPaths;
  }

  /**
   *
   **/

  public void setPropDirty() {
    this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
  }

  public void setTableViewer(final CheckboxTableViewer tableviewer,
      final PreesmAlgorithmTreeContentProvider contentProvider2, final IPropertyListener listener) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setTreeViewer(final CheckboxTreeViewer treeViewer,
      final PreesmAlgorithmTreeContentProvider contentProvider, final IPropertyListener propertyListener) {
    // TODO Auto-generated method stub

  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void checkStateChanged(CheckStateChangedEvent event) {
    // TODO Auto-generated method stub

  }

  @Override
  public void paintControl(PaintEvent e) {
    // TODO Auto-generated method stub

  }
}
