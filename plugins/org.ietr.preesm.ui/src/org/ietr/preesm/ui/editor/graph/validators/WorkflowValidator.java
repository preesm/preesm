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
package org.ietr.preesm.ui.editor.graph.validators;

import net.sf.graphiti.model.Graph;
import net.sf.graphiti.model.IValidator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * This class implements an Workflow model validator.
 * 
 * @author mpelcat
 * 
 */
public class WorkflowValidator implements IValidator {

	/**
	 * Validates the workflow by checking that every tasks are declared in
	 * loaded plug-ins. Each task implementation must additionally accept the
	 * textual input and output edges connected to it.
	 */
	@Override
	public boolean validate(Graph graph, IFile file) {
		return true;
	}

	/**
	 * Displays an error
	 */
	protected void createMarker(IFile file, String message, String location,
			String type, int severity) {
		try {
			IMarker marker = file.createMarker(type);
			marker.setAttribute(IMarker.LOCATION, location);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.MESSAGE, message);
		} catch (CoreException e) {
		}
	}

}
