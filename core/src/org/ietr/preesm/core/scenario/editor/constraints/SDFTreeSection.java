/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.constraints;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.editor.Messages;

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
		section.setLayout(createClearGridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setClient(container);

	}
	
	public static GridLayout createClearGridLayout() {
		GridLayout layout = new GridLayout();

		layout.marginHeight = 0;
		layout.marginWidth = 2;
		layout.marginTop = 2;
		layout.marginBottom = 2;
		layout.marginLeft = 2;
		layout.marginRight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 1;

		return layout;
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
