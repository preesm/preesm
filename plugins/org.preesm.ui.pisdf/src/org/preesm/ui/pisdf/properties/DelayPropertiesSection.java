package org.preesm.ui.pisdf.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.ui.pisdf.features.SetPersistenceLevelFeature;

/**
 * GUI properties section of Delays.
 * 
 * @author ahonorat
 */
public class DelayPropertiesSection extends ParameterizablePropertiesSection {
  CLabel persistenceLabelLevel;
  CCombo persistenceComboLevel;

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

    FormData data;

    /**** PERSISTENCE ANNOTATION ****/
    this.persistenceComboLevel = factory.createCCombo(composite);
    for (final PersistenceLevel pl : PersistenceLevel.values()) {
      this.persistenceComboLevel.add(pl.toString(), pl.getValue());
    }

    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(25, 0);
    data.top = new FormAttachment(this.lblValueObj);
    this.persistenceComboLevel.setLayoutData(data);

    this.persistenceLabelLevel = factory.createCLabel(composite, "Persistence Level:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.persistenceComboLevel, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblValue);
    this.persistenceLabelLevel.setLayoutData(data);

    this.persistenceComboLevel.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setDelayPersistenceFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : setDelayPersistenceFeature) {
          if (feature instanceof SetPersistenceLevelFeature) {
            PersistenceLevel pl = null;
            switch (((CCombo) e.getSource()).getSelectionIndex()) {
              case PersistenceLevel.PERMANENT_VALUE:
                pl = PersistenceLevel.PERMANENT;
                break;
              case PersistenceLevel.LOCAL_VALUE:
                pl = PersistenceLevel.LOCAL;
                break;
              case PersistenceLevel.NONE_VALUE:
                pl = PersistenceLevel.NONE;
                break;
              default:
                break;

            }
            ((SetPersistenceLevelFeature) feature).setCurrentPL(pl);

            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
            // final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            // final ILayoutFeature layoutFeature =
            // getDiagramTypeProvider().getFeatureProvider().getLayoutFeature(contextLayout);
            // getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
      }
    });

    this.persistenceComboLevel.setVisible(false);
    this.persistenceComboLevel.setEnabled(false);
    this.persistenceLabelLevel.setVisible(false);
    this.persistenceLabelLevel.setEnabled(false);

  }

  @Override
  void updateProperties() {
    final PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      if (bo instanceof Delay) {
        final Delay delay = (Delay) bo;
        if (!delay.getSizeExpression().getExpressionAsString().equals(txtExpression.getText())) {
          setNewExpression(delay, txtExpression.getText());
        }
        getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        this.persistenceComboLevel.select(delay.getLevel().getValue());
      }
      refresh();
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
   */
  @Override
  public void refresh() {
    final PictogramElement pictogramElement = getSelectedPictogramElement();
    String elementName = null;
    Expression elementValueExpression = null;
    final boolean expressionHasFocus = txtExpression.isFocusControl();
    final Point selelection = txtExpression.getSelection();
    txtExpression.setEnabled(false);

    if (pictogramElement != null) {
      final Object businessObject = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pictogramElement);
      if (businessObject == null) {
        return;
      }

      if (businessObject instanceof Delay) {
        final Fifo fifo = ((Delay) businessObject).getContainingFifo();
        elementName = fifo.getId();
        elementValueExpression = fifo.getDelay().getSizeExpression();

        this.persistenceComboLevel.select(((Delay) businessObject).getLevel().getValue());
        this.persistenceComboLevel.setVisible(true);
        this.persistenceComboLevel.setEnabled(true);
        this.persistenceLabelLevel.setVisible(true);
        this.persistenceLabelLevel.setEnabled(true);

        lblNameObj.setText(elementName == null ? " " : elementName);

        if (elementValueExpression != null) {
          this.txtExpression.setEnabled(true);

          final String eltExprString = elementValueExpression.getExpressionAsString();
          if (txtExpression.getText().compareTo(eltExprString) != 0) {
            txtExpression.setText(eltExprString);
          }

          try {
            // try out evaluating the expression
            final long evaluate = elementValueExpression.evaluate();
            lblValueObj.setText(Long.toString(evaluate));
            txtExpression.setBackground(new Color(null, 255, 255, 255));
          } catch (final ExpressionEvaluationException e) {
            // otherwise print error message and put red background
            lblValueObj.setText("Error : " + e.getMessage());
            txtExpression.setBackground(new Color(null, 240, 150, 150));
          }

          if (expressionHasFocus) {
            txtExpression.setFocus();
            txtExpression.setSelection(selelection);
          }
        }

      } else {
        throw new UnsupportedOperationException();
      }

    }
  }

}
