package org.ietr.preesm.codegen.xtend.printer.net.c;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ietr.preesm.codegen.xtend.CodegenPlugin;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter;
import org.ietr.preesm.codegen.xtend.task.CodegenException;
import org.ietr.preesm.utils.files.URLResolver;

/**
 *
 * @author anmorvan
 *
 */
public class TcpCPrinter extends CPrinter {

  @Override
  public String printMain(final List<Block> printerBlocks) {
    // 0- without the following class loader initialization, I get the following exception when running as Eclipse plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(TcpCPrinter.class.getClassLoader());

    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("name", "World");

    // 3- init template reader
    final String templateLocalURL = "templates/tcp/main.c";
    final URL mainTemplate = URLResolver.findFirstInPluginList(templateLocalURL, CodegenPlugin.BUNDLE_ID);
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(mainTemplate.openStream());
    } catch (IOException e) {
      throw new CodegenException("Could not locate main template [" + templateLocalURL + "].", e);
    }

    // 4- init output writer
    StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

    // 99- set back default class loader
    Thread.currentThread().setContextClassLoader(oldContextClassLoader);

    return writer.getBuffer().toString();
  }
}
