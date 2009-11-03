/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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
 
package org.ietr.preesm.core.scenario.editor;

import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.ScenarioWriter;
import org.ietr.preesm.core.scenario.editor.codegen.CodegenPage;
import org.ietr.preesm.core.scenario.editor.constraints.ConstraintsPage;
import org.ietr.preesm.core.scenario.editor.simu.SimulationPage;
import org.ietr.preesm.core.scenario.editor.timings.TimingsPage;
import org.ietr.preesm.core.tools.PreesmLogger;

/**
 * The scenario editor allows to change all parameters in scenario; i.e. depending
 * on both algorithm and architecture. It can be called by editing a .scenario file
 * or by creating a new file through File/New/Other/Preesm/Preesm Scenario
 * 
 * @author mpelcat
 */
public class ScenarioEditor extends SharedHeaderFormEditor implements IPropertyListener {

	boolean isDirty = false;
	
	private IFile scenarioFile = null;
	
	private Scenario scenario;
	
	public ScenarioEditor() {
		super();
	}

	/**
	 * Loading the scenario file
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		
		// Starting the console
		PreesmLogger.getLogger().createConsole();
		PreesmLogger.getLogger().setLevel(Level.INFO);
		
		setSite(site);
		setInput(input);
		setPartName(input.getName());
					
		if(input instanceof FileEditorInput){
			FileEditorInput fileInput = (FileEditorInput) input;
			scenarioFile = fileInput.getFile();
		}
		
		if(scenarioFile != null){
			scenario = new Scenario();
			ScenarioParser parser = new ScenarioParser();
			parser.parseXmlFile(scenarioFile);
			scenario = parser.parseDocument();
			
		}
	}

	/**
	 * Adding the editor pages
	 */
	@Override
	protected void addPages() {
		//this.activateSite();
		IFormPage overviewPage = new OverviewPage(scenario,this, "Overview","Overview");
		overviewPage.addPropertyListener(this);
		IFormPage constraintsPage = new ConstraintsPage(scenario,this, "Constraints","Constraints");
		constraintsPage.addPropertyListener(this);
		IFormPage timingsPage = new TimingsPage(scenario,this, "Timings","Timings");
		timingsPage.addPropertyListener(this);
		SimulationPage simulationPage = new SimulationPage(scenario,this, "Simulation","Simulation");
		simulationPage.addPropertyListener(this);
		CodegenPage codegenPage = new CodegenPage(scenario,this, "Codegen","Codegen");
		codegenPage.addPropertyListener(this);
		
		try {
			addPage(overviewPage);
			addPage(constraintsPage);
			addPage(timingsPage);
			addPage(simulationPage);
			addPage(codegenPage);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Saving the scenario
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {

		ScenarioWriter writer = new ScenarioWriter(scenario);
		writer.generateScenarioDOM();
		writer.writeDom(scenarioFile);

		isDirty = false;
		this.firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		if(propId == PROP_DIRTY){
			isDirty = true;
			this.firePropertyChange(propId);
		}
	}

}
