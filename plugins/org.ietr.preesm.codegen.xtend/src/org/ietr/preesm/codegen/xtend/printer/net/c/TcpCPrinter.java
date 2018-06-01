package org.ietr.preesm.codegen.xtend.printer.net.c;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter;

/**
 *
 * @author anmorvan
 *
 */
public class TcpCPrinter extends CPrinter {

  @Override
  public String printMain(final List<Block> printerBlocks) {
    TcpCPrinter.main(new String[0]);
    return super.printMain(printerBlocks);
  }

  /**
   * 
   * 
   */
  public static void main(final String[] args) {
    // without the following class loader initialization, I get the following exception when running as Eclipse plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(TcpCPrinter.class.getClassLoader());

    // sample velocity call
    final Writer writer = new PrintWriter(new OutputStreamWriter(System.out));
    final VelocityContext context = new VelocityContext();
    context.put("name", "World");
    try {
      Velocity.evaluate(context, writer, "org.apache.velocity", "$name");
      writer.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // set back default class loader
    Thread.currentThread().setContextClassLoader(oldContextClassLoader);
  }
}
