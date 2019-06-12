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
package org.preesm.ui.scenario.editor.papify;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.scenario.papi.PapiComponent;
import org.preesm.model.scenario.papi.PapiEventInfo;
import org.preesm.model.scenario.papi.PapiEventSet;
import org.preesm.model.slam.component.Component;
import org.preesm.ui.scenario.editor.papify.PapifyListTreeElement.PAPIStatus;

/**
 * Provides the elements contained in the papify editor.
 *
 * @author dmadronal
 */
public class PapifyComponentListContentProvider2DMatrix implements ITreeContentProvider {

  /** Currently edited scenario. */
  private PreesmScenario                                    scenario           = null;
  private Set<PapifyListTreeElement>                        componentConfig;
  PapifyCheckStateListener                                  checkStateListener = null;
  private Set<PapifyComponentListContentProvider2DMatrixES> editingSupports    = new LinkedHashSet<>();

  public PapifyComponentListContentProvider2DMatrix(PreesmScenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Gets the Papi Component list.
   *
   * @return the Papi Component list
   */
  public Set<PapifyListTreeElement> getComponents() {
    return this.componentConfig;
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
  public void addEstatusSupport(PapifyComponentListContentProvider2DMatrixES edittingSupport) {
    if (edittingSupport != null) {
      this.editingSupports.add(edittingSupport);
    }
  }

  /**
   *
   */
  public void removePEfromComp(final Component peType, final String compName) {
    if (peType != null && !compName.equals("")) {

      PapiEventInfo papiData = this.scenario.getPapifyConfigManager().getPapifyData();
      PapiComponent pc = papiData.getComponent(compName);
      this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupPE(peType).remove(pc);

      for (PapifyListTreeElement treeElement : this.componentConfig) {
        if (treeElement.label.equals(compName)) {
          final Map<Component, PAPIStatus> statuses = treeElement.PAPIStatuses;
          statuses.put(peType, PAPIStatus.NO);
        }
      }
    }
  }

  /**
   *
   */
  public void addPEtoComp(final Component peType, final String compName) {
    if (peType != null && !compName.equals("")) {
      PapiEventInfo papiData = this.scenario.getPapifyConfigManager().getPapifyData();
      PapiComponent pc = papiData.getComponent(compName);
      this.scenario.getPapifyConfigManager().getCorePapifyConfigGroupPE(peType).add(pc);

      for (PapifyListTreeElement treeElement : this.componentConfig) {
        if (treeElement.label.equals(compName)) {
          final Map<Component, PAPIStatus> statuses = treeElement.PAPIStatuses;
          statuses.put(peType, PAPIStatus.YES);
        }
      }
    }
  }

  /**
   *
   */
  public void cleanPE(final Component peType) {

    List<PapiComponent> corePapifyConfigGroupPE = this.scenario.getPapifyConfigManager()
        .getCorePapifyConfigGroupPE(peType);
    if (peType != null && corePapifyConfigGroupPE != null) {
      PapiEventInfo papiData = this.scenario.getPapifyConfigManager().getPapifyData();
      corePapifyConfigGroupPE.removeAll(papiData.getComponents().values());

      for (PapifyListTreeElement treeElement : this.componentConfig) {
        final Map<Component, PAPIStatus> statuses = treeElement.PAPIStatuses;
        statuses.put(peType, PAPIStatus.NO);
      }
    }
  }

  /**
   *
   */
  public void updateView() {

    for (PapifyComponentListContentProvider2DMatrixES viewer : this.editingSupports) {
      for (PapifyListTreeElement treeElement : this.componentConfig) {
        viewer.getViewer().update(treeElement, null);
      }
    }
  }

  /**
   *
   */
  public void setInput() {
    final Map<Component,
        List<PapiComponent>> papifyConfigGroupsPEs = this.scenario.getPapifyConfigManager().getPapifyConfigGroupsPEs();

    final Set<Entry<Component, List<PapiComponent>>> entrySet = papifyConfigGroupsPEs.entrySet();
    for (final Entry<Component, List<PapiComponent>> papiConfig : entrySet) {
      final Component peType = papiConfig.getKey();
      for (PapiComponent compName : papiConfig.getValue()) {
        String comp = compName.getId();
        for (PapifyListTreeElement treeElement : this.componentConfig) {
          if (treeElement.label.equals(comp)) {
            final Map<Component, PAPIStatus> statuses = treeElement.PAPIStatuses;
            statuses.put(peType, PAPIStatus.YES);
          }
        }
      }
    }
    for (PapifyComponentListContentProvider2DMatrixES viewer : this.editingSupports) {
      for (PapifyListTreeElement treeElement : this.componentConfig) {
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

    if (inputElement instanceof PapiEventInfo) {
      final PapiEventInfo inputPapiEventInfo = (PapiEventInfo) inputElement;
      componentConfig = new LinkedHashSet<>();
      boolean componentAdded = false;

      for (final PapiComponent compAux : inputPapiEventInfo.getComponents().values()) {
        if (!compAux.getEventSets().isEmpty()) {
          for (final PapiEventSet eventSet : compAux.getEventSets()) {
            if (!eventSet.getEvents().isEmpty()) {
              componentAdded = true;
            }
          }
        }
        if (componentAdded) {
          componentAdded = false;
          final PapifyListTreeElement element = new PapifyListTreeElement(compAux.getId());
          componentConfig.add(element);
        }
      }
      elementTable = this.componentConfig.toArray();
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
