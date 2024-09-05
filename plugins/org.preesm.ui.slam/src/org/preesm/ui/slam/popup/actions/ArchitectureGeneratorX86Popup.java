/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2024)
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
package org.preesm.ui.slam.popup.actions;

import java.util.concurrent.ExecutionException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.preesm.model.slam.generator.ArchitecturesGenerator;
import org.preesm.ui.slam.NbCoresValidator;
import org.preesm.ui.utils.DialogUtil;
import org.preesm.ui.wizards.PreesmProjectNature;

/**
 * Provides commands to generate default architectures.
 *
 * @author ahonorat
 *
 */
public class ArchitectureGeneratorX86Popup extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      // Get the selected IProject
      final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      final TreeSelection selection = (TreeSelection) page.getSelection();
      final IProject project = (IProject) selection.getFirstElement();
      // If it is a Preesm project, generate default design in Archi/ folder
      if (project.hasNature(PreesmProjectNature.ID)) {
        final String input = DialogUtil.askString("Generate default x86 architecture file.",

            "Enter the number of cores.", "", new NbCoresValidator());
        if (input == null || input.isEmpty()) {
          return null;
        }

        final ArchitecturesGenerator generator = new ArchitecturesGenerator(project);

        generator.generateAndSaveArchitecture(Integer.parseInt(input), null);
      }

    } catch (final Exception e) {
      throw new ExecutionException("Could not generate architecture.", e);
    }
    return null;
  }
}
