/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
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
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Source Interface</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class DataInputInterfaceImpl extends InterfaceActorImpl implements DataInputInterface {

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   */
  protected DataInputInterfaceImpl() {
    super();
    this.kind = PiIdentifiers.DATA_INPUT_INTERFACE;

    // Add the unique output port of the Source Interface
    // The port intentionally has no name
    final DataOutputPort port = PiMMFactory.eINSTANCE.createDataOutputPort();
    port.setName(null);
    getDataOutputPorts().add(port);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.DATA_INPUT_INTERFACE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitDataInputInterface(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl#setName(java.lang.String)
   */
  @Override
  public void setName(final String value) {
    super.setName(value);
    for (final DataOutputPort p : getDataOutputPorts()) {
      p.setName(value);
    }
  }
} // SourceInterfaceImpl
