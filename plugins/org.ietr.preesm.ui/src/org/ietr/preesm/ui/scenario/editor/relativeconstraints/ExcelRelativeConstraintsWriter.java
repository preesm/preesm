/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2015)
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
package org.ietr.preesm.ui.scenario.editor.relativeconstraints;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.serialize.PreesmAlgorithmListContentProvider;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.ui.scenario.editor.ExcelWriter;
import org.ietr.preesm.ui.scenario.editor.SaveAsWizard;

// TODO: Auto-generated Javadoc
/**
 * Exporting relative constraints, i.e. relationships between actor mappings, in an excel sheet
 *
 * @author mpelcat
 */
public class ExcelRelativeConstraintsWriter extends ExcelWriter {

  /** The scenario. */
  private final PreesmScenario scenario;

  /**
   * Instantiates a new excel relative constraints writer.
   *
   * @param scenario
   *          the scenario
   */
  public ExcelRelativeConstraintsWriter(final PreesmScenario scenario) {
    super();
    this.scenario = scenario;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetSelected(final SelectionEvent e) {

    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final SaveAsWizard wizard = new SaveAsWizard(this, "RelativeConstraints");
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
      final WritableSheet sheet = workbook.createSheet("RelativeConstraints", 0);

      addCells(sheet);
      workbook.write();
      workbook.close();

    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Add timing cells to the newly created file.
   *
   * @param sheet
   *          the sheet
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  @Override
  protected void addCells(final WritableSheet sheet) throws InvalidModelException, FileNotFoundException, CoreException {
    if (sheet != null) {

      int maxOpAbscissa = 1;
      int maxVOrdinate = 1;

      final PreesmAlgorithmListContentProvider provider = new PreesmAlgorithmListContentProvider();

      final Set<String> vertexNames = new HashSet<>();

      if (this.scenario.isIBSDFScenario()) {
        final Set<SDFAbstractVertex> vSet = provider.getSortedIBSDFVertices(this.scenario);
        for (final SDFAbstractVertex vertex : vSet) {
          vertexNames.add(vertex.getName());
        }
      } else if (this.scenario.isPISDFScenario()) {
        final Set<AbstractActor> vSet = provider.getSortedPISDFVertices(this.scenario);
        for (final AbstractActor vertex : vSet) {
          vertexNames.add(vertex.getName());
        }
      }

      for (final String opDefId : this.scenario.getOperatorDefinitionIds()) {
        for (final String vertexName : vertexNames) {

          final Timing timing = this.scenario.getTimingManager().getTimingOrDefault(vertexName, opDefId);

          WritableCell opCell = (WritableCell) sheet.findCell(opDefId);
          WritableCell vCell = (WritableCell) sheet.findCell(vertexName);

          try {
            if (opCell == null) {
              opCell = new Label(maxOpAbscissa, 0, opDefId);
              sheet.addCell(opCell);
              maxOpAbscissa++;
            }

            if (vCell == null) {
              vCell = new Label(0, maxVOrdinate, vertexName);
              sheet.addCell(vCell);
              maxVOrdinate++;
            }

            WritableCell timeCell;
            if (timing.isEvaluated()) {
              final long time = timing.getTime();
              timeCell = new Number(opCell.getColumn(), vCell.getRow(), time);
            } else {
              final String time = timing.getStringValue();
              timeCell = new Label(opCell.getColumn(), vCell.getRow(), time);
            }

            sheet.addCell(timeCell);
          } catch (final RowsExceededException e) {
            e.printStackTrace();
          } catch (final WriteException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
