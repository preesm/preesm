package org.preesm.commons.files;

import java.net.URI;

/**
 *
 * Set of methods to help developers locate, load or read resources from the Preesm source code (or binary) base. This
 * is useful for load templates, test inputs, default scripts, etc.
 *
 * To find helper methods for input/output (algorithm, generated code, etc.), see {@link PreesmIOHelper}.
 *
 * @author anmorvan
 *
 */
public class PreesmResourcesHelper {

  private static final PreesmResourcesHelper instance = new PreesmResourcesHelper();

  public static final PreesmResourcesHelper getInstance() {
    return instance;
  }

  public final URI locate() {
    // TODO
    return null;
  }
}
