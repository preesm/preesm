/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

  private static final String POPUP_TITLE = "Checker of PiGraph liveness";

  @Override
  public void processPiSDF(PiGraph pigraph, IProject iProject, Shell shell) {

    PreesmLogger.getLogger().info(() -> "Checking liveness for " + pigraph.getName());

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
      MessageDialog.openInformation(shell, POPUP_TITLE, "The graph is acyclic, so always live.");
      return;
    }
    // if flat graph is not acyclic, check the srdag
    final PiGraph srdag = PiSDFToSingleRate.compute(pigraph, BRVMethod.LCM);
    final FifoCycleDetector fcdSrdag = new FifoCycleDetector(true);
    fcdSrdag.doSwitch(srdag);
    if (!fcdSrdag.cyclesDetected()) {
      MessageDialog.openInformation(shell, POPUP_TITLE,
          "The corresponding SRSDF graph is acyclic, so current PiGraph is live.");
    } else {
      MessageDialog.openInformation(shell, POPUP_TITLE,
          "The corresponding SRSDF graph contains cycles, so current PiGraph is NOT live!");
    }

  }

}
