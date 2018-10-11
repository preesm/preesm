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
package org.ietr.preesm.ui.scenario.editor.papify;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapifyConfig;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;
import org.ietr.preesm.ui.scenario.editor.ISDFCheckStateListener;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.ietr.preesm.ui.scenario.editor.PreesmAlgorithmTreeContentProvider;

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

  /** Current operator. */
  private String currentOpId = null;

  /** Current composite (necessary to diplay busy status). */
  private Composite container = null;

  /** Tables used to set the checked status. */
  private CheckboxTableViewer componentTableViewer = null;
  private CheckboxTableViewer eventTableViewer     = null;

  /** Content provider used to get the elements currently displayed. */
  private PapifyComponentListContentProvider componentContentProvider = null;
  private PapifyEventListContentProvider     eventContentProvider     = null;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  /**
   * Instantiates a new constraints check state listener.
   *
   * @param scenario
   *          the scenario
   */
  public PapifyCheckStateListener(final Composite container, final PreesmScenario scenario) {
    super();
    this.container = container;
    this.scenario = scenario;
  }

  /**
   * Sets the different necessary attributes.
   *
   * @param tableViewer
   *          the tree viewer
   * @param contentProvider
   *          the content provider
   * @param propertyListener
   *          the property listener
   */
  public void setComponentTableViewer(final CheckboxTableViewer tableViewer,
      final PapifyComponentListContentProvider contentProvider, final IPropertyListener propertyListener) {
    this.componentTableViewer = tableViewer;
    this.componentContentProvider = contentProvider;
    this.propertyListener = propertyListener;
  }

  /**
   * Sets the different necessary attributes.
   *
   * @param tableViewer
   *          the tree viewer
   * @param contentProvider
   *          the content provider
   * @param propertyListener
   *          the property listener
   */
  public void setEventTableViewer(final CheckboxTableViewer tableViewer,
      final PapifyEventListContentProvider contentProvider, final IPropertyListener propertyListener) {
    this.eventTableViewer = tableViewer;
    this.eventContentProvider = contentProvider;
    this.propertyListener = propertyListener;
  }

  /**
   * Fired when an element has been checked or unchecked.
   *
   * @param event
   *          the event
   */
  @Override
  public void checkStateChanged(final CheckStateChangedEvent event) {
    final Object element = event.getElement();
    final boolean isChecked = event.getChecked();

    BusyIndicator.showWhile(this.container.getDisplay(), () -> {
      if (element instanceof PapiComponent) {
        final PapiComponent comp1 = (PapiComponent) element;
        fireOnCheck(comp1, isChecked);
        updateComponentCheck();
      } else if (element instanceof PapiEvent) {
        final PapiEvent event1 = (PapiEvent) element;
        fireOnCheck(event1, isChecked);
        updateEventCheck();

      }
    });
    this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
  }

  /**
   * Adds or remove a component depending on the isChecked status.
   *
   * @param comp
   *          the new PAPI component
   * @param isChecked
   *          the is checked
   */
  private void fireOnCheck(final PapiComponent comp, final boolean isChecked) {
    if (this.currentOpId != null) {
      if (isChecked) {
        this.scenario.getPapifyConfigManager().addComponent(this.currentOpId, comp);
      } else {
        this.scenario.getPapifyConfigManager().removeComponent(this.currentOpId, comp);
      }
    }
  }

  /**
   * Fire on check.
   *
   * @param event
   *          the Papi event
   * @param isChecked
   *          the is checked
   */
  private void fireOnCheck(final PapiEvent event, final boolean isChecked) {
    if (this.currentOpId != null) {
      if (isChecked) {
        this.scenario.getPapifyConfigManager().addEvent(this.currentOpId, event);
      } else {
        this.scenario.getPapifyConfigManager().removeEvent(this.currentOpId, event);
      }
    }
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
   * Core combo box listener that selects the current core.
   *
   * @param e
   *          the e
   */
  @Override
  public void widgetSelected(final SelectionEvent e) {
    if (e.getSource() instanceof Combo) {
      final Combo combo = ((Combo) e.getSource());
      final String item = combo.getItem(combo.getSelectionIndex());

      this.currentOpId = item.replace('/', '_');
      updateComponentCheck();
      updateEventCheck();
    }

  }

  /**
   * Update the check status of both the tables.
   */
  public void updateComponentCheck() {
    if (this.scenario != null) {
      final List<PapiComponent> papiComponents = this.componentContentProvider.getComponents();
      final Set<PapiComponent> pcgSet = new LinkedHashSet<>();

      if ((this.currentOpId != null) && (papiComponents != null)) {
        final PapifyConfig pcgSetRead = this.scenario.getPapifyConfigManager()
            .getCorePapifyConfigGroups(this.currentOpId);
        if ((pcgSetRead != null) && (pcgSetRead.getPAPIComponent() != null)) {
          pcgSet.add(pcgSetRead.getPAPIComponent());
        }
        this.componentTableViewer.setCheckedElements(pcgSet.toArray());
      }
    }
  }

  /**
   * Update the check status of event table.
   */
  public void updateEventCheck() {
    if (this.scenario != null) {
      final List<PapiEvent> papiEvents = this.eventContentProvider.getEvents();
      Set<PapiEvent> pegSetfinal = new LinkedHashSet<>();

      if ((this.currentOpId != null) && (papiEvents != null)) {
        final PapifyConfig pegSetRead = this.scenario.getPapifyConfigManager()
            .getCorePapifyConfigGroups(this.currentOpId);
        if ((pegSetRead != null) && !pegSetRead.getPAPIEvents().isEmpty()) {
          pegSetfinal = pegSetRead.getPAPIEvents();
        }

        this.eventTableViewer.setCheckedElements(pegSetfinal.toArray());
      }
    }
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

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
   */
  @Override
  public void paintControl(final PaintEvent e) {
    updateComponentCheck();
    updateEventCheck();

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
}
