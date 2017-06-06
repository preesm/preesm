package org.ietr.preesm.ui.pimm.checker.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.ietr.dftools.ui.workflow.launch.WorkflowLaunchShortcut;
import org.ietr.preesm.ui.Activator;

/**
 *
 */
public class WorkflowRunnerPopup extends AbstractHandler {

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
    } else if (activeSelection instanceof StructuredSelection) {
      final StructuredSelection structuredSelection = (StructuredSelection) activeSelection;
      final Object firstElement = structuredSelection.getFirstElement();
      if (firstElement instanceof ITabbedPropertySheetPageContributor) {
        ITabbedPropertySheetPageContributor graph = (ITabbedPropertySheetPageContributor) firstElement;
        final String contributorId = graph.getContributorId();
        if (org.ietr.dftools.graphiti.ui.properties.PropertiesConstants.CONTRIBUTOR_ID.equals(contributorId)) {
          // get there when the active selection is within a tab with id PropertiesConstants.CONTRIBUTOR_ID from Graphiti package.
          // this is one way to make sure the command is triggered from Graphiti editor
          final IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
          final IEditorPart activeEditor = page.getActiveEditor();
          final IEditorInput input = activeEditor.getEditorInput();
          workflowFile = ResourceUtil.getFile(input);
        }
      }
    }

    if (workflowFile == null) {
      final String message = "Could not locate Workflow file from active selection [" + activeSelection + "] of type [" + activeSelection.getClass() + "]";
      throw new UnsupportedOperationException(message);
    }

    final ILaunchConfiguration configuration = WorkflowLaunchShortcut.createLaunchConfiguration(workflowFile);
    if (configuration != null) {
      DebugUITools.launch(configuration, "run");
    }

    return null;
  }
}
