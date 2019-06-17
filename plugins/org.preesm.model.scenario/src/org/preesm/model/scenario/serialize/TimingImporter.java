package org.preesm.model.scenario.serialize;

import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 *
 */
public class TimingImporter {

  /**
   *
   */
  public static final void importTimings(final Scenario currentScenario) {
    final String excelFileURL = currentScenario.getTimings().getExcelFileURL();
    if (!excelFileURL.isEmpty()) {
      final ExcelTimingParser excelParser = new ExcelTimingParser(currentScenario);
      final CsvTimingParser csvParser = new CsvTimingParser(currentScenario);

      try {
        final String[] fileExt = excelFileURL.split("\\.");
        final Design design = currentScenario.getDesign();
        switch (fileExt[fileExt.length - 1]) {
          case "xls":
            excelParser.parse(excelFileURL, design.getComponents());
            break;
          case "csv":
            csvParser.parse(excelFileURL, design.getComponents());
            break;
          default:
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

}
