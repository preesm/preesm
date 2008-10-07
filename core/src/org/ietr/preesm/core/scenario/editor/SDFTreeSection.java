/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.preesm.core.scenario.Scenario;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * @author mpelcat
 *
 */
public class SDFTreeSection extends SectionPart {

	private CheckboxTreeViewer treeviewer = null;
	private Section section = null;
	private Scenario scenario = null;
	
	
	public SDFTreeSection(Scenario scenario, Composite parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style);

		this.scenario = scenario;
		
		section = getSection();
		section.setVisible(true);
		
		Composite container = createClientContainer(getSection(), 2, toolkit);
		treeviewer = new CheckboxTreeViewer(toolkit.createTree(container, SWT.CHECK));

		treeviewer.setContentProvider(new SDFTreeContentProvider());
		treeviewer.setLabelProvider(new WorkbenchLabelProvider());
		treeviewer.setAutoExpandLevel(0);
		treeviewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(final CheckStateChangedEvent event) {
				final Object element = event.getElement();
				BusyIndicator.showWhile(section.getDisplay(), new Runnable() {

					public void run() {
						if (element instanceof SDFVertex) {
						} 
					}
				});
			}
		});
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.widthHint = 100;
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
	
	protected Composite createClientContainer(Composite parent, int span, FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent);
		container.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, span));
		return container;
	}
}
