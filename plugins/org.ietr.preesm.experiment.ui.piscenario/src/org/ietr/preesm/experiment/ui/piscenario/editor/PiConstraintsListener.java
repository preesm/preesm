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

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.experiment.core.piscenario.Constraints;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;

/**
 * Listener of the check state of the Constraints tree but also of the selection
 * modification of the current core definition. It updates the check state of
 * the vertices depending on {@link Constraints} in the {@link PiScenario}
 * 
 * @author jheulot
 */
public class PiConstraintsListener implements SelectionListener, ICheckStateListener, PaintListener {

	/**
	 * Currently edited scenario
	 */
	private PiScenario piscenario = null;

	/**
	 * Current operator
	 */
	private String currentOpId = null;

	/**
	 * Current section (necessary to diplay busy status)
	 */
	private Section section = null;

	/**
	 * Tree viewer used to set the checked status
	 */
	private CheckboxTreeViewer treeViewer = null;

	/**
	 * Constraints page used as a property listener to change the dirty state
	 */
	private IPropertyListener propertyListener = null;

	public PiConstraintsListener(Section section, PiScenario scenario) {
		super();
		this.piscenario = scenario;
		this.section = section;
	}

	/**
	 * Sets the different necessary attributes
	 */
	public void setTreeViewer(CheckboxTreeViewer treeViewer,
			IPropertyListener propertyListener) {
		this.treeViewer = treeViewer;
		this.propertyListener = propertyListener;
	}

	/**
	 * Fired when an element has been checked or unchecked
	 */
	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		final String element = (String)(event.getElement());
		final boolean isChecked = event.getChecked();
		
		BusyIndicator.showWhile(section.getDisplay(), new Runnable() {
			public void run() {
				piscenario.getConstraints().setConstraint(element, currentOpId, isChecked);
				updateCheck();
			}
		});
		
		propertyListener.propertyChanged(this, IEditorPart.PROP_DIRTY);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	/**
	 * Core combo box listener that selects the current core
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() instanceof Combo) {
			Combo combo = ((Combo) e.getSource());
			String item = combo.getItem(combo.getSelectionIndex());

			currentOpId = item;
			updateCheck();
		}
	}

	/**
	 * Update the check status of the whole tree
	 */
	public void updateCheck() {
		if (piscenario != null && currentOpId != null) {
			treeViewer.setSubtreeChecked(treeViewer.getInput(), false);
			
			Constraints constraints = piscenario.getConstraints();			
			Set<String> checkedActors = constraints.getCheckedActors(currentOpId);
			
			treeViewer.setCheckedElements(checkedActors.toArray());

			// If all the children of a graph are checked, it is checked itself
			for (String actor : checkedActors) {
				boolean allChildrenChecked = true;
				String parent = constraints.getParentOf(actor);
				
				if(parent == null) continue;
				
				for(String child: constraints.getChildrenOf(parent)){
					allChildrenChecked &= constraints.getConstraint(child, currentOpId);
				}
				treeViewer.setChecked(parent, allChildrenChecked);;
			}
		}
	}

	/**
	 * Adds a combo box for the core selection
	 */
	public void addComboBoxSelector(Composite parent, FormToolkit toolkit) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setVisibleItemCount(20);
		combo.setToolTipText(Messages
				.getString("Constraints.coreSelectionTooltip"));
		comboDataInit(combo);
		combo.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				comboDataInit((Combo) e.getSource());
			}

			@Override
			public void focusLost(FocusEvent e) {
			}

		});

		combo.addSelectionListener(this);
	}

	private void comboDataInit(Combo combo) {
		combo.removeAll();
		for (String id : piscenario.getOperatorIds()) {
			combo.add(id);
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		updateCheck();
	}
}
