package org.preesm.commons.files;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author anmorvan
 *
 */
public class URLHelper {

  /**
   * Reads the content of the given URI. This method converts the URI to URL firsts, then open a stream to read its
   * content.
   */
  public static final String read(final URL url) throws IOException {
    final StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line + "\n");
      }
    }
    return builder.toString();
  }

}
