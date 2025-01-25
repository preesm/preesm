/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
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
          if (feature instanceof final SetPersistenceLevelFeature setPLFeature) {
            final PersistenceLevel pl = switch (((CCombo) e.getSource()).getSelectionIndex()) {
              case PersistenceLevel.PERMANENT_VALUE -> PersistenceLevel.PERMANENT;
              case PersistenceLevel.LOCAL_VALUE -> PersistenceLevel.LOCAL;
              case PersistenceLevel.NONE_VALUE -> PersistenceLevel.NONE;
              default -> null;
            };
            setPLFeature.setCurrentPL(pl);

            getDiagramTypeProvider().getDiagramBehavior().executeFeature(setPLFeature, context);
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
        // nothing by default
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
      final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      if (bo instanceof final Delay delay) {
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
    String elementName;
    Expression elementValueExpression;
    final boolean expressionHasFocus = txtExpression.isFocusControl();
    final Point selection = txtExpression.getSelection();
    txtExpression.setEnabled(false);

    if (pictogramElement == null) {
      return;
    }

    final Object businessObject = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pictogramElement);

    if (businessObject == null) {
      return;
    }

    if (!(businessObject instanceof final Delay delay)) {
      throw new UnsupportedOperationException();
    }

    final Fifo fifo = delay.getContainingFifo();
    elementName = fifo.getId();
    elementValueExpression = fifo.getDelay().getSizeExpression();

    this.persistenceComboLevel.select(delay.getLevel().getValue());
    this.persistenceComboLevel.setVisible(true);
    this.persistenceComboLevel.setEnabled(true);
    this.persistenceLabelLevel.setVisible(true);
    this.persistenceLabelLevel.setEnabled(true);

    lblNameObj.setText(elementName == null ? " " : elementName);

    if (elementValueExpression == null) {
      return;
    }

    this.txtExpression.setEnabled(true);

    final String eltExprString = elementValueExpression.getExpressionAsString();
    if (txtExpression.getText().compareTo(eltExprString) != 0) {
      txtExpression.setText(eltExprString);
    }

    try {
      // try out evaluating the expression
      final double evaluate = elementValueExpression.evaluateAsDouble();
      lblValueObj.setText(Double.toString(evaluate));

      if (elementValueExpression.isExpressionInteger()) {
        txtExpression.setBackground(BG_NORMAL_WHITE);
      } else {
        txtExpression.setBackground(BG_WARNING_YELLOW);
      }

    } catch (final ExpressionEvaluationException e) {
      // otherwise print error message and put red background
      lblValueObj.setText("Error : " + e.getMessage());
      txtExpression.setBackground(BG_ERROR_RED);
    }

    if (expressionHasFocus) {
      txtExpression.setFocus();
      txtExpression.setSelection(selection);
    }
  }

}
