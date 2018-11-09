/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.dftools.workflow.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import javax.xml.transform.TransformerConfigurationException;
import org.eclipse.core.runtime.Platform;
import org.ietr.dftools.workflow.Activator;
import org.ietr.dftools.workflow.WorkflowException;
import org.osgi.framework.Bundle;

/**
 * @author mpelcat
 *
 */
public class WorkflowConverter {

  private WorkflowConverter() {
    // prevent instantiation
  }

  /**
   */
  public static void convert(final File file) throws IOException {
    final boolean isNewWorkflow = WorkflowConverter.isNewWorkflow(file);
    if (!isNewWorkflow) {

      final String inputPath = file.getCanonicalPath();
      final String outputPath = file.getCanonicalPath().replaceFirst(".workflow", "_new.workflow");

      final String pluginId = Activator.PLUGIN_ID;
      final Bundle bundle = Platform.getBundle(pluginId);
      final URL fileURL = bundle.getEntry("resources/newWorkflow.xslt");
      final File createTempFile = WorkflowConverter.extract(fileURL);
      final String xslPath = createTempFile.getAbsolutePath();

      if (!inputPath.isEmpty() && !outputPath.isEmpty() && !xslPath.isEmpty()) {
        try {
          final XsltTransformer xsltTransfo = new XsltTransformer();
          final boolean setXSLFile = xsltTransfo.setXSLFile(xslPath);
          if (setXSLFile) {
            xsltTransfo.transformFileToFile(inputPath, outputPath);
          }
        } catch (final TransformerConfigurationException e) {
          throw new WorkflowException("Could not covnert workflow", e);
        } finally {
          Files.delete(createTempFile.toPath());
        }
      }
    }
  }

  private static File extract(final URL fileURL) throws IOException {
    final File createTempFile = File.createTempFile("worklowconvert", ".xslt");
    try (final InputStream inputStream = fileURL.openStream();
        FileOutputStream outputStream = new FileOutputStream(createTempFile)) {
      final byte[] buf = new byte[1024];
      int i = 0;
      while ((i = inputStream.read(buf)) != -1) {
        outputStream.write(buf, 0, i);
      }
    }
    return createTempFile;
  }

  /**
   *
   */
  public static boolean isNewWorkflow(final File file) throws IOException {
    final FileInputStream fin = new FileInputStream(file);
    String thisLine;
    try (final BufferedReader myInput = new BufferedReader(new InputStreamReader(fin))) {
      while ((thisLine = myInput.readLine()) != null) {
        if (thisLine.contains("xmlns:dftools")) {
          return true;
        }
      }
    }
    return false;
  }
}
