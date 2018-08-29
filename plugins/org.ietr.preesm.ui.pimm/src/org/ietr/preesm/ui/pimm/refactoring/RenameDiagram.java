/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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
package org.ietr.preesm.ui.pimm.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

// TODO: Auto-generated Javadoc
/**
 * The purpose of this class is to handle the rename refactoring of a file with the ".diagram" extension. In the rename
 * operation, it is assumed that the corresponding file with the ".pi" extension is renamed similarly.
 *
 * @author kdesnos
 *
 */
public class RenameDiagram extends RenameParticipant {

  /** The old name. */
  String oldName;

  /** The new name. */
  String newName;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
   */
  @Override
  protected boolean initialize(final Object element) {
    this.oldName = ((IFile) element).getName();
    this.newName = getArguments().getNewName();

    // Check that both names end with extension ".diagram"
    // and remove both extensions
    final String extension = ".diagram";
    if (this.oldName.endsWith(extension) && this.newName.endsWith(extension)) {
      this.oldName = this.oldName.substring(0, this.oldName.length() - extension.length());
      this.newName = this.newName.substring(0, this.newName.length() - extension.length());

      return true;

    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
   */
  @Override
  public String getName() {
    return "Rename .diagram file";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.
   * IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
   */
  @Override
  public RefactoringStatus checkConditions(final IProgressMonitor pm, final CheckConditionsContext context)
      throws OperationCanceledException {
    // Nothing to do here
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createPreChange(org.eclipse.core.runtime.
   * IProgressMonitor)
   */
  @Override
  public Change createPreChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {

    // Get the refactored file
    final IFile refactored = (IFile) getProcessor().getElements()[0];

    // The regex is a combination of the two following expressions:
    // "(<pi:Diagram.*?name=\")(" + oldName + ")(\".*?>)"
    // "(<businessObjects href=\")("+ oldName + ")(.pi#.*?\"/>)"
    final Change change = RefactoringHelper.createChange(
        "(<pi:Diagram.*?name=\"|<businessObjects href=\")(" + this.oldName + ")(\".*?>|.pi#.*?\"/>)", 2, this.newName,
        refactored);

    return change;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.
   * IProgressMonitor)
   */
  @Override
  public Change createChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
    // Nothing to do after the file is renamed.
    return null;
  }

}
