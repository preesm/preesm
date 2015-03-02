/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
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
