/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020 - 2021)
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
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.MoldableParameter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.check.MoldableParameterExprChecker;

// TODO: Auto-generated Javadoc
/**
 * The Class PortParameterAndDelayPropertiesSection.
 *
 * @author Romina Racca
 * @author jheulot
 */
public class ParameterizablePropertiesSection extends DataPortPropertiesUpdater implements ITabbedPropertyConstants {

  /** The lbl name. */
  CLabel lblName;

  /** The lbl name obj. */
  CLabel lblNameObj;

  /** The lbl expression. */
  CLabel lblExpression;

  /** The lbl value. */
  CLabel lblValue;

  /** The lbl value obj. */
  CLabel lblValueObj;

  /** The first column width. */
  static final int FIRST_COLUMN_WIDTH = 200;

  /**
   * A text expression can be as an expression: value numbers, trigonometric functions, expression of condition "if
   * (cond, true value, false value)".
   */
  Text txtExpression;

  TabbedPropertySheetWidgetFactory factory   = null;
  Composite                        composite = null;
  /** Used only for moldable parameter due to stupid restriction of the UI with ECore */
  Exception                        errorMP   = null;

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

    factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);
    FormData data;

    /**** NAME ****/
    this.lblNameObj = factory.createCLabel(composite, " ");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
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
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.lblNameObj);
    this.txtExpression.setLayoutData(data);
    this.txtExpression.setEnabled(true);
    this.txtExpression.addModifyListener(e -> updateProperties());

    this.lblExpression = factory.createCLabel(composite, "Expression:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.txtExpression, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblName);
    this.lblExpression.setLayoutData(data);

    /**** VALUE ****/
    this.lblValueObj = factory.createCLabel(composite, "");
    data = new FormData();
    data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(this.txtExpression);
    this.lblValueObj.setLayoutData(data);

    this.lblValue = factory.createCLabel(composite, "Default Value:");
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(this.lblValueObj, -ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(this.lblExpression);
    this.lblValue.setLayoutData(data);

  }

  protected void setNewMoldableParameterUserExpression(final MoldableParameter mp, final String value) {
    final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
      @Override
      protected void doExecute() {
        mp.setUserExpression(value);
        errorMP = MoldableParameterExprChecker.isValid(mp);
      }
    });
  }

  /**
   * Update the {@link Port}/{@link Delay}/{@link Parameter} {@link Expression} with the value stored in the
   * txtEpression.
   */
  void updateProperties() {
    final PictogramElement pe = getSelectedPictogramElement();

    if (pe == null) {
      return;
    }
    final EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
    if (bo == null) {
      return;
    }
    if (bo instanceof final InterfaceActor iActor) {
      final DataPort dp = iActor.getDataPort();
      updateDataPortProperties(dp, txtExpression);
    } else if (bo instanceof final MoldableParameter mp) {
      if (mp.getUserExpression().compareTo(this.txtExpression.getText()) != 0) {
        setNewMoldableParameterUserExpression(mp, this.txtExpression.getText());
        getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
      }
    } else if (bo instanceof final Parameter param) {
      if ((bo instanceof ConfigInputInterface)) {
        this.lblValueObj.setText(
            "Default value is a Long Integer, only used for the computation of subsequent parameters in the GUI.");
      }
      if (param.getValueExpression().getExpressionAsString().compareTo(this.txtExpression.getText()) != 0) {
        setNewExpression(param, this.txtExpression.getText());
        getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
      }
    } // end Parameter

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

    if (pictogramElement == null) {
      return;
    }

    final Object businessObject = Graphiti.getLinkService()
        .getBusinessObjectForLinkedPictogramElement(pictogramElement);
    if (businessObject == null) {
      return;
    }

    if (businessObject instanceof final Parameter param) {
      elementName = param.getName();
      elementValueExpression = ((Parameter) businessObject).getValueExpression();
    } else if (businessObject instanceof final InterfaceActor iface) {
      elementName = iface.getName();
      elementValueExpression = iface.getDataPort().getPortRateExpression();
    } else {
      throw new UnsupportedOperationException();
    }

    this.lblNameObj.setText(elementName == null ? " " : elementName);
    if (elementValueExpression == null) {
      return;
    }
    this.txtExpression.setEnabled(true);

    if (businessObject instanceof final MoldableParameter mp) {
      final String eltExprString = mp.getUserExpression();
      if (this.txtExpression.getText().compareTo(eltExprString) != 0) {
        this.txtExpression.setText(eltExprString);
      }
      if (errorMP == null) {
        // we need to check, since isValid method is called only if an update is done
        try {
          final double evaluate = mp.getExpression().evaluateAsDouble();
          lblValueObj.setText(Double.toString(evaluate));
          txtExpression.setBackground(BG_NORMAL_WHITE);
        } catch (final ExpressionEvaluationException e) {
          lblValueObj.setText("A moldable is a sequence of expression separated by ';'. Error : " + e.getMessage());
          txtExpression.setBackground(BG_ERROR_RED);
        }
      } else {
        lblValueObj.setText("A moldable is a sequence of expression separated by ';'. Error : " + errorMP.getMessage());
        txtExpression.setBackground(BG_ERROR_RED);
      }
    } else {

      // String txt = this.txtExpression.getText();
      // if (txt.matches("^-?+0\\d++.*+$")) {
      // while (txt.matches("^-?+0\\d++.*+$")) {
      // txt = txt.substring(1);
      // }
      // this.txtExpression.setText(txt);
      // }

      final String eltExprString = elementValueExpression.getExpressionAsString();
      if (this.txtExpression.getText().isBlank() && !eltExprString.isBlank()) {
        this.txtExpression.setText(eltExprString);
      }

      try {
        // try out evaluating the expression
        final double evaluate = elementValueExpression.evaluateAsDouble();

        // if evaluation went well, just write the result
        if (!(businessObject instanceof ConfigInputInterface)) {
          this.lblValueObj.setText(Double.toString(evaluate));
        }
        this.txtExpression.setBackground(BG_NORMAL_WHITE);
      } catch (final ExpressionEvaluationException e) {
        // otherwise print error message and put red background
        this.lblValueObj.setText("Error : " + e.getMessage());
        this.txtExpression.setBackground(BG_ERROR_RED);
      }
    }

    if (expressionHasFocus) {
      this.txtExpression.setFocus();
      this.txtExpression.setSelection(selelection);
    }
  }

}
