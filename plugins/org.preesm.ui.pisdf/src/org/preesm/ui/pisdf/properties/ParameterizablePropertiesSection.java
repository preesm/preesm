/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
 * Romina Racca [romina.racca@gmail.com] (2013)
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
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.MalleableParameter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.check.MalleableParameterExprChecker;

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
  final int FIRST_COLUMN_WIDTH = 200;

  /**
   * A text expression can be as an expression: value numbers, trigonometric functions, expression of condition "if
   * (cond, true value, false value)".
   */
  Text txtExpression;

  TabbedPropertySheetWidgetFactory factory   = null;
  Composite                        composite = null;
  /** Used only for malleable parameter due to stupid restriction of the UI with ECore */
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

  }

  protected void setNewMalleableParameterUserExpression(final MalleableParameter mp, final String value) {
    final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
      @Override
      protected void doExecute() {
        mp.setUserExpression(value);
        errorMP = MalleableParameterExprChecker.isValid(mp);
      }
    });
  }

  /**
   * Update the {@link Port}/{@link Delay}/{@link Parameter} {@link Expression} with the value stored in the
   * txtEpression.
   */
  void updateProperties() {
    final PictogramElement pe = getSelectedPictogramElement();

    if (pe != null) {
      EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null) {
        return;
      }
      if (bo instanceof InterfaceActor) {
        bo = ((InterfaceActor) bo).getDataPort();
      }

      if (bo instanceof MalleableParameter) {
        MalleableParameter mp = (MalleableParameter) bo;
        if (mp.getUserExpression().compareTo(this.txtExpression.getText()) != 0) {
          setNewMalleableParameterUserExpression(mp, this.txtExpression.getText());
          getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        }
      } else if (bo instanceof Parameter) {
        if ((bo instanceof ConfigInputInterface)) {
          this.lblValueObj.setText(
              "Default value is a Long Integer, only used for the computation of subsequent parameters in the GUI.");
        }
        final Parameter param = (Parameter) bo;
        if (param.getValueExpression().getExpressionAsString().compareTo(this.txtExpression.getText()) != 0) {
          setNewExpression(param, this.txtExpression.getText());
          getDiagramTypeProvider().getDiagramBehavior().refreshRenderingDecorators(pe);
        }
      } // end Parameter

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
      } else if (businessObject instanceof InterfaceActor) {
        final InterfaceActor iface = ((InterfaceActor) businessObject);
        elementName = iface.getName();
        elementValueExpression = iface.getDataPort().getPortRateExpression();
      } else {
        throw new UnsupportedOperationException();
      }

      this.lblNameObj.setText(elementName == null ? " " : elementName);
      if (elementValueExpression != null) {
        this.txtExpression.setEnabled(true);

        if (businessObject instanceof MalleableParameter) {
          final MalleableParameter mp = (MalleableParameter) businessObject;
          final String eltExprString = mp.getUserExpression();
          if (this.txtExpression.getText().compareTo(eltExprString) != 0) {
            this.txtExpression.setText(eltExprString);
          }
          if (errorMP == null) {
            // we need to check, since isValid method is called only if an update is done
            try {
              final long evaluate = mp.getExpression().evaluate();
              lblValueObj.setText(Long.toString(evaluate));
              txtExpression.setBackground(new Color(null, 255, 255, 255));
            } catch (final ExpressionEvaluationException | UnsupportedOperationException e) {
              lblValueObj
                  .setText("A malleable is a sequence of expression separated by ';'. Error : " + e.getMessage());
              txtExpression.setBackground(new Color(null, 240, 150, 150));
            }
          } else {
            lblValueObj
                .setText("A malleable is a sequence of expression separated by ';'. Error : " + errorMP.getMessage());
            txtExpression.setBackground(new Color(null, 240, 150, 150));
          }
        } else {

          final String eltExprString = elementValueExpression.getExpressionAsString();
          if (this.txtExpression.getText().compareTo(eltExprString) != 0) {
            this.txtExpression.setText(eltExprString);
          }

          try {
            // try out evaluating the expression
            final long evaluate = elementValueExpression.evaluate();

            // if evaluation went well, just write the result
            if (!(businessObject instanceof ConfigInputInterface)) {
              this.lblValueObj.setText(Long.toString(evaluate));
            }
            this.txtExpression.setBackground(new Color(null, 255, 255, 255));
          } catch (final ExpressionEvaluationException | UnsupportedOperationException e) {
            // otherwise print error message and put red background
            this.lblValueObj.setText("Error : " + e.getMessage());
            this.txtExpression.setBackground(new Color(null, 240, 150, 150));
          }
        }

        if (expressionHasFocus) {
          this.txtExpression.setFocus();
          this.txtExpression.setSelection(selelection);
        }

      }
    }
  }

}
