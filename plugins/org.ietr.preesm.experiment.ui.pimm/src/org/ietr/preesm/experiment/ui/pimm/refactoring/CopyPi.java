package org.ietr.preesm.experiment.ui.pimm.refactoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

public class CopyPi extends CopyParticipant {
	
	IFile refactored;

	@Override
	protected boolean initialize(Object element) {
		// Get the destination folder.
		IFolder destinationFolder = (IFolder) getArguments().getDestination();
		IFolder sourceFolder = (IFolder) ((IFile) element).getParent();
				
		// Will participate if the destination folder and the source are
		// identical
		if(destinationFolder.equals(sourceFolder)){
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
				if (oldFileName.endsWith(extension) && newFileName!= null && newFileName.endsWith(extension)) {
					oldName = oldFileName.substring(0, oldFileName.length() - extension.length());
					newName = newFileName.substring(0, newFileName.length() - extension.length());
				} else {
					return null;
				}
				
				// Get the new file
				IFile newFile = ((IFolder) copyArgs.getDestination()).getFile(newFileName);
				if(newFile == null){
					return null;
				}

				// Read file content
				StringBuffer buffer = new StringBuffer();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(newFile.getContents()));
				int nbCharRead;
				char[] cbuf = new char[1024];
				try {
					while ((nbCharRead = reader.read(cbuf)) != -1) {
						buffer.append(cbuf, 0, nbCharRead);
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				TextFileChange change = new TextFileChange(
						newFile.getName(), newFile);
				Pattern pattern = Pattern.compile("(<data key=\"name\">)("
						+ oldName + ")(</data>)");
				Matcher matcher = pattern.matcher(buffer.toString());

				// If the change is applicable: apply it ! (else do nothing)
				if (matcher.find()) {
					ReplaceEdit edit = new ReplaceEdit(matcher.start(2),
							matcher.end(2) - matcher.start(2), newName);
					change.setEdit(new MultiTextEdit());
					change.addEdit(edit);
				}
				
				this.add(change);
				return super.perform(pm);
			}
		};

		return comp;
	}

}
