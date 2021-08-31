package kupo;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import kupo.ui.FrameMain;

public class App {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to load System Theme.","Error", JOptionPane.ERROR_MESSAGE);
		}
		
		if (new File("table.txt").exists())
			new FrameMain();
		else
			JOptionPane.showMessageDialog(null, "File \"table.txt\" is not found.","Error", JOptionPane.ERROR_MESSAGE);
		
	}
}
