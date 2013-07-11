package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.emf.ecore.EObject;
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
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;

/**
 * @author Romina Racca
 *
 */
public class Section extends GFPropertySection implements ITabbedPropertyConstants{
	
	private CLabel lblName;
	private CLabel lblNameObj;
	private CLabel lblExpression;
	private CLabel lblValue;
	private CLabel lblValueObj;
	private CLabel lblMessage;
	private CLabel lblFullExpression;
	
	/**
	 * A text expression can be as an expression: value numbers, trigonometric functions,
	 *  expression of condition "if (cond, true value, false value)"
	 */
	private Text txtExpression;
	
	private Jep jep;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
	
		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		/**** NAME ****/
		lblNameObj = factory.createCLabel(composite, " ");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
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
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
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
	
		/**** FULL EXPRESSION ****/
		lblFullExpression = factory.createCLabel(composite, " ");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lblExpression);
		lblFullExpression.setLayoutData(data);
		
		
		/**** MESSAGE ****/
		lblMessage = factory.createCLabel(composite, "Expression valid.");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lblFullExpression);
		lblMessage.setLayoutData(data);
		
		/**** VALUE ****/
		lblValueObj = factory.createCLabel(composite, " ");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lblMessage);
		lblValueObj.setLayoutData(data);
		
		lblValue = factory.createCLabel(composite, "Value:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(lblValueObj, -HSPACE);
		data.top = new FormAttachment(lblMessage);
		lblValue.setLayoutData(data);
		
		
		txtExpression.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					
					PictogramElement pe = getSelectedPictogramElement();
					if (pe != null) {
						Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
						if (bo == null)
							return;
						
						if(bo instanceof Parameter){
							((Parameter) bo).getExpression().setExpressionString(txtExpression.getText());
							txtExpression.setText(txtExpression.getText());

							//The recursive function is called
							((Parameter) bo).getExpression().setAllExpression(((Parameter) bo).getName()+"="+fullExpression(((Parameter) bo)));

							evaluate(((Parameter) bo));

							lblFullExpression.setText(((Parameter) bo).getExpression().getAllExpression());
						}//Parameter

						if(bo instanceof OutputPort){
							((OutputPort) bo).getExpression().setExpressionString(txtExpression.getText());
							txtExpression.setText(txtExpression.getText());
							
							if(((OutputPort) bo).eContainer() instanceof SourceInterface){
								//The recursive function is called
								((OutputPort) bo).getExpression().setAllExpression(((SourceInterface) ((OutputPort) bo).eContainer()).getName()+"="+fullExpression(((OutputPort) bo)));
							}
							
							if(((OutputPort) bo).eContainer() instanceof Actor){
								//The recursive function is called
								((OutputPort) bo).getExpression().setAllExpression(((OutputPort) bo).getName()+"="+fullExpression(((OutputPort) bo)));
							}
							
							evaluate(((OutputPort) bo));

							lblFullExpression.setText(((OutputPort) bo).getExpression().getAllExpression());
						}//OutputPort
						
						if(bo instanceof InputPort){
							System.out.println("INPUT PORT DE FOCO");
							((InputPort) bo).getExpression().setExpressionString(txtExpression.getText());
							txtExpression.setText(txtExpression.getText());
							
							if(((InputPort) bo).eContainer() instanceof SinkInterface){
								//The recursive function is called
								((InputPort) bo).getExpression().setAllExpression(((SinkInterface) ((InputPort) bo).eContainer()).getName()+"="+fullExpression(((InputPort) bo)));
							}
							
							if(((InputPort) bo).eContainer() instanceof Actor){
								//The recursive function is called
								((InputPort) bo).getExpression().setAllExpression(((InputPort) bo).getName()+"="+fullExpression(((InputPort) bo)));
							}
							
							evaluate(((InputPort) bo));

							lblFullExpression.setText(((InputPort) bo).getExpression().getAllExpression());
						}//InputPort
						
						if(bo instanceof Delay){
							if(((Delay) bo).eContainer() instanceof Fifo){
								((Delay) bo).getExpression().setExpressionString(txtExpression.getText());
								txtExpression.setText(txtExpression.getText());
		
								//The recursive function is called
								((Delay) bo).getExpression().setAllExpression(((Fifo) ((Delay) bo).eContainer()).getId()+"="+fullExpression(((Delay) bo)));
		
								evaluate(((Delay) bo));
		
								lblFullExpression.setText(((Delay) bo).getExpression().getAllExpression());
							}
						}//Delay
					}
				}
			}
		});
		
		
		txtExpression.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			
				PictogramElement pe = getSelectedPictogramElement();
				if (pe != null) {
					Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
					if (bo == null)
						return;
					
					if(bo instanceof Parameter){
						((Parameter) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());

						//The recursive function is called
						((Parameter) bo).getExpression().setAllExpression(((Parameter) bo).getName()+"="+fullExpression(((Parameter) bo)));

						evaluate(((Parameter) bo));

						lblFullExpression.setText(((Parameter) bo).getExpression().getAllExpression());
					}//Parameter

					if(bo instanceof OutputPort){
						((OutputPort) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());
						
						if(((OutputPort) bo).eContainer() instanceof SourceInterface){
							//The recursive function is called
							((OutputPort) bo).getExpression().setAllExpression(((SourceInterface) ((OutputPort) bo).eContainer()).getName()+"="+fullExpression(((OutputPort) bo)));
						}
						
						if(((OutputPort) bo).eContainer() instanceof Actor){
							//The recursive function is called
							((OutputPort) bo).getExpression().setAllExpression(((OutputPort) bo).getName()+"="+fullExpression(((OutputPort) bo)));
						}
						
						evaluate(((OutputPort) bo));

						lblFullExpression.setText(((OutputPort) bo).getExpression().getAllExpression());
					}//OutputPort
					
					if(bo instanceof InputPort){
						System.out.println("INPUT PORT DE FOCO");
						((InputPort) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());
						
						if(((InputPort) bo).eContainer() instanceof SinkInterface){
							//The recursive function is called
							((InputPort) bo).getExpression().setAllExpression(((SinkInterface) ((InputPort) bo).eContainer()).getName()+"="+fullExpression(((InputPort) bo)));
						}
						
						if(((InputPort) bo).eContainer() instanceof Actor){
							//The recursive function is called
							((InputPort) bo).getExpression().setAllExpression(((InputPort) bo).getName()+"="+fullExpression(((InputPort) bo)));
						}
						
						evaluate(((InputPort) bo));

						lblFullExpression.setText(((InputPort) bo).getExpression().getAllExpression());
					}//InputPort
					
					if(bo instanceof Delay){
						if(((Delay) bo).eContainer() instanceof Fifo){
							((Delay) bo).getExpression().setExpressionString(txtExpression.getText());
							txtExpression.setText(txtExpression.getText());
	
							//The recursive function is called
							((Delay) bo).getExpression().setAllExpression(((Fifo) ((Delay) bo).eContainer()).getId()+"="+fullExpression(((Delay) bo)));
	
							evaluate(((Delay) bo));
	
							lblFullExpression.setText(((Delay) bo).getExpression().getAllExpression());
						}
					}//Delay
				}
			}

			@Override
			public void focusGained(FocusEvent e) {}
		});
		
	}

	
	@Override
	public void refresh() {
		jep = new Jep();
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
		
			if (bo instanceof Parameter){
				
				String name = ((Parameter) bo).getName();
				lblNameObj.setText(name == null ? " " : name);
				
				txtExpression.setEnabled(true);
				String expression = ((Parameter) bo).getExpression().getExpressionString();
				txtExpression.setText(expression == "" ? "0" : expression);
				
				//The recursive function is called
				((Parameter) bo).getExpression().setAllExpression(((Parameter) bo).getName()+"="+fullExpression(((Parameter) bo)));
				
				evaluate(((Parameter) bo));
				
				lblFullExpression.setText(((Parameter) bo).getExpression().getAllExpression());
			} // Parameter
			
			if(bo instanceof OutputPort){
				txtExpression.setEnabled(true);
				
				if(((OutputPort) bo).eContainer() instanceof SourceInterface){
					String name = ((SourceInterface) ((OutputPort) bo).eContainer()).getName();
					lblNameObj.setText(name == null ? " " : name);
					
					txtExpression.setEnabled(true);
					String expression = ((OutputPort) bo).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);

					//The recursive function is called
					((OutputPort) bo).getExpression().setAllExpression(((SourceInterface) ((OutputPort) bo).eContainer()).getName()+"="+fullExpression(((OutputPort) bo)));
					
				}else{
					String name = ((OutputPort) bo).getName();
					lblNameObj.setText(name == null ? " " : name);
				}
				
				if(((OutputPort) bo).eContainer() instanceof Actor){
					String expression = ((OutputPort) bo).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					
					//The recursive function is called
					((OutputPort) bo).getExpression().setAllExpression(((OutputPort) bo).getName()+"="+fullExpression(((OutputPort) bo)));
				}
				
				evaluate((OutputPort) bo);
				lblFullExpression.setText(((OutputPort) bo).getExpression().getAllExpression());
			}//OutputPort
			
			
			if(bo instanceof InputPort){
				txtExpression.setEnabled(true);
				
				if(((InputPort) bo).eContainer() instanceof SinkInterface){
					String name = ((SinkInterface) ((InputPort) bo).eContainer()).getName();
					lblNameObj.setText(name == null ? " " : name);
				
					txtExpression.setEnabled(true);
					String expression = ((InputPort) bo).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);

					//The recursive function is called
					((InputPort) bo).getExpression().setAllExpression(((SinkInterface) ((InputPort) bo).eContainer()).getName()+"="+fullExpression(((InputPort) bo)));
					
				}else{
					String name = ((InputPort) bo).getName();
					lblNameObj.setText(name == null ? " " : name);
				}
				
				if(((InputPort) bo).eContainer() instanceof Actor){
					String expression = ((InputPort) bo).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					
					//The recursive function is called
					((InputPort) bo).getExpression().setAllExpression(((InputPort) bo).getName()+"="+fullExpression(((InputPort) bo)));
				}
				
				evaluate((InputPort) bo);
				lblFullExpression.setText(((InputPort) bo).getExpression().getAllExpression());
				
			}//InputPort
			
			if(bo instanceof InterfaceActor){
			
				String name = ((InterfaceActor) bo).getName();
				lblNameObj.setText(name == null ? " " : name);
				
				String expression;
			
				if(bo instanceof SourceInterface){
					expression = ((InterfaceActor) bo).getOutputPorts().get(0).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					txtExpression.setEnabled(false);
					
					//The recursive function is called
					((InterfaceActor) bo).getOutputPorts().get(0).getExpression().setAllExpression(((InterfaceActor) bo).getName()+"="+fullExpression(((InterfaceActor) bo)));
				}
				if(bo instanceof SinkInterface){
					expression = ((InterfaceActor) bo).getInputPorts().get(0).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					txtExpression.setEnabled(false);
					
					//The recursive function is called
					((InterfaceActor) bo).getInputPorts().get(0).getExpression().setAllExpression(((InterfaceActor) bo).getName()+"="+fullExpression(((InterfaceActor) bo)));
				}

				evaluate(((InterfaceActor) bo));
				
				if(bo instanceof SourceInterface){
					expression = ((InterfaceActor) bo).getOutputPorts().get(0).getExpression().getAllExpression();
					lblFullExpression.setText(expression == "" ? "0" : expression);
				}
				if(bo instanceof SinkInterface){
					expression = ((InterfaceActor) bo).getInputPorts().get(0).getExpression().getAllExpression();
					lblFullExpression.setText(expression == "" ? "0" : expression);
				}
				
			}//InterfaceActor

			if(bo instanceof Delay){
				
				if(((Delay) bo).eContainer() instanceof Fifo){

					String name = ((Fifo) ((Delay) bo).eContainer()).getId();
					lblNameObj.setText(name == null ? " " : name);
					
					txtExpression.setEnabled(true);
					String expression = ((Delay) bo).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					
					//The recursive function is called
					((Delay) bo).getExpression().setAllExpression(name+"="+fullExpression(((Delay) bo)));
					
					evaluate(((Delay) bo));
					lblFullExpression.setText(((Delay) bo).getExpression().getAllExpression());
				}
			}//Delay
			
		}
	}
	
	
	/**
	 * Generates the full expression of the object passed as an argument.
	 * The full expression is formed by valid values and the direct dependencies of the object.
	 * @param obj The object that possesses an expression.
	 * @return a string that is the full expression of the object passed as an argument.
	 */
	public String fullExpression(EObject obj){
		
		if(obj instanceof Parameter){
			
			if(((Parameter) obj).getConfigInputPorts().isEmpty()){ //if there is not dependency
				return ((Parameter) obj).getExpression().getExpressionString();
			}else{ //if there is dependency...
				
				if(((Parameter) obj).getExpression().getAllExpression() == "0"){
					((Parameter) obj).getExpression().setAllExpression(((Parameter) obj).getName()+"="+((Parameter) obj).getExpression().getExpressionString());
				}
				
				String allExpression = ((Parameter) obj).getExpression().getExpressionString();
			
				for (ConfigInputPort port : ((Parameter) obj).getConfigInputPorts()) {
					if(port.getIncomingDependency().getSetter() instanceof Parameter){
						Parameter p = (Parameter) port.getIncomingDependency().getSetter();	
						
						if(((Parameter) obj).getExpression().getExpressionString().contains(p.getName())){
							allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
						}
					}
				}
				
				return allExpression;
			}
			//return ((Parameter) obj).getExpression().getExpressionString();
		}// Parameter
		
		
		if(obj instanceof OutputPort){
			
			if(obj.eContainer() instanceof SourceInterface){
				if(((SourceInterface) obj.eContainer()).getConfigInputPorts().isEmpty()){
					return ((OutputPort) obj).getExpression().getExpressionString();
				}else{
					
					if(((OutputPort) obj).getExpression().getAllExpression() == "0"){
						((OutputPort) obj).getExpression().setAllExpression(((OutputPort) obj).getName()+"="+((OutputPort) obj).getExpression().getExpressionString());
					}
					
					String allExpression = ((OutputPort) obj).getExpression().getExpressionString();
				
					for (ConfigInputPort port : ((SourceInterface) obj.eContainer()).getConfigInputPorts()) {
						if(port.getIncomingDependency().getSetter() instanceof Parameter){
							Parameter p = (Parameter) port.getIncomingDependency().getSetter(); 

							if(((OutputPort) obj).getExpression().getExpressionString().contains(p.getName())){
								allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
							}
						}
					}
					return allExpression;
				}
			}// OutputPort - SourfeInterface
			
			if(obj.eContainer() instanceof Actor){
				
				if(((Actor) obj.eContainer()).getConfigInputPorts().isEmpty()){
					return ((OutputPort) obj).getExpression().getExpressionString();
				}else{
					if(((OutputPort) obj).getExpression().getAllExpression() == "0"){
						((OutputPort) obj).getExpression().setAllExpression(((OutputPort) obj).getName()+"="+((OutputPort) obj).getExpression().getExpressionString());
					}
					
					String allExpression = ((OutputPort) obj).getExpression().getExpressionString();
					for (ConfigInputPort port : ((Actor) obj.eContainer()).getConfigInputPorts()) {
						if(port.getIncomingDependency().getSetter() instanceof Parameter){
							Parameter p = (Parameter) port.getIncomingDependency().getSetter(); 

							if(((OutputPort) obj).getExpression().getExpressionString().contains(p.getName())){
								allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
							}
						}
						
					}
					return allExpression;
				}
			} // OutputPort - Actor
			
		}// OutputPort
		
		if(obj instanceof InputPort){
			
			if(obj.eContainer() instanceof SinkInterface){
				if(((SinkInterface) obj.eContainer()).getConfigInputPorts().isEmpty()){
					return ((InputPort) obj).getExpression().getExpressionString();
				}else{
					
					if(((InputPort) obj).getExpression().getAllExpression() == "0"){
						((InputPort) obj).getExpression().setAllExpression(((InputPort) obj).getName()+"="+((InputPort) obj).getExpression().getExpressionString());
					}
					
					String allExpression = ((InputPort) obj).getExpression().getExpressionString();
					for (ConfigInputPort port : ((SinkInterface) obj.eContainer()).getConfigInputPorts()) {
						if(port.getIncomingDependency().getSetter() instanceof Parameter){
							Parameter p = (Parameter) port.getIncomingDependency().getSetter(); 

							if(((InputPort) obj).getExpression().getExpressionString().contains(p.getName())){
								allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
							}
						}
					}
					return allExpression;
				}
			}//InputPort - SourfeInterface
			
			if(obj.eContainer() instanceof Actor){
				
				if(((Actor) obj.eContainer()).getConfigInputPorts().isEmpty()){
					return ((InputPort) obj).getExpression().getExpressionString();
				}else{
					if(((InputPort) obj).getExpression().getAllExpression() == "0"){
						((InputPort) obj).getExpression().setAllExpression(((InputPort) obj).getName()+"="+((InputPort) obj).getExpression().getExpressionString());
					}
					
					String allExpression = ((InputPort) obj).getExpression().getExpressionString();
					for (ConfigInputPort port : ((Actor) obj.eContainer()).getConfigInputPorts()) {
						if(port.getIncomingDependency().getSetter() instanceof Parameter){
							Parameter p = (Parameter) port.getIncomingDependency().getSetter(); 

							if(((InputPort) obj).getExpression().getExpressionString().contains(p.getName())){
								allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
							}
						}
						
					}
					return allExpression;
				}

			}//InputPort - Actor
		}//InputPort
		
		
		if(obj instanceof InterfaceActor){
			
			if(obj instanceof SourceInterface){
				
				if(((SourceInterface) obj).getConfigInputPorts().isEmpty()){
					return ((SourceInterface) obj).getOutputPorts().get(0).getExpression().getExpressionString();
				}else{

					if(((SourceInterface) obj).getOutputPorts().get(0).getExpression().getAllExpression() == "0"){
						((SourceInterface) obj).getOutputPorts().get(0).getExpression().setAllExpression(((SourceInterface) obj).getOutputPorts().get(0).getName()+"="+((SourceInterface) obj).getOutputPorts().get(0).getExpression().getExpressionString());
					}
					
					String allExpression = ((SourceInterface) obj).getOutputPorts().get(0).getExpression().getExpressionString();
					for (ConfigInputPort port : ((SourceInterface) obj).getConfigInputPorts()) {
						if(port.getIncomingDependency().getSetter() instanceof Parameter){
							Parameter p = (Parameter) port.getIncomingDependency().getSetter(); 

							if(((SourceInterface) obj).getOutputPorts().get(0).getExpression().getExpressionString().contains(p.getName())){
								allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
							}
						}
						
					}
					return allExpression;
				}
				
			}// InterfaceActor - SourceInterface
			
			if(obj instanceof SinkInterface){

				if(((SinkInterface) obj).getConfigInputPorts().isEmpty()){
					return ((InterfaceActor) obj).getInputPorts().get(0).getExpression().getExpressionString();
				}else{

					if(((SinkInterface) obj).getInputPorts().get(0).getExpression().getAllExpression() == "0"){
						((SinkInterface) obj).getInputPorts().get(0).getExpression().setAllExpression(((SinkInterface) obj).getInputPorts().get(0).getName()+"="+((SinkInterface) obj).getInputPorts().get(0).getExpression().getExpressionString());
					}
					
					String allExpression = ((SinkInterface) obj).getInputPorts().get(0).getExpression().getExpressionString();
					for (ConfigInputPort port : ((SinkInterface) obj).getConfigInputPorts()) {
						if(port.getIncomingDependency().getSetter() instanceof Parameter){
							Parameter p = (Parameter) port.getIncomingDependency().getSetter(); 

							if(((SinkInterface) obj).getInputPorts().get(0).getExpression().getExpressionString().contains(p.getName())){
								allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
							}
						}
						
					}
					return allExpression;
				}
				
			}// InterfaceActor - SinkInterface
			
		}//InterfaceActor
		
		if(obj instanceof Delay){
			
			if(((Delay) obj).getConfigInputPorts().isEmpty()){ //if theres is not dependency
				return ((Delay) obj).getExpression().getExpressionString();
			}else{ //if there is dependency...
				
				if(((Delay) obj).getExpression().getAllExpression() == "0"){
					((Delay) obj).getExpression().setAllExpression(((Fifo) ((Delay) obj).eContainer()).getId()+"="+((Delay) obj).getExpression().getExpressionString());
				}
				
				String allExpression = ((Delay) obj).getExpression().getExpressionString();
				for (ConfigInputPort port : ((Delay) obj).getConfigInputPorts()) {
					if(port.getIncomingDependency().getSetter() instanceof Parameter){
						Parameter p = (Parameter) port.getIncomingDependency().getSetter();	
						if(((Delay) obj).getExpression().getExpressionString().contains(p.getName())){
							allExpression = allExpression.replace(p.getName(), "("+fullExpression(p)+")");
						}
					}
				}
				return allExpression;
			}

		}// Dalay
		
		
		return "error";
	}
	
	
	
	/**
	 * Parsea and evalua the expression of the EObjetc passed as argument.
	 * @param obj The object that possesses an expression to evaluate.
	 */
	public void evaluate(EObject obj){
		
		try {

			lblMessage.setText("Valid expression");

			if(obj instanceof Parameter){
				jep.parse(((Parameter) obj).getExpression().getAllExpression());
				Object res = jep.evaluate();
				lblValueObj.setText(res.toString().substring(0, res.toString().indexOf(".")));
				
				((Parameter) obj).getExpression().setValueString(String.valueOf(res));
			}//Parameter
		
			if(obj instanceof InterfaceActor){
			
				if(obj instanceof SourceInterface){
					jep.parse(((InterfaceActor) obj).getOutputPorts().get(0).getExpression().getAllExpression());
					Object res = jep.evaluate();

					lblValueObj.setText(res.toString().substring(0, res.toString().indexOf(".")));
					
					((InterfaceActor) obj).getOutputPorts().get(0).getExpression().setValueString(String.valueOf(res));
					
				}//InterfaceActor - SourceInterface
			
				if(obj instanceof SinkInterface){
					jep.parse(((InterfaceActor) obj).getInputPorts().get(0).getExpression().getAllExpression());
					Object res = jep.evaluate();

					lblValueObj.setText(res.toString().substring(0, res.toString().indexOf(".")));
					
					((InterfaceActor) obj).getInputPorts().get(0).getExpression().setValueString(String.valueOf(res));
				}//InterfaceActor - SinkInterface
				
			} //InterfaceActor
			 		
			if(obj instanceof OutputPort){
				
				jep.parse(((OutputPort) obj).getExpression().getAllExpression());
				Object res = jep.evaluate();
				lblValueObj.setText(res.toString().substring(0, res.toString().indexOf(".")));
				
				((OutputPort) obj).getExpression().setValueString(String.valueOf(res));
				
			} //OutputPort

			
			if(obj instanceof InputPort){
				jep.parse(((InputPort) obj).getExpression().getAllExpression());
				Object res = jep.evaluate();
				lblValueObj.setText(res.toString().substring(0, res.toString().indexOf(".")));
				
				((InputPort) obj).getExpression().setValueString(String.valueOf(res));
				
			} //InputPort
			
			if(obj instanceof Delay){
				jep.parse(((Delay) obj).getExpression().getAllExpression().substring(((Delay) obj).getExpression().getAllExpression().indexOf("=")+1));
				Object res = jep.evaluate();
				System.out.println("RES: "+res.toString());
				lblValueObj.setText(res.toString().substring(0, res.toString().indexOf(".")));
				
				((Delay) obj).getExpression().setValueString(String.valueOf(res));
			} //Delay

		}catch (EvaluationException e) {
			lblMessage.setText("Not valid expression");
			lblValueObj.setText("-");
		} catch (ParseException e) {
			lblMessage.setText("Not valid expression");
			lblValueObj.setText("-");
		} 
	}
}