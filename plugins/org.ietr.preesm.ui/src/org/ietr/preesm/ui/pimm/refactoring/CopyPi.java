/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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
package org.ietr.preesm.ui.pimm.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyArguments;
import org.eclipse.ltk.core.refactoring.participants.CopyParticipant;

public class CopyPi extends CopyParticipant {

	IFile refactored;

	@Override
	protected boolean initialize(Object element) {
		// Get the destination folder.
		IFolder destinationFolder = (IFolder) getArguments().getDestination();
		IFolder sourceFolder = (IFolder) ((IFile) element).getParent();

		// Will participate if the destination folder and the source are
		// identical
		if (destinationFolder.equals(sourceFolder)) {
			refactored = (IFile) element;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return "Copy and Rename a .pi File";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		// Nothing to do here
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		// Make arguments accessible to CompositeChange method.
		final CopyArguments currentArgs = this.getArguments();

		// Create the a composite change that will automatically adapt itself
		// to the new name of the copied file.
		CompositeChange comp = new CompositeChange(getName()) {

			final CopyArguments copyArgs = currentArgs;

			@Override
			public Change perform(IProgressMonitor pm) throws CoreException {
				// Get the new name of the copied file.
				String oldFileName = refactored.getName();
				String newFileName = copyArgs.getExecutionLog().getNewName(
						refactored);

				// Check that both names end with extension ".pi"
				// and remove both extensions
				final String extension = ".pi";
				String newName;
				String oldName;
				if (oldFileName.endsWith(extension) && newFileName != null
						&& newFileName.endsWith(extension)) {
					oldName = oldFileName.substring(0, oldFileName.length()
							- extension.length());
					newName = newFileName.substring(0, newFileName.length()
							- extension.length());
				} else {
					return null;
				}

				// Get the new file
				IFile newFile = ((IFolder) copyArgs.getDestination())
						.getFile(newFileName);
				if (newFile == null) {
					return null;
				}

				TextFileChange change = RefactoringHelper.createChange(
						"(<data key=\"name\">)(" + oldName + ")(</data>)", 2,
						newName, newFile);

				if (change != null) {
					this.add(change);
				}
				return super.perform(pm);
			}
		};

		return comp;
	}

}
