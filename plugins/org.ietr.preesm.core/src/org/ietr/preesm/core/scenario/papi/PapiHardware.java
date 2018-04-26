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
    return this.vendor;
  }

  public void setVendor(final String vendor) {
    this.vendor = vendor;
  }

  public int getVendorCode() {
    return this.vendorCode;
  }

  public void setVendorCode(final int vendorCode) {
    this.vendorCode = vendorCode;
  }

  public String getModel() {
    return this.model;
  }

  public void setModel(final String model) {
    this.model = model;
  }

  public int getModelCode() {
    return this.modelCode;
  }

  public void setModelCode(final int modelCode) {
    this.modelCode = modelCode;
  }

  public double getCpuRevision() {
    return this.cpuRevision;
  }

  public void setCpuRevision(final double cpuRevision) {
    this.cpuRevision = cpuRevision;
  }

  public PapiCpuID getCpuID() {
    return this.cpuID;
  }

  public void setCpuID(final PapiCpuID cpuID) {
    this.cpuID = cpuID;
  }

  public int getCpuMaxMegahertz() {
    return this.cpuMaxMegahertz;
  }

  public void setCpuMaxMegahertz(final int cpuMaxMegahertz) {
    this.cpuMaxMegahertz = cpuMaxMegahertz;
  }

  public int getCpuMinMegahertz() {
    return this.cpuMinMegahertz;
  }

  public void setCpuMinMegahertz(final int cpuMinMegahertz) {
    this.cpuMinMegahertz = cpuMinMegahertz;
  }

  public int getThreads() {
    return this.threads;
  }

  public void setThreads(final int threads) {
    this.threads = threads;
  }

  public int getCores() {
    return this.cores;
  }

  public void setCores(final int cores) {
    this.cores = cores;
  }

  public int getSockets() {
    return this.sockets;
  }

  public void setSockets(final int sockets) {
    this.sockets = sockets;
  }

  public int getNodes() {
    return this.nodes;
  }

  public void setNodes(final int nodes) {
    this.nodes = nodes;
  }

  public int getCpuPerNode() {
    return this.cpuPerNode;
  }

  public void setCpuPerNode(final int cpuPerNode) {
    this.cpuPerNode = cpuPerNode;
  }

  public int getTotalCPUs() {
    return this.totalCPUs;
  }

  public void setTotalCPUs(final int totalCPUs) {
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
