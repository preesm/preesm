package Show;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import Show.Show;
public class Main implements ActionListener{
		
	public static void main(String[] zero) throws InterruptedException {
		Fenetre fen = new Fenetre();
		PanneauGeneric pan = new PanneauGeneric();
		JButton bouton = new JButton("Ouvrir");
		bouton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent arg0) {
				Show s = new Show();
				Thread t = new Thread(s);
				t.start();				
			}
		});
		pan.add(bouton);
//		final JTextField textZone = new JTextField(5);
//		textZone.addActionListener(new ActionListener() {			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				System.out.println(textZone.getText());
//			}
//		});
//		pan.add(textZone);
		fen.setSize(300,150);
		fen.setTitle("Any Image");
        fen.setLocationRelativeTo(null); 
		fen.setContentPane(pan);
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.setVisible(true);	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
