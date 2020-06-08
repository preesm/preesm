/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2020) :
 *
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2013)
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
package org.preesm.ui.scenario.editor.timings;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
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
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.expression.ExpressionEvaluator;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.ProcessingElement;
import org.preesm.model.slam.TimingType;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.timings.TimingsPage.TimingColumn;

/**
 * Displays the labels for tasks timings. These labels are the time of each task
 *
 * @author mpelcat
 */
public class TimingsTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider, SelectionListener {

  /** The scenario. */
  private Scenario scenario = null;

  /** The current processing element */
  private ProcessingElement currentPE = null;

  /** The current timing type */
  private TimingType currentTimingType = null;

  /** The table viewer. */
  private TableViewer tableViewer = null;

  /** The image error. */
  private final Image imageError;

  /** The image alert. */
  private final Image imageAlert;

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
  public TimingsTableLabelProvider(final Scenario scenario, final TableViewer tableViewer,
      final IPropertyListener propertyListener) {
    super();
    this.scenario = scenario;
    this.tableViewer = tableViewer;
    this.propertyListener = propertyListener;

    final URL errorIconURL = PreesmResourcesHelper.getInstance().resolve("icons/error.png", PreesmUIPlugin.class);
    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(errorIconURL);
    this.imageError = imageDcr.createImage();

    final URL okIconURL = PreesmResourcesHelper.getInstance().resolve("icons/ok.png", PreesmUIPlugin.class);
    imageDcr = ImageDescriptor.createFromURL(okIconURL);
    this.imageOk = imageDcr.createImage();

    // Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a
    // href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
    final URL alertIconURL = PreesmResourcesHelper.getInstance().resolve("icons/alert.png", PreesmUIPlugin.class);
    imageDcr = ImageDescriptor.createFromURL(alertIconURL);
    this.imageAlert = imageDcr.createImage();

    final Design design = scenario.getDesign();
    final List<ProcessingElement> operators = design.getProcessingElements();
    this.currentPE = operators.get(0);
    this.currentTimingType = operators.get(0).getTimingTypes().get(0);
  }

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    if ((element instanceof AbstractActor) && (this.currentPE != null) && (this.currentTimingType != null)) {
      final AbstractActor vertex = (AbstractActor) element;

      final String timing = this.scenario.getTimings().getTimingOrDefault(vertex, this.currentPE, this.currentTimingType);
      if (columnIndex == TimingColumn.VALUE.ordinal()) {
        if (ExpressionEvaluator.canEvaluate(vertex, timing)) {
          return this.imageOk;
        } else if (ExpressionEvaluator.canEvaluate(vertex.getContainingPiGraph(), timing)) {
          return this.imageAlert;
        } else {
          return this.imageError;
        }
      }
    }
    return null;
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    if (!(element instanceof AbstractActor)) {
      return "";
    }
    if ((this.currentPE == null) || (this.currentTimingType == null)) {
      return "";
    }

    final AbstractActor vertex = (AbstractActor) element;
    final String timing = this.scenario.getTimings().getTimingOrDefault(vertex, this.currentPE, this.currentTimingType);

      switch (columnIndex) {
        case ACTORS:
          return vertex.getVertexPath();
        case PARAMETERS: // Input Parameters
          if (timing == null || vertex.getInputParameters().isEmpty()) {
            return " - ";
          } else {
            return ExpressionEvaluator.lookupParameterValues(vertex, Collections.emptyMap()).keySet().toString();
          }
        case EXPRESSION: // Expression
          if (timing != null) {
            return timing;
          }
          break;
	  // check this case !!
        case 3: // Evaluation Status
          return null;
        case VALUE: // Value
          if (timing != null && ExpressionEvaluator.canEvaluate(vertex, timing)) {
            return Long
                .toString(ExpressionEvaluator.evaluate(vertex, timing, this.scenario.getParameterValues().map()));
          } else if (timing != null && ExpressionEvaluator.canEvaluate(vertex.getContainingPiGraph(), timing)) {
            return Long.toString(ExpressionEvaluator.evaluate(vertex.getContainingPiGraph(), timing,
                this.scenario.getParameterValues().map()));

          } else {
            return "";
          }
        default:
      }
    return "";
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
      ProcessingElement pe = this.scenario.getDesign().getProcessingElement(item);
      if (pe != null) {
        this.currentPE = pe;
      } else {
        currentTimingType = TimingType.get(item);
      }
      this.tableViewer.refresh();
    }
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    // nothing
  }

  /**
   * Handle double click.
   *
   * @param selection
   *          the selection
   */
  public void handleDoubleClick(final IStructuredSelection selection) {
    final IInputValidator validator = newText -> null;

    final Object firstElement = selection.getFirstElement();
    if (firstElement instanceof AbstractActor) {
      final AbstractActor abstractActor = (AbstractActor) firstElement;

      if ((this.currentPE != null) && (this.currentTimingType != null)) {
        final String title = Messages.getString("Timings.dialog.title");
        final String message = Messages.getString("Timings.dialog.message") + abstractActor.getVertexPath();
        final String init = this.scenario.getTimings().getTimingOrDefault(abstractActor, currentPE, currentTimingType);

        final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            title, message, init, validator);
        if (dialog.open() == Window.OK) {
          final String value = dialog.getValue();

          this.scenario.getTimings().setTiming(abstractActor, this.currentPE, this.currentTimingType, value);
          this.propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
          this.tableViewer.refresh();
        }
      }
    }
  }
}
