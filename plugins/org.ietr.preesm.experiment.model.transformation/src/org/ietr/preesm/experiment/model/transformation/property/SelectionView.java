package org.ietr.preesm.experiment.model.transformation.property;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

public class SelectionView extends PageBookView{


	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage messagePage = new MessagePage();
		  initPage(messagePage);
		  messagePage.setMessage("No interested in this part  blablabla");
		  messagePage.createControl(book);
		  return messagePage;
	} 
}
