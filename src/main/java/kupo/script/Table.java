package kupo.script;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;


public class Table{
	
	private Hashtable<Byte, Object> table = new Hashtable<Byte, Object>();
	
	public Table(File arquivoTabela) throws IOException{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoTabela)));
		String linha = null;
		
		while ((linha = br.readLine()) != null){
			
			String partes[] = linha.split("=", 2);
			putTableValue(table, partes[0], partes[1]);
			
		}
		
		br.close();
	}
	
	public synchronized void put(String key, String value){
		putTableValue(table, key, value);
	}
	
	public int get(StringBuffer value, byte key){
		return readTableValue(table, key, value);
	}
	
	public int get(StringBuffer value, String hexaKey){
		return readTableValue(table, Integer.valueOf(hexaKey).byteValue(), value);
	}
	
	public String get(String hexaKey){
		return readTable(table, hexaKey);
	}
	
	public String format(byte[] bytes){
		
		String str = "";
		for (byte b: bytes){
			StringBuffer sb= new StringBuffer();
			this.get(sb, b);
			str += sb;
		}
		return str;
	}
	
	@SuppressWarnings("unchecked")
	private String readTable(Map<Byte, Object> table, String key) throws NumberFormatException{
		
		if (key == null || key.isEmpty() || key.length() % 2 != 0) {
			throw new NumberFormatException();
		}
		
		if (key.length() == 2){ 
			
			return (String) table.get(Integer.valueOf(key,16).byteValue());
		
		} else {
		
			String subKey = key.substring(0,2);
			String newKey = key.substring(2);
			
			if(table.containsKey((byte) Integer.parseInt(subKey,16))){
				 return readTable((Map<Byte, Object>) table.get((byte) Integer.parseInt(subKey,16)), newKey);
			} else {
				return null;
			}	
		}		
	}
	
	private int readTableValue(Map<Byte, Object> table, byte key, StringBuffer buffer){
			
		buffer.delete(0,buffer.length());
		
		if (table.containsKey(key)){
			if (table.get(key) instanceof Map<?, ?>){
				return 0;				
			} else {
				buffer.append(table.get(key).toString());
				return 1;
			}
		} else {
			buffer.append(String.format("{%02X}", key));
		}
		
		return -1; 
	}
	
	@SuppressWarnings("unchecked")
	private void putTableValue(Map<Byte, Object> table, String key, String value) throws NumberFormatException {
		
		if (key == null || key.isEmpty() || key.length() % 2 != 0) {
			throw new NumberFormatException();
		}
		
		if (key.length() == 2) {

			Byte keyByte = Integer.valueOf(key, 16).byteValue();
			table.put(keyByte, value);
		
		} else {
		
			String sk = key.substring(0,2);
			byte subKey = (byte) Integer.parseInt(sk, 16);
			
			String newKey = key.substring(2);
			
			if(table.containsKey(subKey)){
			
				putTableValue((Map<Byte, Object>) table.get(subKey), newKey, value);	
			
			} else {
				
				Map<Byte, Object> newTable = new TreeMap<Byte, Object>();
				
				table.put(subKey, newTable);
				putTableValue(newTable, newKey, value);
			}
		}
	}
}