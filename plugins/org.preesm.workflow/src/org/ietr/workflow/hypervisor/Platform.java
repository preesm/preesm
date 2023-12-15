package org.ietr.workflow.hypervisor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;

public class Platform {
  int configID;
  int nodeNum;
  int algo = 1;

  public Platform(int configID, int nodeNum) {
    this.configID = configID;
    this.nodeNum = nodeNum;
    // this.algo = algo;
  }

  public void execute() {
    final Map<String, String> network = new LinkedHashMap<>();
    feedNetwork(network);

    processXml(network);
  }

  private void feedNetwork(Map<String, String> network) {

    network.put("node", String.valueOf(nodeNum));
    network.put("speed", "1f");
    network.put("bandwith", "125MBps");
    network.put("latency", "50us");
    network.put("loopback", "false");
    switch (configID) {
      case 0:
        network.put("topo", "Cluster with crossbar");
        break;
      case 1:
        network.put("topo", "Cluster with shared backbone");
        network.put("topoparam", "2.25GBps");
        network.put("bbparam", "500us");
        break;
      case 2:
        network.put("topo", "Torus cluster");
        network.put("topoparam", torusConfiguration());
        break;
      case 3:
        network.put("topo", "Fat-tree cluster");
        network.put("topoparam", fatTreeConfiguration());
        break;
      case 4:
        network.put("topo", "Dragonfly cluster");
        network.put("topoparam", dragonflyConfiguration());
        break;
      default:
        break;
    }
  }

  private String dragonflyConfiguration() {
    int g;
    int c;
    int r;
    int n;
    String config = "";
    for (n = 1; n <= nodeNum; n++) {
      for (r = 1; r <= nodeNum / n; r++) {
        for (c = 1; c <= nodeNum / (n * r); c++) {
          g = nodeNum / (n * r * c);

          if (g * c * r * n == nodeNum) {
            config = "\"" + g + ",3;" + c + ",2;" + r + ",1;" + n + "\"";
            return config;
          }
        }
      }
    }
    return "\"1,1;1,1;1,1;" + nodeNum + "\"";
  }

  private String fatTreeConfiguration() {
    final int nRouterPort = 8;
    String config = "";
    switch (algo) {
      case 0:
        config = kAryTree(nRouterPort);
        break;
      case 1:
        config = fatTree(nRouterPort);
        break;
      case 2:
        config = PGFT(nRouterPort);
        break;
      default:
        break;
    }
    return config;

  }

  private String PGFT(int nRouterPort) {
    // TODO Auto-generated method stub
    return null;
  }

  private String fatTree(int nRouterPort) {
    final int nRouterPortSide = nRouterPort / 2;
    int nLevel;
    int[] m;
    int[] w;
    int[] p;
    if (nodeNum < nRouterPortSide) {
      nLevel = 1;
      m = new int[] { nRouterPortSide };
      w = new int[] { 1 };
      p = new int[] { 1 };
    } else if (isPowerOf(nRouterPortSide)) {
      nLevel = (int) (Math.log(nodeNum) / Math.log(nRouterPortSide));
      m = new int[nLevel];
      w = new int[nLevel];
      p = new int[nLevel];

      for (int i = 0; i < nLevel; i++) {
        m[i] = nRouterPortSide;
        w[i] = 1;
        p[i] = 1;
      }
    } else {
      final int nPortConnected = 2; // Remplacez par la valeur souhaitée
      nLevel = (int) (Math.log(nodeNum) / Math.log(nPortConnected));
      m = new int[nLevel];
      w = new int[nLevel];
      p = new int[nLevel];

      for (int i = 0; i < nLevel; i++) {
        m[i] = nPortConnected;
        w[i] = 1;
        p[i] = 1;
      }
    }
    String config = "";
    for (int i = 0; i < nLevel - 1; i++) {
      config += m[i] + ",";
    }
    config += m[nLevel - 1] + ";";
    for (int i = 0; i < nLevel - 1; i++) {
      config += w[i] + ",";
    }
    config += w[nLevel - 1] + ";";
    for (int i = 0; i < nLevel - 1; i++) {
      config += p[i] + ",";
    }
    config += p[nLevel - 1] + "\"";
    return config;
  }

  private String kAryTree(int nRouterPort) {
    final int nRouterPortSide = nRouterPort / 2;
    int nLevel;
    int[] m;
    int[] w;
    int[] p;
    if (nodeNum < nRouterPortSide) {
      nLevel = 1;
      m = new int[] { nRouterPortSide };
      w = new int[] { 1 };
      p = new int[] { 1 };
    } else if (isPowerOf(nRouterPortSide)) {
      nLevel = (int) (Math.log(nodeNum) / Math.log(nRouterPortSide));
      m = new int[nLevel];
      w = new int[nLevel];
      p = new int[nLevel];

      for (int i = 0; i < nLevel; i++) {
        m[i] = nRouterPortSide;
        w[i] = 1;
        p[i] = 1;
      }
    } else {
      final int nPortConnected = 2; // Remplacez par la valeur souhaitée
      nLevel = (int) (Math.log(nodeNum) / Math.log(nPortConnected));
      m = new int[nLevel];
      w = new int[nLevel];
      p = new int[nLevel];

      for (int i = 0; i < nLevel; i++) {
        m[i] = nPortConnected;
        w[i] = 1;
        p[i] = 1;
      }
    }
    String config = "";
    for (int i = 0; i < nLevel - 1; i++) {
      config += m[i] + ",";
    }
    config += m[nLevel - 1] + ";";
    for (int i = 0; i < nLevel - 1; i++) {
      config += w[i] + ",";
    }
    config += w[nLevel - 1] + ";";
    for (int i = 0; i < nLevel - 1; i++) {
      config += p[i] + ",";
    }
    config += p[nLevel - 1] + "\"";
    return config;
  }

  private boolean isPowerOf(int nRouterPortSide) {
    while (nodeNum != 1) {
      if (nodeNum % nRouterPortSide != 0) {
        return false;
      }
      nodeNum /= nRouterPortSide;
    }
    return true;
  }

  private String torusConfiguration() {
    int x = 1;
    int y = 1;
    int z = 1;

    for (int i = 1; i <= Math.sqrt(nodeNum); i++) {
      if (nodeNum % i == 0) {
        x = i;
        y = nodeNum / i;
        z = Math.max(x, y);
      }
    }
    return "\"" + x + "," + y + "," + "z\"";
  }

  private StringConcatenation processXml(Map<String, String> network) {
    final StringConcatenation content = new StringConcatenation();
    content.append("<?xml version='1.0'?>\n");
    content.append("<!DOCTYPE platform SYSTEM \"https://simgrid.org/simgrid.dtd\">\n");
    content.append("<platform version=\"4.1\">\n");
    content.append("<zone id=\"my zone\" routing=\"Floyd\">\n");
    content.append("<cluster id=\"" + network.get("topo") + "\" ");
    content.append(
        "prefix=\"Node\" radical=\"0-" + (Integer.decode(network.get("node")) - 1) + "\" suffix=\".simgrid.org\"");
    content.append("speed=\"" + network.get("speed") + "\" ");
    content.append("bw=\"" + network.get("bandwith") + "\" ");
    content.append("lat=\"" + network.get("latency") + "\" ");

    if (network.get("loopback").equals("true")) {
      content.append("loopback_bw=\"" + network.get("loopbackbandwidth") + "\" ");
      content.append("loopback_lat=\"" + network.get("loopbacklatency") + "\" ");
    }

    final String selectedOption = network.get("topo");
    switch (selectedOption) {
      case "Cluster with shared backbone":
        content.append("bb_bw=\"" + network.get("topoparam") + "\" bb_lat=\"" + network.get("bbparam") + "\"/>\n");
        break;
      case "Torus cluster":
        content.append("topology=\"TORUS\" ");
        content.append("topo_parameters=\"" + network.get("topoparam") + "\">\n");
        break;
      case "Fat-tree cluster":
        content.append("topology=\"FAT_TREE\" ");
        content.append("topo_parameters=\"" + network.get("topoparam") + "\">\n");
        break;
      case "Dragonfly cluster":
        content.append("topology=\"DRAGONFLY\" ");
        content.append("topo_parameters=\"" + network.get("topoparam") + "\">\n");
        break;
      default:
        content.append(">\n");
        break;
    }
    content.append("<prop id=\"wattage_per_state\" value=\"90.0:90.0:150.0\" />\n");
    content.append("<prop id=\"wattage_range\" value=\"100.0:200.0\" />\n");
    content.append("</cluster>\n");
    content.append("</zone>\n");
    content.append("</platform>\n");
    return content;
  }
}
