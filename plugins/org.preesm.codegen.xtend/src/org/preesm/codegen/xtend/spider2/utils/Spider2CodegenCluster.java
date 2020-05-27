/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
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
package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.preesm.model.slam.ComponentInstance;

/**
 * 
 * Class representing an architecture cluster in the sense of Spider2
 * 
 * @author farresti
 *
 */
public class Spider2CodegenCluster {
  /** The MemoryUnit size of the cluster */
  private long memoryUnitSize;

  /** The list of processing elements of the cluster */
  private final List<Spider2CodegenPE> peList = new ArrayList<>();

  /** The name of the cluster */
  private String name;

  /**
   * Constructor of the class.
   * 
   * @param memUnit
   *          the component of the memory unit of the cluster.
   * @param peList
   *          the list of pe
   */
  public Spider2CodegenCluster(final ComponentInstance memUnit, final List<ComponentInstance> peList) {
    /* Get the size of the MemUnit */
    if (!memUnit.getParameters().isEmpty()) {
      boolean found = false;
      for (final Entry<String, String> p : memUnit.getParameters()) {
        if (p.getKey().equals("size")) {
          memoryUnitSize = Long.parseLong(p.getValue());
          found = true;
          break;
        }
      }
      if (!found) {
        memoryUnitSize = 1024 * 1024 * (long) (1024);
      }
    } else {
      memoryUnitSize = 1024 * 1024 * (long) (1024);
    }

    /* Parse the PE connected to the unit */
    this.name = peList.get(0).getComponent().getVlnv().getName();
    for (final ComponentInstance pe : peList) {
      this.peList.add(new Spider2CodegenPE(pe));
      final String peTypeName = pe.getComponent().getVlnv().getName();
      if (!this.name.contains(peTypeName)) {
        this.name += ("_" + peTypeName);
      }
    }
  }

  /**
   * 
   * @return memory unit size.
   */
  public long getMemoryUnitSize() {
    return this.memoryUnitSize;
  }

  /**
   * 
   * @return list of Spider2CodegenPE in the cluster
   */
  public List<Spider2CodegenPE> getProcessingElements() {
    return this.peList;
  }

  /**
   * 
   * @return number of Spider2CodegenPE in the cluster
   */
  public int getPeCount() {
    return this.peList.size();
  }

  /**
   * 
   * @return name of the cluster
   */
  public String getName() {
    return this.name;
  }
}
