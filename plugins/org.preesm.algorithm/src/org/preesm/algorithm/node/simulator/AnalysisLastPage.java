package org.preesm.algorithm.node.simulator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnalysisLastPage {
  public AnalysisLastPage() {

  }

  public JPanel execute() {
    final JPanel panel = new JPanel();
    final ImageIcon icon = new ImageIcon("/home/orenaud/Images/gif/thank-you.gif");
    final JLabel label = new JLabel(icon);
    panel.add(label);
    return panel;

  }
}
