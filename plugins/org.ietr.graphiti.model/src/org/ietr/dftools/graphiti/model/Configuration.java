/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2009 - 2011)
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
package org.ietr.dftools.graphiti.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class provides the configuration for a {@link Graph}. A configuration is defined by an extension point, and
 * contains attributes and parameters that are defined for the graph, vertices and edges.
 * <p>
 * Attributes are specified for classes of objects: for example, all vertices of type T will have an attribute A which
 * has the value V. Examples of such attributes for vertices are "shape" or "color".
 * </p>
 * <p>
 * Parameters are specified for each instance of graph/vertex/edge. Examples of parameters are position or id.
 * </p>
 *
 * @author Matthieu Wipliez
 *
 */
public class Configuration {

  /** The contributor id. */
  private final String contributorId;

  /**
   * A edge type name -> edge type object map.
   */
  private final Map<String, ObjectType> edgeTypes;

  /**
   * The file format associated with this configuration.
   */
  private final FileFormat fileFormat;

  /**
   * A graph type name -> graph type object map.
   */
  private final Map<String, ObjectType> graphTypes;

  /**
   * The configuration absolute file name.
   */
  private final String name;

  /**
   * The refinement policy.
   */
  private IRefinementPolicy refinementPolicy;

  /**
   * The validator called when the graph is about to be saved.
   */
  private final IValidator validator;

  /**
   * A vertex type name -> vertex type object map.
   */
  private final Map<String, ObjectType> vertexTypes;

  /**
   * Creates a new document configuration.
   *
   * @param name
   *          the name of this configuration
   * @param contributorId
   *          the identifier of the contributor of this configuration
   * @param fileFormat
   *          the associated file format
   * @param graphTypes
   *          types of graphs
   * @param vertexTypes
   *          types of vertices
   * @param edgeTypes
   *          types of edges
   * @param validator
   *          the validator
   * @param policy
   *          the policy
   */
  public Configuration(final String name, final String contributorId, final FileFormat fileFormat,
      final Map<String, ObjectType> graphTypes, final Map<String, ObjectType> vertexTypes,
      final Map<String, ObjectType> edgeTypes, final IValidator validator, final IRefinementPolicy policy) {
    this.contributorId = contributorId;
    this.edgeTypes = edgeTypes;
    this.fileFormat = fileFormat;
    this.name = name;
    this.graphTypes = graphTypes;
    if (policy == null) {
      this.refinementPolicy = new DefaultRefinementPolicy();
    } else {
      this.refinementPolicy = policy;
    }
    this.validator = validator;
    this.vertexTypes = vertexTypes;
  }

  /**
   * Returns the identifier of the contributor of this configuration.
   *
   * @return the identifier of the contributor of this configuration
   */
  public String getContributorId() {
    return this.contributorId;
  }

  /**
   * Returns the edge type whose name matches the given name.
   *
   * @param name
   *          The name of the edge type we're looking for.
   * @return The relevant edge type.
   */
  public ObjectType getEdgeType(final String name) {
    return this.edgeTypes.get(name);
  }

  /**
   * Returns the edge types.
   *
   * @return A set of edge types.
   */
  public Set<ObjectType> getEdgeTypes() {
    return new TreeSet<>(this.edgeTypes.values());
  }

  /**
   * Returns the file format associated with this configuration.
   *
   * @return A {@link FileFormat} associated with this configuration.
   */
  public FileFormat getFileFormat() {
    return this.fileFormat;
  }

  /**
   * Returns the graph type whose name matches the given name.
   *
   * @param name
   *          The name of the graph type we're looking for.
   * @return The relevant graph type.
   */
  public ObjectType getGraphType(final String name) {
    return this.graphTypes.get(name);
  }

  /**
   * Returns the graph types.
   *
   * @return A set of graph types.
   */
  public Set<ObjectType> getGraphTypes() {
    return new TreeSet<>(this.graphTypes.values());
  }

  /**
   * Returns the name of this configuration.
   *
   * @return the name of this configuration
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the refinement policy for this configuration.
   *
   * @return the refinement policy for this configuration.
   */
  public IRefinementPolicy getRefinementPolicy() {
    return this.refinementPolicy;
  }

  /**
   * Returns the validator for this configuration.
   *
   * @return the validator for this configuration.
   */
  public IValidator getValidator() {
    return this.validator;
  }

  /**
   * Returns the vertex type whose name matches the given name.
   *
   * @param name
   *          The name of the vertex type we're looking for.
   * @return The relevant vertex type.
   */
  public ObjectType getVertexType(final String name) {
    return this.vertexTypes.get(name);
  }

  /**
   * Returns the vertex types.
   *
   * @return A set of vertex types.
   */
  public Set<ObjectType> getVertexTypes() {
    return new TreeSet<>(this.vertexTypes.values());
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + this.name + "] " + this.fileFormat;
  }

}
