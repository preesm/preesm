/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2012)
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
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
import org.preesm.model.scenario.PapiComponent;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapiEventInfo;
import org.preesm.model.scenario.PapiEventSet;
import org.preesm.model.scenario.PapifyConfig;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.ui.scenario.editor.ISDFCheckStateListener;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.PreesmAlgorithmTreeContentProvider;
import org.preesm.ui.scenario.editor.papify.PapifyEventListTreeElement.PAPIEventStatus;

/**
 * Listener of the check state of both the PAPI component and the PAPI event tables but also of the selection
 * modification of the current core definition. It updates the check Papify configuration depending on the PapifyConf
 * groups in the scenario
 *
 * @author dmadronal
 */
public class PapifyCheckStateListener implements ISDFCheckStateListener {

  private static final String TIMING = "Timing";

  /** Currently edited scenario. */
  private Scenario scenario = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  private Set<PapifyEventListContentProvider2DMatrixES> editingSupports = new LinkedHashSet<>();

  private Set<PapifyEventListTreeElement> elementList;

  // The timing event
  private final PapiEvent timingEvent;

  /**
   * Instantiates a new constraints check state listener.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyCheckStateListener(final Scenario scenario) {
    super();
    this.scenario = scenario;
    this.elementList = new LinkedHashSet<>();
    timingEvent = ScenarioUserFactory.createTimingEvent();
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
  public void removeEventfromActor(AbstractActor actorInstance, String eventName) {

    boolean timing = false;

    if (!eventName.equals("")) {

      if (eventName.equals(timingEvent.getName())) {
        timing = true;
      }
      final PapifyConfig papifyConfig = this.scenario.getPapifyConfig();
      final PapiEventInfo papiData = papifyConfig.getPapiData();
      String compName = "";
      PapiEvent event = null;
      boolean found = false;
      if (!timing) {
        for (final PapiComponent comp : papiData.getComponents().values()) {
          for (final PapiEventSet eventSet : comp.getEventSets()) {
            for (final PapiEvent eventAux : eventSet.getEvents()) {
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
        event = this.timingEvent;
      }

      Map<String, PAPIEventStatus> statuses = new LinkedHashMap<>();
      for (final PapifyEventListTreeElement treeElement : this.elementList) {
        if (treeElement.actorPath.equals(actorInstance)) {
          statuses = treeElement.papiStatuses;
          break;
        }
      }

      // Replacement to test
      // final Map<String, PAPIEventStatus> statuses = this.elementList.stream()
      // .filter(treeElement -> treeElement.actorPath.equals(actorInstance)).findFirst().orElseThrow().PAPIStatuses;

      if (found) {

        if (!timing) {
          papifyConfig.removeActorConfigEvent(actorInstance, compName, event);
        } else {
          papifyConfig.removeActorConfigEvent(actorInstance, TIMING, event);
        }
        statuses.put(eventName, PAPIEventStatus.NO);
        if (hasChildren(actorInstance)) {
          final List<AbstractActor> actorPaths = getChildren(actorInstance);
          for (final AbstractActor oneActorPath : actorPaths) {
            for (final PapifyEventListTreeElement treeElement : this.elementList) {
              if (treeElement.actorPath.equals(oneActorPath)) {
                treeElement.papiStatuses.put(eventName, PAPIEventStatus.NO);
                if (!timing) {
                  papifyConfig.removeActorConfigEvent(oneActorPath, compName, event);
                } else {
                  papifyConfig.removeActorConfigEvent(oneActorPath, TIMING, event);
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
  public void addEventtoActor(AbstractActor actorInstance, String eventName) {

    boolean timing = false;

    if (!eventName.equals("")) {

      if (eventName.equals(timingEvent.getName())) {
        timing = true;
      }
      final PapifyConfig papifyConfig = this.scenario.getPapifyConfig();
      final PapiEventInfo papiData = papifyConfig.getPapiData();
      String compName = "";
      PapiEvent event = null;
      boolean found = false;
      if (!timing) {
        final EMap<String, PapiComponent> components = papiData.getComponents();
        for (final PapiComponent comp : components.values()) {
          for (final PapiEventSet eventSet : comp.getEventSets()) {
            for (final PapiEvent eventAux : eventSet.getEvents()) {
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
        event = this.timingEvent;
      }

      Map<String, PAPIEventStatus> statuses = new LinkedHashMap<>();
      for (final PapifyEventListTreeElement treeElement : this.elementList) {
        if (treeElement.actorPath.equals(actorInstance)) {
          statuses = treeElement.papiStatuses;
          break;
        }
      }
      if (found) {
        if (!timing) {
          papifyConfig.addActorConfigEvent(actorInstance, compName, event);
        } else {
          papifyConfig.addActorConfigEvent(actorInstance, TIMING, event);
        }
        statuses.put(eventName, PAPIEventStatus.YES);
        if (hasChildren(actorInstance)) {
          final List<AbstractActor> actorPaths = getChildren(actorInstance);
          for (final AbstractActor oneActorPath : actorPaths) {
            for (final PapifyEventListTreeElement treeElement : this.elementList) {
              if (treeElement.actorPath.equals(oneActorPath)) {
                treeElement.papiStatuses.put(eventName, PAPIEventStatus.YES);
                if (!timing) {
                  papifyConfig.addActorConfigEvent(oneActorPath, compName, event);
                } else {
                  papifyConfig.addActorConfigEvent(oneActorPath, TIMING, event);
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

    for (final PapifyEventListContentProvider2DMatrixES viewer : this.editingSupports) {
      if (viewer.eventName.equals(event)) {
        for (final PapifyEventListTreeElement treeElement : this.elementList) {
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
  public void setPropertyListener(final IPropertyListener propertyListener) {
    this.propertyListener = propertyListener;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {
    // nothing

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
        // nothing
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
    final PiGraph graph = scenario.getAlgorithm();
    for (final AbstractActor vertex : graph.getAllActors()) {
      if (!(vertex instanceof PiGraph) && !(vertex instanceof DataInputInterface)
          && !(vertex instanceof DataOutputInterface)) {
        finalName = vertex.getVertexPath().substring(vertex.getVertexPath().indexOf('/') + 1);
        result.add(finalName);
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

    final EMap<AbstractActor, EMap<String, EList<PapiEvent>>> papifyConfigActors = this.scenario.getPapifyConfig()
        .getPapifyConfigGroupsActors();

    for (final Entry<AbstractActor, EMap<String, EList<PapiEvent>>> papiConfig : papifyConfigActors) {
      final AbstractActor actorPath = papiConfig.getKey();
      final EMap<String, EList<PapiEvent>> actorEventMap = papiConfig.getValue();

      for (final PapifyEventListTreeElement treeElement : this.elementList) {
        if (treeElement.actorPath.equals(actorPath)) {

          for (final Entry<String, EList<PapiEvent>> entry : actorEventMap) {
            final String comp = entry.getKey();
            for (final PapiEvent oneEvent : actorEventMap.get(comp)) {
              treeElement.papiStatuses.put(oneEvent.getName(), PAPIEventStatus.YES);
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
    if (actorInstance instanceof final PiGraph graph) {
      hasChildren = !graph.getActors().isEmpty();
    } else if (actorInstance instanceof final Actor actor) {
      hasChildren = actor.getRefinement() != null;
    }
    return hasChildren;
  }

  /**
   *
   */
  public List<AbstractActor> getChildren(final AbstractActor parentElement) {
    final List<AbstractActor> table = new ArrayList<>();
    if (parentElement instanceof final PiGraph graph) {
      // Some types of vertices are ignored in the constraints view
      table.addAll(filterPISDFChildren(graph.getAllActors()));
    } else if (parentElement instanceof final Actor actor && actor.isHierarchical()) {
      final PiGraph subGraph = actor.getSubGraph();
      table.addAll(filterPISDFChildren(subGraph.getActors()));
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
  public List<AbstractActor> filterPISDFChildren(final List<AbstractActor> vertices) {
    final List<AbstractActor> actorPaths = new ArrayList<>();
    for (final AbstractActor actor : vertices) {
      if (!(actor instanceof DataInputInterface) && !(actor instanceof DataOutputInterface)
          && !(actor instanceof BroadcastActor) && !(actor instanceof JoinActor) && !(actor instanceof ForkActor)
          && !(actor instanceof RoundBufferActor) && !(actor instanceof DelayActor)) {
        actorPaths.add(actor);

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
    // nothing
  }

  @Override
  public void setTreeViewer(final CheckboxTreeViewer treeViewer,
      final PreesmAlgorithmTreeContentProvider contentProvider, final IPropertyListener propertyListener) {
    // nothing
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    // nothing
  }

  @Override
  public void checkStateChanged(CheckStateChangedEvent event) {
    // nothing
  }

  @Override
  public void paintControl(PaintEvent e) {
    // nothing
  }
}
