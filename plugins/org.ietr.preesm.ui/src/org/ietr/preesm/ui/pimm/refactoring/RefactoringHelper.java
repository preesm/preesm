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
