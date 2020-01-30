package org.preesm.codegen.xtend.spider2.utils;

import org.preesm.model.slam.ComponentInstance;

/**
 * This class encompass processing element information in a convenient and compatible manner with the Spider2 API. For
 * now, it just support name but in the future, if the S-LAM model of PREESM evolves, it could support other features
 * PEType (LRT or PHYS), thread affinity, etc.
 * 
 * @author farresti
 *
 */
public class Spider2CodegenPE {
  /** The component of the PE */
  private final ComponentInstance component;

  public Spider2CodegenPE(final ComponentInstance component) {
    this.component = component;
  }

  public String getName() {
    return this.component.getInstanceName();
  }

  public String getTypeName() {
    return this.component.getComponent().getVlnv().getName();
  }

  public int getAffinity() {
    return this.component.getHardwareId();
  }
}
