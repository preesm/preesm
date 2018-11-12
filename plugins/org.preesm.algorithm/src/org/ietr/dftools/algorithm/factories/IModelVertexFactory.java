/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
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
package org.ietr.dftools.algorithm.factories;

import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.IInterface;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Interface to create vertex in the given model.
 *
 * @author jpiat
 * @param <V>
 *          The model of vertex to create
 */
public interface IModelVertexFactory<V extends AbstractVertex<?>> {

  /**
   * Creates a vertex with the given parameters.
   *
   * @param vertexElt
   *          The DOM element from which to create the vertex
   * @return The created vertex
   */
  public V createVertex(Element vertexElt);

  /**
   * Creates a new ModelVertex object.
   *
   * @param kind
   *          the kind
   * @return the v
   */
  public V createVertex(String kind);

  /**
   * Creates a new ModelVertex object.
   *
   * @param name
   *          the name
   * @param dir
   *          the dir
   * @return the i interface
   */
  public IInterface createInterface(String name, int dir);

  /**
   * Gets the property.
   *
   * @param elt
   *          the elt
   * @param propertyName
   *          the property name
   * @return the property
   */
  public default String getProperty(final Element elt, final String propertyName) {
    final NodeList childList = elt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(propertyName)) {
        return childList.item(i).getTextContent();
      }
    }
    return null;
  }

}
