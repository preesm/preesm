/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
package org.preesm.model.pisdf.factory;

import org.preesm.commons.model.PreesmUserFactory;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.DelayLinkedExpression;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.LongExpression;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.StringExpression;
import org.preesm.model.pisdf.adapter.GraphInterfaceObserver;
import org.preesm.model.pisdf.impl.PiMMFactoryImpl;

/**
 *
 * @author anmorvan
 *
 */
public final class PiMMUserFactory extends PiMMFactoryImpl implements PreesmUserFactory {

  public static final PiMMUserFactory instance = new PiMMUserFactory();

  private PiMMUserFactory() {
    // forbid instantiation
  }

  /**
   *
   */
  public Dependency createDependency(final ISetter setter, final ConfigInputPort target) {
    final Dependency dep = createDependency();
    dep.setGetter(target);
    dep.setSetter(setter);
    return dep;
  }

  /**
   *
   */
  public Fifo createFifo(final DataOutputPort sourcePortCopy, final DataInputPort targetPortCopy, final String type) {
    final Fifo res = createFifo();
    res.setSourcePort(sourcePortCopy);
    res.setTargetPort(targetPortCopy);
    res.setType(type);
    return res;
  }

  @Override
  public ConfigInputInterface createConfigInputInterface() {
    final ConfigInputInterface res = super.createConfigInputInterface();
    final Expression createExpression = createExpression();
    res.setExpression(createExpression);
    return res;
  }

  @Override
  public Parameter createParameter() {
    final Parameter createParameter = super.createParameter();
    final Expression createExpression = createExpression();
    createParameter.setExpression(createExpression);
    return createParameter;
  }

  @Override
  public DataInputPort createDataInputPort() {
    final DataInputPort res = super.createDataInputPort();
    res.setExpression(createExpression());
    return res;
  }

  /**
   * Method to create a data input port with its expression linked to a delay
   *
   * @param delay
   *          the delay to set
   */
  public DataInputPort createDataInputPort(final Delay delay) {
    final DataInputPort res = super.createDataInputPort();
    final DelayLinkedExpression delayExpression = createDelayLinkedExpression();
    delayExpression.setProxy(delay);
    res.setExpression(delayExpression);
    return res;
  }

  @Override
  public DataOutputPort createDataOutputPort() {
    final DataOutputPort res = super.createDataOutputPort();
    res.setExpression(createExpression());
    return res;
  }

  /**
   * Method to create a data output port with its expression linked to a delay
   *
   * @param delay
   *          the delay to set
   */
  public DataOutputPort createDataOutputPort(final Delay delay) {
    final DataOutputPort res = super.createDataOutputPort();
    final DelayLinkedExpression delayExpression = createDelayLinkedExpression();
    delayExpression.setProxy(delay);
    res.setExpression(delayExpression);
    return res;
  }

  @Override
  public ConfigOutputPort createConfigOutputPort() {
    final ConfigOutputPort res = super.createConfigOutputPort();
    res.setExpression(createExpression());
    return res;
  }

  @Override
  public Delay createDelay() {
    final Delay res = super.createDelay();
    // 1. Set default expression
    res.setExpression(createExpression());
    // 2. Set the default level of persistence (permanent)
    res.setLevel(PersistenceLevel.PERMANENT);
    // 3. Create the non executable actor associated with the Delay directly here
    res.setActor(PiMMUserFactory.instance.createDelayActor(res));

    return res;
  }

  /**
   * Method to create a delay actor with the corresponding delay as parameter
   *
   * @param delay
   *          the delay to set
   */
  public DelayActor createDelayActor(final Delay delay) {
    final DelayActor res = super.createDelayActor();
    // Create ports here and force their name
    // Expression of the port are directly linked to the one of the delay
    final DataInputPort setterPort = PiMMUserFactory.instance.createDataInputPort(delay);
    final DataOutputPort getterPort = PiMMUserFactory.instance.createDataOutputPort(delay);
    res.getDataInputPorts().add(setterPort);
    res.getDataInputPort().setName("set");
    res.getDataOutputPorts().add(getterPort);
    res.getDataOutputPort().setName("get");

    // Set the linked delay
    res.setLinkedDelay(delay);
    res.setName("");
    return res;
  }

  @Override
  public PiGraph createPiGraph() {
    final PiGraph res = super.createPiGraph();
    res.eAdapters().add(new GraphInterfaceObserver());
    return res;
  }

  @Override
  public Actor createActor() {
    final Actor res = super.createActor();
    final Expression exp = createExpression();
    res.setExpression(exp);
    return res;
  }

  @Override
  public InitActor createInitActor() {
    final InitActor res = super.createInitActor();
    final DataOutputPort port = PiMMUserFactory.instance.createDataOutputPort();
    res.getDataOutputPorts().add(port);
    return res;
  }

  @Override
  public EndActor createEndActor() {
    final EndActor res = super.createEndActor();
    final DataInputPort port = PiMMUserFactory.instance.createDataInputPort();
    res.getDataInputPorts().add(port);
    return res;
  }

  public Expression createExpression() {
    return createExpression(0L);
  }

  /**
   *
   */
  public Expression createExpression(final String value) {
    try {
      // try to convert the expression in its long value
      return createExpression(Long.parseLong(value));
    } catch (final NumberFormatException e) {
      final StringExpression createStringExpression = super.createStringExpression();
      createStringExpression.setExpressionString(value);
      return createStringExpression;
    }
  }

  /**
   *
   */
  public Expression createExpression(final long value) {
    final LongExpression createLongExpression = super.createLongExpression();
    createLongExpression.setValue(value);
    return createLongExpression;
  }

  @Override
  public DataInputInterface createDataInputInterface() {
    final DataInputInterface res = super.createDataInputInterface();
    final DataOutputPort port = PiMMUserFactory.instance.createDataOutputPort();
    res.getDataOutputPorts().add(port);
    return res;
  }

  @Override
  public DataOutputInterface createDataOutputInterface() {
    final DataOutputInterface res = super.createDataOutputInterface();
    final DataInputPort port = PiMMUserFactory.instance.createDataInputPort();
    res.getDataInputPorts().add(port);
    return res;
  }

  @Override
  public ConfigOutputInterface createConfigOutputInterface() {
    final ConfigOutputInterface res = super.createConfigOutputInterface();
    final DataInputPort port = PiMMUserFactory.instance.createDataInputPort();
    res.getDataInputPorts().add(port);
    return res;
  }
}
