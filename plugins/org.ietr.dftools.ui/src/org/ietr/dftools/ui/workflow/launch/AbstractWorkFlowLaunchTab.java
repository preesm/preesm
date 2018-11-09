/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
 *
 */
package org.ietr.dftools.ui.workflow.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

// TODO: Auto-generated Javadoc
/**
 * Containing common funtionalities of launch tabs.
 *
 * @author mpelcat
 */
public abstract class AbstractWorkFlowLaunchTab extends AbstractLaunchConfigurationTab {

  /** current Composite. */
  private Composite currentComposite;

  /** file attribute name to save the entered file. */
  private String fileAttributeName = null;

  /**
   * file path of the current Tab. There can be only one file chooser for the moment
   */
  private IPath fileIPath = null;

  /** The file path. */
  private Text filePath = null;

  /**
   * Displays a file browser in a shell.
   *
   * @param shell
   *          the shell
   */
  protected void browseFiles(final Shell shell) {
    final ElementTreeSelectionDialog tree = new ElementTreeSelectionDialog(shell,
        WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), new WorkbenchContentProvider());
    tree.setAllowMultiple(false);
    tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
    tree.setMessage("Please select an existing file:");
    tree.setTitle("Choose an existing file");
    // opens the dialog
    if (tree.open() == Window.OK) {
      this.fileIPath = ((IFile) tree.getFirstResult()).getFullPath();
      this.filePath.setText(this.fileIPath.toString());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(final Composite parent) {

    this.currentComposite = new Composite(parent, SWT.NONE);
    setControl(this.currentComposite);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.currentComposite.setLayout(gridLayout);

  }

  /**
   * Displays a file text window with a browser button.
   *
   * @param title
   *          A line of text displayed before the file chooser
   * @param attributeName
   *          The name of the attribute in which the property should be saved
   */
  public void drawFileChooser(final String title, final String attributeName) {

    final Label label2 = new Label(this.currentComposite, SWT.NONE);
    label2.setText(title);
    this.fileAttributeName = attributeName;

    new Label(this.currentComposite, SWT.NONE);

    final Button buttonBrowse = new Button(this.currentComposite, SWT.PUSH);
    buttonBrowse.setText("Browse...");
    buttonBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        browseFiles(getCurrentComposite().getShell());
      }
    });

    this.filePath = new Text(this.currentComposite, SWT.BORDER);

    final GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
    layoutData.widthHint = 200;
    this.filePath.setLayoutData(layoutData);
    this.filePath.addModifyListener(e -> {
      setDirty(true);
      updateLaunchConfigurationDialog();
    });

  }

  /**
   * Gets the current composite.
   *
   * @return the current composite
   */
  protected Composite getCurrentComposite() {
    return this.currentComposite;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
   */
  @Override
  public void initializeFrom(final ILaunchConfiguration configuration) {
    try {
      this.filePath.setText(configuration.getAttribute(this.fileAttributeName, ""));
    } catch (final CoreException e) {
      // OcamlPlugin.logError("ocaml plugin error", e);
      this.filePath.setText("");
    }

    setDirty(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
   */
  @Override
  public boolean isValid(final ILaunchConfiguration launchConfig) {
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
   */
  @Override
  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    // Saving the file path chosen in a tab attribute
    if ((this.filePath != null) && (this.fileAttributeName != null)) {
      configuration.setAttribute(this.fileAttributeName, this.filePath.getText());
    }
    setDirty(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
   */
  @Override
  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {

    configuration.setAttribute(this.fileAttributeName, "");
    setDirty(false);
  }

}
