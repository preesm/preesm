/**
 * 
 */
package org.ietr.preesm.core.codegen;


/**
 * Function initializing a point to point communication system
 * 
 * @author mpelcat
 */
public class CommunicationFunctionInit extends AbstractCodeElement {

	/**
	 * ID of the core to connect during this initialization function call
	 */
	private String connectedCoreId;

	/**
	 * ID of the medium used to connect to the core
	 */
	private String mediumId;

	public CommunicationFunctionInit(String name,
			AbstractBufferContainer parentContainer, String connectedCoreId,
			String mediumId) {
		super(name, parentContainer, null);
		this.connectedCoreId = connectedCoreId;
		this.mediumId = mediumId;
	}

	public String getConnectedCoreId() {
		return connectedCoreId;
	}

	public String getMediumId() {
		return mediumId;
	}
	
	

}
