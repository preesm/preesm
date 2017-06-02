package org.ietr.preesm.ui.pimm.checker.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ietr.dftools.ui.workflow.launch.WorkflowLaunchShortcut;
import org.ietr.preesm.ui.Activator;

/**
 *
 */
public class WorkflowRunnerPopup extends AbstractHandler {

  final Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    final TreeSelection activeMenuSelection = (TreeSelection) HandlerUtil.getActiveMenuSelection(event);
    final IFile workflowFile = (IFile) activeMenuSelection.getFirstElement();

    final ILaunchConfiguration configuration = WorkflowLaunchShortcut.createLaunchConfiguration(workflowFile);
    if (configuration != null) {
      DebugUITools.launch(configuration, "run");
    }

    return null;
  }

}
