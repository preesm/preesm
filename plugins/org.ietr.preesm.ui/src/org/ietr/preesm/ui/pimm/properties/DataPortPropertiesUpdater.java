package org.ietr.preesm.ui.pimm.properties;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.widgets.Text;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Expression;

public class DataPortPropertiesUpdater extends GFPropertySection {
	protected void updateDataPortProperties(DataPort port, Text textToUpdate) {
		if (!port.getExpression().getString().equals(textToUpdate.getText())) {
			setNewExpression(port.getExpression(), textToUpdate.getText());
			// If port is contained by an DataInterface, we should
			// also update the graph port of the DataInterface
			if (port.eContainer() instanceof DataOutputInterface) {
				DataOutputInterface doi = (DataOutputInterface) port
						.eContainer();
				DataOutputPort oPort = (DataOutputPort) doi.getGraphPort();
				if (!oPort.getExpression().getString()
						.equals(textToUpdate.getText())) {
					setNewExpression(oPort.getExpression(),
							textToUpdate.getText());
				}
			} else if (port.eContainer() instanceof DataInputInterface) {
				DataInputInterface dii = (DataInputInterface) port.eContainer();
				DataInputPort iPort = (DataInputPort) dii.getGraphPort();
				if (!iPort.getExpression().getString()
						.equals(textToUpdate.getText())) {
					setNewExpression(iPort.getExpression(),
							textToUpdate.getText());
				}
			}
		}
	}

	/**
	 * Safely set an {@link Expression} with the given value.
	 * 
	 * @param e
	 *            {@link Expression} to set
	 * @param value
	 *            String value
	 */
	protected void setNewExpression(final Expression e, final String value) {
		TransactionalEditingDomain editingDomain = getDiagramTypeProvider()
				.getDiagramBehavior().getEditingDomain();
		editingDomain.getCommandStack().execute(
				new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
						e.setString(value);
					}
				});
	}
}
