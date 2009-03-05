package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

/**
 * Initializing a point to point communication channel to send data
 * 
 * @author mpelcat
 */
public class SendInit extends CommunicationFunctionInit {

	public SendInit(AbstractBufferContainer parentContainer, String connectedCoreId,
			String mediumId) {
		super("sendInit", parentContainer, connectedCoreId,
				mediumId);
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit self
	}

}
