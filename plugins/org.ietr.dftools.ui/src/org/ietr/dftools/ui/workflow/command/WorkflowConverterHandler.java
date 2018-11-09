/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.dftools.ui.workflow.command;

import java.io.File;
import java.io.IOException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.converter.WorkflowConverter;

/**
 *
 */
public class WorkflowConverterHandler extends AbstractHandler {

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    final ISelection activeSelection = HandlerUtil.getActiveMenuSelection(event);

    IFile workflowFile = null;
    if (activeSelection instanceof ITreeSelection) {
      final ITreeSelection treeSelection = (ITreeSelection) activeSelection;
      final Object firstElement = treeSelection.getFirstElement();
      if (firstElement instanceof IFile) {
        // get there when right clicking on a workflow file in the file tree explorer:
        // that is a TreeSelection and active element is an IFile
        workflowFile = (IFile) firstElement;
      }
    }

    if (workflowFile == null) {
      final String message = "Could not locate Workflow file from active selection [" + activeSelection + "] of type ["
          + activeSelection.getClass() + "]";
      throw new UnsupportedOperationException(message);
    }
    final File file = workflowFile.getLocation().toFile();
    try {
      final boolean newWorkflow = WorkflowConverter.isNewWorkflow(file);
      if (!newWorkflow) {

        WorkflowConverter.convert(file);
        workflowFile.getParent().refreshLocal(IResource.DEPTH_ONE, null);
      }
    } catch (final IOException | CoreException e) {
      throw new WorkflowException("Could not convert workflow", e);
    }

    return null;
  }

}
