/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.algorithm.utils.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.preesm.algorithm.mapper.exporter.XsltTransform;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * This class provides methods to transform an XML file via XSLT.
 *
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class XsltTransformer {

  /** The transformer. */
  private Transformer transformer;

  /**
   * Creates a new {@link XsltTransform}.
   */
  public XsltTransformer() {
    super();
  }

  /**
   * Sets an XSLT stylesheet contained in the file whose name is <code>fileName</code>.
   *
   * @param fileName
   *          The XSLT stylesheet file name.
   * @return true, if successful
   * @throws TransformerConfigurationException
   *           Thrown if there are errors when parsing the Source or it is not possible to create a {@link Transformer}
   *           instance.
   */
  public boolean setXSLFile(final String fileName) throws TransformerConfigurationException {

    final TransformerFactory factory = TransformerFactory.newInstance();

    final Path xslFilePath = new Path(fileName);
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IFile xslFile = root.getFile(xslFilePath);
    final IPath path = xslFile.getLocation();
    if (path != null) {
      final String xslFileLoc = xslFile.getLocation().toOSString();
      final StreamSource source = new StreamSource(xslFileLoc);

      try {
        this.transformer = factory.newTransformer(source);
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

    if (this.transformer == null) {
      PreesmLogger.getLogger().log(Level.SEVERE, "XSL sheet not found or not valid: " + fileName);
      return false;
    }

    return true;
  }

  /**
   * Transforms the given input file and generates the output file.
   *
   * @param sourceFilePath
   *          the source file path
   * @param destFilePath
   *          the dest file path
   */
  public void transformFileToFile(final String sourceFilePath, final String destFilePath) {

    if (this.transformer != null) {
      final Path osSourceFilePath = new Path(sourceFilePath);
      final Path osDestFilePath = new Path(destFilePath);
      final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      final IFile sourceFile = root.getFile(osSourceFilePath);
      final IFile destFile = root.getFile(osDestFilePath);
      final String sourceFileLoc = sourceFile.getLocation().toOSString();
      final String destFileLoc = destFile.getLocation().toOSString();

      try {
        final FileOutputStream outStream = new FileOutputStream(destFileLoc);
        final StreamResult outResult = new StreamResult(outStream);
        this.transformer.transform(new StreamSource(sourceFileLoc), outResult);
        outStream.flush();
        outStream.close();

      } catch (final FileNotFoundException e) {
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Problem finding files for XSL transfo (" + osSourceFilePath + "," + osDestFilePath + ")");
      } catch (final TransformerException e) {
        e.printStackTrace();
      } catch (final IOException e) {
        e.printStackTrace();
      }

    }

  }

}
