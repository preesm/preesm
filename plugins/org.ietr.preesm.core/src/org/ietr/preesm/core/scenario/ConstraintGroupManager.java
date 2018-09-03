/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2015)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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

import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.preesm.core.scenario.serialize.ExcelConstraintsParser;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;

// TODO: Auto-generated Javadoc
/**
 * container and manager of Constraint groups. It can load and store constraint groups
 *
 * @author mpelcat
 */
public class ConstraintGroupManager {

  /** List of all constraint groups. */
  private final Set<ConstraintGroup> constraintgroups;

  /** Path to a file containing constraints. */
  private String excelFileURL = "";

  /**
   * Instantiates a new constraint group manager.
   */
  public ConstraintGroupManager() {
    this.constraintgroups = new LinkedHashSet<>();
  }

  /**
   * Adds the constraint group.
   *
   * @param cg
   *          the cg
   */
  public void addConstraintGroup(final ConstraintGroup cg) {

    this.constraintgroups.add(cg);
  }

  /**
   * Adding a simple constraint on one vertex and one operator.
   *
   * @param opId
   *          the op id
   * @param vertex
   *          the vertex
   */
  public void addConstraint(final String opId, final SDFAbstractVertex vertex) {

    final Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

    if (cgSet.isEmpty()) {
      final ConstraintGroup cg = new ConstraintGroup();
      cg.addOperatorId(opId);
      cg.addActorPath(vertex.getInfo());
      this.constraintgroups.add(cg);
    } else {
      ((ConstraintGroup) cgSet.toArray()[0]).addActorPath(vertex.getInfo());
    }
  }

  /**
   * Adds the constraint.
   *
   * @param opId
   *          the op id
   * @param vertex
   *          the vertex
   */
  public void addConstraint(final String opId, final AbstractActor vertex) {

    final Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

    if (cgSet.isEmpty()) {
      final ConstraintGroup cg = new ConstraintGroup();
      cg.addOperatorId(opId);
      cg.addActorPath(vertex.getVertexPath());
      this.constraintgroups.add(cg);
    } else {
      ((ConstraintGroup) cgSet.toArray()[0]).addActorPath(vertex.getVertexPath());
    }
  }

  /**
   * Adding a constraint group on several vertices and one core.
   *
   * @param opId
   *          the op id
   * @param vertexSet
   *          the vertex set
   */
  public void addConstraints(final String opId, final Set<String> vertexSet) {

    final Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

    if (cgSet.isEmpty()) {
      final ConstraintGroup cg = new ConstraintGroup();
      cg.addOperatorId(opId);
      cg.addVertexPaths(vertexSet);
      this.constraintgroups.add(cg);
    } else {
      ((ConstraintGroup) cgSet.toArray()[0]).addVertexPaths(vertexSet);
    }
  }

  /**
   * Removing a simple constraint on one vertex and one core.
   *
   * @param opId
   *          the op id
   * @param vertex
   *          the vertex
   */
  public void removeConstraint(final String opId, final SDFAbstractVertex vertex) {

    final Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

    if (!cgSet.isEmpty()) {
      for (final ConstraintGroup cg : cgSet) {
        cg.removeVertexPath(vertex.getInfo());
      }
    }
  }

  /**
   * Removes the constraint.
   *
   * @param opId
   *          the op id
   * @param vertex
   *          the vertex
   */
  public void removeConstraint(final String opId, final AbstractActor vertex) {
    final Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

    if (!cgSet.isEmpty()) {
      for (final ConstraintGroup cg : cgSet) {
        cg.removeVertexPath(vertex.getVertexPath());
      }
    }
  }

  /**
   * Gets the constraint groups.
   *
   * @return the constraint groups
   */
  public Set<ConstraintGroup> getConstraintGroups() {

    return new LinkedHashSet<>(this.constraintgroups);
  }

  /**
   * Gets the graph constraint groups.
   *
   * @param vertex
   *          the vertex
   * @return the graph constraint groups
   */
  public Set<ConstraintGroup> getGraphConstraintGroups(final SDFAbstractVertex vertex) {
    final Set<ConstraintGroup> graphConstraintGroups = new LinkedHashSet<>();

    for (final ConstraintGroup cg : this.constraintgroups) {
      if (cg.hasVertexPath(vertex.getInfo())) {
        graphConstraintGroups.add(cg);
      }
    }

    return graphConstraintGroups;
  }

  /**
   * Gets the op constraint groups.
   *
   * @param opId
   *          the op id
   * @return the op constraint groups
   */
  public Set<ConstraintGroup> getOpConstraintGroups(final String opId) {
    final Set<ConstraintGroup> graphConstraintGroups = new LinkedHashSet<>();

    for (final ConstraintGroup cg : this.constraintgroups) {
      if (cg.hasOperatorId(opId)) {
        graphConstraintGroups.add(cg);
      }
    }

    return graphConstraintGroups;
  }

  /**
   * Checks if is compatible to constraints.
   *
   * @param vertex
   *          the vertex
   * @param opId
   *          the op id
   * @return true, if is compatible to constraints
   */
  public boolean isCompatibleToConstraints(final SDFAbstractVertex vertex, final String opId) {
    final Set<ConstraintGroup> opGroups = getOpConstraintGroups(opId);
    final Set<ConstraintGroup> graphGroups = getGraphConstraintGroups(vertex);

    opGroups.retainAll(graphGroups);

    return !opGroups.isEmpty();
  }

  /**
   * Removes the all.
   */
  public void removeAll() {

    this.constraintgroups.clear();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "";

    for (final ConstraintGroup cg : this.constraintgroups) {
      s += cg.toString();
    }

    return s;
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
   * Import constraints.
   *
   * @param currentScenario
   *          the current scenario
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  public void importConstraints(final PreesmScenario currentScenario)
      throws InvalidModelException, FileNotFoundException, CoreException {
    if (!this.excelFileURL.isEmpty() && (currentScenario != null)) {
      final ExcelConstraintsParser parser = new ExcelConstraintsParser(currentScenario);
      parser.parse(this.excelFileURL, currentScenario.getOperatorIds());
    }
  }

  /**
   * Update.
   */
  public void update() {
    removeAll();
  }
}
