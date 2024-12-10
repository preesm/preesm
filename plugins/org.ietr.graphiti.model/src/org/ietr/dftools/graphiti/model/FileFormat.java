/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2009 - 2011)
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
package org.ietr.dftools.graphiti.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a file format.
 *
 * @author Matthieu Wipliez
 *
 */
public class FileFormat {

  /** The exports. */
  private final List<Transformation> exports;

  /** The extension. */
  private final String extension;

  /** The imports. */
  private final List<Transformation> imports;

  /** The type. */
  private final String type;

  /**
   * Instantiates a new file format.
   *
   * @param extension
   *          the extension
   * @param type
   *          the type
   */
  public FileFormat(final String extension, final String type) {
    this.extension = extension;
    this.type = type;
    this.exports = new ArrayList<>();
    this.imports = new ArrayList<>();
  }

  /**
   * Gets the content type.
   *
   * @return the content type
   */
  public String getContentType() {
    return this.type;
  }

  /**
   * Gets the export transformations.
   *
   * @return the export transformations
   */
  public List<Transformation> getExportTransformations() {
    return this.exports;
  }

  /**
   * Gets the file extension.
   *
   * @return the file extension
   */
  public String getFileExtension() {
    return this.extension;
  }

  /**
   * Gets the import transformations.
   *
   * @return the import transformations
   */
  public List<Transformation> getImportTransformations() {
    return this.imports;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "*." + this.extension + ": " + this.type;
  }

}
