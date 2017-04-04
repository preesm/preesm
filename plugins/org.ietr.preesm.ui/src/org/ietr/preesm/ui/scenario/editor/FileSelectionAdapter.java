/*******************************************************************************
 * Copyright or © or Copr. 2011 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2011)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/

package org.ietr.preesm.ui.scenario.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class calls the file browser when a "browse" button is pushed
 * 
 * @author mpelcat
 */
public class FileSelectionAdapter extends SelectionAdapter {

	private Text filePath;
	private Shell shell;
	private String title;
	Set<String> fileExtensions;

	public FileSelectionAdapter(Text filePath, Shell shell, String title,
			String fileExtension) {
		super();
		this.filePath = filePath;
		this.shell = shell;
		this.title = title;
		this.fileExtensions = new HashSet<String>();
		this.fileExtensions.add(fileExtension);
	}

	public FileSelectionAdapter(Text filePath, Shell shell, String title,
			Set<String> fileExtensions) {
		super();
		this.filePath = filePath;
		this.shell = shell;
		this.title = title;
		this.fileExtensions = fileExtensions;
	}

	public FileSelectionAdapter(Text filePath, Shell shell, String title) {
		super();
		this.filePath = filePath;
		this.shell = shell;
		this.title = title;
		this.fileExtensions = null;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		filePath.setText(EditorTools.browseFiles(shell, title, fileExtensions));
	}
}
