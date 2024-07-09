/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2017)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
package org.preesm.codegen.xtend.spider.visitor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * The Class SpiderPreProcessVisitor.
 */
public class SpiderPreProcessVisitor extends PiMMSwitch<Boolean> {

  /** The current abstract actor. */
  private AbstractActor currentAbstractActor = null;

  /** The current abstract vertex name. */
  private String currentAbstractVertexName = "";

  /** The port map. */
  private final Map<Port, Integer> portMap = new LinkedHashMap<>();

  /** The setter map. */
  private final Map<ISetter, String> setterMap = new LinkedHashMap<>();

  /** The actor names. */
  // Map from Actor names to pairs of CoreType numbers and Timing expressions
  private final Map<String, AbstractActor> actorNames = new LinkedHashMap<>();

  /** The function map. */
  private final Map<AbstractActor, Integer> functionMap = new LinkedHashMap<>();

  /** The data in port indices. */
  private final Map<AbstractActor, Integer> dataInPortIndices = new LinkedHashMap<>();

  /** The data out port indices. */
  private final Map<AbstractActor, Integer> dataOutPortIndices = new LinkedHashMap<>();

  /** The cfg in port indices. */
  private final Map<AbstractActor, Integer> cfgInPortIndices = new LinkedHashMap<>();

  /** The cfg out port indices. */
  private final Map<AbstractActor, Integer> cfgOutPortIndices = new LinkedHashMap<>();

  // Variables containing the name of the currently visited AbstractActor for
  // PortDescriptions
  // Map linking data ports to their corresponding description

  /**
   * Gets the port map.
   *
   * @return the port map
   */
  public Map<Port, Integer> getPortMap() {
    return this.portMap;
  }

  /**
   * Gets the actor names.
   *
   * @return the actor names
   */
  public Map<String, AbstractActor> getActorNames() {
    return this.actorNames;
  }

  /**
   * Gets the function map.
   *
   * @return the function map
   */
  public Map<AbstractActor, Integer> getFunctionMap() {
    return this.functionMap;
  }

  @Override
  public Boolean casePiGraph(final PiGraph pg) {
    caseAbstractActor(pg);
    for (final AbstractActor a : pg.getActors()) {
      doSwitch(a);
    }
    for (final Parameter p : pg.getParameters()) {
      doSwitch(p);
    }
    return true;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor aa) {
    // Fix currentAbstractActor
    this.currentAbstractActor = aa;
    // Fix currentAbstractVertexName
    this.currentAbstractVertexName = "vx" + aa.getName();
    this.dataInPortIndices.put(aa, 0);
    this.dataOutPortIndices.put(aa, 0);
    this.cfgInPortIndices.put(aa, 0);
    this.cfgOutPortIndices.put(aa, 0);

    // Visit configuration input ports to fill cfgInPortMap
    caseConfigurable(aa);
    // Visit data ports to fill the dataPortMap
    for (final DataInputPort p : aa.getDataInputPorts()) {
      doSwitch(p);
    }
    for (final DataOutputPort p : aa.getDataOutputPorts()) {
      doSwitch(p);
    }
    // Visit configuration output ports to fill the setterMap
    for (final ConfigOutputPort p : aa.getConfigOutputPorts()) {
      doSwitch(p);
    }
    this.actorNames.put(aa.getVertexPath().replace("/", "_"), aa);
    return true;
  }

  @Override
  public Boolean caseConfigurable(final Configurable av) {
    // Visit configuration input ports to fill cfgInPortMap
    for (final ConfigInputPort p : av.getConfigInputPorts()) {
      doSwitch(p);
    }
    return true;
  }

  @Override
  public Boolean caseActor(final Actor a) {
    // Register associated function
    if (!(a instanceof PiGraph)) {
      this.functionMap.put(a, this.functionMap.size());
      if (!(a.getRefinement() instanceof CHeaderRefinement)) {
        PreesmLogger.getLogger().warning("Actor " + a.getName() + " doesn't have correct refinement.");
      }
    }

    caseAbstractActor(a);
    return true;
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cip) {
    final int index = this.cfgInPortIndices.get(this.currentAbstractActor);
    this.cfgInPortIndices.put(this.currentAbstractActor, index + 1);
    this.portMap.put(cip, index);
    return true;
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort cop) {
    final int index = this.cfgOutPortIndices.get(this.currentAbstractActor);
    this.cfgOutPortIndices.put(this.currentAbstractActor, index + 1);
    this.portMap.put(cop, index);
    return true;
  }

  /**
   * When visiting data ports, we stock the necessary informations for edge generation into PortDescriptions.
   *
   * @param dip
   *          the dip
   */
  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
    // XXX: setParentEdge workaround (see visitDataInputInterface and
    // visitDataOutputInterface in CPPCodeGenerationVisitor)
    // XXX Ugly way to do this. Must suppose that fifos are always obtained
    // in the same order => Modify the C++ headers?
    // Get the position of the incoming fifo of dip wrt.
    // currentAbstractActor
    final int index = this.dataInPortIndices.get(this.currentAbstractActor);
    this.dataInPortIndices.put(this.currentAbstractActor, index + 1);

    // Fill dataPortMap
    this.portMap.put(dip, index);
    return true;
  }

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dop) {
    // XXX: setParentEdge workaround (see visitDataInputInterface and
    // visitDataOutputInterface in CPPCodeGenerationVisitor)
    // XXX Ugly way to do this. Must suppose that fifos are always obtained
    // in the same order => Modify the C++ headers?
    // Get the position of the outgoing fifo of dop wrt.
    // currentAbstractActor
    final int index = this.dataOutPortIndices.get(this.currentAbstractActor);
    this.dataOutPortIndices.put(this.currentAbstractActor, index + 1);

    // Fill dataPortMap
    this.portMap.put(dop, index);
    return true;
  }

  @Override
  public Boolean caseParameter(final Parameter p) {
    // Fix currentAbstractVertexName
    this.currentAbstractVertexName = "param_" + p.getName();
    // Visit configuration input ports to fill cfgInPortMap
    caseConfigurable(p);
    // Fill the setterMap
    this.setterMap.put(p, this.currentAbstractVertexName);
    return true;
  }

  @Override
  public Boolean caseDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigOutputInterface(final ConfigOutputInterface coi) {
    caseInterfaceActor(coi);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface dii) {
    caseInterfaceActor(dii);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    caseInterfaceActor(doi);
    return true;
  }

  @Override
  public Boolean caseInterfaceActor(final InterfaceActor ia) {
    caseAbstractActor(ia);
    return true;
  }

  @Override
  public Boolean caseDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFifo(final Fifo f) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseParameterizable(final Parameterizable p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement r) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFunctionArgument(final FunctionArgument functionParameter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFunctionPrototype(final FunctionPrototype functionPrototype) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseCHeaderRefinement(final CHeaderRefinement hRefinement) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor ba) {
    caseAbstractActor(ba);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor ja) {
    caseAbstractActor(ja);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor fa) {
    caseAbstractActor(fa);
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor rba) {
    caseAbstractActor(rba);
    return true;
  }

  @Override
  public Boolean caseInitActor(final InitActor rba) {
    caseAbstractActor(rba);
    return true;
  }

  @Override
  public Boolean caseEndActor(final EndActor rba) {
    caseAbstractActor(rba);
    return true;
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException(
        "Actor <" + ea.getVertexPath() + "> of type" + ea.getClass().getName() + ", has an unsupported type.");
  }
}
