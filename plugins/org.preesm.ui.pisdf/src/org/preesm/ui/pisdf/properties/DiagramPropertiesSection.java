/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2020)
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
package org.preesm.ui.pisdf.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;

/**
 * Properties of Diagram, for graph period.
 *
 * @author ahonorat
 */
public class DiagramPropertiesSection extends GFPropertySection implements ITabbedPropertyConstants {

  /** Items of the {@link DiagramPropertiesSection}. */

  /** The lbl for the actor period. */
  private CLabel lblPeriod;

  /** The txt for the actor period. */
  private Text txtPeriod;

  /** The lbl value. */
  private CLabel lblPeriodValue;

  /** The lbl value obj. */
  private CLabel lblPeriodValueObj;

  /** The first column width. */
  private static final int FIRST_COLUMN_WIDTH = 150;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
   * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
   */
  @Override
  public void createControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {

    super.createControls(parent, tabbedPropertySheetPage);

    final TabbedPropertySheetWidgetFactory factory = getWidgetFactory();

    final Composite composite = factory.createFlatFormComposite(parent);

    FormData data;

    final Label explanations = factory.createLabel(composite,
        "Graph period here, works only if set on top graph, and with the correct code generation."
            + "\nPut \"0\" if not periodic.");

    /**** Period ****/
    this.txtPeriod = factory.createText(composite, "0");
    data = new FormData();
    data.top = new FormAttachment(explanations);
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(50, 0);
    this.txtPeriod.setLayoutData(data);
    this.txtPeriod.setEnabled(true);
    this.txtPeriod.setToolTipText("Enter a positive expression if this graph is periodic.\n"
        + "Any negative or zero value means it is aperiodic.");

    this.lblPeriod = factory.createCLabel(composite, "Period expression:");
    data = new FormData();
    data.top = new FormAttachment(explanations);
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtPeriod, -ITabbedPropertyConstants.HSPACE);
    this.lblPeriod.setLayoutData(data);

    /*** Period box listener ***/
    this.txtPeriod.addModifyListener(e -> updatePeriod());

    this.lblPeriodValueObj = factory.createCLabel(composite, "0");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(lblPeriod);
    this.lblPeriodValueObj.setLayoutData(data);

    this.lblPeriodValue = factory.createCLabel(composite, "Period value:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblPeriodValueObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(lblPeriod);
    this.lblPeriodValue.setLayoutData(data);

  }

  protected void setNewPeriod(final ExpressionHolder e, final String value) {
    final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
      @Override
      protected void doExecute() {
        e.setExpression(value);
      }
    });
  }

  private void updatePeriod() {
    final PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      if (bo instanceof final PeriodicElement periodEl) {
        final Expression periodicExp = periodEl.getExpression();
        final String strPeriod = this.txtPeriod.getText();
        if (strPeriod.compareTo(periodicExp.getExpressionAsString()) != 0) {
          setNewPeriod(periodEl, strPeriod);
          refresh();
        }
      } // end PeriodicElement
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
   */
  @Override
  public void refresh() {

    final PictogramElement pe = getSelectedPictogramElement();
    if (pe == null) {
      return;
    }

    final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo == null) {
      return;
    }

    final PiGraph graph = (PiGraph) bo;

    final Point selection = this.txtPeriod.getSelection();
    final boolean expressionHasFocus = this.txtPeriod.isFocusControl();

    boolean periodVisible = false;
    if (graph instanceof PeriodicElement && graph.getDataInputInterfaces().isEmpty()
        && graph.getDataOutputInterfaces().isEmpty()) {
      periodVisible = true;
      final PeriodicElement periodEl = (PeriodicElement) bo;
      final Expression periodicExp = periodEl.getExpression();

      if (periodicExp != null) {
        this.txtPeriod.setEnabled(true);

        final String eltExprString = periodicExp.getExpressionAsString();
        if (this.txtPeriod.getText().compareTo(eltExprString) != 0) {
          this.txtPeriod.setText(eltExprString);
        }

        try {
          // try out evaluating the expression
          final long evaluate = periodicExp.evaluateAsLong();
          if (evaluate < 0) {
            throw new IllegalArgumentException("Period cannot be negative: either positive or 0 if aperiodic.");
          }
          // if evaluation went well, just write the result
          if (evaluate == 0) {
            this.lblPeriodValueObj.setText("0 (aperiodic)");
          } else {
            this.lblPeriodValueObj.setText(Long.toString(evaluate));
          }
          this.txtPeriod.setBackground(new Color(null, 255, 255, 255));
        } catch (final ExpressionEvaluationException | IllegalArgumentException e) {
          // otherwise print error message and put red background
          this.lblPeriodValueObj.setText("Error : " + e.getMessage());
          this.txtPeriod.setBackground(new Color(null, 240, 150, 150));
        }

        if (expressionHasFocus) {
          this.txtPeriod.setFocus();
          this.txtPeriod.setSelection(selection);
        }
      }

    } // end PeriodicElement

    this.lblPeriod.setVisible(periodVisible);
    this.txtPeriod.setVisible(periodVisible);
    this.lblPeriodValue.setVisible(periodVisible);
    this.lblPeriodValueObj.setVisible(periodVisible);
  }

}
