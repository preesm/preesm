/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2013)
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
package org.ietr.dftools.algorithm.model;

import org.eclipse.core.runtime.IPath;

/**
 * Code to refine a vertex.
 *
 * @author jpiat
 */
public class CodeRefinement implements IRefinement, CloneableProperty<CodeRefinement> {

  /**
   * Describe the language that can be used as a code refinement.
   *
   * @author jpiat
   */
  public enum Language {

  /** CAL Actor Language. */
  CAL,

  /** C Language. */
  C,

  /** C++ Language. */
  CPP,

  /** Java Language. */
  JAVA,

  /** Interface Description Language. */
  IDL,

  /** Text refinement, not a path. */
  TEXT;

    /**
     * Gives a Language corresponding to the given extension.
     *
     * @param s
     *          The extension of the code refinement
     * @return The Language instance
     */
    public static Language fromExtension(final String s) {
      if (s.equals(".c") || s.equals("c")) {
        return C;
      } else if (s.equals(".cal") || s.equals("cal")) {
        return CAL;
      } else if (s.equals(".cpp") || s.equals("cpp")) {
        return CPP;
      } else if (s.equals(".java") || s.equals("java")) {
        return JAVA;
      } else if (s.equals(".idl") || s.equals("idl")) {
        return IDL;
      } else {
        return TEXT;
      }
    }
  }

  /** The file path. */
  private final IPath filePath;

  /** The lang. */
  private Language lang;

  /**
   * Builds a new CodeRefinement instance.
   *
   * @param path
   *          the path
   */
  public CodeRefinement(final IPath path) {
    this.filePath = path;
    if (this.filePath != null) {
      final String extension = this.filePath.getFileExtension();
      if (extension != null) {
        this.lang = Language.fromExtension(extension);
      } else {
        this.lang = Language.TEXT;
      }
    }
  }

  /**
   * Gets the path.
   *
   * @return the path
   */
  public IPath getPath() {
    return this.filePath;
  }

  /**
   * Gives the code refinement language.
   *
   * @return The code refinement language
   */
  public Language getLanguage() {
    return this.lang;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (this.filePath != null) {
      return this.filePath.toString();
    } else {
      return "Empty refinement";
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  public CodeRefinement copy() {
    return new CodeRefinement(this.filePath);
  }

}
