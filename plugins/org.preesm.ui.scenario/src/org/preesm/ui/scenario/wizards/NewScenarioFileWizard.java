/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2011)
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
package org.preesm.ui.scenario.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 * The Class NewScenarioFileWizard.
 *
 * @author mpelcat
 */
public class NewScenarioFileWizard extends BasicNewFileResourceWizard {

  public static final String SCENARIO_INITIAL_CONTENT = """
      <?xml version="1.0" encoding="UTF-8"?>
      <scenario>
          <flags>
              <sizesAreInBit/>
          </flags>
      </scenario>""";

  @Override
  public void addPages() {
    super.addPages();
    super.setWindowTitle("New Scenario File");
  }

  @Override
  public boolean performFinish() {
    final WizardNewFileCreationPage page = (WizardNewFileCreationPage) (getPage("newFilePage1"));
    String filename = page.getFileName();

    if (!filename.endsWith(".scenario")) {
      filename += ".scenario";
      page.setFileName(filename);
    }

    final byte[] bytes = SCENARIO_INITIAL_CONTENT.getBytes();
    final InputStream source = new ByteArrayInputStream(bytes);

    final IFile createdFile = page.createNewFile();
    try {
      createdFile.setContents(source, IResource.FORCE, null);
    } catch (final CoreException e) {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("Could not initialize scenario file content", e);
    }

    return createdFile != null;
  }
}
