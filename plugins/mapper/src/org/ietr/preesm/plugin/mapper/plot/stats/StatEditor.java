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
 
package org.ietr.preesm.plugin.mapper.plot.stats;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.ietr.preesm.plugin.mapper.plot.stats.overview.OverviewPage;

/**
 * The statistic editor displays statistics on the generated implementation
 * 
 * @author mpelcat
 */
public class StatEditor extends SharedHeaderFormEditor implements IPropertyListener {

	private StatGenerator statGen = null;
	
	public StatEditor() {
		super();
	}

	/**
	 * Loading the scenario file
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

			setSite(site);
			setInput(input);
			setPartName(input.getName());
			
			if(input instanceof StatEditorInput){
				StatEditorInput statinput = (StatEditorInput)input;
				this.statGen = new StatGenerator(statinput.getAbc(), statinput.getScenario(),
						statinput.getParams());
			}
			/*
		} catch (Exception e) {
			// Editor might not exist anymore if switching databases.  So
			// just close it.
			PreesmLogger.getLogger().log(Level.SEVERE,e.getMessage());
			this.getEditorSite().getPage().closeEditor(this, false);
		} */
	}

	/**
	 * Adding the editor pages
	 */
	@Override
	protected void addPages() {
		//this.activateSite();
		IFormPage ganttPage = new GanttPage(statGen,this, "Gantt","Gantt");
		IFormPage overviewPage = new OverviewPage(statGen,this, "Loads","Loads");
		overviewPage.addPropertyListener(this);
		PerformancePage performancePage = new PerformancePage(statGen,this, "Performance","Work, Span and Achieved Speedup");
		performancePage.addPropertyListener(this);
		
		try {
			addPage(ganttPage);
			addPage(overviewPage);
			//addPage(performancePage);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isDirty() {
		return false;
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
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		// TODO Auto-generated method stub
		
	}
}
