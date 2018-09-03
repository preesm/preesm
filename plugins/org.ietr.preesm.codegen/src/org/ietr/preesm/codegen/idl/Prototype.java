/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2009 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
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
package org.ietr.preesm.codegen.idl;

import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;

// TODO: Auto-generated Javadoc
/**
 * Function prototype.
 *
 * @author mpelcat
 */
public class Prototype implements IRefinement {

  /** The function name. */
  private String functionName;

  /** The nb args. */
  private int nbArgs;

  /**
   * This map associates {@link CodeGenArgument} of the prototype with an integer that represent the order of the
   * argument in the prototype.
   */
  private final Map<CodeGenArgument, Integer> arguments;

  /**
   * This map associates {@link CodeGenParameter} of the prototype with an integer that represent the order of the
   * argument in the prototype.
   */
  private final Map<CodeGenParameter, Integer> parameters;

  /**
   * Instantiates a new prototype.
   */
  public Prototype() {
    this.functionName = "";
    this.arguments = new LinkedHashMap<>();
    this.parameters = new LinkedHashMap<>();
    this.nbArgs = 0;
  }

  /**
   * Instantiates a new prototype.
   *
   * @param name
   *          the name
   */
  public Prototype(final String name) {
    this.functionName = name;
    this.arguments = new LinkedHashMap<>();
    this.parameters = new LinkedHashMap<>();
    this.nbArgs = 0;
  }

  /**
   * Gets the function name.
   *
   * @return the function name
   */
  public String getFunctionName() {
    return this.functionName;
  }

  /**
   * Sets the function name.
   *
   * @param name
   *          the new function name
   */
  public void setFunctionName(final String name) {
    this.functionName = name;
  }

  /**
   * Adds the argument.
   *
   * @param arg
   *          the arg
   */
  public void addArgument(final CodeGenArgument arg) {
    this.arguments.put(arg, this.nbArgs);
    this.nbArgs++;
  }

  /**
   * Adds the parameter.
   *
   * @param parameterName
   *          the parameter name
   */
  public void addParameter(final CodeGenParameter parameterName) {
    this.parameters.put(parameterName, this.nbArgs);
    this.nbArgs++;
  }

  /**
   * Gets the arguments.
   *
   * @return the arguments
   */
  public Map<CodeGenArgument, Integer> getArguments() {
    return this.arguments;
  }

  /**
   * Gets the parameters.
   *
   * @return the parameters
   */
  public Map<CodeGenParameter, Integer> getParameters() {
    return this.parameters;
  }

  /**
   * Gets the nb args.
   *
   * @return the nb args
   */
  public int getNbArgs() {
    return this.nbArgs;
  }

}
