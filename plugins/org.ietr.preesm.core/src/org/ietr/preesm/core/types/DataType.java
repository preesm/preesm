/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.types;

import java.util.HashMap;

/**
 * Representing a data type in code generation (exple: char, int...).
 * 
 * @author mpelcat
 */
public class DataType {

	private String typeName;

	/**
	 * Size in base units (usually bytes)
	 */
	private Integer size;

	public static final Integer defaultDataTypeSize = 1;
	public static final HashMap<String, Integer> nameToSize = new HashMap<String, Integer>();

	public DataType(String typeName) {
		super();
		this.typeName = typeName;
		if (nameToSize.get(typeName) == null) {
			this.size = defaultDataTypeSize;
		} else {
			this.size = nameToSize.get(typeName);
		}
	}

	public DataType(DataType type) {
		super();
		this.typeName = type.getTypeName();
		this.size = type.getSize();
	}

	public DataType(String typeName, Integer size) {
		super();
		this.typeName = typeName;
		this.size = size;
		nameToSize.put(typeName, size);
	}

	public String getTypeName() {
		return typeName;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return typeName;
	}
}
