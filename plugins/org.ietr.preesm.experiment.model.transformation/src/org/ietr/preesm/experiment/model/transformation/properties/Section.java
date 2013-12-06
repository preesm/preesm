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
package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

/**
 * @author Romina Racca
 * @author jheulot
 *
 */
public class Section extends GFPropertySection implements ITabbedPropertyConstants{
	
	private CLabel lblName;
	private CLabel lblNameObj;
	private CLabel lblExpression;
	private CLabel lblValue;
	private CLabel lblValueObj;

	private final int FIRST_COLUMN_WIDTH = 125;
	
	/**
	 * A text expression can be as an expression: value numbers, trigonometric functions,
	 *  expression of condition "if (cond, true value, false value)"
	 */
	private Text txtExpression;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

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
		
		txtExpression.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == 13){ //If you press the enter key
					updateProperties();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		
		txtExpression.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				updateProperties();
			}

			@Override
			public void focusGained(FocusEvent e) {}
		});
		
	}
	
	/**
	 * Update the {@link Port}/{@link Delay}/{@link Parameter} {@link Expression} 
	 * with the value stored in the txtEpression 
	 */
	private void updateProperties(){
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
			
			if(bo instanceof Parameter){
				Parameter param = (Parameter) bo;
				setNewExpression(param.getExpression(), txtExpression.getText());
				getDiagramEditor().refreshRenderingDecorators(pe);
			}//end Parameter

			if(bo instanceof OutputPort){
				OutputPort oPort = (OutputPort) bo;
				setNewExpression(oPort.getExpression(), txtExpression.getText());
				getDiagramEditor().refreshRenderingDecorators((PictogramElement)(pe.eContainer()));
			}//end OutputPort
			
			if(bo instanceof InputPort){
				InputPort iPort = (InputPort) bo;
				setNewExpression(iPort.getExpression(), txtExpression.getText());
				getDiagramEditor().refreshRenderingDecorators((PictogramElement)(pe.eContainer()));
			}//end InputPort
			
			if(bo instanceof Delay){
				Delay delay = (Delay) bo;
				setNewExpression(delay.getExpression(), txtExpression.getText());
				getDiagramEditor().refreshRenderingDecorators(pe);
			}//end Delay
		}
		
		refresh();
	}

	/**
	 * Safely set an {@link Expression} with the given value.
	 * @param e 		{@link Expression} to set
	 * @param value		String value
	 */
	private void setNewExpression(final Expression e, final String value){
		TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramEditor().getEditingDomain(); 
		editingDomain.getCommandStack().execute(
					new RecordingCommand(editingDomain) {
						
						@Override
						protected void doExecute() {
							e.setString(value);
						}
					}
				);
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		String name = null;
		Expression e = null;
		txtExpression.setEnabled(false);
		
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
		
			if (bo instanceof Parameter){	
				name = ((Parameter) bo).getName();											
				e = ((Parameter) bo).getExpression();
			}//end Parameter
			
			if(bo instanceof OutputPort){
				OutputPort oPort = ((OutputPort) bo);
				
				if(oPort.eContainer() instanceof SourceInterface){
					name = ((SourceInterface) oPort.eContainer()).getName();					
				}else{
					name = oPort.getName();
				}

				e = oPort.getExpression();
			}//end OutputPort
			
			
			if(bo instanceof InputPort){
				InputPort iPort = ((InputPort) bo);
				
				if(iPort.eContainer() instanceof SinkInterface){
					name = ((SinkInterface) iPort.eContainer()).getName();					
				}else{
					name = iPort.getName();
				}

				e = iPort.getExpression();
				
			}//end InputPort
			
			if(bo instanceof InterfaceActor){
				InterfaceActor iface = ((InterfaceActor) bo);
				name = iface.getName();
							
				if(iface instanceof SourceInterface){
					e = iface.getOutputPorts().get(0).getExpression();
				}else if(iface instanceof SinkInterface){
					e = iface.getInputPorts().get(0).getExpression();
				}else{
					e = null;
				}
								
			}//end InterfaceActor

			if(bo instanceof Delay){
				if(((Delay) bo).eContainer() instanceof Fifo){
					Fifo fifo = (Fifo) ((Delay) bo).eContainer();					
					name = fifo.getId();									
					e = fifo.getDelay().getExpression();
				}
			}//end Delay
			

			lblNameObj.setText(name == null ? " " : name);
			if( e != null){
				if(!(bo instanceof InterfaceActor))
					txtExpression.setEnabled(true);
				
				txtExpression.setText(e.getString());
							
				lblValueObj.setText(e.evaluate());
			}
		}
	}
}	
