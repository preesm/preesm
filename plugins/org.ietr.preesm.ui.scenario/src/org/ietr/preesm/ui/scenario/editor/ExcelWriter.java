/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2009)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
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
package org.ietr.preesm.ui.scenario.editor;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import jxl.write.WritableSheet;
import jxl.write.biff.RowsExceededException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.SelectionListener;
import org.ietr.dftools.algorithm.importer.InvalidModelException;

/**
 * Abstract class, must be implementing when exporting Timings, or variables into an excel sheet.
 *
 * @author kdesnos
 */
public abstract class ExcelWriter implements SelectionListener {

  /**
   * Instantiates a new excel writer.
   */
  public ExcelWriter() {
    super();
  }

  /**
   * Add timing cells to the newly created file.
   *
   * @param os
   *          the os
   */
  public abstract void write(OutputStream os);

  /**
   * Add cells to the created excel sheet.
   *
   * @param sheet
   *          the sheet
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws RowsExceededException
   *           the rows exceeded exception
   * @throws CoreException
   *           the core exception
   */
  protected abstract void addCells(WritableSheet sheet) throws InvalidModelException, FileNotFoundException, RowsExceededException, CoreException;

}
