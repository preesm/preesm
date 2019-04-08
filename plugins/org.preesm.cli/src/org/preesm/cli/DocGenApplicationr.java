package org.preesm.cli;

import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.preesm.commons.doc.generators.MarkdownPrinter;
import org.preesm.commons.logger.PreesmLogger;

/**
 *
 * @author anmorvan
 *
 */
public class DocGenApplicationr implements IApplication {

  private Options getCommandLineOptions() {
    final Options options = new Options();
    Option opt;

    opt = new Option("mdd", "markdowndoc", true, "outputs MarkDown task reference to file given as argument");
    options.addOption(opt);

    return options;
  }

  @Override
  public Object start(IApplicationContext context) throws Exception {

    final Options options = getCommandLineOptions();
    final CommandLineParser parser = new PosixParser();

    final CommandLine line = parser.parse(options,
        (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS));
    if (line.hasOption("mdd")) {
      final String mdFilePath = line.getOptionValue("mdd");
      MarkdownPrinter.prettyPrintTo(mdFilePath);
      return IApplication.EXIT_OK;
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, "Expecting -mdd <path> option");
      return IApplication.EXIT_OK;
    }
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

}
