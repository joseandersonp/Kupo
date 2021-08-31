package kupo.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import kupo.io.MemBuffer;
import kupo.script.BinaryScript;
import kupo.script.BinaryScriptReader;
import kupo.script.BinaryStriptWriter;
import kupo.script.DynamicTable;
import kupo.script.KernelReader;
import kupo.script.KernelWriter;

@SuppressWarnings("serial")
public class FrameMain extends JFrame{

	private Font fontSansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private KernelReader kernelReader;

	private DialogAbout  dialogAbout;
	private JPanel       panelMain;
	private JTabbedPane  tabbedPane;
	private MenuBar      menuBarMain;
	private ToolBar      toolBarMain;

	private File		 kernelFile;

	private ArrayList<TableScrollPanel> tableScrollPanels;

	public FrameMain() {

		new ListenerFrameMain(this);

		try {
			this.setIconImage(ImageIO.read(getClass().getResource("/kupo/images/icon.png")));
		} catch (IOException e) {}

		this.setTitle("KUPO-Kernel Script Editor for Final Fantasy VII (Playstation)");

		this.setJMenuBar(getMenuBarMain());
		this.add(getPanelMain());
		this.setSize(new Dimension(900,600));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		openFile(true);
	}

	public void close() {
		this.dispose();
	}

	void openFile(boolean firstOpen){

		JFileChooser fileChooser = getFileChooser("./", "Binary Kernel File (*.bin) ", ".bin");

		int option = fileChooser.showOpenDialog(this);

		if (option == JFileChooser.APPROVE_OPTION){
			try {
				this.kernelFile = fileChooser.getSelectedFile();
				this.setTitle("KUPO - "+ this.kernelFile.getPath());
				processKernel();
			} catch (Exception e) {				
				option = JFileChooser.ERROR_OPTION;
			}
		}

		if (option == JFileChooser.ERROR_OPTION){
			JOptionPane.showMessageDialog(null, "Failed open file " + kernelFile.getName(),"Error", JOptionPane.ERROR_MESSAGE);
			openFile(firstOpen);
		}

		if (option == JFileChooser.CANCEL_OPTION){
			if (firstOpen)
				close();
		}

	}

	void saveFileAs(){

		JFileChooser fileChooser = getFileChooser("./", "Binary Kernel File (*.bin) ", ".bin");
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){

			this.kernelFile = fileChooser.getSelectedFile();
			this.setTitle("KUPO - "+ this.kernelFile.getPath());

			if (kernelFile.exists()){
				String message = "The file '"+ kernelFile +"' already exists.\nDo you want to overwrite it?";
				if(JOptionPane.showConfirmDialog(this, message , "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_NO_OPTION){
					saveFile();
				} else {
					saveFileAs();
				}

			} else {				
				saveFile();
			}
		}
	}

	public void updateSceneTable() {

		JFileChooser fileChooser = getFileChooser("./", "Binary Scene Files (*.bin)", ".bin");
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			
			//Battle and growth data
			byte[] section3 = kernelReader.getSection(2);
			//Scene Look-Up Table offset
			int so = 0xF1C; 
			section3[so] = 0;
			
			File sceneFile = fileChooser.getSelectedFile();
			try(FileInputStream isScene = new FileInputStream(sceneFile)){
				
				byte[] data = new byte[isScene.available()];
				isScene.read(data);
				MemBuffer buffer = MemBuffer.wrap(data);
				
				
				for (int j = 0; j < 0x40; j++) {
					System.out.printf("%02X, ", section3[so+j]);
				}
				System.out.println();
				
				int numBlocks = (buffer.getSize() / 0x2000) - 1;
				int i = 0;
				while (i < numBlocks) {
					int cfiles = 0;
					buffer.setOffset(i * 0x2000);
					for (int j = 0; j < 0x40; j+=4) {
						int pointer = buffer.getInt();
						if (pointer == -1)
							break;
						cfiles++;
					}
					section3[so + i + 1] = (byte) (section3[so + i] + cfiles);
					i++;
				}
				
				for (int j = so + i + 1; j < so + 0x40; j++)
					section3[j] = -1; //0xFF;
				
				/*for (int j = 0; j < 0x40; j++) {
					System.out.printf("%02X, ", section3[so+j]);
				}*/
				
				JOptionPane.showMessageDialog(this, "Scene look-up table was updated! (kernel section 3)", "", JOptionPane.INFORMATION_MESSAGE);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to update Scene look-up table. (kernel section 3)" ,"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void saveFile(){

		DynamicTable table;
		try {

			table = new DynamicTable(new File("table.txt"));
			KernelWriter kernelWriter = new KernelWriter(kernelReader.getSections());

			for (int i = 0; i < 8; i++){

				BinaryScript scriptName = new BinaryScript();
				BinaryScript scriptDescription = new BinaryScript();

				List<List<String>> dataKernel = getTableScrollPanels().get(i).getTableModel().getData();

				for (List<String> fields : dataKernel){
					scriptName.add(table.parseBytes(fields.get(0)));
					scriptDescription.add(table.parseBytes(fields.get(1)));
				}

				BinaryStriptWriter bswName = new BinaryStriptWriter(scriptName);
				BinaryStriptWriter bswDesc = new BinaryStriptWriter(scriptDescription);

				byte[] bufferName = bswName.generateUncompressed();				
				byte[] bufferDesc = (i == 0)? bswDesc.generate09() : bswDesc.generate();

				kernelWriter.setSection(i + 17, bufferName);			
				kernelWriter.setSection(i + 9, bufferDesc);

			}

			for (int i = 8; i < 10; i++) {

				BinaryScript scriptDescription = new BinaryScript();

				List<List<String>> dataKernel = getTableScrollPanels().get(i).getTableModel().getData();

				for (List<String> fields : dataKernel){
					scriptDescription.add(table.parseBytes(fields.get(0)));
				}

				BinaryStriptWriter bswDesc = new BinaryStriptWriter(scriptDescription);
				byte[] bufferDesc = bswDesc.generateUncompressed();				
				kernelWriter.setSection(i + 17, bufferDesc);

			}

			kernelWriter.writeFile(kernelFile);

			JOptionPane.showMessageDialog(this, "The file '" +kernelFile.getName() + " was saved successfully!", "", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void processKernel() throws IOException{

		BinaryScriptReader scriptReader = null;

		BinaryScript scriptName = null;
		BinaryScript scriptDescription = null;

		int count = 0;

		kernelReader = new KernelReader(this.kernelFile);

		DynamicTable table;

		table = new DynamicTable(new File("table.txt"));

		while (count < 8){

			scriptReader = new BinaryScriptReader(kernelReader.getSection(count + 17));
			scriptName = scriptReader.generateScript();

			scriptReader = new BinaryScriptReader(kernelReader.getSection(count + 9));
			scriptDescription = scriptReader.generateScript();

			List<List<String>> data = new ArrayList<List<String>>();

			for (int i = 0; i < scriptDescription.size(); i++){

				ArrayList<String> fields = new ArrayList<String>();

				fields.add(table.format(scriptName.get(i)));					
				fields.add(table.format(scriptDescription.get(i)));

				data.add(fields);
			}

			tableScrollPanels.get(count).getTableModel().setData(data);
			count++;
		}

		while (count < 10){

			scriptReader = new BinaryScriptReader(kernelReader.getSection(count + 17));
			scriptDescription = scriptReader.generateScript();

			List<List<String>> data = new ArrayList<List<String>>();

			for (int i = 0; i < scriptDescription.size(); i++){

				ArrayList<String> fields = new ArrayList<String>();
				fields.add(table.format(scriptDescription.get(i)));
				data.add(fields);
			}

			tableScrollPanels.get(count).getTableModel().setData(data);
			count++;
		}	
	}

	ArrayList<TableScrollPanel> getTableScrollPanels(){
		if (tableScrollPanels == null){
			tableScrollPanels = new ArrayList<TableScrollPanel>();
		}
		return tableScrollPanels;
	}	

	public MenuBar getMenuBarMain() {

		if (menuBarMain == null){
			menuBarMain = new MenuBar();
		}
		return menuBarMain;
	}

	ToolBar getToolBarMain() {

		if (toolBarMain == null){
			toolBarMain = new ToolBar();
		}
		return toolBarMain;
	}

	public JFileChooser getFileChooser(String curDirPath,String description, String extension){

		JFileChooser fileChooser = new JFileChooser();
		File curDir = new File(curDirPath);			
		if (curDir.exists())
			fileChooser.setCurrentDirectory(curDir);

		FileFilter filter = new FileFilter() {

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;						
				return f.getName().toLowerCase().endsWith(extension);
			}
		};
		fileChooser.setFileFilter(filter);

		return fileChooser;
	}

	public DialogAbout getDialogAbout(){
		if (dialogAbout == null){
			dialogAbout = new DialogAbout(this);
		}
		return dialogAbout;
	}

	public JPanel getPanelMain(){

		if(panelMain == null){
			panelMain = new JPanel();
			panelMain.setLayout(new BorderLayout());
			//panelMain.add(getToolBarMain(), BorderLayout.NORTH);
			panelMain.add(getTabbedPane(),BorderLayout.CENTER);
		}
		return panelMain;
	}	

	public JTabbedPane getTabbedPane(){

		if (tabbedPane == null){

			String[] tabNames = {"Commands", "Spells and Limit Breaks", "Items", "Weapons",
					"Armors", "Accessories" , "Materias", "Key Items", "Misc.","Summons"};

			tabbedPane = new JTabbedPane();
			tabbedPane.setFont(fontSansSerif);

			int i;
			for (i = 0; i < tabNames.length -2; i++) {
				getTableScrollPanels().add(createTableScrollPanel());
				tabbedPane.addTab(tabNames[i], getTableScrollPanels().get(i));
			}

			getTableScrollPanels().add(createTableScrollPanelDescription());
			tabbedPane.addTab(tabNames[i], getTableScrollPanels().get(i));
			i++;
			getTableScrollPanels().add(createTableScrollPanelDescription());
			tabbedPane.addTab(tabNames[i], getTableScrollPanels().get(i));

		}
		return tabbedPane;
	}

	TableScrollPanel createTableScrollPanel() {

		TableScrollPanel tableScrollPanel = new TableScrollPanel();

		List<List<String>> data = new ArrayList<List<String>>();
		ArrayList<String> columnNames = new ArrayList<String>();
		columnNames.add("Name");
		columnNames.add("Description");

		tableScrollPanel.getTableModel().setColumnNames(columnNames);
		tableScrollPanel.getTableModel().setData(data);

		return tableScrollPanel;
	}

	TableScrollPanel createTableScrollPanelDescription() {

		TableScrollPanel tableScrollPanel = new TableScrollPanel();

		List<List<String>> data = new ArrayList<List<String>>();
		ArrayList<String> columnNames = new ArrayList<String>();

		columnNames.add("Description");

		tableScrollPanel.getTableModel().setColumnNames(columnNames);
		tableScrollPanel.getTableModel().setData(data);

		return tableScrollPanel;
	}	

}