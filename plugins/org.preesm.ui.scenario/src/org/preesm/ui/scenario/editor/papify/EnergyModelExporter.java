/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.ui.scenario.editor.papify;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import jxl.write.WritableSheet;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.ui.scenario.editor.ExcelWriter;
import org.preesm.ui.scenario.editor.SaveAsWizard;

/**
 * Exporting energy model based on PAPIFY.
 *
 * @author mpelcat
 */
public class EnergyModelExporter extends ExcelWriter {

  private final Scenario       scenario;
  private final Set<PapiEvent> papiEvents;

  /**
   */
  public EnergyModelExporter(final Scenario scenario) {
    super();
    this.scenario = scenario;
    this.papiEvents = new LinkedHashSet<>();
  }

  @Override
  public void widgetDefaultSelected(final SelectionEvent e) {
    // no behavior by default
  }

  @Override
  public void widgetSelected(final SelectionEvent e) {

    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final SaveAsWizard wizard = new SaveAsWizard(this, "EnergyModel");
    final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.open();
  }

  /**
   * Add energy cells to the newly created file.
   *
   * @param os
   *          the os
   */
  @Override
  public void write(final OutputStream os) {
    try {
      os.write(addFirstLine().getBytes());
      os.write(addModels().getBytes());
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Could not write energy model", e);
    }
  }

  /**
   * Add header to the newly created file.
   *
   */
  protected String addFirstLine() {
    String text = " ";
    for (final Entry<Component, EMap<PapiEvent, Double>> models : this.scenario.getPapifyConfig()
        .getPapifyEnergyKPIModels()) {
      for (final Entry<PapiEvent, Double> params : models.getValue()) {
        if (!papiEvents.contains(params.getKey())) {
          this.papiEvents.add(params.getKey());
        }
      }
    }
    for (final PapiEvent event : this.papiEvents) {
      text = text.concat(",").concat(event.getName());
    }
    text = text.concat("\n");
    return text;
  }

  /**
   * Add models to the newly created file.
   *
   */
  protected String addModels() {
    String text = "";
    for (final Entry<Component, EMap<PapiEvent, Double>> models : this.scenario.getPapifyConfig()
        .getPapifyEnergyKPIModels()) {
      String modelPart = "";
      for (final PapiEvent event : this.papiEvents) {
        if (models.getValue().containsKey(event)) {
          modelPart = modelPart.concat(",").concat(models.getValue().get(event).toString());
        } else {
          modelPart = modelPart.concat(",");
        }
      }
      for (final ComponentInstance componentInstance : models.getKey().getInstances()) {
        text = text.concat(componentInstance.getInstanceName()).concat(modelPart).concat("\n");
      }
    }
    return text;
  }

  @Override
  protected void addCells(WritableSheet sheet) {
    // empty
  }
}
