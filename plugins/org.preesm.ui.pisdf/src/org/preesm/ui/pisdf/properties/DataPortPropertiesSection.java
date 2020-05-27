/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
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
import org.eclipse.swt.graphics.Color;
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
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
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
          if (feature instanceof SetPortMemoryAnnotationFeature) {
            PortMemoryAnnotation pma = null;
            switch (((CCombo) e.getSource()).getSelectionIndex()) {
              case PortMemoryAnnotation.READ_ONLY_VALUE:
                pma = PortMemoryAnnotation.READ_ONLY;
                break;
              case PortMemoryAnnotation.WRITE_ONLY_VALUE:
                pma = PortMemoryAnnotation.WRITE_ONLY;
                break;
              case PortMemoryAnnotation.UNUSED_VALUE:
                pma = PortMemoryAnnotation.UNUSED;
                break;
              default:
                break;

            }
            ((SetPortMemoryAnnotationFeature) feature).setCurrentPMA(pma);

            getDiagramTypeProvider().getDiagramBehavior().executeFeature(feature, context);
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

    if (pe != null) {
      EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }

      if (bo instanceof DataPort) {
        final DataPort port = (DataPort) bo;
        updateDataPortProperties(port, txtExpression);
        getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators((PictogramElement) (pe.eContainer()));
        this.memoryComboAnnotation.select(((DataPort) bo).getAnnotation().getValue());
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
      if (businessObject instanceof DataPort) {
        final DataPort iPort = ((DataPort) businessObject);

        if (iPort.eContainer() instanceof InterfaceActor) {
          elementName = ((InterfaceActor) iPort.eContainer()).getName();
        } else {
          elementName = iPort.getName();

          boolean isHierarchicalActor = false;
          if (iPort.eContainer() instanceof Actor) {
            final Actor actor = (Actor) iPort.eContainer();
            if (actor.isHierarchical()) {
              isHierarchicalActor = true;
            }
          }
          if (!isHierarchicalActor) {
            this.memoryComboAnnotation.setVisible(true);
            this.memoryComboAnnotation.setEnabled(true);
            this.memoryLabelAnnotation.setVisible(true);
            this.memoryLabelAnnotation.setEnabled(true);
          }
        }

        elementValueExpression = iPort.getPortRateExpression();

        this.memoryComboAnnotation.select(((DataPort) businessObject).getAnnotation().getValue());

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
