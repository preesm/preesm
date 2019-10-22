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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.PeriodicElement;

/**
 * Period tab of Actor properties.
 * 
 * @author ahonorat
 */
public class ActorPeriodPropertiesSection extends GFPropertySection implements ITabbedPropertyConstants {

  /** Items of the {@link ActorPropertiesSection}. */
  private Composite composite;

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
    this.composite = factory.createFlatFormComposite(parent);

    FormData data;

    /**** Period ****/
    this.txtPeriod = factory.createText(this.composite, "0");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(50, 0);
    this.txtPeriod.setLayoutData(data);
    this.txtPeriod.setEnabled(true);
    this.txtPeriod.setToolTipText("Enter a positive expression if this actor is periodic.\n"
        + "Any negative or zero value means it is aperiodic.");

    this.lblPeriod = factory.createCLabel(this.composite, "Period expression:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtPeriod, -ITabbedPropertyConstants.HSPACE);
    this.lblPeriod.setLayoutData(data);

    /*** Period box listener ***/
    this.txtPeriod.addModifyListener(e -> updatePeriod());

    this.lblPeriodValueObj = factory.createCLabel(this.composite, "0");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(lblPeriod);
    this.lblPeriodValueObj.setLayoutData(data);

    this.lblPeriodValue = factory.createCLabel(this.composite, "Period value:");
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

      if (bo instanceof PeriodicElement) {
        final PeriodicElement periodEl = (PeriodicElement) bo;
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

    if (pe != null) {
      final Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      final Actor actor = (Actor) bo;

      final Point selelection = this.txtPeriod.getSelection();
      final boolean expressionHasFocus = this.txtPeriod.isFocusControl();

      boolean periodVisible = false;
      if (actor instanceof PeriodicElement && !actor.isHierarchical() && !actor.isConfigurationActor()) {
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
            final long evaluate = periodicExp.evaluate();
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
            this.txtPeriod.setSelection(selelection);
          }
        }

      } // end PeriodicElement

      this.lblPeriod.setVisible(periodVisible);
      this.txtPeriod.setVisible(periodVisible);
      this.lblPeriodValue.setVisible(periodVisible);
      this.lblPeriodValueObj.setVisible(periodVisible);
    }

  }
}
