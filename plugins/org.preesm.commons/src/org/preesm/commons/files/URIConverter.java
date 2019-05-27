package org.preesm.commons.files;

/**
 *
 * To convert EMF URI to/from Java URI.
 *
 * Unsafe: TODO check platform/workspace URIs
 *
 * @author anmorvan
 *
 */
public class URIConverter {

  public static final java.net.URI emfToJava(final org.eclipse.emf.common.util.URI emfURI) {
    return java.net.URI.create(emfURI.toString());
  }

  public static final org.eclipse.emf.common.util.URI javaToEmf(final java.net.URI javaURI) {
    return org.eclipse.emf.common.util.URI.createURI(javaURI.toString());
  }
}
