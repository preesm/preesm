/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
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
package org.preesm.algorithm.synthesis.memalloc.meg;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import org.eclipse.xtext.util.Pair;
import org.preesm.algorithm.memory.exclusiongraph.IWeightedVertex;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.PropertyFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.scenario.Scenario;

/**
 * MemoryExclusionVertex is used to represent vertices in the Exclusion graph.
 *
 * @author kdesnos
 *
 */
public class PiMemoryExclusionVertex extends AbstractVertex<PiMemoryExclusionGraph> implements IWeightedVertex<Long> {

  /**
   * String used in the {@link PropertyBean} of a {@link PiMemoryExclusionVertex} to store the offset at which the
   * memory object is stored in memory.
   */
  public static final String MEMORY_OFFSET_PROPERTY = "memory_offset";

  /**
   * Property of the {@link PiMemoryExclusionVertex}. The object associated to this property is:<br>
   * <code>
   * List&lt;Pair&lt;MemoryExclusionVertex,Pair&lt;Range,Range&gt;&gt;</code> <br>
   * This {@link List} stores {@link Pair} of {@link PiMemoryExclusionVertex} and {@link Pair}. Each {@link Pair}
   * corresponds to a {@link Range} of real tokens of the memory object and their position in the actual
   * {@link PiMemoryExclusionVertex} (i.e. the key of the first {@link Pair}). <br>
   * For the host memory object, this property gives the position of the range of bits of the host within the memory
   * allocated for it.<br>
   * For hosted memory object, this property gives the position of the range(s) of bits of the hosted memory object
   * relatively to the position of the 0 index of the host memory object within the memory allocated for it.
   */
  public static final String REAL_TOKEN_RANGE_PROPERTY = "real_token_range";

  /**
   * Property of the {@link PiMemoryExclusionVertex}. The object associated to this property is:<br>
   * <code>
   * List&lt;MemoryExclusionVertex&gt;</code><br>
   * This list contains the fake {@link PiMemoryExclusionVertex} that are added to the {@link PiMemoryExclusionGraph}
   * during memory allocation when the current {@link PiMemoryExclusionVertex} is divided because of scripts. These fake
   * {@link PiMemoryExclusionVertex} should be removed from the {@link PiMemoryExclusionGraph} if it is
   * {@link PiMemoryExclusionGraph#deallocate() deallocated}.
   */
  public static final String FAKE_MOBJECT = "fake_mobject";

  /**
   * Property of the {@link PiMemoryExclusionVertex}. The object associated to this property is:<br>
   * <code>
   * List&lt;MemoryExclusionVertex&gt;</code><br>
   * This {@link List} stores {@link PiMemoryExclusionVertex} corresponding to the
   * {@link PiMemoryExclusionGraph#getAdjacentVertexOf(PiMemoryExclusionVertex) adjacent vertices} of the current
   * {@link PiMemoryExclusionVertex} before it was merged as a result of memory scripts execution.
   */
  public static final String ADJACENT_VERTICES_BACKUP = "adjacent_vertices_backup";

  /**
   * Property of the {@link PiMemoryExclusionVertex}. The object associated to this property is an {@link Integer} that
   * corresponds to the space in bits between the offset at which the {@link PiMemoryExclusionVertex} is allocated and
   * the actual beginning of the real token ranges. This property is set after the memory script execution.
   */
  public static final String EMPTY_SPACE_BEFORE = "empty_space_before";

  /**
   * Property of the {@link PiMemoryExclusionVertex}. The object associated to this property is an {@link Integer} that
   * corresponds to the size in bits of the {@link PiMemoryExclusionVertex} when it hosts merged
   * {@link PiMemoryExclusionVertex} as a result of scripts execution. This value is stored in case the host
   * {@link PiMemoryExclusionVertex} needs to be deallocated, and restored to the size it has when all hosted
   * {@link PiMemoryExclusionVertex} are merged.
   */
  public static final String HOST_SIZE = "host_size";

  /**
   * Property associated to {@link PiMemoryExclusionVertex} that are divided as a result of the application of memory
   * scripts. The object associated to this property is a {@link List} of {@link PiMemoryExclusionVertex} that
   * corresponds to the {@link PiMemoryExclusionVertex} in which the parts of the divided
   * {@link PiMemoryExclusionVertex} will be merged.
   */
  public static final String DIVIDED_PARTS_HOSTS = "divided_parts_hosts";

  /**
   * {@link PiMemoryExclusionVertex} property associated to a {@link List} of {@link Integer} that represent the space
   * <b>in bits</b> between successive "subbuffers" of a {@link PiMemoryExclusionVertex}.
   */
  public static final String INTER_BUFFER_SPACES = "inter_buffer_spaces";

  /**
   * Property used with fifo {@link PiMemoryExclusionVertex memory objects} to relate the size of one token in the fifo.
   */
  public static final String TYPE_SIZE = "type_size";

  /**
   * ID of the task consuming the memory.
   */
  private final String sink;

  /** Size of the memory used. */
  private long size;

  /**
   * ID of the task producing the memory.
   */
  private final String source;

  /**
   * The edge in the DAG that corresponds to this vertex in the exclusion graph. (This attribute is used only if the
   * vertices corresponds to an edge in the dag, i.e. a transfer between actors)
   */
  private Fifo edge;

  private final Scenario scenario;

  /**
   * Constructor of the class.
   *
   * @param inputEdge
   *          the DAG edge corresponding to the constructed vertex
   */
  public PiMemoryExclusionVertex(final Fifo inputEdge, final Scenario scenario) {
    this(inputEdge.getSourcePort().getContainingActor().getName(),
        inputEdge.getTargetPort().getContainingActor().getName(), getSize(inputEdge, scenario), scenario);
    this.edge = inputEdge;
    if (this.size == 0) {
      PreesmLogger.getLogger().log(Level.WARNING, "Probable ERROR: Vertex weight is 0");
    }

  }

  private static long getSize(final Fifo inputEdge, final Scenario scenario) {
    final long sourceRate = inputEdge.getSourcePort().getPortRateExpression().evaluateAsLong();
    final long targetRate = inputEdge.getTargetPort().getPortRateExpression().evaluateAsLong();
    if (sourceRate != targetRate) {
      throw new PreesmRuntimeException(
          "Source and Target rate are not equal. PiGraph should be in SRDAG to run allocation.");
    }
    final String typeStr = inputEdge.getType();
    return scenario.getSimulationInfo().getBufferSizeInBit(typeStr, sourceRate);
  }

  /**
   * Constructor of the class.
   *
   * @param sourceTask
   *          The size of the memory
   * @param sinkTask
   *          the sink task
   * @param sizeMem
   *          the size mem
   */
  public PiMemoryExclusionVertex(final String sourceTask, final String sinkTask, final long sizeMem,
      final Scenario scenario) {
    this.scenario = scenario;
    this.source = sourceTask;
    this.sink = sinkTask;
    this.size = sizeMem;
  }

  public final Scenario getScenario() {
    return this.scenario;
  }

  public Fifo getEdge() {
    return this.edge;
  }

  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

  public String getSink() {
    return this.sink;
  }

  public String getSource() {
    return this.source;
  }

  @Override
  public Long getWeight() {
    return this.size;
  }

  public Long getWeightInByte() {
    return (this.size + 7L) / 8L;
  }

  @Override
  public void setWeight(final Long w) {
    this.size = w;
  }

  @Override
  public PiMemoryExclusionVertex getClone() {
    PiMemoryExclusionVertex copy;
    copy = new PiMemoryExclusionVertex(this.getSource(), this.getSink(), this.getWeight(), this.getScenario());
    copy.edge = this.edge;
    return copy;
  }

  @Override
  public PiMemoryExclusionVertex copy() {
    return null;
  }

  /**
   * Test equality of two {@link MemoryExclusionVertex vertices}.<br>
   * Two {@link MemoryExclusionVertex vertices} are considered equals if their {@link #getSource() source} and
   * {@link #getSink() sink} are equals. Neither the weight nor the explodeImplode attributes of the vertices are taken
   * into account to test the equality.
   *
   * <p>
   * Do not change the way the comparison is done since several other classes relate on it, like ScriptRunner#updateMEG
   * method.
   * </p>
   *
   * @param o
   *          the object to compare.
   * @return true if the object is a similar vertex, false else.
   */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof final PiMemoryExclusionVertex piMemExVertex) {
      final Fifo otherEdge = piMemExVertex.edge;
      final boolean sameEdge = this.edge == otherEdge;
      final boolean sameSource = this.getSource().equals(piMemExVertex.getSource());
      final boolean sameSink = this.getSink().equals(piMemExVertex.getSink());
      return sameSink && sameSource && sameEdge;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getSource(), this.getSink());
  }

  @Override
  public String toString() {
    return this.getSource() + "=>" + this.getSink() + ":" + this.getWeightInByte();
  }
}
