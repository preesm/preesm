package org.ietr.preesm.plugin.mapper.model;

/**
 * Properties of an edge set when converting dag to mapper dag
 * 
 * @author mpelcat
 */
public class InitialEdgeProperty {

	private int dataSize;

	public InitialEdgeProperty() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InitialEdgeProperty(int dataSize) {
		super();
		this.dataSize = dataSize;
	}

	public InitialEdgeProperty clone() {
		InitialEdgeProperty property = new InitialEdgeProperty();
		property.setDataSize(getDataSize());
		return property;
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
}
