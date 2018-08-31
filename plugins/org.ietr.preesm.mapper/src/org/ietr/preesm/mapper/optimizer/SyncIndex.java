package org.ietr.preesm.mapper.optimizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.architecture.slam.ComponentInstance;

/**
 * Utility class used to store per component index of the last sync group executed.
 *
 * @author kdesnos
 *
 */
class SyncIndex {
  private final Map<ComponentInstance, Integer> syncIndexPerComponent = new LinkedHashMap<>();

  public SyncIndex(final SyncIndex components) {
    this.syncIndexPerComponent.putAll(components.syncIndexPerComponent);
  }

  public SyncIndex(final Set<ComponentInstance> components) {
    for (final ComponentInstance comp : components) {
      this.syncIndexPerComponent.put(comp, -1);
    }
  }

  public boolean strictlySmallerOrEqual(final SyncIndex syncIndex) {
    boolean result = true;
    for (final ComponentInstance comp : syncIndex.syncIndexPerComponent.keySet()) {
      result &= this.syncIndexPerComponent.get(comp) <= syncIndex.syncIndexPerComponent.get(comp);
    }
    return result;
  }

  public int getSyncIndexOfComponent(final ComponentInstance component) {
    return this.syncIndexPerComponent.getOrDefault(component, Integer.MIN_VALUE);
  }

  public void increment(final ComponentInstance component) {
    this.syncIndexPerComponent.put(component, this.syncIndexPerComponent.get(component) + 1);
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();
    final List<ComponentInstance> components = new ArrayList<>(this.syncIndexPerComponent.keySet());
    components.sort((a, b) -> a.getInstanceName().compareTo(b.getInstanceName()));
    for (final ComponentInstance c : components) {
      result.append(c.getInstanceName() + ": " + this.syncIndexPerComponent.get(c) + '\n');
    }

    return result.toString();
  }

  // @Override
  // public SyncIndex clone() {
  // final SyncIndex res = new SyncIndex(this.syncIndexPerComponent.keySet());
  // this.syncIndexPerComponent.forEach(res.syncIndexPerComponent::put);
  // return res;
  // }

  /**
   * Update this with the max index of itself versus the one of the {@link SyncIndex} passed as a paramater for each
   * {@link ComponentInstance}.
   *
   * @param a
   *          the {@link SyncIndex} used to update the max value
   */
  public void max(final SyncIndex a) {
    for (final ComponentInstance c : a.syncIndexPerComponent.keySet()) {
      this.syncIndexPerComponent.put(c, Math.max(this.syncIndexPerComponent.get(c), a.syncIndexPerComponent.get(c)));
    }
  }
}
