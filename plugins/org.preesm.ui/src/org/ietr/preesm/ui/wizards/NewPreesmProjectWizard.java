/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2011)
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
package org.ietr.preesm.ui.wizards;

import java.net.URI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

// TODO: Auto-generated Javadoc
/**
 * This class provides a wizard to create a new Preesm Project.
 *
 * @author Matthieu Wipliez
 * @author Clément Guy
 */
public class NewPreesmProjectWizard extends BasicNewProjectResourceWizard {

  /** The Constant WINDOW_TITLE. */
  // Strings displayed in the wizard (titles, names, descriptions...)
  private static final String WINDOW_TITLE = "New PREESM Project";

  /** The Constant PAGE_NAME. */
  private static final String PAGE_NAME = "PREESM Project Wizard";

  /** The Constant PAGE_TITLE. */
  private static final String PAGE_TITLE = "PREESM Project";

  /** The Constant PAGE_DESCRIPTION. */
  private static final String PAGE_DESCRIPTION = "Create a new PREESM project.";

  /** The first page. */
  // Pages of the wizard
  private WizardNewProjectCreationPage firstPage;

  /**
   * Instantiates a new new preesm project wizard.
   */
  public NewPreesmProjectWizard() {
    setWindowTitle(NewPreesmProjectWizard.WINDOW_TITLE);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#addPages()
   */
  @Override
  public void addPages() {
    this.firstPage = new WizardNewProjectCreationPage(NewPreesmProjectWizard.PAGE_NAME);
    this.firstPage.setTitle(NewPreesmProjectWizard.PAGE_TITLE);
    this.firstPage.setDescription(NewPreesmProjectWizard.PAGE_DESCRIPTION);

    addPage(this.firstPage);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#initializeDefaultPageImageDescriptor()
   */
  @Override
  protected void initializeDefaultPageImageDescriptor() {
    super.initializeDefaultPageImageDescriptor();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    final String name = this.firstPage.getProjectName();
    URI location = null;
    if (!this.firstPage.useDefaults()) {
      location = this.firstPage.getLocationURI();
    } // else location == null

    NewPreesmProjectCreator.createProject(name, location);

    return true;
  }
}
