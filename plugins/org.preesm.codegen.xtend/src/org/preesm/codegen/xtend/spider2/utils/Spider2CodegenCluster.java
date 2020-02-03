package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.preesm.model.slam.ComponentInstance;

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
