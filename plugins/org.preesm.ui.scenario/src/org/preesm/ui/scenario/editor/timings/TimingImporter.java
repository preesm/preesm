/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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
package org.preesm.ui.scenario.editor.timings;

import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.serialize.CsvActorParameterizationParser;
import org.preesm.model.scenario.serialize.CsvActorParameterizationParser.ParameterizationType;
import org.preesm.model.scenario.serialize.ExcelTimingParser;
import org.preesm.model.scenario.serialize.PapifyOutputTimingParser;
import org.preesm.model.scenario.serialize.PapifyTimingParser;
import org.preesm.model.slam.Design;

/**
 *
 */
public class TimingImporter {

  private TimingImporter() {
    // forbid instantiation
  }

  /**
   *
   */
  public static final void importTimings(final Scenario currentScenario) {
    final String excelFileURL = currentScenario.getTimings().getExcelFileURL();
    if (!excelFileURL.isEmpty()) {
      final ExcelTimingParser excelParser = new ExcelTimingParser(currentScenario);
      final CsvActorParameterizationParser csvParser = new CsvActorParameterizationParser(currentScenario,
          ParameterizationType.TIMING);
      final PapifyTimingParser papifyParser = new PapifyTimingParser(currentScenario);
      final PapifyOutputTimingParser papifyOutputParser = new PapifyOutputTimingParser(currentScenario);

      try {
        final String[] fileExt = excelFileURL.split("\\.");
        final Design design = currentScenario.getDesign();
        switch (fileExt[fileExt.length - 1]) {
          case "xls":
            excelParser.parse(excelFileURL, design.getProcessingElements());
            break;
          case "csv":
            csvParser.parse(excelFileURL, design.getProcessingElements());
            break;
          case "papify":
            papifyParser.parse(excelFileURL, design.getProcessingElements());
            break;
          default:
        }
        if (excelFileURL.endsWith("papify-output")) {
          papifyOutputParser.parse(excelFileURL, design.getProcessingElements());
        }
      } catch (final Exception e) {
        PreesmLogger.getLogger().log(Level.WARNING, "Could not import timings", e);
      }
    }
  }

}
