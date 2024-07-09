/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.cli;

import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.preesm.commons.doc.generators.MarkdownPrinter;
import org.preesm.commons.logger.PreesmLogger;

/**
 *
 * @author anmorvan
 *
 */
public class DocGenApplication implements IApplication {

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
    final CommandLineParser parser = new DefaultParser();

    final CommandLine line = parser.parse(options,
        (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS));
    if (line.hasOption("mdd")) {
      final String mdFilePath = line.getOptionValue("mdd");
      MarkdownPrinter.prettyPrintTo(mdFilePath);
      return IApplication.EXIT_OK;
    }
    PreesmLogger.getLogger().log(Level.SEVERE, "Expecting -mdd <path> option");
    return IApplication.EXIT_OK;
  }

  @Override
  public void stop() {
    // nothing
  }

}
