/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet, Karol Desnos

[mwipliez,jpiat,mpelcat,jnezan,mraulet,kdesnos]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.ui.scenario.editor.timings;

import java.io.FileNotFoundException;
import java.io.IOException;
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

/**
 * Exporting timings in an excel sheet
 * 
 * @author mpelcat
 */
public class ExcelTimingWriter extends ExcelWriter {

	private PreesmScenario scenario;

	public ExcelTimingWriter(PreesmScenario scenario) {
		super();
		this.scenario = scenario;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		SaveAsWizard wizard = new SaveAsWizard(this, "Timings");
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	/**
	 * Add timing cells to the newly created file
	 * 
	 * @throws IOException
	 */
	@Override
	public void write(OutputStream os) {

		try {
			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			WritableWorkbook workbook = Workbook.createWorkbook(os, ws);
			WritableSheet sheet = workbook.createSheet("Timings", 0);

			addCells(sheet);
			workbook.write();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add timing cells to the newly created file
	 * 
	 * @throws CoreException
	 */
	@Override
	protected void addCells(WritableSheet sheet) throws InvalidModelException,
			FileNotFoundException, CoreException {
		if (sheet != null) {

			Integer maxOpAbscissa = 1, maxVOrdinate = 1;

			PreesmAlgorithmListContentProvider provider = new PreesmAlgorithmListContentProvider();

			Set<String> vertexNames = new HashSet<String>();

			if (scenario.isIBSDFScenario()) {
				Set<SDFAbstractVertex> vSet = provider
						.getSortedIBSDFVertices(scenario);
				for (SDFAbstractVertex vertex : vSet)
					vertexNames.add(vertex.getName());
			} else if (scenario.isPISDFScenario()) {
				Set<AbstractActor> vSet = provider
						.getSortedPISDFVertices(scenario);
				for (AbstractActor vertex : vSet)
					vertexNames.add(vertex.getName());
			}

			for (String opDefId : scenario.getOperatorDefinitionIds()) {
				for (String vertexName : vertexNames) {

					Timing timing = scenario.getTimingManager()
							.getTimingOrDefault(vertexName, opDefId);

					WritableCell opCell = (WritableCell) sheet
							.findCell(opDefId), vCell = (WritableCell) sheet
							.findCell(vertexName);

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
							long time = timing.getTime();
							timeCell = new Number(opCell.getColumn(),
									vCell.getRow(), time);
						} else {
							String time = timing.getStringValue();
							timeCell = new Label(opCell.getColumn(),
									vCell.getRow(), time);
						}

						sheet.addCell(timeCell);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
