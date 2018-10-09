/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2011)
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
package org.ietr.preesm.ui.scenario.editor.papify;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapiEventInfo;
import org.ietr.preesm.core.scenario.papi.PapiEventModifier;
import org.ietr.preesm.core.scenario.papi.PapiEventSet;
import org.ietr.preesm.core.scenario.papi.PapifyConfigActor;
import org.ietr.preesm.ui.scenario.editor.papify.PapifyListTreeElement.PAPIStatus;

// TODO: Auto-generated Javadoc
/**
 * Provides the elements contained in the papify editor.
 *
 * @author dmadronal
 */
public class PapifyActorListContentProvider2DMatrix implements ITreeContentProvider {

  /** Currently edited scenario. */
  private PreesmScenario                                scenario           = null;
  private Set<PapifyListTreeElement>                    actorConfig;
  PapifyCheckStateListener                              checkStateListener = null;
  private Set<PapifyActorListContentProvider2DMatrixES> editingSupports    = new LinkedHashSet<>();

  public PapifyActorListContentProvider2DMatrix(PreesmScenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Gets the Papi Component list.
   *
   * @return the Papi Component list
   */
  public Set<PapifyListTreeElement> getComponents() {
    return this.actorConfig;
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
  public void addEstatusSupport(PapifyActorListContentProvider2DMatrixES editingSupport) {
    if (editingSupport != null) {
      this.editingSupports.add(editingSupport);
    }
  }

  /**
   * 
   */
  public void removeEventfromActor(String actorName, String eventName) {
    // The timing event
    PapiEvent timingEvent = new PapiEvent();
    ArrayList<PapiEventModifier> modifTimingList = new ArrayList<>();
    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    timingEvent.setModifiers(modifTimingList);
    boolean timing = false;

    if (!actorName.equals("") && !eventName.equals("")) {

      if (eventName.equals(timingEvent.getName())) {
        timing = true;
      }
      PapifyConfigActor papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(actorName);
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
              }
            }
          }
        }
      } else {
        found = true;
        event = timingEvent;
      }

      boolean hierarchy = false;
      int hierarchyLevel = 0;
      Map<String, PAPIStatus> statuses = new LinkedHashMap<>();
      for (PapifyListTreeElement treeElement : this.actorConfig) {
        if (treeElement.label.equals(eventName)) {
          statuses = treeElement.PAPIStatuses;
        }
      }
      if (found) {
        Map<String, Integer> actorNamesAndLevels = this.checkStateListener.getAllActorNamesAndLevels();
        for (String actorNameSearch : actorNamesAndLevels.keySet()) {
          if (hierarchy) {
            if (hierarchyLevel >= actorNamesAndLevels.get(actorNameSearch)) {
              hierarchy = false;
            } else {
              papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(actorNameSearch);
              statuses.put(actorNameSearch, PAPIStatus.NO);
              if (!timing) {
                papiConfig.removePAPIEvent(compName, event);
              } else {
                papiConfig.removePAPIEvent("Timing", event);
              }
            }
          }
          if (actorName.equals(actorNameSearch)) {
            statuses.put(actorNameSearch, PAPIStatus.NO);
            if (!timing) {
              papiConfig.removePAPIEvent(compName, event);
            } else {
              papiConfig.removePAPIEvent("Timing", event);
            }
            hierarchy = true;
            hierarchyLevel = actorNamesAndLevels.get(actorName);
          }
        }
      }
    }
  }

  /**
   * 
   */
  public void addEventtoActor(String actorName, String eventName) {
    // The timing event
    PapiEvent timingEvent = new PapiEvent();
    ArrayList<PapiEventModifier> modifTimingList = new ArrayList<>();
    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    timingEvent.setModifiers(modifTimingList);
    boolean timing = false;

    if (!actorName.equals("") && !eventName.equals("")) {

      if (eventName.equals(timingEvent.getName())) {
        timing = true;
      }
      PapifyConfigActor papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(actorName);
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
              }
            }
          }
        }
      } else {
        found = true;
        event = timingEvent;
      }

      boolean hierarchy = false;
      int hierarchyLevel = 0;
      Map<String, PAPIStatus> statuses = new LinkedHashMap<>();
      for (PapifyListTreeElement treeElement : this.actorConfig) {
        if (treeElement.label.equals(eventName)) {
          statuses = treeElement.PAPIStatuses;
        }
      }
      if (found) {
        Map<String, Integer> actorNamesAndLevels = this.checkStateListener.getAllActorNamesAndLevels();
        for (String actorNameSearch : actorNamesAndLevels.keySet()) {
          if (hierarchy) {
            if (hierarchyLevel >= actorNamesAndLevels.get(actorNameSearch)) {
              hierarchy = false;
            } else {
              papiConfig = this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupActor(actorNameSearch);
              statuses.put(actorNameSearch, PAPIStatus.YES);
              if (!timing) {
                papiConfig.addPAPIEvent(compName, event);
              } else {
                papiConfig.addPAPIEvent("Timing", event);
              }
            }
          }
          if (actorName.equals(actorNameSearch)) {
            statuses.put(actorNameSearch, PAPIStatus.YES);
            if (!timing) {
              papiConfig.addPAPIEvent(compName, event);
            } else {
              papiConfig.addPAPIEvent("Timing", event);
            }
            hierarchy = true;
            hierarchyLevel = actorNamesAndLevels.get(actorName);
          }
        }
      }
    }
  }

  /**
   * 
   */
  public void updateView() {

    for (PapifyActorListContentProvider2DMatrixES viewer : this.editingSupports) {
      for (PapifyListTreeElement treeElement : this.actorConfig) {
        viewer.getViewer().update(treeElement, null);
      }
    }
  }

  /**
   * 
   */
  public void setInput() {

    Set<PapifyConfigActor> papiConfigs = this.scenario.getPapifyConfigManager().getPapifyConfigGroupsActors();

    for (PapifyConfigActor papiConfig : papiConfigs) {
      String actorId = papiConfig.getActorId();
      for (String compName : papiConfig.getPAPIEvents().keySet()) {
        for (PapiEvent event : papiConfig.getPAPIEvents().get(compName)) {
          for (PapifyListTreeElement treeElement : this.actorConfig) {
            if (treeElement.label.equals(event.getName())) {
              final Map<String, PAPIStatus> statuses = treeElement.PAPIStatuses;
              statuses.put(actorId, PAPIStatus.YES);
            }
          }
        }
      }
    }
    for (PapifyActorListContentProvider2DMatrixES viewer : this.editingSupports) {
      for (PapifyListTreeElement treeElement : this.actorConfig) {
        viewer.getViewer().update(treeElement, null);
      }
    }
  }

  /**
   * 
   */
  public void selectionUpdated() {

    this.checkStateListener.setPropDirty();

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    Object[] elementTable = null;

    // The timing event
    PapiEvent timingEvent = new PapiEvent();
    ArrayList<PapiEventModifier> modifTimingList = new ArrayList<>();
    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    timingEvent.setModifiers(modifTimingList);

    if (inputElement instanceof PapiEventInfo) {
      final PapiEventInfo inputPapiEventInfo = (PapiEventInfo) inputElement;
      actorConfig = new LinkedHashSet<>();
      final PapifyListTreeElement elementTiming = new PapifyListTreeElement(timingEvent.getName());
      actorConfig.add(elementTiming);
      for (final PapiComponent compAux : inputPapiEventInfo.getComponents()) {
        if (!compAux.getEventSets().isEmpty()) {
          for (final PapiEventSet eventSet : compAux.getEventSets()) {
            for (final PapiEvent event : eventSet.getEvents()) {
              if (event.getModifiers().isEmpty()) {
                final PapifyListTreeElement element = new PapifyListTreeElement(event.getName());
                actorConfig.add(element);
              }
            }
          }
        }
      }
      elementTable = this.actorConfig.toArray();
    }

    return elementTable;
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

  @Override
  public Object[] getChildren(Object parentElement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getParent(Object element) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasChildren(Object element) {
    // TODO Auto-generated method stub
    return false;
  }

}
