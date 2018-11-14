/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.ui.slam;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.ietr.dftools.graphiti.model.DefaultRefinementPolicy;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.ui.DFToolsUIException;
import org.ietr.dftools.ui.util.FileUtils;

/**
 * This class extends the default refinement policy with S-LAM-specific policy.
 *
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class SlamRefinementPolicy extends DefaultRefinementPolicy {

  /**
   * Ask the user to choose an existing S-LAM file to refine the selected vertex.
   *
   * @param vertex
   *          the vertex
   * @param extension
   *          the extension
   * @return the i path
   */
  public IPath useExistingFile(final Vertex vertex, final String extension) {
    IPath filePath = null;

    if (extension.equals("slam")) {
      filePath = FileUtils.browseFiles("Please select an existing S-LAM network file:", extension);
    } else if (extension.equals("cdl")) {
      filePath = FileUtils.browseFiles("Please select an existing CDL file:", extension);
    } else {
      throw new DFToolsUIException("Unsupported file extension " + extension);
    }

    // Getting relative path
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath.toString()));

    return getRefinementValue(vertex, file);
  }

  /**
   * Asking the user for a new refinement.
   *
   * @param vertex
   *          the vertex being refined
   * @return the new refinement
   */
  @Override
  public IPath getNewRefinement(final Vertex vertex) {

    // prompts the user to choose a file
    final String message = "The selected instance can be refined by an existing "
        + "S-LAM network, by a list of S-LAM networks or by a Component Description" + "Language (CDL) file.";
    final MessageDialog dialog = new MessageDialog(null, "Set/Update Refinement", null, message, MessageDialog.QUESTION,
        new String[] { "Select network", "Select CDL file" }, 0);

    final int index = dialog.open();
    IPath newRefinement = null;

    // The user can select either a single new network or a list of networks
    if (index == 0) {
      newRefinement = useExistingFile(vertex, "slam");
    } else if (index == 1) {
      newRefinement = useExistingFile(vertex, "cdl");
    }

    return newRefinement;
  }

  @Override
  public IFile getRefinementFile(final Vertex vertex) {
    // Getting the refinement string that contains either a list of
    // refinements or a single one
    final IPath refinement = getRefinement(vertex);

    if (refinement == null) {
      return null;
    }
    IFile file = null;
    file = super.getRefinementFile(vertex);

    return file;
  }

}
