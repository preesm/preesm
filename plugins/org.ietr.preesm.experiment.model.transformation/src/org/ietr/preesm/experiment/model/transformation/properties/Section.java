package org.ietr.preesm.experiment.model.transformation.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.parser.Node;


public class Section extends GFPropertySection implements ITabbedPropertyConstants{

	private Text txtName;
	private Text txtExpression;
	private Text txtValue;

	private CLabel lblName;
	private CLabel lblExpression;
	private CLabel lblValue;
	private CLabel lblMessage;
	
	private List<String> listExpression = new ArrayList<String>();
	private List<Parameter> listParameter = new ArrayList<Parameter>();
	
	private Jep jep;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
	
		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;
		
		/** NAME **/
		txtName = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		txtName.setLayoutData(data);
		txtName.setEnabled(false);
		
		lblName = factory.createCLabel(composite, "Name:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtName, -HSPACE);
		lblName.setLayoutData(data);

		/** EXPRESION **/
		txtExpression = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(txtName);
		txtExpression.setLayoutData(data);
		txtExpression.setEnabled(true);
		
		lblExpression = factory.createCLabel(composite, "Expression:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtExpression, -HSPACE);
		data.top = new FormAttachment(lblName);
		lblExpression.setLayoutData(data);
	
		/** MESSAGE **/
		lblMessage = factory.createCLabel(composite, "Expression valid.");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lblExpression);
		lblMessage.setLayoutData(data);
		
		/** VALUE **/
		txtValue = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(lblMessage);
		txtValue.setLayoutData(data);
		
		lblValue = factory.createCLabel(composite, "Value:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtValue, -HSPACE);
		data.top = new FormAttachment(lblMessage);
		lblValue.setLayoutData(data);
		
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
						
						listParameter.clear();
						listExpression.clear();
				
						listExpression.add(((Parameter) bo).getName()+"="+((Parameter) bo).getExpression().getExpressionString());

						dependency(((Parameter) bo));
						
						String eqns[] = new String[listExpression.size()];
						
						for (int i = 1; i <= listExpression.size(); i++) {
							eqns[i-1] = listExpression.get(listExpression.size()-i);
						}
			
						try {
							
							//Parse each expression 
							Node[] nodes = new Node[eqns.length];
							for(int j=0;j<eqns.length;++j) {
								nodes[j]=jep.parse(eqns[j]);
							}
						
							//Evaluate them in turn 
							Object res = null;
							for(Node n:nodes) { 
								//jep.println(n)
								res = jep.evaluate(n);
							}
							lblMessage.setText("Expresion valida");
							txtValue.setText(res.toString() == "" || res.toString() == null ? "0" : res.toString());
							
						}catch (EvaluationException ex) {
							listExpression.clear();
							listParameter.clear();
							lblMessage.setText("Expresion no valida");
					
							txtValue.setText("");
						} catch (ParseException ex) {
							listExpression.clear();
							listParameter.clear();
							lblMessage.setText("Expresion no valida");

							txtValue.setText("");
						} 

						
					}
					if(bo instanceof OutputPort){
						((OutputPort) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());
					}
					
					if(bo instanceof InputPort){
						((InputPort) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());
					}
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
				txtName.setText(name == null ? " " : name);

				txtExpression.setEnabled(true);
				String expression = ((Parameter) bo).getExpression().getExpressionString();
				txtExpression.setText(expression == "" ? "0" : expression);
				
				listParameter.clear();
				listExpression.clear();
		
				listExpression.add(((Parameter) bo).getName()+"="+((Parameter) bo).getExpression().getExpressionString());

				dependency(((Parameter) bo));
				
				String eqns[] = new String[listExpression.size()];
				
				for (int i = 1; i <= listExpression.size(); i++) {
					eqns[i-1] = listExpression.get(listExpression.size()-i);
				}
	
				try {
					
					//Parse each expression 
					Node[] nodes = new Node[eqns.length];
					for(int j=0;j<eqns.length;++j) {
						nodes[j]=jep.parse(eqns[j]);
					}
				
					//Evaluate them in turn 
					Object res = null;
					for(Node n:nodes) { 
						//jep.println(n)
						res = jep.evaluate(n);
					}
					lblMessage.setText("Expresion valida");
					txtValue.setText(res.toString() == "" || res.toString() == null ? "0" : res.toString());
					
				}catch (EvaluationException e) {
					listExpression.clear();
					listParameter.clear();
					lblMessage.setText("Expresion no valida");

					txtValue.setText("");
				} catch (ParseException e) {
					listExpression.clear();
					listParameter.clear();
					lblMessage.setText("Expresion no valida");

					txtValue.setText("");
				} 
			}
			
			if(bo instanceof OutputPort){
				String name = "OutputPort";
				txtName.setText(name == null ? " " : name);
				
				txtExpression.setEnabled(true);
				String expression = ((OutputPort) bo).getExpression().getExpressionString();
				txtExpression.setText(expression == "" ? "0" : expression);
				
				txtValue.setText("View Interface Actor");
			}
			
			if(bo instanceof InputPort){
				String name = "InputPort";
				txtName.setText(name == null ? " " : name);
				
				txtExpression.setEnabled(true);
				String expression = ((InputPort) bo).getExpression().getExpressionString();
				txtExpression.setText(expression == "" ? "0" : expression);
				
				txtValue.setText("View Interface Actor");
			}
			
			if(bo instanceof InterfaceActor){
				List<ConfigInputPort> ports = ((InterfaceActor)bo).getConfigInputPorts();
				
				listParameter.clear();
				listExpression.clear();
				
				String name = ((InterfaceActor) bo).getName();
				txtName.setText(name == null ? " " : name);
				
				String expression;
			
				
				if(bo instanceof SourceInterface){
					expression = ((InterfaceActor) bo).getOutputPorts().get(0).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					txtExpression.setEnabled(false);
					
					listExpression.add(((InterfaceActor) bo).getName()+"="+((InterfaceActor) bo).getOutputPorts().get(0).getExpression().getExpressionString());
				}
				if(bo instanceof SinkInterface){
					expression = ((InterfaceActor) bo).getInputPorts().get(0).getExpression().getExpressionString();
					txtExpression.setText(expression == "" ? "0" : expression);
					txtExpression.setEnabled(false);
					
					listExpression.add(((InterfaceActor) bo).getName()+"="+((InterfaceActor) bo).getInputPorts().get(0).getExpression().getExpressionString());
				}
					
				for (ConfigInputPort port : ports) {
					if (port.getIncomingDependency() != null) {
						ISetter setter = port.getIncomingDependency().getSetter();
						
						Parameter myParam = ((Parameter) setter);
						listExpression.add(myParam.getName()+"="+myParam.getExpression().getExpressionString()); 
						dependency(myParam);
			
					} 
				}
				
				String eqns[] = new String[listExpression.size()];
				for (int i = 1; i <= listExpression.size(); i++) {
					if(i==1){
						eqns[0] = listExpression.get(listExpression.size()-i);
					}else{
						eqns[i-1] = listExpression.get(listExpression.size()-i);
					}
				}
	
				try {
					// Parse each expression 
					Node[] nodes = new Node[eqns.length];
					for(int j=0;j<eqns.length;++j) {
						nodes[j]=jep.parse(eqns[j]);
					}
				
					// Evaluate them in turn 
					Object res = null;
					for(Node n:nodes) { 			
						res = jep.evaluate(n);
					}
					lblMessage.setText("Expresion valida");
					txtValue.setText(res.toString() == ""  || res.toString() == null ? "0" : res.toString());
					
				}catch (EvaluationException e) {
					listExpression.clear();
					listParameter.clear();
					lblMessage.setText("Expresion no valida");
					txtValue.setText("");
				} catch (ParseException e) {
					listExpression.clear();
					listParameter.clear();
					lblMessage.setText("Expresion no valida");
					txtValue.setText("");
				} 
			} //if(bo instanceof InterfaceActor)
		}
	}
	
	public void dependency(Parameter p){
		if(!p.getConfigInputPorts().isEmpty()){ //if there is dependency...
			for (int i = 0; i < p.getConfigInputPorts().size(); i++) {
				listParameter.add((Parameter)p.getConfigInputPorts().get(i).getIncomingDependency().getSetter());
				listExpression.add(((Parameter)p.getConfigInputPorts().get(i).getIncomingDependency().getSetter()).getName()+"="+((Parameter)p.getConfigInputPorts().get(i).getIncomingDependency().getSetter()).getExpression().getExpressionString());
			}
		}
		if(!listParameter.isEmpty()){
			p = listParameter.get(0);
			listParameter.remove(0);
			dependency(p);
		}
	}
}