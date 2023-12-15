package org.preesm.algorithm.node.simulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnalysisLastPage {
  public AnalysisLastPage() {

  }

  public JPanel execute() {
    final JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    final ImageIcon icon = new ImageIcon("/home/orenaud/Images/gif/thank-you.gif");
    final Image image = icon.getImage();

    final int panelWidth = 750;
    final int panelHeight = 650;
    final Image scaledImage = image.getScaledInstance(panelWidth, panelHeight, Image.SCALE_DEFAULT);
    final ImageIcon scaledIcon = new ImageIcon(scaledImage);

    final JLabel label = new JLabel(scaledIcon);

    panel.add(label);
    panel.setBackground(Color.white);
    return panel;

  }
}
