/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
package org.ietr.preesm.pimm.algorithm.spider.codegen.utils;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * The Class SpiderNameGenerator.
 */
public final class SpiderNameGenerator {

  /**
   * Private constructor: prevents instantiation by client code.
   */
  private SpiderNameGenerator() {

  }

  /**
   * Returns the name of the subgraph pg.
   *
   * @param pg
   *          the pg
   * @return the subraph name
   */
  public static String getSubraphName(final PiGraph pg) {
    return pg.getName() + "_subGraph";
  }

  /**
   * Gets the core type name.
   *
   * @param coreType
   *          the core type
   * @return the core type name
   */
  public static String getCoreTypeName(final String coreType) {
    return "CORE_TYPE_" + coreType.toUpperCase();
  }

  /**
   * Returns the name of the variable pointing to the C++ object corresponding to AbstractActor aa.
   *
   * @param aa
   *          the aa
   * @return the vertex name
   */
  public static String getVertexName(final AbstractVertex aa) {
    switch (SpiderTypeConverter.getType(aa)) {
      case PISDF_TYPE_BODY:
        return "bo_" + aa.getName();
      case PISDF_TYPE_CONFIG:
        return "cf_" + aa.getName();
      case PISDF_TYPE_IF:
        return "if_" + aa.getName();
      default:
    }
    return null;
  }

  /**
   * Returns the name of the building method for the PiGraph pg.
   *
   * @param pg
   *          the pg
   * @return the method name
   */
  public static String getMethodName(final PiGraph pg) {
    String finalName = pg.getName();
    PiGraph containingPiGraph = pg.getContainingPiGraph();
    while (containingPiGraph != null) {
      finalName = containingPiGraph.getName() + "_" + finalName;
      containingPiGraph = containingPiGraph.getContainingPiGraph();
    }
    return finalName;
  }

  /**
   * Gets the function name.
   *
   * @param aa
   *          the aa
   * @return the function name
   */
  public static String getFunctionName(final AbstractActor aa) {
    return getMethodName(aa.getContainingPiGraph()) + "_" + aa.getName();
  }

  /**
   * Returns the name of the parameter.
   *
   * @param p
   *          the p
   * @return the parameter name
   */
  public static String getParameterName(final Parameter p) {
    return "param_" + p.getName();
  }

  /**
   * Gets the core name.
   *
   * @param core
   *          the core
   * @return the core name
   */
  public static String getCoreName(final String core) {
    return "CORE_" + core.toUpperCase();
  }
}
