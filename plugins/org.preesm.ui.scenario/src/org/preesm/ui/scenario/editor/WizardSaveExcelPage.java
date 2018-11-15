/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Jonathan Piat <jpiat@laas.fr> (2009)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.preesm.ui.scenario.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

// TODO: Auto-generated Javadoc
/**
 * This class provides a page for the save timings as excel sheet wizard.
 *
 * @author mpelcat
 * @author kdesnos
 */
public class WizardSaveExcelPage extends WizardNewFileCreationPage {

  /** The writer. */
  private ExcelWriter writer;

  /**
   * Constructor for {@link WizardSaveExcelPage}.
   *
   * @param savedObject
   *          the saved object
   */
  public WizardSaveExcelPage(final String savedObject) {
    super("save" + savedObject, new StructuredSelection());

    setTitle(Messages.getString("Timings.timingExport.dialog"));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
   */
  @Override
  public InputStream getInitialContents() {
    // writes graph
    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    this.writer.write(out);
    return new ByteArrayInputStream(out.toByteArray());
  }

  /**
   * Sets a new graph for this page.
   *
   * @param writer
   *          the new writer
   */
  public void setWriter(final ExcelWriter writer) {
    this.writer = writer;
  }

}
