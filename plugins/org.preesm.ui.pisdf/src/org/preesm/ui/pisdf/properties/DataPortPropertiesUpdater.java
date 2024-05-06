/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.preesm.ui.pisdf.properties;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.widgets.Text;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.ExpressionHolder;

// TODO: Auto-generated Javadoc
/**
 * The Class DataPortPropertiesUpdater.
 */
public class DataPortPropertiesUpdater extends GFPropertySection {

  /**
   * Update data port properties.
   *
   * @param port
   *          the port
   * @param textToUpdate
   *          the text to update
   */
  protected void updateDataPortProperties(final DataPort port, final Text textToUpdate) {
    if (!port.getPortRateExpression().getExpressionAsString().equals(textToUpdate.getText())) {
      setNewExpression(port, textToUpdate.getText());
      // If port is contained by an DataInterface, we should
      // also update the graph port of the DataInterface
      if (port.eContainer() instanceof final DataOutputInterface doi) {
        final DataOutputPort oPort = (DataOutputPort) doi.getGraphPort();
        if (!oPort.getPortRateExpression().getExpressionAsString().equals(textToUpdate.getText())) {
          setNewExpression(oPort, textToUpdate.getText());
        }
      } else if (port.eContainer() instanceof final DataInputInterface dii) {
        final DataInputPort iPort = (DataInputPort) dii.getGraphPort();
        if (!iPort.getPortRateExpression().getExpressionAsString().equals(textToUpdate.getText())) {
          setNewExpression(iPort, textToUpdate.getText());
        }
      }
    }
  }

  /**
   * Safely set an {@link Expression} with the given value.
   *
   * @param e
   *          {@link Expression} to set
   * @param value
   *          String value
   */
  protected void setNewExpression(final ExpressionHolder e, final String value) {
    final TransactionalEditingDomain editingDomain = getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
      @Override
      protected void doExecute() {
        e.setExpression(value);
      }
    });
  }
}
