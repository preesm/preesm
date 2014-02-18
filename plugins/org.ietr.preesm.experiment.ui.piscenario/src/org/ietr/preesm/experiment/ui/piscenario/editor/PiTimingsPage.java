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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
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
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Timing editor within the implementation editor
 * 
 * @author jheulot
 */
public class PiTimingsPage extends FormPage implements IPropertyListener {
	/**
	 * Currently edited scenario
	 */
	private PiScenario piscenario = null;
	private Section section;
	private TreeViewer treeViewer;
	private Combo comboBox;
	private String currentOperatorType;
	
	/**
	 * Table of Column name of the multi-column tree viewer
	 */
	private final String[] COLUMN_NAMES = {"Actors","Parsing","Evaluation","Variables","Expression"};
	
	/**
	 * Default Constructor of an Constraint Page
	 */	
	public PiTimingsPage(PiScenario scenario, FormEditor editor,
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
		f.setText(Messages.getString("Timings.title"));
		f.getBody().setLayout(new GridLayout());
		
		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		
		section = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), 
				Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);

		section.setText(Messages.getString("Timings.title"));
		section.setDescription(Messages.getString("Timings.description"));
		section.setLayout(new ColumnLayout());


		// Creates the section part containing the tree with SDF vertices
		
		Composite container = managedForm.getToolkit().createComposite(section);
		container.setLayout(new GridLayout());

		comboBox = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboBox.setVisibleItemCount(20);
		comboBox.setToolTipText(Messages.getString("Timings.coreSelectionTooltip"));
		comboBox.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo comboBox = (Combo) e.getSource();
				int selected = comboBox.getSelectionIndex();
				if(selected == -1){
					currentOperatorType = null;
				}else{
					currentOperatorType = comboBox.getItem(selected);
				}
				treeViewer.refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// Creating the tree view
		final Tree tree = managedForm.getToolkit().createTree(container, SWT.FULL_SELECTION);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		for(String prop : COLUMN_NAMES){
			TreeColumn col = new TreeColumn(tree, SWT.CENTER);
			col.setText(prop);
			col.setWidth(prop.length()*10);
		}
		tree.getColumn(0).setWidth(200);		
		tree.setSortColumn(tree.getColumn(0));
		tree.setSortDirection(SWT.UP);	
		
		CellEditor[] editors = new CellEditor[tree.getColumnCount()]; 
		for(int i=0; i<tree.getColumnCount(); i++){
			editors[i] = new TextCellEditor(tree);
		}
				
		treeViewer = new TreeViewer(tree);

		// The content provider fills the tree				
		treeViewer.setContentProvider(new PiActorTreeContentProvider());
		treeViewer.setLabelProvider(new PiTimingsTreeLabelProvider(this));
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);		
		treeViewer.setUseHashlookup(true);
		treeViewer.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object e1, Object e2){
				return ((ActorNode) e1).getName().compareTo(((ActorNode) e2).getName());
			}
			
		});
		treeViewer.setInput(piscenario.getActorTree());
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 250;
		treeViewer.getTree().setLayoutData(gd);	

		section.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Boolean changed = false;
				
				piscenario.update();
				treeViewer.refresh();
				
				for(String core: comboBox.getItems()){
					changed |= !piscenario.getOperatorTypes().contains(core);
				}
				changed |= !(comboBox.getItems().length == piscenario.getOperatorTypes().size());
				
				if(changed){
					comboBox.removeAll();		
					comboBox.setSize(0,0);
					for(String operatorType : piscenario.getOperatorTypes())
						comboBox.add(operatorType);
					comboBox.setSize(comboBox.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					currentOperatorType = null;
				}
			}
		});
		
		
		treeViewer.setColumnProperties(COLUMN_NAMES);
		treeViewer.setCellEditors(editors);		
		treeViewer.setCellModifier(new ICellModifier() {
			@Override
			public void modify(Object element, String property, Object value) {
				if(element instanceof TreeItem){
					ActorNode node = (ActorNode) ((TreeItem) element).getData();
					if(node.getTiming(currentOperatorType).getStringValue().contentEquals((String)value)){
						return;
					}
					node.getTiming(currentOperatorType).setStringValue((String) value);
					propertyChanged(this, IEditorPart.PROP_DIRTY);
					treeViewer.refresh();
				}
			}
			
			@Override
			public Object getValue(Object element, String property) {
				if(element instanceof ActorNode && !((ActorNode) element).isHierarchical()){
					ActorNode node = (ActorNode)element;
					if(currentOperatorType != null)
						return node.getTiming(currentOperatorType).getStringValue();
				}
				return "";
			}
			
			@Override
			public boolean canModify(Object element, String property) {
				if(property.contentEquals("Expression")){
					if(element instanceof ActorNode && !((ActorNode) element).isHierarchical()){
						if(currentOperatorType != null){
							return true;
						}
					}
				}
				return false;
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
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (source instanceof ICellModifier
				&& propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);
	}


	public String getSelectedOperatorType() {
		return currentOperatorType;
	}
}
