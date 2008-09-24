/**
 * 
 */
package org.ietr.preesm.core.codegen.sdfProperties;

/**
 * Objects used to tag the SDF edges. The buffer couple definition 
 * is transmitted to the code generation. One buffer is created 
 * for the source and one for the destination.
 * 
 * @author mpelcat
 *
 */
public class BufferProperties {
	
	private String dataType;
	
	private String destInputPortID;
	
	private int size;
	
	private String sourceOutputPortID;

	public BufferProperties(String dataType, String sourceOutputPortID, String destInputPortID,
			int size) {
		super();
		this.dataType = dataType;
		this.destInputPortID = destInputPortID;
		this.size = size;
		this.sourceOutputPortID = sourceOutputPortID;
	}

	public String getDataType() {
		return dataType;
	}

	public String getDestInputPortID() {
		return destInputPortID;
	}

	public int getSize() {
		return size;
	}

	public String getSourceOutputPortID() {
		return sourceOutputPortID;
	}
	
	
}
