/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
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
package org.preesm.commons.exceptions;

/**
 *
 * Exception thrown during Preesm execution, by one of its task or menus;
 *
 * If non fatal, should not stop the execution.
 *
 * @author anmorvan
 *
 */
public class PreesmRuntimeException extends PreesmException {

  private static final long serialVersionUID = 4420197523628616963L;
  private final boolean     fatal;

  public PreesmRuntimeException() {
    this(true, null, null);
  }

  public PreesmRuntimeException(final String message) {
    this(true, message, null);
  }

  public PreesmRuntimeException(final String message, final Throwable cause) {
    this(true, message, cause);
  }

  public PreesmRuntimeException(final Throwable cause) {
    this(true, null, cause);
  }

  public PreesmRuntimeException(final boolean fatal, final String message, final Throwable cause) {
    super(message, cause);
    this.fatal = fatal;
  }

  public final boolean isFatal() {
    return fatal;
  }

}
