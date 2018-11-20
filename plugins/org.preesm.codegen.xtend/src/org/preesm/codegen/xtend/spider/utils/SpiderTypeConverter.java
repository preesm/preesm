/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2016)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.preesm.codegen.xtend.spider.utils;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.InterfaceActor;

// TODO: Auto-generated Javadoc
/**
 * The Class SpiderTypeConverter.
 */
public final class SpiderTypeConverter {

  /**
   * The Enum PiSDFType.
   */
  public enum PiSDFType {

  /** The pisdf type body. */
  PISDF_TYPE_BODY,
  /** The pisdf type config. */
  PISDF_TYPE_CONFIG,
  /** The pisdf type if. */
  PISDF_TYPE_IF
  }

  /**
   * The Enum PiSDFSubType.
   */
  public enum PiSDFSubType {

    /** The pisdf subtype normal. */
    PISDF_SUBTYPE_NORMAL,
    /** The pisdf subtype broadcast. */
    PISDF_SUBTYPE_BROADCAST,
    /** The pisdf subtype fork. */
    PISDF_SUBTYPE_FORK,
    /** The pisdf subtype join. */
    PISDF_SUBTYPE_JOIN,
    /** The pisdf subtype end. */
    PISDF_SUBTYPE_END,
    /** The pisdf subtype input if. */
    PISDF_SUBTYPE_INPUT_IF,
    /** The pisdf subtype output if. */
    PISDF_SUBTYPE_OUTPUT_IF
  }

  /**
   * Private constructor: prevents instantiation by client code.
   */
  private SpiderTypeConverter() {
  }

  /**
   * Gets the type.
   *
   * @param aa
   *          the aa
   * @return the type
   */
  public static PiSDFType getType(final AbstractVertex aa) {
    if (aa instanceof InterfaceActor) {
      return PiSDFType.PISDF_TYPE_IF;
    } else if ((aa instanceof Actor) && ((Actor) aa).isConfigurationActor()) {
      return PiSDFType.PISDF_TYPE_CONFIG;
    } else {
      return PiSDFType.PISDF_TYPE_BODY;
    }
  }

  /**
   * Gets the sub type.
   *
   * @param aa
   *          the aa
   * @return the sub type
   */
  public static PiSDFSubType getSubType(final AbstractVertex aa) {
    switch (SpiderTypeConverter.getType(aa)) {
      case PISDF_TYPE_BODY:
      case PISDF_TYPE_CONFIG:
        return PiSDFSubType.PISDF_SUBTYPE_NORMAL;
      case PISDF_TYPE_IF:
        if (((AbstractActor) aa).getDataInputPorts().size() > 0) {
          return PiSDFSubType.PISDF_SUBTYPE_OUTPUT_IF;
        } else {
          return PiSDFSubType.PISDF_SUBTYPE_INPUT_IF;
        }
      default:
    }
    return null;
  }

}
