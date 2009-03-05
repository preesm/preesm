/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

/**
 * Initializing a point to point communication channel to receive data
 * 
 * @author mpelcat
 */
public class ReceiveInit extends CommunicationFunctionInit {

	public ReceiveInit(AbstractBufferContainer parentContainer, String connectedCoreId,
			String mediumId) {
		super("receiveInit", parentContainer, connectedCoreId,
				mediumId);
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit self
	}

}
