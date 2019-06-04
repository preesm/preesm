package org.preesm.model.slam.serialize;

import java.util.logging.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.Design;

/**
 *
 */
public class SlamParser {

  /**
   * Parses the slam design.
   *
   * @param url
   *          the url
   * @return the design
   */
  public static Design parseSlamDesign(final String url) {

    Design slamDesign = null;
    final ResourceSet resourceSet = new ResourceSetImpl();

    final URI uri = URI.createPlatformResourceURI(url, true);
    if ((uri.fileExtension() == null) || !uri.fileExtension().contentEquals("slam")) {
      throw new PreesmRuntimeException("Expecting .slam file");
    }
    final Resource ressource;
    try {
      ressource = resourceSet.getResource(uri, true);
      slamDesign = (Design) (ressource.getContents().get(0));
    } catch (final WrappedException e) {
      final String message = "The algorithm file \"" + uri + "\" specified by the scenario does not exist any more.";
      PreesmLogger.getLogger().log(Level.WARNING, message);
    }
    slamDesign.setUrl(url);
    return slamDesign;
  }
}
