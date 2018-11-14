/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.algorithm.exportSdf3Xml;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.preesm.model.slam.Design;

/**
 * The Class SDF3ExporterEngine.
 */
public class SDF3ExporterEngine {

  /**
   * Prints the SDF graph to SDF 3 file.
   *
   * @param sdf
   *          the sdf
   * @param scenario
   *          the scenario
   * @param architecture
   *          the architecture
   * @param path
   *          the path
   */
  public void printSDFGraphToSDF3File(final SDFGraph sdf, final PreesmScenario scenario, final Design architecture,
      IPath path) {
    // Create the exporter
    final Sdf3Printer exporter = new Sdf3Printer(sdf, scenario, architecture);

    try {
      if ((path.getFileExtension() == null) || !path.getFileExtension().equals("xml")) {
        path = path.addFileExtension("xml");
      }
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      final IFile iFile = workspace.getRoot().getFile(path);
      final File file = new File(iFile.getRawLocation().toOSString());
      final File parentFile = file.getParentFile();
      if (parentFile != null) {
        parentFile.mkdirs();
      }

      // Write the result into the text file
      exporter.write(file);
      workspace.getRoot().touch(null);

    } catch (final CoreException e) {
      throw new WorkflowException("Could not export SDF", e);
    }
  }
}
