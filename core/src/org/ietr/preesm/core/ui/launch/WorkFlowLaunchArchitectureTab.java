/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.ui.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.ietr.preesm.core.ui.Activator;
import org.ietr.preesm.core.workflow.sources.ArchitectureConfiguration;

/**
 * Launch Tab for architecture options. From this tab, an
 * {@link ArchitectureConfiguration} is generated that feeds an
 * {@link ArchitectureRetriever} to create the input architecture.
 * 
 * @author mpelcat
 */
public class WorkFlowLaunchArchitectureTab extends AbstractWorkFlowLaunchTab {

	public static final int ARCH_TYPE_FILE = 2;
	public static final int ARCH_TYPE_TI_C64x2 = 0;
	public static final int ARCH_TYPE_TI_C64x3 = 1;

	/**
	 * ID used to save architecture type in tab attributes
	 */
	public static final String ATTR_ARCHITECTURE_TYPE = "org.ietr.preesm.core.architectureType";

	/**
	 * Choice between predefined archis or file archi
	 */
	private int architectureReference = ARCH_TYPE_FILE;

	private Button[] radioButtons;

	public WorkFlowLaunchArchitectureTab() {
		radioButtons = new Button[3];
	}

	@Override
	public void createControl(Composite parent) {

		super.createControl(parent);

		Composite composite = getCurrentComposite();

		radioButtons[0] = new Button(composite, SWT.RADIO);
		radioButtons[0].setText("2 C64x+ and EDMA");
		radioButtons[0].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				architectureReference = ARCH_TYPE_TI_C64x2;
				setDirty(true);
			}
		});

		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);

		radioButtons[1] = new Button(composite, SWT.RADIO);
		radioButtons[1].setText("3 C64x+ and EDMA");
		radioButtons[1].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				architectureReference = ARCH_TYPE_TI_C64x3;
				setDirty(true);
			}
		});

		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);

		radioButtons[2] = new Button(composite, SWT.RADIO);
		radioButtons[2].setText("Architecture from File");
		radioButtons[2].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				architectureReference = ARCH_TYPE_FILE;
				setDirty(true);
			}
		});

		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);

		drawFileChooser("Architecture file:",
				ArchitectureConfiguration.ATTR_ARCHITECTURE_FILE_NAME);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Architecture";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);

		try {
			architectureReference = configuration.getAttribute(
					ATTR_ARCHITECTURE_TYPE, ARCH_TYPE_FILE);
			radioButtons[architectureReference].setSelection(true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		// configuration.setAttribute(ATTR_ARCHITECTURE_TYPE,architectureReference);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage() {
		Image image = Activator.getImage("icons/preesm3mini.png");

		if (image != null)
			return image;

		return super.getImage();
	}
}
