/*******************************************************************************
 * Copyright or © or Copr. 2011 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2011 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.ui.scenario.editor;

import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.core.scenario.serialize.ScenarioWriter;
import org.ietr.preesm.ui.scenario.editor.codegen.CodegenPage;
import org.ietr.preesm.ui.scenario.editor.constraints.ConstraintsPage;
import org.ietr.preesm.ui.scenario.editor.parametervalues.PiParametersPage;
import org.ietr.preesm.ui.scenario.editor.relativeconstraints.RelativeConstraintsPage;
import org.ietr.preesm.ui.scenario.editor.simulation.SimulationPage;
import org.ietr.preesm.ui.scenario.editor.timings.TimingsPage;
import org.ietr.preesm.ui.scenario.editor.variables.VariablesPage;

// TODO: Auto-generated Javadoc
/**
 * The scenario editor allows to change all parameters in scenario; i.e. depending on both algorithm and architecture. It can be called by editing a .scenario
 * file or by creating a new file through File/New/Other/Preesm/Preesm Scenario
 *
 * @author mpelcat
 */
public class ScenarioEditor extends SharedHeaderFormEditor implements IPropertyListener {

  /** The is dirty. */
  boolean isDirty = false;

  /** The scenario file. */
  private IFile scenarioFile = null;

  /** The scenario. */
  private PreesmScenario scenario;

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

    // Starting the console
    WorkflowLogger.getLogger().log(Level.INFO, "");

    setSite(site);
    setInput(input);
    setPartName(input.getName());

    if (input instanceof FileEditorInput) {
      final FileEditorInput fileInput = (FileEditorInput) input;
      this.scenarioFile = fileInput.getFile();
    }

    if (this.scenarioFile != null) {
      this.scenario = new PreesmScenario();
      final ScenarioParser parser = new ScenarioParser();
      try {
        this.scenario = parser.parseXmlFile(this.scenarioFile);
      } catch (final Exception e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Adding the editor pages.
   */
  @Override
  protected void addPages() {
    // this.activateSite();
    final IFormPage overviewPage = new OverviewPage(this.scenario, this, "Overview", "Overview");
    overviewPage.addPropertyListener(this);
    final IFormPage constraintsPage = new ConstraintsPage(this.scenario, this, "Constraints", "Constraints");
    constraintsPage.addPropertyListener(this);

    final IFormPage relativeConstraintsPage = new RelativeConstraintsPage(this.scenario, this, "RelativeConstraints", "Relative Constraints");
    relativeConstraintsPage.addPropertyListener(this);
    final IFormPage timingsPage = new TimingsPage(this.scenario, this, "Timings", "Timings");
    timingsPage.addPropertyListener(this);
    final SimulationPage simulationPage = new SimulationPage(this.scenario, this, "Simulation", "Simulation");
    simulationPage.addPropertyListener(this);
    final CodegenPage codegenPage = new CodegenPage(this.scenario, this, "Codegen", "Codegen");
    codegenPage.addPropertyListener(this);
    final VariablesPage variablesPage = new VariablesPage(this.scenario, this, "Variables", "Variables");
    variablesPage.addPropertyListener(this);
    final PiParametersPage paramPage = new PiParametersPage(this.scenario, this, "Parameters", "Parameters");
    paramPage.addPropertyListener(this);

    try {
      addPage(overviewPage);
      addPage(constraintsPage);
      addPage(relativeConstraintsPage);
      addPage(timingsPage);
      addPage(simulationPage);
      addPage(codegenPage);
      addPage(variablesPage);
      addPage(paramPage);
    } catch (final PartInitException e) {
      e.printStackTrace();
    }
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
    writer.generateScenarioDOM();
    writer.writeDom(this.scenarioFile);

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
    // TODO Auto-generated method stub

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
