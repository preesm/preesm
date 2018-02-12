package org.ietr.preesm.core.scenario.papi;

import java.util.Locale;

/**
 *
 *
 */
public class PapiHardware {

  private String    vendor;
  private int       vendorCode;
  private String    model;
  private int       modelCode;
  private double    cpuRevision;
  private PapiCpuID cpuID;
  private int       cpuMaxMegahertz;
  private int       cpuMinMegahertz;

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public int getVendorCode() {
    return vendorCode;
  }

  public void setVendorCode(int vendorCode) {
    this.vendorCode = vendorCode;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public int getModelCode() {
    return modelCode;
  }

  public void setModelCode(int modelCode) {
    this.modelCode = modelCode;
  }

  public double getCpuRevision() {
    return cpuRevision;
  }

  public void setCpuRevision(double cpuRevision) {
    this.cpuRevision = cpuRevision;
  }

  public PapiCpuID getCpuID() {
    return cpuID;
  }

  public void setCpuID(PapiCpuID cpuID) {
    this.cpuID = cpuID;
  }

  public int getCpuMaxMegahertz() {
    return cpuMaxMegahertz;
  }

  public void setCpuMaxMegahertz(int cpuMaxMegahertz) {
    this.cpuMaxMegahertz = cpuMaxMegahertz;
  }

  public int getCpuMinMegahertz() {
    return cpuMinMegahertz;
  }

  public void setCpuMinMegahertz(int cpuMinMegahertz) {
    this.cpuMinMegahertz = cpuMinMegahertz;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(int threads) {
    this.threads = threads;
  }

  public int getCores() {
    return cores;
  }

  public void setCores(int cores) {
    this.cores = cores;
  }

  public int getSockets() {
    return sockets;
  }

  public void setSockets(int sockets) {
    this.sockets = sockets;
  }

  public int getNodes() {
    return nodes;
  }

  public void setNodes(int nodes) {
    this.nodes = nodes;
  }

  public int getCpuPerNode() {
    return cpuPerNode;
  }

  public void setCpuPerNode(int cpuPerNode) {
    this.cpuPerNode = cpuPerNode;
  }

  public int getTotalCPUs() {
    return totalCPUs;
  }

  public void setTotalCPUs(int totalCPUs) {
    this.totalCPUs = totalCPUs;
  }

  private int threads;
  private int cores;
  private int sockets;
  private int nodes;
  private int cpuPerNode;
  private int totalCPUs;

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("  <hardware>%n"));
    b.append(String.format("    <vendor string=\"%s\"/>%n", this.vendor));
    b.append(String.format("    <vendorCode value=\"%d\"/>%n", this.vendorCode));
    b.append(String.format("    <model string=\"%s\"/>%n", this.model));
    b.append(String.format("    <modelCode value=\"%d\"/>%n", this.modelCode));
    b.append(String.format(Locale.US, "    <cpuRevision value=\"%f\"/>%n", this.cpuRevision));
    b.append(this.cpuID.toString());
    b.append(String.format("    <cpuMaxMegahertz value=\"%d\"/>%n", this.cpuMaxMegahertz));
    b.append(String.format("    <cpuMinMegahertz value=\"%d\"/>%n", this.cpuMinMegahertz));
    b.append(String.format("    <threads value=\"%d\"/>%n", this.threads));
    b.append(String.format("    <cores value=\"%d\"/>%n", this.cores));
    b.append(String.format("    <sockets value=\"%d\"/>%n", this.sockets));
    b.append(String.format("    <nodes value=\"%d\"/>%n", this.nodes));
    b.append(String.format("    <cpuPerNode value=\"%d\"/>%n", this.cpuPerNode));
    b.append(String.format("    <totalCPUs value=\"%d\"/>%n", this.totalCPUs));
    b.append(String.format("  </hardware>%n"));
    return b.toString();
  }
}
