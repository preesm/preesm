/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2013)
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

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.ui.scenario.editor.ExcelWriter;
import org.preesm.ui.scenario.editor.SaveAsWizard;
import org.preesm.ui.scenario.editor.utils.PreesmAlgorithmListContentProvider;

/**
 * Exporting timings in an excel sheet.
 *
 * @author mpelcat
 */
public class ExcelTimingWriter extends ExcelWriter {

  private final Scenario scenario;

  /**
   */
  public ExcelTimingWriter(final Scenario scenario) {
    super();
    this.scenario = scenario;
  }

  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {
    // no behavior by default
  }

  @Override
  public void widgetSelected(final SelectionEvent e) {

    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final SaveAsWizard wizard = new SaveAsWizard(this, "Timings");
    final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.open();
  }

  /**
   * Add timing cells to the newly created file.
   *
   * @param os
   *          the os
   */
  @Override
  public void write(final OutputStream os) {

    try {
      final WorkbookSettings ws = new WorkbookSettings();
      ws.setLocale(new Locale("en", "EN"));
      final WritableWorkbook workbook = Workbook.createWorkbook(os, ws);
      final WritableSheet sheet = workbook.createSheet("Timings", 0);

      addCells(sheet);
      workbook.write();
      workbook.close();

    } catch (final Exception e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not write timings", e);
    }
  }

  /**
   * Add timing cells to the newly created file.
   *
   * @param sheet
   *          the sheet
   * @throws PreesmException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  @Override
  protected void addCells(final WritableSheet sheet) throws FileNotFoundException, CoreException {
    if (sheet != null) {

      Integer maxOpAbscissa = 1;
      Integer maxVOrdinate = 1;

      final List<AbstractActor> vSet = PreesmAlgorithmListContentProvider.getSortedPISDFVertices(this.scenario);

      final Design design = this.scenario.getDesign();
      for (final Component opDefId : design.getOperatorComponents()) {
        for (final AbstractActor vertexName : vSet) {

          final String timing = this.scenario.getTimings().getTimingOrDefault(vertexName, opDefId);

          WritableCell opCell = (WritableCell) sheet.findCell(opDefId.getVlnv().getName());
          WritableCell vCell = (WritableCell) sheet.findCell(vertexName.getVertexPath());

          try {
            if (opCell == null) {
              opCell = new Label(maxOpAbscissa, 0, opDefId.getVlnv().getName());
              sheet.addCell(opCell);
              maxOpAbscissa++;
            }

            if (vCell == null) {
              vCell = new Label(0, maxVOrdinate, vertexName.getVertexPath());
              sheet.addCell(vCell);
              maxVOrdinate++;
            }

            WritableCell timeCell;
            timeCell = new Label(opCell.getColumn(), vCell.getRow(), timing);

            sheet.addCell(timeCell);
          } catch (final WriteException e) {
            PreesmLogger.getLogger().log(Level.WARNING, "Could not add cell", e);
          }
        }
      }
    }
  }
}
