/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011 - 2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.model.sdf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.InterfaceDirection;
import org.ietr.dftools.algorithm.model.PropertyBean;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.NoIntegerValueException;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * Abstract class representing SDF Vertices.
 *
 * @author jpiat
 * @author kdesnos
 */
public abstract class SDFAbstractVertex extends AbstractVertex<SDFGraph> {

  /** Property nb repeat of the node. */
  public static final String NB_REPEAT = "nbRepeat";

  static {
    AbstractVertex.public_properties.add(SDFAbstractVertex.NB_REPEAT);
  }

  /** The sinks. */
  protected List<SDFSinkInterfaceVertex> sinks;

  /** The sources. */
  protected List<SDFSourceInterfaceVertex> sources;

  /**
   * Constructs a new SDFAbstractVertex using the given Edge Factory ef.
   */
  public SDFAbstractVertex() {
    super();
    this.sinks = new ArrayList<>();
    this.sources = new ArrayList<>();
    setId(UUID.randomUUID().toString());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#addInterface(org.ietr.dftools.algorithm.model.IInterface)
   */
  @Override
  public boolean addInterface(final IInterface port) {
    if (port.getDirection().equals(InterfaceDirection.INPUT)) {
      return addSource((SDFSourceInterfaceVertex) port);
    } else if (port.getDirection().equals(InterfaceDirection.OUTPUT)) {
      return addSink((SDFSinkInterfaceVertex) port);
    }
    return false;
  }

  /**
   * Add a Sink interface vertex linked to the given edge in the base graph.
   *
   * @param sink
   *          the sink
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  public boolean addSink(final SDFSinkInterfaceVertex sink) {
    if (this.sinks == null) {
      this.sinks = new ArrayList<>();
    }
    super.addInterface(sink);
    this.sinks.add(sink);
    if ((getGraphDescription() != null) && (getGraphDescription().getVertex(sink.getName()) == null)) {
      return getGraphDescription().addVertex(sink);
    } else {
      return false;
    }
  }

  /**
   * Add a Source interface vertex linked to the given edge in the base graph.
   *
   * @param src
   *          the src
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  public boolean addSource(final SDFSourceInterfaceVertex src) {
    if (this.sources == null) {
      this.sources = new ArrayList<>();
    }
    super.addInterface(src);
    this.sources.add(src);
    if ((getGraphDescription() != null) && (getGraphDescription().getVertex(src.getName()) == null)) {
      return getGraphDescription().addVertex(src);
    } else {
      return false;
    }
  }

  /**
   * Cleans the vertex by removing all its properties.
   */
  public void clean() {
    this.sinks.clear();
    this.sources.clear();
    this.properties = new PropertyBean();
  }

  /**
   * Gives the edge associated with the given interface.
   *
   * @param graphInterface
   *          The interface the edge is connected to
   * @return The Edge the given interface is connected to
   */
  public SDFEdge getAssociatedEdge(final SDFInterfaceVertex graphInterface) {
    for (final SDFEdge edge : ((SDFGraph) getBase()).incomingEdgesOf(this)) {
      if (((edge.getTargetInterface() != null) && edge.getTargetInterface().equals(graphInterface))) {
        return edge;
      }
    }
    for (final SDFEdge edge : ((SDFGraph) getBase()).outgoingEdgesOf(this)) {
      if ((edge.getSourceInterface() != null) && edge.getSourceInterface().equals(graphInterface)) {
        return edge;
      }
    }
    return null;
  }

  /**
   * Gives the interface vertex associated with the given edge.
   *
   * @param edge
   *          The which is connected to the interface
   * @return The Interface the given edge is connected to
   */
  public SDFInterfaceVertex getAssociatedInterface(final SDFEdge edge) {
    for (final SDFInterfaceVertex source : this.sources) {
      if (source.equals(edge.getTargetInterface())) {
        return source;
      }
    }
    for (final SDFInterfaceVertex sink : this.sinks) {
      if (sink.equals(edge.getSourceInterface())) {
        return sink;
      }
    }
    return null;
  }

  /**
   * Gives the interface with the given name.
   *
   * @param name
   *          The name of the interface
   * @return The interface with the given name, null if the interface does not exist
   */
  public SDFInterfaceVertex getInterface(final String name) {
    for (final SDFInterfaceVertex port : this.sources) {
      if (port.getName().equals(name)) {
        return port;
      }
    }
    for (final SDFInterfaceVertex port : this.sinks) {
      if (port.getName().equals(name)) {
        return port;
      }
    }
    return null;
  }

  /**
   * Getter of the property <tt>sinks</tt>.
   *
   * @return Returns the sinks.
   */
  public List<SDFSinkInterfaceVertex> getSinks() {
    return this.sinks;
  }

  /**
   * Gives the sink with the given name.
   *
   * @param name
   *          The name of the sink to return
   * @return The Sink with the given name
   */
  public SDFInterfaceVertex getSink(final String name) {
    for (final SDFInterfaceVertex sink : this.sinks) {
      if (sink.getName().equals(name)) {
        return sink;
      }
    }
    return null;
  }

  /**
   * Gives the source with the given name.
   *
   * @param name
   *          The name of the source to return
   * @return The Source with the given name
   */
  public SDFInterfaceVertex getSource(final String name) {
    for (final SDFInterfaceVertex source : this.sources) {
      if (source.getName().equals(name)) {
        return source;
      }
    }
    return null;
  }

  @Override
  public abstract SDFAbstractVertex copy();

  /**
   * Getter of the property <tt>sources</tt>.
   *
   * @return Returns the sources.
   */
  public List<SDFSourceInterfaceVertex> getSources() {
    return this.sources;
  }

  /**
   * Remove the interface vertex connected to the given edge in the parent graph. If the interface is are still
   * connected to an edge, they are not removed. This may happen when a removed edge was replaced just before being
   * removed, the replacement edge and the removed edge are thus briefly connected to the same interface.
   *
   * @param edge
   *          the edge
   */
  public void removeSink(final SDFEdge edge) {
    // Check if the interface is still used before removing it
    final SDFSinkInterfaceVertex sinkInterface = (SDFSinkInterfaceVertex) edge.getSourceInterface();
    if (this.getAssociatedEdge(sinkInterface) == null) {
      this.sinks.remove(sinkInterface);
    }
  }

  /**
   * Removes the interface vertex linked to the given edge in the base graph If the interface is are still connected to
   * an edge, they are not removed. This may happen when a removed edge was replaced just before being removed, the
   * replacement edge and the removed edge are thus briefly connected to the same interface.
   *
   * @param edge
   *          the edge
   */
  public void removeSource(final SDFEdge edge) {
    // Check if the interface is still used before removing it
    final SDFSourceInterfaceVertex sourceInterface = (SDFSourceInterfaceVertex) edge.getTargetInterface();
    if (this.getAssociatedEdge(sourceInterface) == null) {
      this.sources.remove(sourceInterface);
    }
  }

  /**
   * Set an interface vertex external edge.
   *
   * @param extEdge
   *          The edge the given interface is to associate
   * @param interfaceVertex
   *          The interface vertex the edge is to associate
   */
  public void setInterfaceVertexExternalLink(final SDFEdge extEdge, final SDFInterfaceVertex interfaceVertex) {
    if (interfaceVertex.getDirection() == InterfaceDirection.OUTPUT) {
      extEdge.setSourceInterface(interfaceVertex);
    } else {
      extEdge.setTargetInterface(interfaceVertex);
    }

  }

  /**
   * Setter of the property <tt>sinks</tt>.
   *
   * @param sinks
   *          The sinks to set.
   */
  public void setSinks(final List<SDFSinkInterfaceVertex> sinks) {
    this.sinks = sinks;
  }

  /**
   * Setter of the property <tt>sources</tt>.
   *
   * @param sources
   *          The sources to set.
   */
  public void setSources(final List<SDFSourceInterfaceVertex> sources) {
    this.sources = sources;
  }

  /**
   * Gives this vertex Nb repeat.
   *
   * @return The number of time to repeat this vertex
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  public long getNbRepeat() {
    if (getPropertyBean().getValue(SDFAbstractVertex.NB_REPEAT) == null) {
      ((SDFGraph) getBase()).computeVRB();
    }
    return getPropertyBean().getValue(SDFAbstractVertex.NB_REPEAT);
  }

  /**
   * Gives this vertex Nb repeat.
   *
   * @return The number of time to repeat this vertex
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  public long getNbRepeatAsLong() {
    if (getPropertyBean().getValue(SDFAbstractVertex.NB_REPEAT) == null) {
      ((SDFGraph) getBase()).computeVRB();
    }
    if (getPropertyBean().getValue(SDFAbstractVertex.NB_REPEAT) instanceof Number) {
      return getPropertyBean().<Number>getValue(SDFAbstractVertex.NB_REPEAT).longValue();
    } else {
      return 1;
    }
  }

  /**
   * Set the number of time to repeat this vertex.
   *
   * @param nbRepeat
   *          The number of time to repeat this vertex
   */
  public void setNbRepeat(final long nbRepeat) {
    getPropertyBean().setValue(SDFAbstractVertex.NB_REPEAT, nbRepeat);
  }

  /**
   * Set the number of time to repeat this vertex as a generic.
   *
   * @param nbRepeat
   *          The number of time to repeat this vertex
   */
  public void setNbRepeat(final Object nbRepeat) {
    getPropertyBean().setValue(SDFAbstractVertex.NB_REPEAT, nbRepeat);
  }

  /**
   * Validate model.
   *
   * @return true, if successful
   * @throws SDF4JException
   *           the SDF 4 J exception
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public boolean validateModel() {
    int i = 0;
    while (i < this.sources.size()) {
      final SDFInterfaceVertex source = this.sources.get(i);
      final SDFEdge outsideEdge = this.getAssociatedEdge(source);
      if (getGraphDescription() != null) {
        final AbstractVertex truePort = getGraphDescription().getVertex(source.getName());
        if (getGraphDescription().outgoingEdgesOf(truePort).isEmpty()) {
          if (WorkflowLogger.getLogger() != null) {
            WorkflowLogger.getLogger().log(Level.INFO,
                "The interface " + source.getName()
                    + " has no inside connection and will be removed for further processing.\n "
                    + "Outside connection has been taken into account for reptition factor computation");
          }
          this.sources.remove(i);
          getGraphDescription().removeVertex(source);
          getBase().removeEdge(outsideEdge);
        } else {
          i++;
        }
      } else {
        i++;
      }
    }
    for (final SDFInterfaceVertex sink : this.sinks) {
      if (getGraphDescription() != null) {
        final AbstractVertex truePort = getGraphDescription().getVertex(sink.getName());
        if (getGraphDescription().incomingEdgesOf(truePort).isEmpty()) {
          WorkflowLogger.getLogger().log(Level.INFO,
              "interface " + sink.getName() + " has no inside connection, consider removing this interface if unused");
          throw (new DFToolsAlgoException(
              "interface " + sink.getName() + " has no inside connection, consider removing this interface if unused"));
        }
      }
    }
    if (getArguments() != null) {
      for (final Argument arg : getArguments().values()) {
        try {
          arg.longValue();
        } catch (final NoIntegerValueException e) {
          throw new DFToolsAlgoException("Could not evaluate argument value", e);
        }
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

}
