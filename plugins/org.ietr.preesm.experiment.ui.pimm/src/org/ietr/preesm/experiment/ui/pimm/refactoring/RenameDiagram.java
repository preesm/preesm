package org.ietr.preesm.experiment.ui.pimm.refactoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

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
			oldName = oldName.substring(0, oldName.length() - extension.length());
			newName = newName.substring(0, newName.length() - extension.length());

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

	public Change createPreChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {

		// Get the refactored file
		IFile refactored = (IFile) getProcessor().getElements()[0];

		// Read file content
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				refactored.getContents()));
		int nbCharRead;
		char[] cbuf = new char[1024]; 
		try {
			while ((nbCharRead = reader.read(cbuf)) != -1) {
				buffer.append(cbuf,0,nbCharRead);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create the change
		TextFileChange change = new TextFileChange(refactored.getName(),
				refactored);
		Pattern pattern = Pattern.compile("(<pi:Diagram.*?name=\")("+oldName+")(\".*?>)");
		Matcher matcher = pattern.matcher(buffer.toString());

		// If the change is applicable: apply it ! (else do nothing)
		if (matcher.find()) {
			ReplaceEdit edit = new ReplaceEdit(matcher.start(2), matcher.end(2)
					- matcher.start(2), newName);
			change.setEdit(new MultiTextEdit());
			change.addEdit(edit);
			return change;
		} else {
			return null;
		}
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// Nothing to do after the file is renamed.
		return null;
	}

}
