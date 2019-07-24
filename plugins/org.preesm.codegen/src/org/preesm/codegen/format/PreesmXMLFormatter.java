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
