package org.preesm.commons.files;

import java.net.URI;

/**
 *
 * Set of methods to help reading or writing "user" files, that is user input (algorithm, architecture, etc.) or output
 * (generated code, IR, etc.).
 *
 * To find helper methods for Preesm resources (templates, default scripts, etc.), see {@link PreesmResourcesHelper}.
 *
 * @author anmorvan
 *
 */
public class PreesmIOHelper {

  private static final PreesmIOHelper instance = new PreesmIOHelper();

  public static final PreesmIOHelper getInstance() {
    return instance;
  }

  public final URI locate() {
    // TODO
    return null;
  }
}
