package org.preesm.algorithm.node.simulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represents the last page of an analysis and provides a visual representation. It creates a JPanel with a
 * thank-you image displayed in a JLabel. The image is loaded from a file, scaled to fit the panel dimensions, and added
 * to the panel. The panel is set to have a white background.
 *
 * Note: The file path for the image is hardcoded ("/home/orenaud/Images/gif/thank-you.gif"). Ensure the image file
 * exists at the specified path.
 *
 * Usage: Create an instance of AnalysisLastPage and call the execute method to obtain the JPanel.
 *
 * @author orenaud
 */
public class AnalysisLastPage {
  /**
   * Constructs an instance of AnalysisLastPage.
   */
  public AnalysisLastPage() {
    // Constructor logic, if needed
  }

  /**
   * Executes the visual representation of the last page of the analysis. Creates a JPanel with a thank-you image and
   * returns it.
   *
   * @return JPanel containing the visual representation of the last page.
   */
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
