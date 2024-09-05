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
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.ui.pisdf.features.SetPortMemoryAnnotationFeature;

/**
 * GUI properties section of ports.
 *
 * @author ahonorat
 */
public class DataPortPropertiesSection extends ParameterizablePropertiesSection {

  /** The lbl annotation. */
  CLabel memoryLabelAnnotation;

  /** The combo annotation. */
  CCombo memoryComboAnnotation;

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

    /**** MEMORY ANNOTATION ****/
    this.memoryComboAnnotation = factory.createCCombo(composite);
    for (final PortMemoryAnnotation pma : PortMemoryAnnotation.values()) {
      this.memoryComboAnnotation.add(pma.toString(), pma.getValue());
    }

    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(25, 0);
    data.top = new FormAttachment(this.lblValueObj);
    this.memoryComboAnnotation.setLayoutData(data);

    this.memoryLabelAnnotation = factory.createCLabel(composite, "Memory Annotation:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.memoryComboAnnotation, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblValue);
    this.memoryLabelAnnotation.setLayoutData(data);

    this.memoryComboAnnotation.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final PictogramElement[] pes = new PictogramElement[1];
        pes[0] = getSelectedPictogramElement();

        final CustomContext context = new CustomContext(pes);
        final ICustomFeature[] setPotMemoryAnnotationFeature = getDiagramTypeProvider().getFeatureProvider()
            .getCustomFeatures(context);

        for (final ICustomFeature feature : setPotMemoryAnnotationFeature) {
          if (feature instanceof final SetPortMemoryAnnotationFeature setPMAFeature) {
            final PortMemoryAnnotation pma = switch (((CCombo) e.getSource()).getSelectionIndex()) {
              case PortMemoryAnnotation.READ_ONLY_VALUE -> PortMemoryAnnotation.READ_ONLY;
              case PortMemoryAnnotation.WRITE_ONLY_VALUE -> PortMemoryAnnotation.WRITE_ONLY;
              case PortMemoryAnnotation.UNUSED_VALUE -> PortMemoryAnnotation.UNUSED;
              default -> null;
            };
            setPMAFeature.setCurrentPMA(pma);

            getDiagramTypeProvider().getDiagramBehavior().executeFeature(setPMAFeature, context);
            // final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            // final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
            // .getLayoutFeature(contextLayout);
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

    this.memoryComboAnnotation.setVisible(false);
    this.memoryComboAnnotation.setEnabled(false);
    this.memoryLabelAnnotation.setVisible(false);
    this.memoryLabelAnnotation.setEnabled(false);

  }

  @Override
  void updateProperties() {
    final PictogramElement pe = getSelectedPictogramElement();

    if (pe == null) {
      return;
    }

    final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo == null) {
      return;
    }

    if (bo instanceof final DataPort port) {
      updateDataPortProperties(port, txtExpression);
      getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators((PictogramElement) (pe.eContainer()));
      this.memoryComboAnnotation.select(((DataPort) bo).getAnnotation().getValue());
    }
    refresh();

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
    Expression elementValueExpression;
    final boolean expressionHasFocus = txtExpression.isFocusControl();
    final Point selelection = txtExpression.getSelection();
    txtExpression.setEnabled(false);

    if (pictogramElement == null) {
      return;
    }

    final Object businessObject = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pictogramElement);

    if (businessObject == null) {
      return;
    }

    if (!(businessObject instanceof final DataPort iPort)) {
      throw new UnsupportedOperationException();
    }

    if (iPort.eContainer() instanceof final InterfaceActor iActor) {
      elementName = iActor.getName();
    } else {
      elementName = iPort.getName();

      boolean isHierarchicalActor = false;
      if (iPort.eContainer() instanceof final Actor actor && actor.isHierarchical()) {
        isHierarchicalActor = true;
      }

      if (!isHierarchicalActor) {
        this.memoryComboAnnotation.setVisible(true);
        this.memoryComboAnnotation.setEnabled(true);
        this.memoryLabelAnnotation.setVisible(true);
        this.memoryLabelAnnotation.setEnabled(true);
      }
    }

    elementValueExpression = iPort.getPortRateExpression();

    this.memoryComboAnnotation.select(iPort.getAnnotation().getValue());

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
      txtExpression.setSelection(selelection);
    }
  }

}
