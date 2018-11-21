/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.expression.ExpressionEvaluationException;

// TODO: Auto-generated Javadoc
/**
 * The Class FifoPropertiesSection.
 */
public class FifoPropertiesSection extends DataPortPropertiesUpdater implements ITabbedPropertyConstants {

  /** Items of the {@link FifoPropertiesSection}. */
  private CLabel lblType;

  /** The txt type obj. */
  private Text txtTypeObj;

  /** The txt source port expression. */
  private Text txtSourcePortExpression;

  /** The lbl source port expression. */
  private CLabel lblSourcePortExpression;

  /** The lbl source port value obj. */
  private CLabel lblSourcePortValueObj;

  /** The lbl source port value. */
  private CLabel lblSourcePortValue;

  /** The txt target port expression. */
  private Text txtTargetPortExpression;

  /** The lbl target port expression. */
  private CLabel lblTargetPortExpression;

  /** The lbl target port value obj. */
  private CLabel lblTargetPortValueObj;

  /** The lbl target port value. */
  private CLabel lblTargetPortValue;

  /** The first column width. */
  private final int FIRST_COLUMN_WIDTH = 150;

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

    /**** TYPE ****/
    this.txtTypeObj = factory.createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(25, 0);
    this.txtTypeObj.setLayoutData(data);
    this.txtTypeObj.setEnabled(true);

    this.lblType = factory.createCLabel(composite, "Data type:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtTypeObj, -ITabbedPropertyConstants.HSPACE);
    this.lblType.setLayoutData(data);

    /**** SOURCE PORT ****/
    /**** EXPRESSION ****/
    this.txtSourcePortExpression = factory.createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtTypeObj);
    this.txtSourcePortExpression.setLayoutData(data);
    this.txtSourcePortExpression.setEnabled(true);

    this.lblSourcePortExpression = factory.createCLabel(composite, "Source port rate:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtSourcePortExpression, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblType);
    this.lblSourcePortExpression.setLayoutData(data);

    /**** VALUE ****/
    this.lblSourcePortValueObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtSourcePortExpression);
    this.lblSourcePortValueObj.setLayoutData(data);

    this.lblSourcePortValue = factory.createCLabel(composite, "Default Value:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblSourcePortValueObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblSourcePortExpression);
    this.lblSourcePortValue.setLayoutData(data);

    /**** TARGET PORT ****/
    /**** EXPRESION ****/
    this.txtTargetPortExpression = factory.createText(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblSourcePortValueObj);
    this.txtTargetPortExpression.setLayoutData(data);
    this.txtTargetPortExpression.setEnabled(true);

    this.lblTargetPortExpression = factory.createCLabel(composite, "Target port rate:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtTargetPortExpression, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblSourcePortValue);
    this.lblTargetPortExpression.setLayoutData(data);

    /**** VALUE ****/
    this.lblTargetPortValueObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, this.FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtTargetPortExpression);
    this.lblTargetPortValueObj.setLayoutData(data);

    this.lblTargetPortValue = factory.createCLabel(composite, "Default Value:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblTargetPortValueObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblTargetPortExpression);
    this.lblTargetPortValue.setLayoutData(data);

    /*** Type box listener ***/
    this.txtTypeObj.addModifyListener(e -> {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null) {
          return;
        }

        if (bo instanceof Fifo) {
          final Fifo fifo = (Fifo) bo;
          if (!FifoPropertiesSection.this.txtTypeObj.getText().equals(fifo.getType())) {
            setNewType(fifo, FifoPropertiesSection.this.txtTypeObj.getText());
            getDiagramTypeProvider().getDiagramBehavior().refreshContent();
          }
        }
      }
    });

    /** SourcePort expression listener */
    this.txtSourcePortExpression.addModifyListener(e -> {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null) {
          return;
        }
        if (bo instanceof Fifo) {
          final DataPort port = ((Fifo) bo).getSourcePort();
          updateDataPortProperties(port, FifoPropertiesSection.this.txtSourcePortExpression);
          final PictogramElement pict = Graphiti.getLinkService().getPictogramElements(getDiagram(), port).get(0);
          getDiagramTypeProvider().getDiagramBehavior()
              .refreshRenderingDecorators((PictogramElement) pict.eContainer());
        }
      }
      refresh();
    });

    /** TargetPort expression listener */
    this.txtTargetPortExpression.addModifyListener(e -> {
      final PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null) {
          return;
        }
        if (bo instanceof Fifo) {
          final DataPort port = ((Fifo) bo).getTargetPort();
          updateDataPortProperties(port, FifoPropertiesSection.this.txtTargetPortExpression);
          final PictogramElement pict = Graphiti.getLinkService().getPictogramElements(getDiagram(), port).get(0);
          getDiagramTypeProvider().getDiagramBehavior()
              .refreshRenderingDecorators((PictogramElement) pict.eContainer());
        }
      }

      refresh();
    });

    // txtSourcePortExpression.addModifyListener(new ModifyListener() {
    // @Override
    // public void modifyText(ModifyEvent e) {
    // updateProperties();
    // }
    // });
    //
    // txtTargetPortExpression.addModifyListener(new ModifyListener() {
    // @Override
    // public void modifyText(ModifyEvent e) {
    // updateProperties();
    // }
    // });

  }

  // private void updateProperties() {
  // PictogramElement pe = getSelectedPictogramElement();
  // if (pe != null) {
  // EObject bo = Graphiti.getLinkService()
  // .getBusinessObjectForLinkedPictogramElement(pe);
  // if (bo == null)
  // return;
  //
  // if (bo instanceof Fifo) {
  // Fifo fifo = (Fifo) bo;
  // updateDataPortProperties(fifo.getSourcePort(),
  // txtSourcePortExpression);
  // updateDataPortProperties(fifo.getTargetPort(),
  // txtTargetPortExpression);
  //
  // getDiagramTypeProvider().getDiagramBehavior()
  // .refreshRenderingDecorators(
  // (PictogramElement) (pe.eContainer()));
  // }
  // }
  // refresh();
  // }

  /**
   * Safely set a new type to the {@link Fifo}.
   *
   * @param fifo
   *          {@link Fifo} to set
   * @param value
   *          String value
   */
  private void setNewType(final Fifo fifo, final String value) {
    final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
      @Override
      protected void doExecute() {
        fifo.setType(value);
      }
    });
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

      if (bo instanceof Fifo) {
        final Fifo fifo = (Fifo) bo;
        final Expression srcRate = fifo.getSourcePort().getPortRateExpression();
        final String srcExprString = srcRate.getExpressionAsString();

        final Expression tgtRate = fifo.getTargetPort().getPortRateExpression();
        final String tgtExprString = tgtRate.getExpressionAsString();

        this.txtTypeObj.setText(fifo.getType());
        this.txtSourcePortExpression.setEnabled(true);
        if (!this.txtSourcePortExpression.getText().equals(srcExprString)) {
          this.txtSourcePortExpression.setText(srcExprString);
        }
        this.txtTargetPortExpression.setEnabled(true);
        if (!this.txtTargetPortExpression.getText().equals(tgtExprString)) {
          this.txtTargetPortExpression.setText(tgtExprString);
        }

        try {
          // try out evaluating the expression
          // if evaluation went well, just write the result
          final long evaluate = srcRate.evaluate();
          this.lblSourcePortValueObj.setText(Long.toString(evaluate));
          this.txtSourcePortExpression.setBackground(new Color(null, 255, 255, 255));
        } catch (final ExpressionEvaluationException e) {
          // otherwise print error message and put red background
          this.lblSourcePortValueObj.setText("Error : " + e.getMessage());
          this.txtSourcePortExpression.setBackground(new Color(null, 240, 150, 150));
        }
        try {
          // try out evaluating the expression
          final long evaluate = tgtRate.evaluate();
          // if evaluation went well, just write the result
          this.lblTargetPortValueObj.setText(Long.toString(evaluate));
          this.txtTargetPortExpression.setBackground(new Color(null, 255, 255, 255));
        } catch (final ExpressionEvaluationException e) {
          // otherwise print error message and put red background
          this.lblTargetPortValueObj.setText("Error : " + e.getMessage());
          this.txtTargetPortExpression.setBackground(new Color(null, 240, 150, 150));
        }
      }

    }
  }
}
