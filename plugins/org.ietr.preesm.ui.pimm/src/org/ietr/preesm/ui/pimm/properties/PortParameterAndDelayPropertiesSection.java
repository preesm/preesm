/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2013)
 * Romina Racca <romina.racca@gmail.com> (2013)
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
package org.ietr.preesm.ui.pimm.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluationException;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PersistenceLevel;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.ui.pimm.features.SetPersistenceLevelFeature;
import org.ietr.preesm.ui.pimm.features.SetPortMemoryAnnotationFeature;

// TODO: Auto-generated Javadoc
/**
 * The Class PortParameterAndDelayPropertiesSection.
 *
 * @author Romina Racca
 * @author jheulot
 */
public class PortParameterAndDelayPropertiesSection extends DataPortPropertiesUpdater
    implements ITabbedPropertyConstants {

  /** The lbl name. */
  private CLabel lblName;

  /** The lbl name obj. */
  private CLabel lblNameObj;

  /** The lbl expression. */
  private CLabel lblExpression;

  /** The lbl value. */
  private CLabel lblValue;

  /** The lbl value obj. */
  private CLabel lblValueObj;

  /** The lbl annotation. */
  private CLabel memoryLabelAnnotation;

  /** The combo annotation. */
  private CCombo memoryComboAnnotation;

  private CLabel persistenceLabelLevel;
  private CCombo persistenceComboLevel;

  /** The first column width. */
  private final int FIRST_COLUMN_WIDTH = 200;

  /**
   * A text expression can be as an expression: value numbers, trigonometric functions, expression of condition "if
   * (cond, true value, false value)".
   */
  private Text txtExpression;

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

    /**** NAME ****/
    this.lblNameObj = factory.createCLabel(composite, " ");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    this.lblNameObj.setLayoutData(data);

    this.lblName = factory.createCLabel(composite, "Name:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblNameObj, -ITabbedPropertyConstants.HSPACE);
    this.lblName.setLayoutData(data);

    /**** EXPRESION ****/
    this.txtExpression = factory.createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblNameObj);
    this.txtExpression.setLayoutData(data);
    this.txtExpression.setEnabled(true);

    this.lblExpression = factory.createCLabel(composite, "Expression:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtExpression, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblName);
    this.lblExpression.setLayoutData(data);

    /**** VALUE ****/
    this.lblValueObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtExpression);
    this.lblValueObj.setLayoutData(data);

    this.lblValue = factory.createCLabel(composite, "Default Value:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblValueObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblExpression);
    this.lblValue.setLayoutData(data);

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

    this.txtExpression.addModifyListener(e -> updateProperties());

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
            final LayoutContext contextLayout = new LayoutContext(getSelectedPictogramElement());
            final ILayoutFeature layoutFeature = getDiagramTypeProvider().getFeatureProvider()
                .getLayoutFeature(contextLayout);
            getDiagramTypeProvider().getDiagramBehavior().executeFeature(layoutFeature, contextLayout);
          }
        }

        refresh();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
      }
    });

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

    this.txtExpression.addModifyListener(e -> updateProperties());

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

  }

  /**
   * Update the {@link Port}/{@link Delay}/{@link Parameter} {@link Expression} with the value stored in the
   * txtEpression.
   */
  private void updateProperties() {
    final PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }
      if (bo instanceof InterfaceActor) {
        bo = ((InterfaceActor) bo).getDataPort();
      }

      if (bo instanceof Parameter) {
        final Parameter param = (Parameter) bo;
        if (param.getValueExpression().getExpressionString().compareTo(this.txtExpression.getText()) != 0) {
          setNewExpression(param.getValueExpression(), this.txtExpression.getText());
          getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        }
      } // end Parameter

      if (bo instanceof DataPort) {
        final DataPort port = (DataPort) bo;
        updateDataPortProperties(port, this.txtExpression);

        getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators((PictogramElement) (pe.eContainer()));

        this.memoryComboAnnotation.setEnabled(false);
        this.memoryComboAnnotation.select(((DataPort) bo).getAnnotation().getValue());
        this.memoryComboAnnotation.setVisible(true);
        this.memoryComboAnnotation.setEnabled(true);
        this.memoryLabelAnnotation.setEnabled(false);
        this.memoryLabelAnnotation.setVisible(true);
        this.memoryLabelAnnotation.setEnabled(true);

        this.persistenceComboLevel.setEnabled(false);
        this.persistenceComboLevel.setVisible(false);
        this.persistenceComboLevel.setEnabled(true);
        this.persistenceLabelLevel.setEnabled(false);
        this.persistenceLabelLevel.setVisible(false);
        this.persistenceLabelLevel.setEnabled(true);
      } else {
        this.memoryComboAnnotation.setEnabled(false);
        this.memoryComboAnnotation.setVisible(false);
        this.memoryComboAnnotation.setEnabled(true);
        this.memoryLabelAnnotation.setEnabled(false);
        this.memoryLabelAnnotation.setVisible(false);
        this.memoryLabelAnnotation.setEnabled(true);
      }

      if (bo instanceof Delay) {
        final Delay delay = (Delay) bo;
        if (delay.getSizeExpression().getExpressionString().compareTo(this.txtExpression.getText()) != 0) {
          setNewExpression(delay.getSizeExpression(), this.txtExpression.getText());
          getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        }

        this.persistenceComboLevel.setEnabled(false);
        this.persistenceComboLevel.select(delay.getLevel().getValue());
        this.persistenceComboLevel.setVisible(true);
        this.persistenceComboLevel.setEnabled(true);
        this.persistenceLabelLevel.setEnabled(false);
        this.persistenceLabelLevel.setVisible(true);
        this.persistenceLabelLevel.setEnabled(true);
      } // end Delay
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
    Expression elementValueExpression = null;
    final boolean expressionHasFocus = this.txtExpression.isFocusControl();
    final Point selelection = this.txtExpression.getSelection();
    this.txtExpression.setEnabled(false);

    if (pictogramElement != null) {
      final Object businessObject = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pictogramElement);
      if (businessObject == null) {
        return;
      }

      if (businessObject instanceof Parameter) {
        elementName = ((Parameter) businessObject).getName();
        elementValueExpression = ((Parameter) businessObject).getValueExpression();
      } else if (businessObject instanceof DataPort) {
        final DataPort iPort = ((DataPort) businessObject);

        this.memoryComboAnnotation.select(((DataPort) businessObject).getAnnotation().getValue());

        if (iPort.eContainer() instanceof InterfaceActor) {
          elementName = ((InterfaceActor) iPort.eContainer()).getName();
        } else {
          elementName = iPort.getName();
        }

        elementValueExpression = iPort.getPortRateExpression();

      } else if (businessObject instanceof InterfaceActor) {
        final InterfaceActor iface = ((InterfaceActor) businessObject);
        elementName = iface.getName();
        elementValueExpression = iface.getDataPort().getPortRateExpression();
      } else if (businessObject instanceof Delay) {
        final Fifo fifo = (Fifo) ((Delay) businessObject).getContainingFifo();
        elementName = fifo.getId();
        elementValueExpression = fifo.getDelay().getSizeExpression();

        this.persistenceComboLevel.select(((Delay) businessObject).getLevel().getValue());
      } else {
        throw new UnsupportedOperationException();
      }

      this.lblNameObj.setText(elementName == null ? " " : elementName);
      if (elementValueExpression != null) {
        this.txtExpression.setEnabled(true);

        final String eltExprString = elementValueExpression.getExpressionString();
        if (this.txtExpression.getText().compareTo(eltExprString) != 0) {
          this.txtExpression.setText(eltExprString);
        }

        try {
          // try out evaluating the expression
          final long evaluate = ExpressionEvaluator.evaluate(elementValueExpression);
          // if evaluation went well, just write the result
          this.lblValueObj.setText(Long.toString(evaluate));
          this.txtExpression.setBackground(new Color(null, 255, 255, 255));
        } catch (final ExpressionEvaluationException e) {
          // otherwise print error message and put red background
          this.lblValueObj.setText("Error : " + e.getMessage());
          this.txtExpression.setBackground(new Color(null, 240, 150, 150));
        }

        if (expressionHasFocus) {
          this.txtExpression.setFocus();
          this.txtExpression.setSelection(selelection);
        }
      }
    }
  }
}
