package kupo.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
class MenuBar extends JMenuBar {

	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private JMenuItem updateScTableMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem quitMenuItem;

	private Font fontSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	MenuBar() {

		fileMenu = new JMenu("File");

		aboutMenuItem = new JMenuItem("About Kupo...");
		openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
		saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveAsMenuItem = new JMenuItem("Save As...");
		updateScTableMenuItem = new JMenuItem("Update Scene Table", KeyEvent.VK_U);
		quitMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);

		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(updateScTableMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(quitMenuItem);
		
		helpMenu = new JMenu("Help");
		helpMenu.add(aboutMenuItem);

		fileMenu.setFont(fontSansSerif);
		aboutMenuItem.setFont(fontSansSerif);
		openMenuItem.setFont(fontSansSerif);
		saveMenuItem.setFont(fontSansSerif);
		saveAsMenuItem.setFont(fontSansSerif);
		quitMenuItem.setFont(fontSansSerif);
		helpMenu.setFont(fontSansSerif);

		fileMenu.setMnemonic(KeyEvent.VK_F);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		updateScTableMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		
		this.add(fileMenu);
		this.add(helpMenu);

	}

	public JMenu getMenuFile() {
		return fileMenu;
	}

	public JMenuItem getMenuAbout() {
		return aboutMenuItem;
	}

	public JMenuItem getMenuOpen() {
		return openMenuItem;
	}

	public JMenuItem getMenuSave() {
		return saveMenuItem;
	}

	public JMenuItem getMenuSaveAs() {
		return saveAsMenuItem;
	}
	
	public JMenuItem getUpdateScTableMenuItem() {
		return updateScTableMenuItem;
	}

	public JMenuItem getMenuQuit() {
		return quitMenuItem;
	}

}