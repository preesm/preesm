/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Yaset Oliva <yaset.oliva@insa-rennes.fr> (2013)
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
package org.preesm.algorithm.io.xml;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.preesm.algorithm.io.gml.GMLSDFExporter;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.workflow.WorkflowException;

/**
 * The Class SDF2GraphmlExporter.
 */
public class SDF2GraphmlExporter {

  /**
   * Export.
   *
   * @param algorithm
   *          the algorithm
   * @param xmlPath
   *          the xml path
   */
  public void export(final SDFGraph algorithm, IPath xmlPath) {
    final GMLSDFExporter exporter = new GMLSDFExporter();
    if ((xmlPath.getFileExtension() == null) || !xmlPath.getFileExtension().equals("graphml")) {
      xmlPath = xmlPath.addFileExtension("graphml");
    }

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    final IFile iFile = workspace.getRoot().getFile(xmlPath);
    try {
      if (!iFile.exists()) {
        iFile.create(null, false, new NullProgressMonitor());
      }
      exporter.export(algorithm, iFile.getRawLocation().toOSString());
    } catch (final CoreException ex) {
      throw new WorkflowException("Could not export SDF graph", ex);
    }
  }
}
