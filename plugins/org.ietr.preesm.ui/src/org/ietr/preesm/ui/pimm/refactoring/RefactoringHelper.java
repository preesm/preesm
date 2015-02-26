package org.ietr.preesm.ui.pimm.refactoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

public class RefactoringHelper {

	/**
	 * @param regex
	 *            The regular expression matched in the {@link IFile} content.
	 * @param replacedGroup
	 *            The index of the group in the regular expression that must be
	 *            replaced with the replacementString.
	 * @param replacementString
	 *            The {@link String} used to replace matched parts of the file
	 *            content.
	 * @param file
	 *            the {@link IFile} to which changes are to be applied.
	 * @return a {@link TextFileChange} if the regex was matched and text
	 *         changes were created. <code>null</code> otherwise.
	 * @throws CoreException
	 * @throws MalformedTreeException
	 */
	static protected TextFileChange createChange(String regex,
			int replacedGroup, String replacementString, IFile file)
			throws CoreException, MalformedTreeException {

		// Read file content
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				file.getContents()));
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

		TextFileChange change = new TextFileChange(file.getName(), file);
		change.setEdit(new MultiTextEdit());

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(buffer.toString());

		// If the change is applicable: apply it as many time as possible.
		while (matcher.find()) {
			ReplaceEdit edit = new ReplaceEdit(matcher.start(replacedGroup),
					matcher.end(replacedGroup) - matcher.start(replacedGroup),
					replacementString);
			change.addEdit(edit);
		}

		if (change.getEdit().hasChildren()) {
			return change;
		} else {
			return null;
		}
	}
}
