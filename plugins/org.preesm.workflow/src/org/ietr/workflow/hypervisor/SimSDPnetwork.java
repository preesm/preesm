package org.ietr.workflow.hypervisor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;

public class SimSDPnetwork {
  int    configID;
  int    nodeNum;
  int    coreNum;
  int    coreFreq;
  int    algo = 1;
  String projectPath;

  public static final String TOPO_NAME1     = "Cluster with crossbar";
  public static final String TOPO_NAME2     = "Cluster with shared backbone";
  public static final String TOPO_NAME3     = "Torus cluster";
  public static final String TOPO_NAME4     = "Fat-tree cluster";
  public static final String TOPO_NAME5     = "Dragonfly cluster";
  public static final String TOPO_PARAM_KEY = "topoparam";
  static final Integer       ROUTER_PORT    = 8;

  Integer findNode = 2;

  public SimSDPnetwork(int configID, int nodeNum, int coreNum, int coreFreq, String projectPath) {
    this.configID = configID;
    this.nodeNum = nodeNum;
    this.coreNum = coreNum;
    this.coreFreq = coreFreq;
    this.projectPath = projectPath;
  }

  public Boolean execute() {
    final Map<String, String> network = new LinkedHashMap<>();
    final Boolean existingNetwork = feedNetwork(network);

    final StringConcatenation content = processXml(network);
    PreesmIOHelper.getInstance().print(projectPath + "/Archi/", "SimSDP_network.xml", content);
    return existingNetwork;
  }

  private Boolean feedNetwork(Map<String, String> network) {

    network.put("node", String.valueOf(nodeNum));
    network.put("speed", "1f");
    network.put("bandwith", "125MBps");
    network.put("latency", "50us");
    network.put("loopback", "false");
    switch (configID) {
      case 0:
        network.put("topo", TOPO_NAME1);
        return check();
      case 1:
        network.put("topo", TOPO_NAME2);
        network.put(TOPO_PARAM_KEY, "2.25GBps");
        network.put("bbparam", "500us");
        return check();
      case 2:
        network.put("topo", TOPO_NAME3);
        network.put(TOPO_PARAM_KEY, torusConfiguration());
        network.put("node", String.valueOf(findNode));
        break;
      case 3:
        network.put("topo", TOPO_NAME4);
        network.put(TOPO_PARAM_KEY, fatTreeConfiguration());
        network.put("node", String.valueOf(findNode));
        break;
      case 4:
        network.put("topo", TOPO_NAME5);
        network.put(TOPO_PARAM_KEY, dragonflyConfiguration());
        network.put("node", String.valueOf(findNode));
        break;
      default:
        break;
    }
    return true;
  }

  private Boolean check() {
    return (nodeNum > ROUTER_PORT) ? false : true;
  }

  /**
   * Calculates the Dragonfly network configuration based on the number of nodes. Adjusts the number of nodes to ensure
   * it is even and greater than 4.
   *
   * @return A string representing the Dragonfly configuration.
   */
  private String dragonflyConfiguration() {
    int g = 1;
    int c = 1;
    final int r = 1;
    // Check if the number of nodes is odd and greater than 4
    if (nodeNum % 2 != 0 && nodeNum > ROUTER_PORT) {
      // If odd, adjust the number of nodes to the nearest even number
      findNode = nodeNum + 1;
    } else {
      findNode = nodeNum;
    }
    int n = findNode;

    while (n > ROUTER_PORT) {
      if (n % ROUTER_PORT == 0) {
        // If the number of nodes per router is a multiple of 4, reduce the number of routers
        // n *= 2;
        n /= 2;
      } else {
        // Otherwise, increase the number of chassis and reduce the number of nodes per router
        c++;
        n = (int) Math.ceil(nodeNum / (double) (g * c * r * 4));
      }
    }

    // Final calculation to determine the number of groups (g)
    g = (int) Math.ceil(nodeNum / (double) (c * r * n * 4));

    return "\"" + g + ",3;" + c + ",2;" + r + ",1;" + n + "\"";
  }

  /**
   * Calculates the Fat Tree configuration based on the number of nodes. Adjusts the number of nodes to the next power
   * of two.
   *
   * @return A string representing the Fat Tree configuration in the format:
   *         "level;downlink,...;uplink,...;parallellink,..."
   */
  private String fatTreeConfiguration() {
    final int totalNodes = getNextPowerOfTwo(nodeNum);
    findNode = totalNodes;

    // Calcul du nombre d'étages
    int levels = 1;
    int nodesPerRouterLeaf = 0;
    if (totalNodes > ROUTER_PORT) {
      nodesPerRouterLeaf = ROUTER_PORT;
      levels = 2;
    } else {
      nodesPerRouterLeaf = totalNodes;
    }
    String result = "";

    if (levels == 1) {
      result = levels + ";" + nodesPerRouterLeaf + ";" + 1 + ";" + 1;
    } else {
      final int com = totalNodes / nodesPerRouterLeaf;
      result = levels + ";" + nodesPerRouterLeaf + "," + com + ";" + 1 + "," + com + ";" + 1 + "," + 1;
    }

    return result;

  }

  public static int getNextPowerOfTwo(int totalNodes) {
    if (totalNodes <= 0) {
      throw new IllegalArgumentException("Le nombre total de nœuds doit être positif.");
    }

    if ((totalNodes & (totalNodes - 1)) == 0) {
      // Le nombre est déjà une puissance de 2
      return totalNodes;
    }
    // Trouver la puissance de 2 juste au-dessus du nombre
    int powerOfTwo = 1;
    while (powerOfTwo < totalNodes) {
      powerOfTwo <<= 1; // Décalage vers la gauche pour doubler la valeur
    }
    return powerOfTwo;
  }

  /**
   * Generates a Torus cluster configuration based on the given number of nodes. It iteratively searches for valid
   * configurations by adjusting the number of nodes. The configuration is returned in the format "x,y,z".
   *
   * @return A string representing the Torus cluster configuration.
   */
  private String torusConfiguration() {
    int x = 1;
    if (nodeNum == 1) {
      return "\"2,1,1\"";
    }

    int nNode = nodeNum;
    // Initial approximation of cube root of nNode 1/3 bc. 3 axes
    final int n = (int) Math.ceil(Math.pow(nNode, 1.0 / 3.0));

    while (true) {
      for (x = n; x >= 2; x--) {
        if (nNode % x == 0) {

          final String result = findXZ(n, nNode, x);
          if (result != null) {
            return result;
          }

        }
      }
      nNode++; // If no valid configuration found, increment nNode and try again
    }

  }

  /**
   * Finds the values of y and z for a given Torus cluster configuration.
   *
   * @param n
   *          Initial approximation of the cube root of nNode.
   * @param nNode
   *          The number of nodes.
   * @param x
   *          The value of x obtained from the outer loop.
   * @return A string representing the configuration in the format "x,y,z" or null if no valid configuration is found.
   */
  private String findXZ(int n, int nNode, int x) {
    int y = 1;
    final int remainXY = nNode / x;
    for (y = n; y >= 2; y--) {
      if (remainXY % y == 0 && remainXY / y >= 1) {
        findNode = nNode;
        return "\"" + x + "," + y + "," + remainXY / y + "\"";

      }
    }
    return null;
  }

  private StringConcatenation processXml(Map<String, String> network) {
    final StringConcatenation content = new StringConcatenation();
    content.append("<!-- " + network.get("topo") + ":" + nodeNum + ":" + coreNum + ":" + coreFreq + " -->\n");
    content.append("<?xml version='1.0'?>\n");
    content.append("<!DOCTYPE platform SYSTEM \"https://simgrid.org/simgrid.dtd\">\n");
    content.append("<platform version=\"4.1\">\n");
    content.append("<zone id=\"my zone\" routing=\"Floyd\">\n");
    content.append("<cluster id=\"" + network.get("topo") + "\" ");
    content.append("prefix=\"Node\" radical=\"0-" + (Integer.decode(network.get("node")) - 1) + "\" suffix=\"\" ");
    content.append("speed=\"" + network.get("speed") + "\" ");
    content.append("bw=\"" + network.get("bandwith") + "\" ");
    content.append("lat=\"" + network.get("latency") + "\" ");

    if (network.get("loopback").equals("true")) {
      content.append("loopback_bw=\"" + network.get("loopbackbandwidth") + "\" ");
      content.append("loopback_lat=\"" + network.get("loopbacklatency") + "\" ");
    }

    final String selectedOption = network.get("topo");
    switch (selectedOption) {
      case TOPO_NAME2:
        content.append("bb_bw=\"" + network.get(TOPO_PARAM_KEY) + "\" bb_lat=\"" + network.get("bbparam") + "\"");
        break;
      case TOPO_NAME3:
        content.append("topology=\"TORUS\" ");
        content.append("topo_parameters=" + network.get(TOPO_PARAM_KEY));
        break;
      case TOPO_NAME4:
        content.append("topology=\"FAT_TREE\" ");
        content.append("topo_parameters=\"" + network.get(TOPO_PARAM_KEY) + "\"");
        break;
      case TOPO_NAME5:
        content.append("topology=\"DRAGONFLY\" ");
        content.append("topo_parameters=" + network.get(TOPO_PARAM_KEY));
        break;
      default:
        break;
    }
    content.append(">\n");
    content.append("<prop id=\"wattage_per_state\" value=\"90.0:90.0:150.0\" />\n");
    content.append("<prop id=\"wattage_range\" value=\"100.0:200.0\" />\n");
    content.append("</cluster>\n");
    content.append("</zone>\n");
    content.append("</platform>\n");
    return content;
  }
}
