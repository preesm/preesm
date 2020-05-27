/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2020)
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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.commons.model.PreesmUserFactory;
import org.preesm.model.pisdf.AbstractActor;
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
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.LongExpression;
import org.preesm.model.pisdf.MalleableParameter;
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
   * Copy PiGraph tracking history of its children (actors, fifos, parameters, dependencies).
   */
  public PiGraph copyPiGraphWithHistory(final PiGraph origGraph) {
    // generic type forced to EObject to call the default copy from PreesmUserFactory
    final PiGraph copyGraph = this.copyWithHistory(origGraph);

    // we copy all known observer to all relevant objects (here for PiGraph)
    List<PiGraph> allPiGraph = new ArrayList<>();
    allPiGraph.add(copyGraph);
    while (!allPiGraph.isEmpty()) {
      PiGraph pg = allPiGraph.remove(0);
      pg.eAdapters().add(new GraphInterfaceObserver());
      allPiGraph.addAll(pg.getChildrenGraphs());
    }

    // track parameters
    final EList<Parameter> allOrigParams = origGraph.getAllParameters();
    final EList<Parameter> allCopyParams = copyGraph.getAllParameters();
    if (allOrigParams.size() != allCopyParams.size()) {
      throw new PreesmRuntimeException("Copy is not consistent regarding parameters");
    }
    for (int i = 0; i < allOrigParams.size(); i++) {
      final Parameter paramOrig = allOrigParams.get(i);
      final Parameter paramCopy = allCopyParams.get(i);
      if (!paramOrig.getVertexPath().endsWith(paramCopy.getVertexPath())) {
        throw new PreesmRuntimeException("Copy did not preserve order on parameters");
      }
      PreesmCopyTracker.trackCopy(paramOrig, paramCopy);
    }

    // track actors
    final EList<AbstractActor> allOrigActors = origGraph.getAllActors();
    final EList<AbstractActor> allCopyActors = copyGraph.getAllActors();
    if (allOrigActors.size() != allCopyActors.size()) {
      throw new PreesmRuntimeException("Copy is not consistent regarding actors");
    }
    for (int i = 0; i < allOrigActors.size(); i++) {
      final AbstractActor actorOrig = allOrigActors.get(i);
      final AbstractActor actorCopy = allCopyActors.get(i);
      if (!actorOrig.getVertexPath().endsWith(actorCopy.getVertexPath())) {
        throw new PreesmRuntimeException("Copy did not preserve order on actors");
      }
      PreesmCopyTracker.trackCopy(actorOrig, actorCopy);
    }

    // track dependencies
    final EList<Dependency> allOrigDeps = origGraph.getAllDependencies();
    final EList<Dependency> allCopyDeps = copyGraph.getAllDependencies();
    if (allOrigDeps.size() != allCopyDeps.size()) {
      throw new PreesmRuntimeException("Copy is not consistent regarding dependencies");
    }
    for (int i = 0; i < allOrigDeps.size(); i++) {
      final Dependency depOrig = allOrigDeps.get(i);
      final Dependency depCopy = allCopyDeps.get(i);
      PreesmCopyTracker.trackCopy(depOrig, depCopy);
    }

    // track fifos
    final EList<Fifo> allOrigFifos = origGraph.getAllFifos();
    final EList<Fifo> allCopyFifos = copyGraph.getAllFifos();
    if (allOrigFifos.size() != allCopyFifos.size()) {
      throw new PreesmRuntimeException("Copy is not consistent regarding fifos");
    }
    for (int i = 0; i < allOrigFifos.size(); i++) {
      final Fifo fifoOrig = allOrigFifos.get(i);
      final Fifo fifoCopy = allCopyFifos.get(i);
      PreesmCopyTracker.trackCopy(fifoOrig, fifoCopy);
    }

    return copyGraph;
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
  public Fifo createFifo(final DataOutputPort sourcePort, final DataInputPort targetPort, final String type) {
    final Fifo res = createFifo();
    res.setSourcePort(sourcePort);
    res.setTargetPort(targetPort);
    res.setType(type);
    return res;
  }

  @Override
  public Parameter createParameter() {
    return createParameter(null, 0);
  }

  /**
   *
   */
  public Parameter createParameter(final String name, final long evaluate) {
    final Parameter createParameter = super.createParameter();
    final Expression createExpression = createExpression(evaluate);
    createParameter.setExpression(createExpression);
    createParameter.setName(name);
    return createParameter;
  }

  @Override
  public MalleableParameter createMalleableParameter() {
    return createMalleableParameter(null, 0);
  }

  /**
   * 
   */
  public MalleableParameter createMalleableParameter(final String name, final long evaluate) {
    final MalleableParameter res = super.createMalleableParameter();
    final Expression createExpression = createExpression(evaluate);
    res.setExpression(createExpression);
    res.setName(name);
    res.setUserExpression("0");
    return res;
  }

  @Override
  public DataInputPort createDataInputPort() {
    final DataInputPort res = super.createDataInputPort();
    res.setExpression(createExpression());
    return res;
  }

  /**
   *
   */
  public DataInputPort createDataInputPort(final String name) {
    final DataInputPort res = this.createDataInputPort();
    res.setName(name);
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
    res.setName("set");
    return res;
  }

  @Override
  public DataOutputPort createDataOutputPort() {
    final DataOutputPort res = super.createDataOutputPort();
    res.setExpression(createExpression());
    return res;
  }

  /**
   *
   */
  public DataOutputPort createDataOutputPort(final String name) {
    final DataOutputPort res = this.createDataOutputPort();
    res.setName(name);
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
    res.setName("get");
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
    res.setActor(createDelayActor(res));

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
    final DataInputPort setterPort = createDataInputPort(delay);
    final DataOutputPort getterPort = createDataOutputPort(delay);
    res.getDataInputPorts().add(setterPort);
    res.getDataOutputPorts().add(getterPort);
    // Set the linked delay
    res.setLinkedDelay(delay);
    res.setName("");
    return res;
  }

  @Override
  public PiGraph createPiGraph() {
    final PiGraph res = super.createPiGraph();
    final Expression exp = createExpression();
    res.setExpression(exp);
    res.eAdapters().add(new GraphInterfaceObserver());
    return res;
  }

  @Override
  public Actor createActor() {
    final Actor res = super.createActor();
    final Expression exp = createExpression();
    res.setExpression(exp);
    res.setFiringInstance(0L);
    return res;
  }

  /**
   *
   */
  public Actor createActor(final String name) {
    final Actor res = this.createActor();
    res.setName(name);
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
  public ConfigInputInterface createConfigInputInterface() {
    final ConfigInputInterface res = super.createConfigInputInterface();
    final Expression createExpression = createExpression();
    res.setExpression(createExpression);
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
