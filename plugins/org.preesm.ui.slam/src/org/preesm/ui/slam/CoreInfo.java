package org.preesm.ui.slam;

public class CoreInfo {
  private final String nodeName;
  private final String nodeCommunicationRate;
  private final String coreName;
  private final String coreID;
  private final String coreFrequency;
  private final String coreCommunicationRate;
  private final String coreType;

  public CoreInfo(String nodeName, String nodeCommunicationRate, String coreName, String coreID, String coreFrequency,
      String coreCommunicationRate, String coreType) {
    this.nodeName = nodeName;
    this.nodeCommunicationRate = nodeCommunicationRate;
    this.coreName = coreName;
    this.coreID = coreID;
    this.coreCommunicationRate = coreCommunicationRate;
    this.coreFrequency = coreFrequency;
    this.coreType = coreType;
  }

  public String getNodeName() {
    return nodeName;
  }

  public String getNodeCommunicationRate() {
    return nodeCommunicationRate;
  }

  public String getCoreName() {
    return coreName;
  }

  public String getCoreID() {
    return coreID;
  }

  public String getCoreFrequency() {
    return coreFrequency;
  }

  public String getCoreCommunicationRate() {
    return coreCommunicationRate;
  }

  public String getCoreType() {
    return coreType;
  }
}
