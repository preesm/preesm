package org.ietr.preesm.experiment.model.transformation.test;

//import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
//import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

public class ActivePartTrackerView extends PageBookView{

	@Override
	protected IPage createDefaultPage(PageBook book) {
	/*	 
		MessagePage messagePage = new MessagePage();
		  initPage(messagePage);
		  messagePage.setMessage("No interested in this part");
		  messagePage.createControl(book);
		  return messagePage;
		  */
		return null;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		/*
		MessagePage messagePage = new MessagePage();
		  initPage(messagePage);
		  messagePage.setMessage("Page for "+part.getTitle());
		  messagePage.createControl(getPageBook());
		  return new PageRec(part, messagePage);
		  */
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		//pageRecord.page.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
/*IWorkbenchPage page = getSite().getPage();
		  if(page != null) {
		   // check whether the active part is important to us
		   IWorkbenchPart activePart = page.getActivePart();
		   return isImportant(activePart)?activePart:null;
		  }
		 return null;
*/
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// return part.getSite().getPluginId().startsWith("org.eclipse.ui");
		return false;
	}

}
