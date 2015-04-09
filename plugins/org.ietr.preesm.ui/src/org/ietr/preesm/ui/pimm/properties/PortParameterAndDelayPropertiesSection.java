/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.ui.pimm.features.SetPortMemoryAnnotationFeature;

/**
 * @author Romina Racca
 * @author jheulot
 * 
 */
public class PortParameterAndDelayPropertiesSection extends
		DataPortPropertiesUpdater implements ITabbedPropertyConstants {

	private CLabel lblName;
	private CLabel lblNameObj;
	private CLabel lblExpression;
	private CLabel lblValue;
	private CLabel lblValueObj;
	private CLabel lblAnnotation;
	private CCombo comboAnnotation;

	private final int FIRST_COLUMN_WIDTH = 200;

	/**
	 * A text expression can be as an expression: value numbers, trigonometric
	 * functions, expression of condition "if (cond, true value, false value)"
	 */
	private Text txtExpression;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		/**** NAME ****/
		lblNameObj = factory.createCLabel(composite, " ");
		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(100, 0);
		lblNameObj.setLayoutData(data);

		lblName = factory.createCLabel(composite, "Name:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lblNameObj, -HSPACE);
		lblName.setLayoutData(data);

		/**** EXPRESION ****/
		txtExpression = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lblNameObj);
		txtExpression.setLayoutData(data);
		txtExpression.setEnabled(true);

		lblExpression = factory.createCLabel(composite, "Expression:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtExpression, -HSPACE);
		data.top = new FormAttachment(lblName);
		lblExpression.setLayoutData(data);

		/**** VALUE ****/
		lblValueObj = factory.createCLabel(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(txtExpression);
		lblValueObj.setLayoutData(data);

		lblValue = factory.createCLabel(composite, "Default Value:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lblValueObj, -HSPACE);
		data.top = new FormAttachment(lblExpression);
		lblValue.setLayoutData(data);

		/**** MEMORY ANNOTATION ****/
		comboAnnotation = factory.createCCombo(composite);
		for (PortMemoryAnnotation pma : PortMemoryAnnotation.values()) {
			comboAnnotation.add(pma.toString(), pma.getValue());
		}

		data = new FormData();
		data.left = new FormAttachment(0, FIRST_COLUMN_WIDTH);
		data.right = new FormAttachment(25, 0);
		data.top = new FormAttachment(lblValueObj);
		comboAnnotation.setLayoutData(data);

		lblAnnotation = factory.createCLabel(composite, "Memory Annotation:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(comboAnnotation, -HSPACE);
		data.top = new FormAttachment(lblValue);
		lblAnnotation.setLayoutData(data);

		txtExpression.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateProperties();
			}
		});

		comboAnnotation.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				PictogramElement pes[] = new PictogramElement[1];
				pes[0] = getSelectedPictogramElement();

				CustomContext context = new CustomContext(pes);
				ICustomFeature[] setPotMemoryAnnotationFeature = getDiagramTypeProvider()
						.getFeatureProvider().getCustomFeatures(context);

				for (ICustomFeature feature : setPotMemoryAnnotationFeature) {
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
						((SetPortMemoryAnnotationFeature) feature)
								.setCurrentPMA(pma);

						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(feature, context);
						LayoutContext contextLayout = new LayoutContext(
								getSelectedPictogramElement());
						ILayoutFeature layoutFeature = getDiagramTypeProvider()
								.getFeatureProvider().getLayoutFeature(
										contextLayout);
						getDiagramTypeProvider().getDiagramBehavior()
								.executeFeature(layoutFeature, contextLayout);
					}
				}

				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	/**
	 * Update the {@link Port}/{@link Delay}/{@link Parameter}
	 * {@link Expression} with the value stored in the txtEpression
	 */
	private void updateProperties() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			EObject bo = Graphiti.getLinkService()
					.getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;

			if (bo instanceof Parameter) {
				Parameter param = (Parameter) bo;
				if (param.getExpression().getString()
						.compareTo(txtExpression.getText()) != 0) {
					setNewExpression(param.getExpression(),
							txtExpression.getText());
					getDiagramTypeProvider().getDiagramBehavior()
							.refreshRenderingDecorators(pe);
				}
			}// end Parameter

			if (bo instanceof DataPort) {
				DataPort port = (DataPort) bo;
				updateDataPortProperties(port, txtExpression);

				getDiagramTypeProvider().getDiagramBehavior()
						.refreshRenderingDecorators(
								(PictogramElement) (pe.eContainer()));
			}// end DataPort

			if (bo instanceof DataPort) {
				comboAnnotation.setEnabled(false);
				comboAnnotation.select(((DataPort) bo).getAnnotation()
						.getValue());
				comboAnnotation.setVisible(true);
				comboAnnotation.setEnabled(true);
				lblAnnotation.setEnabled(false);
				lblAnnotation.setVisible(true);
				lblAnnotation.setEnabled(true);
			} else {
				comboAnnotation.setEnabled(false);
				comboAnnotation.setVisible(false);
				comboAnnotation.setEnabled(true);
				lblAnnotation.setEnabled(false);
				lblAnnotation.setVisible(false);
				lblAnnotation.setEnabled(true);
			}

			if (bo instanceof Delay) {
				Delay delay = (Delay) bo;
				if (delay.getExpression().getString()
						.compareTo(txtExpression.getText()) != 0) {
					setNewExpression(delay.getExpression(),
							txtExpression.getText());
					getDiagramTypeProvider().getDiagramBehavior()
							.refreshRenderingDecorators(pe);
				}
			}// end Delay
		}
		refresh();
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		String name = null;
		Expression e = null;
		boolean expressionFocus = txtExpression.isFocusControl();
		Point sel = txtExpression.getSelection();
		txtExpression.setEnabled(false);

		if (pe != null) {
			Object bo = Graphiti.getLinkService()
					.getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;

			if (bo instanceof Parameter) {
				name = ((Parameter) bo).getName();
				e = ((Parameter) bo).getExpression();
			}// end Parameter

			// if (bo instanceof DataOutputPort) {
			// DataOutputPort oPort = ((DataOutputPort) bo);
			//
			// if (oPort.eContainer() instanceof DataInputInterface) {
			// name = ((DataInputInterface) oPort.eContainer()).getName();
			// } else {
			// name = oPort.getName();
			// }
			//
			// e = oPort.getExpression();
			// }// end OutputPort

			if (bo instanceof DataPort) {
				DataPort iPort = ((DataPort) bo);

				comboAnnotation.select(((DataPort) bo).getAnnotation()
						.getValue());

				if (iPort.eContainer() instanceof DataOutputInterface) {
					name = ((DataOutputInterface) iPort.eContainer()).getName();
				} else {
					name = iPort.getName();
				}

				e = iPort.getExpression();

			}// end InputPort

			if (bo instanceof InterfaceActor) {
				InterfaceActor iface = ((InterfaceActor) bo);
				name = iface.getName();

				if (iface instanceof DataInputInterface) {
					e = iface.getDataOutputPorts().get(0).getExpression();
				} else if (iface instanceof DataOutputInterface) {
					e = iface.getDataInputPorts().get(0).getExpression();
				} else {
					e = null;
				}

			}// end InterfaceActor

			if (bo instanceof Delay) {
				if (((Delay) bo).eContainer() instanceof Fifo) {
					Fifo fifo = (Fifo) ((Delay) bo).eContainer();
					name = fifo.getId();
					e = fifo.getDelay().getExpression();
				}
			}// end Delay

			lblNameObj.setText(name == null ? " " : name);
			if (e != null) {
				if (!(bo instanceof InterfaceActor))
					txtExpression.setEnabled(true);

				if (txtExpression.getText().compareTo(e.getString()) != 0) {
					txtExpression.setText(e.getString());
				}

				lblValueObj.setText(e.evaluate());

				if (expressionFocus) {
					txtExpression.setFocus();
					txtExpression.setSelection(sel);
				}
			}
		}
	}
}
