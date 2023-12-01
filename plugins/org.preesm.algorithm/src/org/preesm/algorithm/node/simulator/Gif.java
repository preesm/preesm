package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "gif.identifier", name = "gif", category = "gif")
public class Gif extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    // Créer une instance de JFrame (fenêtre principale)
    final JFrame frame = new JFrame("GIF Dialog Example");

    // Créer une instance de la boîte de dialogue
    final JDialog dialog = createGifDialog(frame);

    // Définir la taille de la fenêtre principale et la rendre visible
    frame.setSize(400, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // Afficher la boîte de dialogue
    dialog.setVisible(true);
    return new LinkedHashMap<>();
  }

  private static JDialog createGifDialog(JFrame parentFrame) {
    // Créer une instance de JDialog (boîte de dialogue)
    final JDialog dialog = new JDialog(parentFrame, "GIF Dialog", true);

    // Charger l'icône GIF à afficher dans la boîte de dialogue
    final ImageIcon icon = new ImageIcon("/home/orenaud/Images/gif/thank-you.gif");

    // Créer un composant JLabel avec l'icône GIF
    final JLabel label = new JLabel(icon);

    // Ajouter le composant JLabel à la boîte de dialogue
    dialog.add(label, BorderLayout.CENTER);

    // Définir la taille de la boîte de dialogue en fonction de la taille de l'icône
    dialog.setSize(icon.getIconWidth() + 20, icon.getIconHeight() + 20);

    // Centrer la boîte de dialogue par rapport à la fenêtre principale
    dialog.setLocationRelativeTo(parentFrame);

    return dialog;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return "launch gif";
  }

}
