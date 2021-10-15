package org.preesm.ui.pisdf.popup.actions;

import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.pisdf.util.FifoCycleDetector;

/**
 * Class to provide a popup checking if a PiSDF graph is live, that is when the SRSDF is acyclic.
 * 
 * @author ahonorat
 */
public class PiSDFLivenessCheckerPopup extends AbstractGenericMultiplePiHandler {

  @Override
  public void processPiSDF(PiGraph pigraph, IProject iProject, Shell shell) {

    PreesmLogger.getLogger().log(Level.INFO, "Checking liveness for " + pigraph.getName());

    final boolean locallyStatic = pigraph.isLocallyStatic();
    if (!locallyStatic) {
      PreesmLogger.getLogger().log(Level.WARNING, "Cannot check the liveness of a dynamic graph.");
      return;
    }

    // first check if the flat graph is acyclic, in this cas all good
    final PiGraph flatOptimized = PiSDFFlattener.flatten(pigraph, true);
    final FifoCycleDetector fcdFlat = new FifoCycleDetector(true);
    fcdFlat.doSwitch(flatOptimized);
    if (!fcdFlat.cyclesDetected()) {
      MessageDialog.openInformation(shell, "Checker of PiGraph liveness", "The graph is acyclic, so always live.");
      return;
    }
    // if flat graph is not acyclic, check the srdag
    final PiGraph srdag = PiSDFToSingleRate.compute(pigraph, BRVMethod.LCM);
    final FifoCycleDetector fcdSrdag = new FifoCycleDetector(true);
    fcdSrdag.doSwitch(srdag);
    if (!fcdSrdag.cyclesDetected()) {
      MessageDialog.openInformation(shell, "Checker of PiGraph liveness",
          "The corresponding SRSDF graph is acyclic, so current PiGraph is live.");
    } else {
      MessageDialog.openInformation(shell, "Checker of PiGraph liveness",
          "The corresponding SRSDF graph contains cycles, so current PiGraph is NOT live!");
    }

  }

}
