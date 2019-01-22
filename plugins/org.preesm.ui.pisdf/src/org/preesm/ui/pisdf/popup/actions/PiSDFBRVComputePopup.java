package org.preesm.ui.pisdf.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 *
 */
public class PiSDFBRVComputePopup extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ISelection activeSelection = HandlerUtil.getActiveMenuSelection(event);

    System.out.println(activeSelection);
    System.out.println(activeSelection.getClass());
    return null;
  }

}
