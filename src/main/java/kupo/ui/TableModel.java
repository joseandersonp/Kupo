package kupo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
class TableModel extends AbstractTableModel {

	private String[]   columnNames;
	private String[][] data;

	TableModel(){}

	TableModel(String[][] data, String[] colNames){
		this.columnNames = colNames;
		this.data = data;
	}

	public String getColumnName(int col){
		return columnNames[col];
	}

	public void setColumnName(int col, String name){
		columnNames[col] = name;
	}

	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public void setData(String[][] data){
		this.data = data;
		fireTableDataChanged();
	}

	public void setValueAt(String value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
}


@SuppressWarnings("serial")
class DynamicTableModel extends AbstractTableModel {

	private List<String>   columnNames;
	private List<List<String>> data;

	DynamicTableModel(){		
		this.columnNames = new ArrayList<String>();
		this.data = new ArrayList<List<String>>();
	}

	DynamicTableModel(List<List<String>> data, List<String> colNames){
		this.columnNames = colNames;
		this.data = data;
	}

	public String getColumnName(int col){
		return columnNames.get(col);
	}

	public void setColumnName(int col, String name){
		columnNames.set(col, name);
	}

	
	public int getColumnCount() {
		return columnNames.size();
	}

	public int getRowCount() {
		return data.size();
	}

	public void setColumnNames(List<String> columnNames){
		this.columnNames = columnNames;
		//fireTableStructureChanged();
	}

	public void setData(List<List<String>> data){
		this.data = data;
		fireTableStructureChanged();
	}

	public List<List<String>> getData(){
		return this.data;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.get(rowIndex).set(columnIndex, (String)aValue);
		//fireTableCellUpdated(rowIndex, rowIndex);
	}

	
	public String getValueAt(int row, int col) {
		return data.get(row).get(col);
	}	

	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
}