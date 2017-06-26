/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2013)
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
package org.ietr.preesm.ui.scenario.editor.timings;

import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

// TODO: Auto-generated Javadoc
/**
 * Displays the labels for tasks timings. These labels are the time of each task
 *
 * @author mpelcat
 */
public class TimingsTableLabelProvider implements ITableLabelProvider, SelectionListener {

  /** The scenario. */
  private PreesmScenario scenario = null;

  /** The current op def id. */
  private String currentOpDefId = null;

  /** The table viewer. */
  private TableViewer tableViewer = null;

  /** The image ok. */
  private final Image imageOk;

  /** The image error. */
  private final Image imageError;

  /** Constraints page used as a property listener to change the dirty state. */
  private IPropertyListener propertyListener = null;

  /**
   * Instantiates a new timings table label provider.
   *
   * @param scenario
   *          the scenario
   * @param tableViewer
   *          the table viewer
   * @param propertyListener
   *          the property listener
   */
  public TimingsTableLabelProvider(final PreesmScenario scenario, final TableViewer tableViewer, final IPropertyListener propertyListener) {
    super();
    this.scenario = scenario;
    this.tableViewer = tableViewer;
    this.propertyListener = propertyListener;

    final Bundle bundle = FrameworkUtil.getBundle(TimingsTableLabelProvider.class);

    URL url = FileLocator.find(bundle, new Path("icons/error.png"), null);
    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
    this.imageError = imageDcr.createImage();

    url = FileLocator.find(bundle, new Path("icons/ok.png"), null);
    imageDcr = ImageDescriptor.createFromURL(url);
    this.imageOk = imageDcr.createImage();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    if (this.scenario.isPISDFScenario()) {
      return getPISDFColumnImage(element, columnIndex);
    } else {
      return null;
    }
  }

  /**
   * Gets the PISDF column image.
   *
   * @param element
   *          the element
   * @param columnIndex
   *          the column index
   * @return the PISDF column image
   */
  private Image getPISDFColumnImage(final Object element, final int columnIndex) {
    if ((element instanceof AbstractActor) && (this.currentOpDefId != null)) {
      final AbstractActor vertex = (AbstractActor) element;

      final Timing timing = this.scenario.getTimingManager().getTimingOrDefault(vertex.getName(), this.currentOpDefId);
      switch (columnIndex) {
        case 1:// Parsing column
          if (timing.canParse()) {
            return this.imageOk;
          } else {
            return this.imageError;
          }
        case 2:// Evaluation column
          if (timing.canEvaluate()) {
            return this.imageOk;
          } else {
            return this.imageError;
          }
        default:// Others
          break;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    if (this.scenario.isPISDFScenario()) {
      return getPISDFColumnText(element, columnIndex);
    } else if (this.scenario.isIBSDFScenario()) {
      return getIBSDFColumnText(element, columnIndex);
    } else {
      return null;
    }
  }

  /**
   * Gets the IBSDF column text.
   *
   * @param element
   *          the element
   * @param columnIndex
   *          the column index
   * @return the IBSDF column text
   */
  private String getIBSDFColumnText(final Object element, final int columnIndex) {
    String text = "";
    if ((element instanceof SDFAbstractVertex) && (this.currentOpDefId != null)) {
      final SDFAbstractVertex vertex = (SDFAbstractVertex) element;

      final Timing timing = this.scenario.getTimingManager().getTimingOrDefault(vertex.getName(), this.currentOpDefId);
      switch (columnIndex) {
        case 0:
          return vertex.getName();
        case 1: // Expression Column
          if (timing != null) {
            text = timing.getStringValue();
          }
          break;
        default:// Others
          break;
      }
    }
    return text;
  }

  /**
   * Gets the PISDF column text.
   *
   * @param element
   *          the element
   * @param columnIndex
   *          the column index
   * @return the PISDF column text
   */
  private String getPISDFColumnText(final Object element, final int columnIndex) {
    String text = "";
    if ((element instanceof AbstractActor) && (this.currentOpDefId != null)) {
      final AbstractActor vertex = (AbstractActor) element;

      final Timing timing = this.scenario.getTimingManager().getTimingOrDefault(vertex.getName(), this.currentOpDefId);

      switch (columnIndex) {
        case 0:
          return vertex.getName();
        case 1: // Parsing Column
        case 2: // Evaluation Column
          return null;
        case 3: // Variables Column
          if (timing != null) {
            if (timing.getInputParameters().isEmpty()) {
              text = "-";
            } else {
              text = timing.getInputParameters().toString();
            }
          }
          break;
        case 4: // Expression Column
          if (timing != null) {
            text = timing.getStringValue();
          }
          break;
        default:// Others
          break;
      }
    }
    return text;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {

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

      this.currentOpDefId = item;
      this.tableViewer.refresh();
    }

  }

  /**
   * Handle double click.
   *
   * @param selection
   *          the selection
   */
  public void handleDoubleClick(final IStructuredSelection selection) {
    final IInputValidator validator = newText -> {
      final String message = null;
      // int time = 0;
      //
      // try {
      // time = Integer.valueOf(newText);
      // } catch (NumberFormatException e) {
      // time = 0;
      // }
      //
      // if (time == 0)
      // message = Messages.getString("Timings.invalid");

      return message;
    };

    String vertexName = null;
    if (selection.getFirstElement() instanceof SDFVertex) {
      vertexName = ((SDFVertex) selection.getFirstElement()).getName();
    } else if (selection.getFirstElement() instanceof AbstractActor) {
      vertexName = ((AbstractActor) selection.getFirstElement()).getName();
    }

    if ((vertexName != null) && (this.currentOpDefId != null)) {
      final String title = Messages.getString("Timings.dialog.title");
      final String message = Messages.getString("Timings.dialog.message") + vertexName;
      final String init = this.scenario.getTimingManager().getTimingOrDefault(vertexName, this.currentOpDefId).getStringValue();

      final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message, init, validator);
      if (dialog.open() == Window.OK) {
        final String value = dialog.getValue();

        this.scenario.getTimingManager().setTiming(vertexName, this.currentOpDefId, value);

        this.tableViewer.refresh();
        this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
      }
    }
  }

}
