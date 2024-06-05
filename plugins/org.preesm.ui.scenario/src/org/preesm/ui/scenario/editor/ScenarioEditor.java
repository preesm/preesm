/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2014)
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
package org.preesm.ui.scenario.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.preesm.commons.DomUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.serialize.ScenarioParser;
import org.preesm.model.scenario.serialize.ScenarioWriter;
import org.preesm.ui.scenario.editor.codegen.CodegenPage;
import org.preesm.ui.scenario.editor.constraints.ConstraintsPage;
import org.preesm.ui.scenario.editor.energy.EnergyPage;
import org.preesm.ui.scenario.editor.papify.PapifyPage;
import org.preesm.ui.scenario.editor.parametervalues.PiParametersPage;
import org.preesm.ui.scenario.editor.simulation.SimulationPage;
import org.preesm.ui.scenario.editor.timings.TimingsPage;
import org.preesm.ui.utils.ErrorWithExceptionDialog;
import org.w3c.dom.Document;

/**
 * The scenario editor allows to change all parameters in scenario; i.e. depending on both algorithm and architecture.
 * It can be called by editing a .scenario file or by creating a new file through File/New/Other/Preesm/Preesm Scenario
 *
 * @author mpelcat
 */
public class ScenarioEditor extends SharedHeaderFormEditor implements IPropertyListener {

  /** The is dirty. */
  boolean isDirty = false;

  /** The scenario file. */
  private IFile scenarioFile = null;

  /** The scenario. */
  private Scenario scenario;

  /**
   * Instantiates a new scenario editor.
   */
  public ScenarioEditor() {
    super();
  }

  /**
   * Loading the scenario file.
   *
   * @param site
   *          the site
   * @param input
   *          the input
   * @throws PartInitException
   *           the part init exception
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {

    setSite(site);
    setInput(input);
    setPartName(input.getName());

    if (input instanceof FileEditorInput) {
      final FileEditorInput fileInput = (FileEditorInput) input;
      this.scenarioFile = fileInput.getFile();
    }

    if (this.scenarioFile != null) {
      this.scenario = null;
      final ScenarioParser parser = new ScenarioParser();
      try {
        this.scenario = parser.parseXmlFile(this.scenarioFile);

        if (!this.scenario.getSizesAreInBit()) {
          MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
              "Scenario " + scenario.getScenarioURL() + " is out-of-date.\n",
              "The Scenario was created with an older version of PREESM."
                  + " Change the datatype sizes from bytes to bits in the Simulation tab and save the Scenario."
                  + " Check the \"Data alignment\" in Workflow tasks.");
        }
      } catch (final Exception e) {
        final String scenarioName = (this.scenario == null) ? this.scenarioFile.toString()
            : this.scenario.getScenarioURL();
        final String errorTitle = "Could not open scenario " + scenarioName;
        ErrorWithExceptionDialog.errorDialogWithStackTrace(errorTitle, e);
        throw new PartInitException(errorTitle, e);
      }
    }
  }

  /**
   * Adding the editor pages.
   */
  @Override
  protected void addPages() {
    final ScenarioPage overviewPage = new OverviewPage(this.scenario, this, "Overview", "Overview");
    final ScenarioPage constraintsPage = new ConstraintsPage(this.scenario, this, "Constraints", "Constraints");
    final ScenarioPage timingsPage = new TimingsPage(this.scenario, this, "Timings", "Timings");
    final ScenarioPage simulationPage = new SimulationPage(this.scenario, this, "Simulation", "Simulation");
    final ScenarioPage codegenPage = new CodegenPage(this.scenario, this, "Codegen", "Codegen");
    final ScenarioPage paramPage = new PiParametersPage(this.scenario, this, "Parameters", "Parameters");
    final ScenarioPage papifyPage = new PapifyPage(this.scenario, this, "PAPIFY", "PAPIFY");
    final ScenarioPage energyPage = new EnergyPage(this.scenario, this, "Energy", "Energy");

    // redraw timings when parameter value change
    paramPage.addPropertyListener(timingsPage);

    try {
      addPage(overviewPage);
      addPage(constraintsPage);
      addPage(timingsPage);
      addPage(simulationPage);
      addPage(codegenPage);
      addPage(paramPage);
      addPage(papifyPage);
      addPage(energyPage);
    } catch (final PartInitException e) {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("Could not open scenario", e);
      close(false);
    }
  }

  /**
   *
   */
  public int addPage(final ScenarioPage page) throws PartInitException {
    page.addPropertyListener(this);
    return super.addPage(page);
  }

  /**
   * Saving the scenario.
   *
   * @param monitor
   *          the monitor
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {

    final ScenarioWriter writer = new ScenarioWriter(this.scenario);
    final Document generateScenarioDOM = writer.generateScenarioDOM();
    try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
      DomUtil.writeDocument(generateScenarioDOM, byteStream);
      scenarioFile.setContents(new ByteArrayInputStream(byteStream.toByteArray()), true, false,
          new NullProgressMonitor());
    } catch (final IOException | CoreException e) {
      throw new PreesmRuntimeException(e);
    }

    this.isDirty = false;
    firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#isDirty()
   */
  @Override
  public boolean isDirty() {
    return this.isDirty;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {
    // can only save as scenario
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
   */
  @Override
  public void propertyChanged(final Object source, final int propId) {
    if (propId == IEditorPart.PROP_DIRTY) {
      this.isDirty = true;
      firePropertyChange(propId);
    }
  }
}
