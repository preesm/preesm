/*
 * Copyright (c) 2008, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.ietr.preesm.core.scenario.editor.timings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.editor.Messages;

/**
 * This class provides a page for the save timings as excel sheet wizard.
 * 
 * @author mpelcat
 */
public class WizardSaveExcelPage extends WizardNewFileCreationPage {

	private ExcelTimingWriter writer;

	/**
	 * Constructor for {@link WizardSaveExcelPage}.
	 */
	public WizardSaveExcelPage() {
		super("saveTimings", new StructuredSelection());

		setTitle(Messages.getString("Timings.timingExport.dialog"));
	}

	@Override
	public InputStream getInitialContents() {
		// writes graph
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		writer.write(out);
		return new ByteArrayInputStream(out.toByteArray());
	}

	/**
	 * Sets a new graph for this page.
	 * 
	 * @param graph
	 *            A {@link Graph}.
	 */
	public void setWriter(ExcelTimingWriter writer) {
		this.writer = writer;
	}

}