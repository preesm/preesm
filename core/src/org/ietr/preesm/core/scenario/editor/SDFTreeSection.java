/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import javax.swing.ComboBoxEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.layout.CellLayout;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Tree representing a SDF graph
 * 
 * @author mpelcat
 */
public class SDFTreeSection extends SectionPart {

	private CheckboxTreeViewer treeviewer = null;
	private Section section = null;
	private Scenario scenario = null;
	private IPropertyListener listener = null;
	
	public SDFTreeSection(Scenario scenario, Section inputSection, FormToolkit toolkit, int style, IPropertyListener listener) {
		super(inputSection);
		
		this.scenario = scenario;
		this.section = inputSection;
		this.listener = listener;
		
		section.setVisible(true);
		Composite container = createClientContainer(getSection(), 2, toolkit);

		SDFCheckStateListener checkStateListener = new SDFCheckStateListener(section,scenario);
		// Creating a selector for available cores
		addCoreSelector(container,toolkit,checkStateListener);

		treeviewer = new CheckboxTreeViewer(toolkit.createTree(container, SWT.CHECK));
		SDFTreeContentProvider contentProvider = new SDFTreeContentProvider(treeviewer);
		treeviewer.setContentProvider(contentProvider);
		checkStateListener.setTreeViewer(treeviewer,contentProvider,listener);
		treeviewer.setLabelProvider(new SDFLabelProvider());
		treeviewer.setAutoExpandLevel(treeviewer.ALL_LEVELS);
		
		treeviewer.addCheckStateListener(checkStateListener);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 250;
		treeviewer.getTree().setLayoutData(gd);
		
		initialize();
		toolkit.paintBordersFor(container);
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setClient(container);

	}
	
	public void initialize() {
		if (treeviewer.getInput() == null) {
			treeviewer.setUseHashlookup(true);
			treeviewer.setInput(scenario);
		}
	}
	
	protected void addCoreSelector(Composite parent, FormToolkit toolkit,SDFCheckStateListener checkStateListener) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps,SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText(Messages.getString("Constraints.coreSelectionTooltip"));
		
		IArchitecture archi = ScenarioParser.getArchitecture(scenario.getArchitectureURL());
		
		for(OperatorDefinition def:archi.getOperatorDefinitions()){
			combo.add(def.getId());
		}
		
		combo.setData(archi);
		combo.addSelectionListener(checkStateListener);
	}
	
	protected Composite createClientContainer(Composite parent, int span, FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent);
		container.setLayout(new GridLayout());
		return container;
	}
}
