package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;


public class Section extends GFPropertySection implements ITabbedPropertyConstants{

	private Text txtName;
	private Text txtExpression;
	private Expression exp = null;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		txtName = factory.createText(composite, "");
		
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		txtName.setLayoutData(data);
		
		CLabel lblName = factory.createCLabel(composite, "Name:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtName, -HSPACE);
		data.top = new FormAttachment(txtName, 0, SWT.CENTER);
		lblName.setLayoutData(data);

		txtExpression = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(50, VSPACE);
		txtExpression.setLayoutData(data);

		CLabel lblExpression = factory.createCLabel(composite, "Expression:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtExpression, -HSPACE);
		data.top = new FormAttachment(txtExpression, 50, SWT.CENTER);
		lblExpression.setLayoutData(data);
		
		
	/*	
		txtExpression.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				exp = (Expression) txtExpression;
				System.out.println("Expression focus: "+exp.toString());
//				Parameter p = null;
//				p.setExpression(exp);
				
				PictogramElement pe = getSelectedPictogramElement();
				if (pe != null) {
					Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
					if (bo == null)
						return;
					((Parameter) bo).setExpression(exp);
					//((Parameter) bo).getExpression();
					//System.out.println("TEXT"+txtExpression.getText());
					
					txtExpression.setText(exp.toString());
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		*/
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
			
			if (bo instanceof Graph){
				String name = ((Graph) bo).getName();
				txtName.setText(name == null ? " " : name);
				txtExpression.setText(exp == null ? "Exp Null Graph" : "exp");
			}
			
			if (bo instanceof Parameter){
				String name = ((Parameter) bo).getName();
				txtName.setText(name == null ? " " : name);
				
				try{
					//if(((Parameter) bo).getExpression() == null){System.out.println("Esto es null");}
					
					
					//((Parameter) bo).getExpression().setExpressionString("2+5");
					String expresion= ((Parameter) bo).getExpression().getExpressionString();
					txtExpression.setText(exp == null ? "Exp Null Param" : expresion);
				}catch(NullPointerException e){
					System.out.println("ERROR"+e.getMessage());
				}
			}
			
			if(bo instanceof OutputPort){
				String name = ((OutputPort) bo).getName();
				txtName.setText(name == null ? " " : name);
				txtExpression.setText(exp == null ? "Exp Null Port" : "exp");
			}
		}
	}
}
