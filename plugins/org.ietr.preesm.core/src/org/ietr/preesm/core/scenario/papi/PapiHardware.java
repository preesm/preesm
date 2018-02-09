package org.ietr.preesm.core.scenario.papi;

import java.util.Locale;

/**
 *
 *
 */
public class PapiHardware {

  String    vendor;
  int       vendorCode;
  String    model;
  int       modelCode;
  double    cpuRevision;
  PapiCpuID cpuID;
  int       cpuMaxMegahertz;
  int       cpuMinMegahertz;
  int       threads;
  int       cores;
  int       sockets;
  int       nodes;
  int       cpuPerNode;
  int       totalCPUs;

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
