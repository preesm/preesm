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
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;

/**
 * Tree representing a  {@link PiGraph} in the constraint page 
 *  
 * @author jheulot
 */
public class PiConstraintsTreeSection extends SectionPart {

	public PiConstraintsTreeSection(PiScenario piscenario, Section section,
			FormToolkit toolkit, int style, IPropertyListener listener, PiConstraintsListener constraintsListener) {
		super(section);

		section.setVisible(true);
		Composite container = toolkit.createComposite(getSection());
		container.setLayout(new GridLayout());

		// Creating a selector for available cores
		constraintsListener.addComboBoxSelector(container, toolkit);

		// Creating the tree view
		CheckboxTreeViewer treeviewer = new CheckboxTreeViewer(toolkit.createTree(container, SWT.CHECK));

		// The content provider fills the tree				
		treeviewer.setContentProvider(new PiConstraintsTreeContentProvider(treeviewer));

		// The check state listener modifies the check status of elements
		constraintsListener.setTreeViewer(treeviewer, listener);
		treeviewer.setLabelProvider(new PiConstraintsTreeLabelProvider());
		treeviewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		treeviewer.addCheckStateListener(constraintsListener);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 250;
		treeviewer.getTree().setLayoutData(gd);

		treeviewer.setUseHashlookup(true);
		treeviewer.setInput(piscenario.getConstraints());
		toolkit.paintBordersFor(container);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));
		section.setClient(container);

		// Tree is refreshed in case of algorithm modifications
		section.addPaintListener(constraintsListener);

	}

}
