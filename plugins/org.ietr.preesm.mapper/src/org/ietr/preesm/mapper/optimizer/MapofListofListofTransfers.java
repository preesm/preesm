package org.ietr.preesm.mapper.optimizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 *
 * @author anmorvan
 *
 */
public class MapofListofListofTransfers extends LinkedHashMap<ComponentInstance, List<List<TransferVertex>>> {

  private Map<TransferVertex, List<TransferVertex>> lookupSyncGroup = new LinkedHashMap<>();

  public MapofListofListofTransfers() {
    super();
  }

  /**
   *
   */
  private static final long serialVersionUID = -8985393970586882459L;

  /**
   */
  public List<List<TransferVertex>> getOrPutDefault(final ComponentInstance component) {
    if (!containsKey(component)) {
      put(component, new ArrayList<>());
    }
    return get(component);

  }

  public boolean isEmpty(final ComponentInstance component) {
    return getOrPutDefault(component).isEmpty();
  }

  public Set<ComponentInstance> getComponents() {
    return this.keySet();
  }

  /**
   *
   */
  public List<TransferVertex> lookupSyncGroup(DAGVertex vertex) {
    final ComponentInstance cc = (ComponentInstance) vertex.getPropertyBean().getValue("Operator");
    final List<List<TransferVertex>> groups = this.getOrPutDefault(cc);
    for (List<TransferVertex> group : groups) {
      if (group.contains(vertex)) {
        if (lookupSyncGroup.get(vertex) != group) {
          throw new RuntimeException();
        }
        return group;
      }
    }
    return null;
  }

  /**
   */
  public void addNewGroup(final ComponentInstance component, final TransferVertex currentVertex) {
    final ArrayList<TransferVertex> newGroup = new ArrayList<>();
    newGroup.add(currentVertex);
    getOrPutDefault(component).add(newGroup);
    lookupSyncGroup.put(currentVertex, newGroup);
  }

  public List<TransferVertex> getLastGroup(final ComponentInstance component) {
    final List<List<TransferVertex>> orPutDefault = getOrPutDefault(component);
    return orPutDefault.get(orPutDefault.size() - 1);
  }

  /**
   *
   */
  public void addInGroup(final ComponentInstance component, final TransferVertex currentVertex) {
    final List<TransferVertex> lastGroup = getLastGroup(component);
    lastGroup.add(currentVertex);
    lookupSyncGroup.put(currentVertex, lastGroup);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Entry<ComponentInstance, List<List<TransferVertex>>> e : this.entrySet()) {
      final ComponentInstance componentInstance = e.getKey();
      sb.append("  - " + componentInstance.getInstanceName() + ": ");
      for (final List<TransferVertex> set : e.getValue()) {
        if (e.getValue().get(0) != set) {
          sb.append("-");
        }
        sb.append(set.size() + "[");
        for (TransferVertex tv : set) {
          if (tv.getSource().getEffectiveComponent() == componentInstance) {
            sb.append("S");
          } else if (tv.getTarget().getEffectiveComponent() == componentInstance) {
            sb.append("R");
          } else {
            sb.append("T");
          }
        }
        sb.append("]");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

}
