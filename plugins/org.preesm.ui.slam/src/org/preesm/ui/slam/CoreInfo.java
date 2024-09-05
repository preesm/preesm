package org.preesm.ui.slam;

public class CoreInfo {
  private String nodeName;
  private String nodeCommunicationRate;
  private String coreName;
  private String coreID;
  private String coreFrequency;
  private String coreCommunicationRate;
  private String coreType;

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

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public String getNodeCommunicationRate() {
    return nodeCommunicationRate;
  }

  public void setNodeCommunicationRate(String nodeCommunicationRate) {
    this.nodeCommunicationRate = nodeCommunicationRate;
  }

  public String getCoreName() {
    return coreName;
  }

  public void setCoreName(String coreName) {
    this.coreName = coreName;
  }

  public String getCoreID() {
    return coreID;
  }

  public void setCoreID(String coreID) {
    this.coreID = coreID;
  }

  public String getCoreFrequency() {
    return coreFrequency;
  }

  public void setCoreFrequency(String coreFrequency) {
    this.coreFrequency = coreFrequency;
  }

  public String getCoreCommunicationRate() {
    return coreCommunicationRate;
  }

  public void setCoreCommunicationRate(String coreCommunicationRate) {
    this.coreCommunicationRate = coreCommunicationRate;
  }

  public String getCoreType() {
    return coreType;
  }

  public void setCoreType(String coreType) {
    this.coreType = coreType;
  }
}
