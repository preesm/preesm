package org.ietr.preesm.experiment.model.filter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filtering the .pi files that do not need to be displayed in the file
 * explorer
 * @author Romina Racca
 *
 */
public class ProjectExplorerPiFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (element instanceof IFile) {

			IFile file = (IFile) element;
			String fileName = file.getName();
			if (fileName.endsWith(".pi")) {
				return false;
			}
		}

		return true;
		
	}

}
