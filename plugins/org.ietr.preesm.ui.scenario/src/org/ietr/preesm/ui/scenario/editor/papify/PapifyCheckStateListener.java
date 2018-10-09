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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
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
    this.scenario = scenario;
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
  public Set<String> getChildrenNames(PiGraph graph) {

    final Set<String> result = new LinkedHashSet<>();
    for (final AbstractActor vertex : graph.getAllActors()) {
      if (vertex instanceof PiGraph) {
        result.add(vertex.getName());
        result.addAll(getChildrenNames((PiGraph) vertex));
      } else if (!(vertex instanceof DataInputInterface) && !(vertex instanceof DataOutputInterface)
          && !(vertex instanceof BroadcastActor) && !(vertex instanceof JoinActor) && !(vertex instanceof ForkActor)
          && !(vertex instanceof RoundBufferActor) && !(vertex instanceof DelayActor)) {
        result.add(vertex.getName());
      }
    }
    return result;
  }

  /**
   *
   */
  public Map<String, Integer> getChildrenNamesAndLevels(PiGraph graph, int count) {

    int countNew = count + 1;
    final Map<String, Integer> result = new LinkedHashMap<>();
    for (final AbstractActor vertex : graph.getAllActors()) {
      if (vertex instanceof PiGraph) {
        if (!result.containsKey(vertex.getName())) {
          result.put(vertex.getName(), countNew);
        }
        Map<String, Integer> resultAux = getChildrenNamesAndLevels((PiGraph) vertex, countNew);
        for (String actorName : resultAux.keySet()) {
          if (!result.containsKey(actorName)) {
            result.put(actorName, resultAux.get(actorName));
          }
        }
      } else if (!(vertex instanceof DataInputInterface) && !(vertex instanceof DataOutputInterface)
          && !(vertex instanceof BroadcastActor) && !(vertex instanceof JoinActor) && !(vertex instanceof ForkActor)
          && !(vertex instanceof RoundBufferActor) && !(vertex instanceof DelayActor)) {
        if (!result.containsKey(vertex.getName())) {
          result.put(vertex.getName(), countNew);
        }
      }
    }
    return result;
  }

  /**
   *
   */
  public Map<String, Integer> getAllActorNamesAndLevels() {

    final Map<String, Integer> result = new LinkedHashMap<>();
    int count = 1;
    if (this.scenario.isPISDFScenario()) {
      final PiGraph graph = PiParser.getPiGraph(this.scenario.getAlgorithmURL());
      if (graph != null) {
        if (!result.containsKey(graph.getName())) {
          result.put(graph.getName(), count);
        }
        if (graph instanceof PiGraph) {
          Map<String, Integer> resultAux = getChildrenNamesAndLevels(graph, count);
          for (String actorName : resultAux.keySet()) {
            if (!result.containsKey(actorName)) {
              result.put(actorName, resultAux.get(actorName));
            }
          }
        }
      }
    } else if (this.scenario.isIBSDFScenario()) {
      try {
        final SDFGraph graph = ScenarioParser.getSDFGraph(this.scenario.getAlgorithmURL());
        for (final SDFAbstractVertex vertex : graph.vertexSet()) {
          if (!result.containsKey(vertex.getName())) {
            result.put(vertex.getName(), count);
          }
        }
      } catch (final InvalidModelException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   *
   */
  public Set<String> getAllActorNames() {

    final Set<String> result = new LinkedHashSet<>();
    if (this.scenario.isPISDFScenario()) {
      final PiGraph graph = PiParser.getPiGraph(this.scenario.getAlgorithmURL());
      if (graph != null) {
        result.add(graph.getName());
        if (graph instanceof PiGraph) {
          result.addAll(getChildrenNames(graph));
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

    return result;
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
