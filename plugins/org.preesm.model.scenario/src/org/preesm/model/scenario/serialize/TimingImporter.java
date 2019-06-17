package org.preesm.model.scenario.serialize;

import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.utils.DesignTools;

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
        switch (fileExt[fileExt.length - 1]) {
          case "xls":
            excelParser.parse(excelFileURL, DesignTools.getOperatorComponents(currentScenario.getDesign()));
            break;
          case "csv":
            csvParser.parse(excelFileURL, DesignTools.getOperatorComponents(currentScenario.getDesign()));
            break;
          default:
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

}
