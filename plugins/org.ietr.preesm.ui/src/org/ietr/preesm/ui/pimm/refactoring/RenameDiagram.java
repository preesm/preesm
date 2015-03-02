/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
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
package org.ietr.preesm.ui.pimm.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

/**
 * The purpose of this class is to handle the rename refactoring of a file with
 * the ".diagram" extension. In the rename operation, it is assumed that the
 * corresponding file with the ".pi" extension is renamed similarly.
 * 
 * @author kdesnos
 * 
 */
public class RenameDiagram extends RenameParticipant {

	String oldName;
	String newName;

	@Override
	protected boolean initialize(Object element) {
		oldName = ((IFile) element).getName();
		newName = this.getArguments().getNewName();

		// Check that both names end with extension ".diagram"
		// and remove both extensions
		final String extension = ".diagram";
		if (oldName.endsWith(extension) && newName.endsWith(extension)) {
			oldName = oldName.substring(0,
					oldName.length() - extension.length());
			newName = newName.substring(0,
					newName.length() - extension.length());

			return true;

		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return "Rename .diagram file";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		// Nothing to do here
		return null;
	}

	@Override
	public Change createPreChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		// Get the refactored file
		IFile refactored = (IFile) getProcessor().getElements()[0];

		// The regex is a combination of the two following expressions:
		// "(<pi:Diagram.*?name=\")(" + oldName + ")(\".*?>)"
		// "(<businessObjects href=\")("+ oldName + ")(.pi#.*?\"/>)"
		Change change = RefactoringHelper.createChange(
				"(<pi:Diagram.*?name=\"|<businessObjects href=\")(" + oldName
						+ ")(\".*?>|.pi#.*?\"/>)", 2, newName, refactored);

		return change;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// Nothing to do after the file is renamed.
		return null;
	}

}
