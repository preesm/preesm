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
 * the ".pi" extension. In the rename operation, it is assumed that the
 * corresponding file with the ".diagram" extension is renamed similarly.
 * 
 * @author kdesnos
 * 
 */
public class RenamePi extends RenameParticipant {

	String oldName;
	String newName;

	@Override
	protected boolean initialize(Object element) {
		oldName = ((IFile) element).getName();
		newName = this.getArguments().getNewName();

		// Check that both names end with extension ".pi"
		// and remove both extensions
		if (oldName.endsWith(".pi") && newName.endsWith(".pi")) {
			oldName = oldName.substring(0, oldName.length() - 3);
			newName = newName.substring(0, newName.length() - 3);

			return true;

		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return "Rename .pi file";
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
		
		// Read file content
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				refactored.getContents()));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create the change
		TextFileChange change = new TextFileChange(refactored.getName(),
				refactored);
		Pattern pattern = Pattern.compile("(<data key=\"name\">)(" + oldName
				+ ")(</data>)");
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
