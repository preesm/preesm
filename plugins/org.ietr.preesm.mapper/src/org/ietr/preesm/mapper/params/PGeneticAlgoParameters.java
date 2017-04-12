/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper.params;

import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

// TODO: Auto-generated Javadoc
/**
 * Parameters for task scheduling genetic algorithm multithread.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class PGeneticAlgoParameters {

  /** The population size. */
  // Number of individuals the algorithm will keep in the best population
  private int populationSize;

  /** The generation number. */
  // Number of generation performed by the PGenetic algorithm
  private int generationNumber;

  /** The proc number. */
  // Number of thread/processor available to perform the PGenetic Algorithm
  private int procNumber;

  /** The pfastused 2 makepopulation. */
  // Boolean to determine the type of algorithm to make the population
  private boolean pfastused2makepopulation;

  /** Time in seconds between two FAST probabilistic hops in the critical path. */
  private int fastTime = 200;

  /** Time in seconds spent in one FAST local neighborhood. */
  private int fastLocalSearchTime = 10;

  /** Number of fast iterations to execute before stopping PFast. */
  private int fastNumber = 100;

  /**
   * Constructors.
   *
   * @param textParameters
   *          the text parameters
   */

  public PGeneticAlgoParameters(final Map<String, String> textParameters) {

    this.generationNumber = Integer.valueOf(textParameters.get("generationNumber"));
    this.populationSize = Integer.valueOf(textParameters.get("populationSize"));
    this.procNumber = Integer.valueOf(textParameters.get("procNumber"));
    this.pfastused2makepopulation = Boolean.valueOf(textParameters.get("pfastused2makepopulation"));
    if (Integer.valueOf(textParameters.get("fastTime")) > 0) {
      this.fastTime = Integer.valueOf(textParameters.get("fastTime"));
    }
    if (Integer.valueOf(textParameters.get("fastLocalSearchTime")) > 0) {
      this.fastLocalSearchTime = Integer.valueOf(textParameters.get("fastLocalSearchTime"));
    }
    if (Integer.valueOf(textParameters.get("fastNumber")) != 0) {
      this.fastNumber = Integer.valueOf(textParameters.get("fastNumber"));
    }

    WorkflowLogger.getLogger().log(Level.INFO,
        "The Genetic algo parameters are: generationNumber; populationSize;"
            + "procNumber; pfastused2makepopulation=true/false; fastTime in seconds; fastLocalSearchTime in seconds; fastNumber");
  }

  /**
   * Checks if is pfastused 2 makepopulation.
   *
   * @return the pfastused2makepopulation
   */
  public boolean isPfastused2makepopulation() {
    return this.pfastused2makepopulation;
  }

  /**
   * Sets the pfastused 2 makepopulation.
   *
   * @param pfastused2makepopulation
   *          the pfastused2makepopulation to set
   */
  public void setPfastused2makepopulation(final boolean pfastused2makepopulation) {
    this.pfastused2makepopulation = pfastused2makepopulation;
  }

  /**
   * Gets the population size.
   *
   * @return the populationSize
   */
  public int getPopulationSize() {
    return this.populationSize;
  }

  /**
   * Sets the population size.
   *
   * @param populationSize
   *          the populationSize to set
   */
  public void setPopulationSize(final int populationSize) {
    this.populationSize = populationSize;
  }

  /**
   * Gets the generation number.
   *
   * @return the generationNumber
   */
  public int getGenerationNumber() {
    return this.generationNumber;
  }

  /**
   * Sets the generation number.
   *
   * @param generationNumber
   *          the generationNumber to set
   */
  public void setGenerationNumber(final int generationNumber) {
    this.generationNumber = generationNumber;
  }

  /**
   * Gets the processor number.
   *
   * @return the processorNumber
   */
  public int getProcessorNumber() {
    return this.procNumber;
  }

  /**
   * Sets the processor number.
   *
   * @param processorNumber
   *          the processorNumber to set
   */
  public void setProcessorNumber(final int processorNumber) {
    this.procNumber = processorNumber;
  }

  /**
   * Returns the Number of fast iterations to execute before stopping PFast.
   *
   * @return the fast number
   */
  public int getFastNumber() {
    return this.fastNumber;
  }

  /**
   * Returns the time in seconds for one whole FAST process.
   *
   * @return the fast time
   */
  public int getFastTime() {
    return this.fastTime;
  }

  /**
   * Returns the time in seconds spent in one FAST local neighborhood.
   *
   * @return the fast local search time
   */
  public int getFastLocalSearchTime() {
    return this.fastLocalSearchTime;
  }

}
