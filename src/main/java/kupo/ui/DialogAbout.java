package kupo.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class DialogAbout extends JDialog{

	private JFrame frameParent;
	private JLabel imageMoggle;
	private JLabel nameProgram;
	private JLabel descProgram;
	private JLabel author;
	private JLabel url;
	private JButton btnOK;

	public DialogAbout(JFrame parent) {
		
		frameParent = parent;

		try {
			this.setIconImage(ImageIO.read(getClass().getResource("/kupo/images/icon.png")));
		} catch (IOException e) {}
		
		imageMoggle = new JLabel(new ImageIcon(getClass().getResource("/kupo/images/kupo.png")));
		nameProgram = new JLabel("KUPO 1.0.1");
		descProgram = new JLabel("<html>Kernel Script Editor for <br> Final Fantasy VII (Playstation)</html>");
		author = new JLabel("by José Anderson (aka Joapeer)");
		url = new JLabel("<html><color='blue'><u>http://www.romhacking.rip</u></color></html>");

		btnOK  = new JButton("OK");

		imageMoggle.setBounds(10, 10, 142, 180);
		nameProgram.setBounds(200, 10, 200, 40);
		descProgram.setBounds(200, 10, 200, 100);
		author.setBounds(200, 10, 200, 170);
		url.setBounds(200, 100, 200, 20);
		url.setBackground(Color.red);
		btnOK.setBounds(275, 160, 100, 30);

		nameProgram.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
		descProgram.setFont(new Font(Font.DIALOG, Font.ITALIC, 12));

		url.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.romhackers.org"));
				} catch (Exception e1) {}
			}
		});
		
		btnOK.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		this.add(imageMoggle);
		
		this.add(nameProgram);
		this.add(descProgram);
		this.add(author);
		this.add(url);
		this.add(btnOK);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("About");
		this.setLayout(null);
		this.setSize(400, 240);
		this.setModal(true);
	}
	
	@Override
	public void setVisible(boolean b) {
		this.setLocationRelativeTo(frameParent);
		super.setVisible(b);
	}
}