package org.preesm.model.slam.serialize;

import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamPackage;

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

    // XXX ?
    final Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    Object instance = extToFactoryMap.get("slam");
    if (instance == null) {
      instance = new IPXACTResourceFactoryImpl();
      extToFactoryMap.put("slam", instance);
    }

    if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
      EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI, SlamPackage.eINSTANCE);
    }

    Design slamDesign = null;
    final ResourceSet resourceSet = new ResourceSetImpl();

    final URI uri = URI.createPlatformResourceURI(url, true);
    if ((uri.fileExtension() == null) || !uri.fileExtension().contentEquals("slam")) {
      final String message = "The architecture file \"" + uri + "\" specified by the scenario has improper extension.";
      throw new PreesmRuntimeException(message);
    }

    final Resource ressource;
    try {
      ressource = resourceSet.getResource(uri, true);
      slamDesign = (Design) (ressource.getContents().get(0));
      slamDesign.setUrl(url);
    } catch (final WrappedException e) {
      final String message = "The architecture file \"" + uri + "\" specified by the scenario does not exist.";
      throw new PreesmRuntimeException(message);
    }
    return slamDesign;
  }
}
