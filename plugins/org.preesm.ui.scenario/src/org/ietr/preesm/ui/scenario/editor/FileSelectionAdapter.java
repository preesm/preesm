/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.ui.scenario.editor;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.preesm.ui.utils.FileUtils;

/**
 * This class calls the file browser when a "browse" button is pushed.
 *
 * @author mpelcat
 */
public class FileSelectionAdapter extends SelectionAdapter {

  /** The file path. */
  private final Text filePath;

  /** The title. */
  private final String title;

  /** The file extensions. */
  Set<String> fileExtensions;

  /**
   * Instantiates a new file selection adapter.
   *
   * @param filePath
   *          the file path
   * @param title
   *          the title
   * @param fileExtension
   *          the file extension
   */
  public FileSelectionAdapter(final Text filePath, final String title, final String fileExtension) {
    super();
    this.filePath = filePath;
    this.title = title;
    this.fileExtensions = new LinkedHashSet<>();
    this.fileExtensions.add(fileExtension);
  }

  /**
   * Instantiates a new file selection adapter.
   *
   * @param filePath
   *          the file path
   * @param title
   *          the title
   * @param fileExtensions
   *          the file extensions
   */
  public FileSelectionAdapter(final Text filePath, final String title, final Set<String> fileExtensions) {
    super();
    this.filePath = filePath;
    this.title = title;
    this.fileExtensions = fileExtensions;
  }

  /**
   * Instantiates a new file selection adapter.
   *
   * @param filePath
   *          the file path
   * @param shell
   *          the shell
   * @param title
   *          the title
   */
  public FileSelectionAdapter(final Text filePath, final Shell shell, final String title) {
    super();
    this.filePath = filePath;
    this.title = title;
    this.fileExtensions = null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
   */
  @Override
  public void widgetSelected(final SelectionEvent e) {

    final IPath browseFiles = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        this.title, this.fileExtensions);
    final String pathString = browseFiles.toString();
    this.filePath.setText(pathString);
  }
}
