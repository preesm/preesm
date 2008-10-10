/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.constraints;

import org.eclipse.jface.viewers.AbstractTreeViewer;
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
 * Tree representing a SDF graph in the constraint page
 * 
 * @author mpelcat
 */
public class SDFTreeSection extends SectionPart {

	/**
	 * Current tree viewer initialized here
	 */
	private CheckboxTreeViewer treeviewer = null;

	/**
	 * Current section to which this section part corresponds
	 */
	private Section section = null;

	/**
	 * Currently edited scenario
	 */
	private Scenario scenario = null;

	/**
	 * Creates the tree view
	 */
	public SDFTreeSection(Scenario scenario, Section inputSection,
			FormToolkit toolkit, int style, IPropertyListener listener) {
		super(inputSection);

		this.scenario = scenario;
		this.section = inputSection;

		section.setVisible(true);
		Composite container = toolkit.createComposite(getSection());
		container.setLayout(new GridLayout());

		SDFCheckStateListener checkStateListener = new SDFCheckStateListener(
				section, scenario);
		// Creating a selector for available cores
		addCoreSelector(container, toolkit, checkStateListener);

		// Creating the tree view
		treeviewer = new CheckboxTreeViewer(toolkit.createTree(container,
				SWT.CHECK));

		// The content provider fills the tree
		SDFTreeContentProvider contentProvider = new SDFTreeContentProvider(
				treeviewer);
		treeviewer.setContentProvider(contentProvider);

		// The check state listener modifies the check status of elements
		checkStateListener.setTreeViewer(treeviewer, contentProvider, listener);
		treeviewer.setLabelProvider(new SDFLabelProvider());
		treeviewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		treeviewer.addCheckStateListener(checkStateListener);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 250;
		treeviewer.getTree().setLayoutData(gd);

		treeviewer.setUseHashlookup(true);
		treeviewer.setInput(scenario);
		toolkit.paintBordersFor(container);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		section.setClient(container);

	}

	/**
	 * Adds a combo box for the core selection
	 */
	protected void addCoreSelector(Composite parent, FormToolkit toolkit,
			SDFCheckStateListener checkStateListener) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText(Messages
				.getString("Constraints.coreSelectionTooltip"));

		IArchitecture archi = ScenarioParser.getArchitecture(scenario
				.getArchitectureURL());

		for (OperatorDefinition def : archi.getOperatorDefinitions()) {
			combo.add(def.getId());
		}

		combo.setData(archi);
		combo.addSelectionListener(checkStateListener);
	}
}
