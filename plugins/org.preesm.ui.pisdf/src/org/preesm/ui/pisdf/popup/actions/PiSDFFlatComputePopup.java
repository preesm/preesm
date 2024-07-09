/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Shell;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.ui.pisdf.util.SavePiGraph;

/**
 * Enables to call the PiSDF flat transformation directly from the menu.
 * 
 * @author ahonorat
 */
public class PiSDFFlatComputePopup extends AbstractGenericMultiplePiHandler {

  @Override
  public void processPiSDF(final PiGraph pigraph, final IProject iProject, final Shell shell) {

    // performs optim only if already flat
    boolean optim = pigraph.getChildrenGraphs().isEmpty();
    String message = optim ? "Computing optimized graph of " : "Computing flat graphs of ";

    PreesmLogger.getLogger().log(Level.INFO, () -> message + pigraph.getName());

    final PiGraph flatOptimized = PiSDFFlattener.flatten(pigraph, true);
    final IPath folder = SavePiGraph.save(iProject, flatOptimized, "_optimized");
    if (!optim && folder != null) {
      final PiGraph flat = PiSDFFlattener.flatten(pigraph, false);
      SavePiGraph.savePiGraphInFolder(iProject, folder, flat, "");
    }

  }

}
