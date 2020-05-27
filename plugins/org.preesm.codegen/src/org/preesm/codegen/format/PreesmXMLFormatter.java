/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
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
package org.preesm.codegen.format;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFile;
import org.preesm.commons.DomUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author anmorvan
 *
 */
public class PreesmXMLFormatter {

  private PreesmXMLFormatter() {
    // forbid instantiation
  }

  /**
   *
   */
  public static final void format(final IFile iFile) {
    try {
      final File file = iFile.getRawLocation().toFile();
      final String initialContent = URLHelper.read(file.toURI().toURL());
      final String formatedContent = formatXMLContent(initialContent);
      try (final BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
        out.write(formatedContent);
        out.flush();
      }
    } catch (IOException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, "problem", e);
    }
  }

  /**
   *
   */
  private static final String formatXMLContent(final String unformattedXml) {
    final Document document = parseXmlFile(unformattedXml);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DomUtil.writeDocument(document, out);
    return out.toString();
  }

  private static final Document parseXmlFile(final String in) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(in));
      return db.parse(is);
    } catch (final ParserConfigurationException | SAXException | IOException e) {
      throw new PreesmRuntimeException(e);
    }
  }
}
