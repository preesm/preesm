/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
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
package org.ietr.preesm.core.scenario;

import org.ietr.preesm.core.scenario.serialize.ExcelVariablesParser;
import org.preesm.algorithm.model.parameters.Variable;
import org.preesm.algorithm.model.parameters.VariableSet;
import org.preesm.algorithm.model.sdf.SDFGraph;

// TODO: Auto-generated Javadoc
/**
 * Handles graph variables which values are redefined in the scenario.
 *
 * @author mpelcat
 */
public class VariablesManager {

  /** The variables. */
  private final VariableSet variables;

  /** Path to a file containing variables. */
  private String excelFileURL = "";

  /**
   * Instantiates a new variables manager.
   */
  public VariablesManager() {
    this.variables = new VariableSet();
  }

  /**
   * Sets the variable.
   *
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public void setVariable(final String name, final String value) {

    if (this.variables.keySet().contains(name)) {
      this.variables.get(name).setValue(value);
    } else {
      this.variables.put(name, new Variable(name, value));
    }
  }

  /**
   * Gets the variables.
   *
   * @return the variables
   */
  public VariableSet getVariables() {
    return this.variables;
  }

  /**
   * Removes the variable.
   *
   * @param varName
   *          the var name
   */
  public void removeVariable(final String varName) {
    this.variables.remove(varName);
  }

  /**
   * Gets the excel file URL.
   *
   * @return the excel file URL
   */
  public String getExcelFileURL() {
    return this.excelFileURL;
  }

  /**
   * Sets the excel file URL.
   *
   * @param excelFileURL
   *          the new excel file URL
   */
  public void setExcelFileURL(final String excelFileURL) {
    this.excelFileURL = excelFileURL;
  }

  /**
   * Import variables.
   *
   * @param currentScenario
   *          the current scenario
   */
  public void importVariables(final PreesmScenario currentScenario) {
    if (!this.excelFileURL.isEmpty() && (currentScenario != null)) {
      final ExcelVariablesParser parser = new ExcelVariablesParser(currentScenario);

      try {
        parser.parse(this.excelFileURL);
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Update with.
   *
   * @param sdfGraph
   *          the sdf graph
   */
  public void updateWith(final SDFGraph sdfGraph) {
    getVariables().clear();
    for (final String v : sdfGraph.getVariables().keySet()) {
      setVariable(v, sdfGraph.getVariable(v).getValue());
    }
  }

}
