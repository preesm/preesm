/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.preesm.ui.pisdf.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyArguments;
import org.eclipse.ltk.core.refactoring.participants.CopyParticipant;

/**
 * The Class CopyDiagram.
 */
public class CopyDiagram extends CopyParticipant {

  /** The refactored. */
  IFile refactored;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
   */
  @Override
  protected boolean initialize(final Object element) {
    // Get the destination folder.
    final IFolder destinationFolder = (IFolder) getArguments().getDestination();
    final IFolder sourceFolder = (IFolder) ((IFile) element).getParent();

    // Will participate if the destination folder and the source are
    // identical
    if (destinationFolder.equals(sourceFolder)) {
      this.refactored = (IFile) element;
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
    return "Copy and Rename a .diagram File";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.
   * IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
   */
  @Override
  public RefactoringStatus checkConditions(final IProgressMonitor pm, final CheckConditionsContext context) {
    // Nothing to do here
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.
   * IProgressMonitor)
   */
  @Override
  public Change createChange(final IProgressMonitor pm) throws CoreException {
    // Make arguments accessible to CompositeChange method.
    final CopyArguments currentArgs = getArguments();

    // Create a composite change that will automatically adapt itself
    // to the new name of the copied file.
    return new CompositeChange(getName()) {

      final CopyArguments copyArgs = currentArgs;

      @Override
      public Change perform(final IProgressMonitor pm) throws CoreException {
        // Get the new name of the copied file.
        final String oldFileName = CopyDiagram.this.refactored.getName();
        final String newFileName = this.copyArgs.getExecutionLog().getNewName(CopyDiagram.this.refactored);

        // Check that both names end with extension ".diagram"
        // and remove both extensions
        final String extension = ".diagram";
        String newName;
        String oldName;
        if (oldFileName.endsWith(extension) && (newFileName != null) && newFileName.endsWith(extension)) {
          oldName = oldFileName.substring(0, oldFileName.length() - extension.length());
          newName = newFileName.substring(0, newFileName.length() - extension.length());
        } else {
          return null;
        }

        // Get the new file
        final IFile newFile = ((IFolder) this.copyArgs.getDestination()).getFile(newFileName);
        if (newFile == null) {
          return null;
        }

        final TextFileChange change = RefactoringHelper.createChange(
            "(<pi:Diagram.*?name=\"|<businessObjects href=\")(" + oldName + ")(\".*?>|.pi#.*?\"/>)", 2, newName,
            newFile);

        if (change != null) {
          add(change);
        }
        return super.perform(pm);
      }
    };

  }

}
