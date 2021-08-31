package kupo.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListenerFrameMain {

	FrameMain frameMain;
	
	public ListenerFrameMain(FrameMain frameMain) {
		
		this.frameMain = frameMain;
		loadMenuBarListener();
		
	}
	
	private void loadMenuBarListener(){
		
		frameMain.getMenuBarMain().getMenuSaveAs().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frameMain.saveFileAs();
			}
		});
		
		frameMain.getMenuBarMain().getMenuOpen().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frameMain.openFile(false);
			}
		});
		
		frameMain.getMenuBarMain().getMenuSave().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frameMain.saveFile();
			}
		});
		
		frameMain.getMenuBarMain().getMenuQuit().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frameMain.close();
			}
		});
		
		frameMain.getMenuBarMain().getMenuAbout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frameMain.getDialogAbout().setVisible(true);
			}
		});
		
		frameMain.getMenuBarMain().getUpdateScTableMenuItem().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frameMain.updateSceneTable();
			}
		});
		
	}
	
}
