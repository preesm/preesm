/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.utils.files;

/**
 * Used to store files writing results. It maintains the number of really
 * written files in an operation, and the number of cached files (not written
 * because already up-to-date)
 * 
 * Code adapted from ORCC (net.sf.orcc.util, https://github.com/orcc/orcc)
 * @author Antoine Lorence
 * 
 */
public class Result {

	private int written = 0;
	private int cached = 0;

	private Result(int written, int cached) {
		this.written = written;
		this.cached = cached;
	}

	/**
	 * Create a new empty Result instance.
	 * 
	 * @return
	 */
	public static Result newInstance() {
		return new Result(0, 0);
	}

	/**
	 * Create a new Result instance for a written file.
	 * 
	 * @return
	 */
	public static Result newOkInstance() {
		return new Result(1, 0);
	}

	/**
	 * Create a new Result instance for a cached file.
	 * 
	 * @return
	 */
	public static Result newCachedInstance() {
		return new Result(0, 1);
	}

	/**
	 * Merge the given <em>other</em> instance into this one by adding their
	 * respective members.
	 * 
	 * @param other
	 * @return
	 */
	public Result merge(final Result other) {
		written += other.written;
		cached += other.cached;
		return this;
	}

	public int cached() {
		return cached;
	}

	public int written() {
		return written;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Result) {
			return ((Result) obj).written == written
					&& ((Result) obj).cached == cached;
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Result: ");
		builder.append(written).append (" file(s) written - ");
		builder.append(cached).append(" file(s) cached");
		return builder.toString();
	}

	public boolean isEmpty() {
		return written == 0 && cached == 0;
	}
}
