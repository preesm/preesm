package org.ietr.preesm.core.scenario.editor;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * This class calls the file browser when a "browse" button
 * is pushed
 * 
 * @author mpelcat
 */
public class FileSelectionAdapter extends SelectionAdapter{
	
	private Text filePath;
	private Shell shell;
	private String title;
	private String fileExtension;
	
	public FileSelectionAdapter(Text filePath,Shell shell,String title,String fileExtension) {
		super();
		this.filePath = filePath;
		this.shell = shell;
		this.title = title;
		this.fileExtension = fileExtension;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		filePath.setText(EditorTools.browseFiles(shell, title, fileExtension));
	}
}
