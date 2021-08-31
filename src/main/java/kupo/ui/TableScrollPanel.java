package kupo.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
class TableScrollPanel extends JPanel{

	private JTable table;
	private DynamicTableModel tableModel;

	TableScrollPanel(){

		tableModel = new DynamicTableModel();
		table = createJtable();
		table.setRowHeight(20);
		table.setModel(tableModel); 

		this.setLayout(new BorderLayout());		
		this.add(new JScrollPane(table));
	}

	private JTable createJtable(){
		return new JTable(){

			@Override
			public void changeSelection(int rowIndex, int columnIndex,
					boolean toggle, boolean extend) {	
				super.changeSelection(rowIndex, columnIndex, toggle, extend);

				if (editCellAt(rowIndex, columnIndex)){

					Component editor = getEditorComponent();  
					editor.requestFocusInWindow(); 
					
					if (editor instanceof JTextComponent){
						((JTextComponent) editor).selectAll();     
					}
				}
			}
		};
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public DynamicTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(DynamicTableModel tableModel) {
		this.tableModel = tableModel;
	}
}