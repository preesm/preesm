/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
package org.ietr.preesm.utils.files;

// TODO: Auto-generated Javadoc
/**
 * Used to store files writing results. It maintains the number of really written files in an operation, and the number
 * of cached files (not written because already up-to-date)
 *
 * <p>
 * Code adapted from ORCC (net.sf.orcc.util, https://github.com/orcc/orcc)
 * </p>
 *
 * @author Antoine Lorence
 *
 */
public class Result {

  /** The written. */
  private int written = 0;

  /** The cached. */
  private int cached = 0;

  /**
   * Instantiates a new result.
   *
   * @param written
   *          the written
   * @param cached
   *          the cached
   */
  private Result(final int written, final int cached) {
    this.written = written;
    this.cached = cached;
  }

  /**
   * Create a new empty Result instance.
   *
   * @return the result
   */
  public static Result newInstance() {
    return new Result(0, 0);
  }

  /**
   * Create a new Result instance for a written file.
   *
   * @return the result
   */
  public static Result newOkInstance() {
    return new Result(1, 0);
  }

  /**
   * Create a new Result instance for a cached file.
   *
   * @return the result
   */
  public static Result newCachedInstance() {
    return new Result(0, 1);
  }

  /**
   * Merge the given <em>other</em> instance into this one by adding their respective members.
   *
   * @param other
   *          the other
   * @return the result
   */
  public Result merge(final Result other) {
    this.written += other.written;
    this.cached += other.cached;
    return this;
  }

  /**
   * Cached.
   *
   * @return the int
   */
  public int cached() {
    return this.cached;
  }

  /**
   * Written.
   *
   * @return the int
   */
  public int written() {
    return this.written;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Result) {
      return (((Result) obj).written == this.written) && (((Result) obj).cached == this.cached);
    }
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Result: ");
    builder.append(this.written).append(" file(s) written - ");
    builder.append(this.cached).append(" file(s) cached");
    return builder.toString();
  }

  /**
   * Checks if is empty.
   *
   * @return true, if is empty
   */
  public boolean isEmpty() {
    return (this.written == 0) && (this.cached == 0);
  }
}
