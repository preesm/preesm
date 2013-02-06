package org.ietr.preesm.experiment.model.transformation.property;


//org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

public class SampleView extends AbstractPropertySection{
	//PropertySheet
	//extends ViewPart 
//	private ListViewer viewer;
	//private Group grp1;

	//@Override
	public void createPartControl(Composite parent) {
		// create all the GUI controls
		// create two groups
			//	viewer = new ListViewer(parent, SWT.SINGLE );		

					
			//	grp1 = new Group (parent,SWT.NONE);
			//	grp1.setText("Preview");		
			
			//	RowLayout rowLayout = new RowLayout();
			//	grp1.setLayout(rowLayout);
				
				//Button btn = new Button(grp1,SWT.PUSH);
				//btn.setText("Hello");
				
				// fill in the element		
				//AdaptableList ctlList = new AdaptableList();
			//	ButtonElement btnEl = new ButtonElement(btn,"Button");
			//	ctlList.add(btnEl);
				
			//	viewer.setContentProvider(new WorkbenchContentProvider());
			//	viewer.setLabelProvider(new WorkbenchLabelProvider());			
			//	viewer.setInput(ctlList);
			//	getSite().setSelectionProvider(viewer);
		
	}

	//@Override
	public void setFocus() {
		//viewer.getControl().setFocus();
		
	}

	 protected boolean isImportant(IWorkbenchPart part) {
		  if (part.getSite().getId().equals(IPageLayout.ID_PROJECT_EXPLORER))
		   return true;
		  return false;
		 }
}
