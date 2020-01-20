package org.preesm.codegen.xtend.spider2.utils;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;

public class Spider2CodegenActor {
  /** The type of the actor */
  private final String type;

  /** The actor */
  private final AbstractActor actor;

  /** The refinement ConfigInputPort list */
  private final List<ConfigInputPort> refinementConfigInputPorts;

  /** The refinement ConfigOutputPort list */
  private final List<ConfigOutputPort> refinementConfigOutputPorts;

  public Spider2CodegenActor(final String type, final AbstractActor actor) {
    this.type = type;
    this.actor = actor;
    if (actor instanceof Actor) {
      final Actor a = (Actor) (actor);
      if (a.getRefinement() instanceof CHeaderRefinement) {
        final CHeaderRefinement refinement = (CHeaderRefinement) (a.getRefinement());
        this.refinementConfigInputPorts = refinement.getLoopConfigInputPorts();
        this.refinementConfigOutputPorts = refinement.getLoopConfigOutputPorts();
      } else {
        this.refinementConfigInputPorts = new ArrayList<>();
        this.refinementConfigOutputPorts = new ArrayList<>();
      }
    } else {
      this.refinementConfigInputPorts = new ArrayList<>();
      this.refinementConfigOutputPorts = new ArrayList<>();
    }
  }

  public String getType() {
    return this.type;
  }

  public AbstractActor getActor() {
    return this.actor;
  }

  public List<ConfigInputPort> getRefinementConfigInputPorts() {
    return this.refinementConfigInputPorts;
  }

  public List<ConfigOutputPort> getRefinementConfigOutputPorts() {
    return this.refinementConfigOutputPorts;
  }
}
