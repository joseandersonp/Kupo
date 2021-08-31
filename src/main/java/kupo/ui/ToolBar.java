package kupo.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class ToolBar extends JToolBar{

	JButton btnOpen;
	JButton btnSave;
	JButton btnSaveAs;
	
	public ToolBar(){
		
		ImageIcon openIcon = new ImageIcon(FrameMain.class.getResource("/icons/open-folder.png"));		
		btnOpen = new JButton(openIcon);
		openIcon = new ImageIcon(FrameMain.class.getResource("/icons/save.png"));
		btnSave = new JButton(openIcon);
		openIcon = new ImageIcon(FrameMain.class.getResource("/icons/save-as.png"));
		btnSaveAs = new JButton(openIcon);
		
		this.add(btnOpen);
		this.add(btnSave);
		this.add(btnSaveAs);
	}
}
