/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.experiment.ui.piscenario.editor;

import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.experiment.core.piscenario.ActorTree;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.eclipse.swt.widgets.Tree;

/**
 * Constraint editor 
 * 
 * @author jheulot
 */
public class PiConstraintsPage extends FormPage implements IPropertyListener {
	/**
	 * Currently edited scenario
	 */
	private PiScenario piscenario = null;
	private Section section;
	private CheckboxTreeViewer treeViewer;
	private Combo comboBox;
	private String currentOperator;
	
	/**
	 * Default Constructor of an Constraint Page
	 */	
	public PiConstraintsPage(PiScenario scenario, FormEditor editor,
			String id, String title) {
		super(editor, id, title);
		this.piscenario = scenario;
	}
	

	/**
	 * Initializes the display content
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm f = managedForm.getForm();
		f.setText(Messages.getString("Constraints.title"));
		f.getBody().setLayout(new GridLayout());
		
		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		
		section = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), 
				Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);

		section.setText(Messages.getString("Constraints.title"));
		section.setDescription(Messages.getString("Constraints.description"));
		section.setLayout(new ColumnLayout());


		// Creates the section part containing the tree with SDF vertices
		
		Composite container = managedForm.getToolkit().createComposite(section);
		container.setLayout(new GridLayout());

		comboBox = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboBox.setVisibleItemCount(20);
		comboBox.setToolTipText(Messages.getString("Constraints.coreSelectionTooltip"));
		comboBox.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo comboBox = (Combo) e.getSource();
				int selected = comboBox.getSelectionIndex();
				if(selected == -1){
					currentOperator = null;
				}else{
					currentOperator = comboBox.getItem(selected);
				}
				updateCheckStates();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// Creating the tree view
		Tree tree = managedForm.getToolkit().createTree(container, SWT.CHECK);
		tree.setLinesVisible(true);
		treeViewer = new CheckboxTreeViewer(tree);

		// The content provider fills the tree				
		treeViewer.setContentProvider(new PiActorTreeContentProvider());
		treeViewer.setLabelProvider(new LabelProvider(){
			public String getText(Object element) {
				if (element instanceof ActorNode) {
					return ((ActorNode) element).getName();
				}
				return "";
			}
		});
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);		
		treeViewer.setUseHashlookup(true);
		treeViewer.setInput(piscenario.getActorTree());
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 250;
		treeViewer.getTree().setLayoutData(gd);
		
		treeViewer.addCheckStateListener(new ICheckStateListener() {			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if(currentOperator != null){
					final ActorNode node = (ActorNode)(event.getElement());
					final boolean isChecked = event.getChecked();
					node.setConstraint(currentOperator, isChecked);
					propertyChanged(this, IEditorPart.PROP_DIRTY);
					updateCheckStates();	
				}
			}
		});		
		

		section.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Boolean changed = false;
				
				piscenario.update();
				
				treeViewer.refresh();
				
				for(String core: comboBox.getItems()){
					changed |= !piscenario.getOperatorIds().contains(core);
				}
				changed |= !(comboBox.getItems().length == piscenario.getOperatorIds().size());
				
				if(changed){
					comboBox.removeAll();		
					comboBox.setSize(0,0);
					for(String operator : piscenario.getOperatorIds())
						comboBox.add(operator);
					comboBox.setSize(comboBox.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					currentOperator = null;
				}

				
			}
		});

		managedForm.getToolkit().paintBordersFor(container);
		managedForm.getToolkit().paintBordersFor(treeViewer.getTree());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		section.setClient(container);

		managedForm.refresh();
		managedForm.reflow(true);
	}
	
	/**
	 * Update the check status of the whole tree
	 */
	public void updateCheckStates() {
		if (piscenario != null && currentOperator != null) {
			// Uncheck All
			treeViewer.setSubtreeChecked(treeViewer.getInput(), false);
			
			// Check checked actors
			ActorTree actorTree = piscenario.getActorTree();			
			Set<ActorNode> checkedNodes = actorTree.getCheckedNodes(currentOperator);
			treeViewer.setCheckedElements(checkedNodes.toArray());

			treeViewer.refresh();
			treeViewer.expandAll();
		}
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (source instanceof ICheckStateListener
				&& propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);
	}
}
