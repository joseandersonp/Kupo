package kupo.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class DynamicTable extends Hashtable<Long, String>{

	private File table;	
	private int level;

	public DynamicTable(File table) throws NumberFormatException, IOException {
		this.table = table;
		initialize();
	}

	public void initialize() throws NumberFormatException, IOException{

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(table)));

		String line = null;

		while ((line = br.readLine()) != null){

			int level = 0;
			String parts[] = line.split("=", 2);

			long key = Long.parseLong(parts[0], 16);
			String value = parts[1];
			level = parts[0].length() / 2;
			this.put(key, value);

			if (level > this.level){
				this.level = level;
			}
		}
		
		br.close();
	}

	public String format(byte buf[]){

		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < buf.length ; i++){
			long key = 0;

			for (int j = i; j < this.level + i; j++){

				key += ((long)buf[j]) & 0xFF;

				if (this.containsKey(key)){
					strBuilder.append(this.get(key));
					i=j;
					break;

				} else if (j == buf.length -1 || j == this.level + i - 1) {
					strBuilder.append(String.format("{0x%02X}", buf[i]));
					break;

				} else{

					key = key << 8;

				}
			}
		}
		return strBuilder.toString();
	}

	public ArrayList<Byte> parseList(String text){

		String subText = null;
		Pattern especialByte = Pattern.compile("\\{.+?\\}");
		ArrayList<Byte> bytes = new ArrayList<Byte>();

		int i = 0;
		while (i < text.length()) {

			byte[] bufBytes = null;

			if (text.charAt(i) == '{') {
				Matcher matcher = especialByte.matcher(text);
				if (matcher.find(i) && i == matcher.start(0)){

					String specialValue = matcher.group(0);

					if (specialValue.startsWith("{0x"))
						bufBytes = new byte[]{(byte)Integer.parseInt(specialValue.substring(3, 5), 16)};
					else 
						bufBytes = getKeyBytes(specialValue);

					if (bufBytes != null)
						i+= specialValue.length() - 1;

				}
			}

			if (bufBytes == null && i + 2 < text.length() && text.charAt(i) == '.'){
				subText = text.substring(i, i+3);
				bufBytes = getKeyBytes(subText);
				if (bufBytes != null){
					i+=2;
				}
			}

			if (bufBytes == null && i + 1 < text.length()){					
				subText = text.substring(i, i+2);
				bufBytes = getKeyBytes(subText);
				if (bufBytes != null){
					i++;
				}
			}

			if (bufBytes == null){
				bufBytes = getKeyBytes(Character.toString(text.charAt(i)));
			}

			if (bufBytes != null){
				for (byte b : bufBytes){
					bytes.add(b);
				}
			} else {
				bytes.add((byte)0);
			}
			i++;
		}		
		return bytes;
	}

	public byte[] parseBytes(String text){

		if (text == null || text.isEmpty()){	
			return new byte[]{(byte)0xff};
		}

		ArrayList<Byte> bytes = parseList(text);
		byte[] buf = new byte[bytes.size()+1];

		for (int i = 0; i < bytes.size(); i++) {
			buf[i] = bytes.get(i);
		}

		buf[bytes.size()] = (byte) 0xFF;

		return buf;
	}

	private Long getKey(String value){
		for(Long key : this.keySet()){
			if(this.get(key).equals(value)) return key;
		}
		return null;
	}


	private byte[] getKeyBytes(String value){

		try {
			long key = getKey(value);

			byte[] buf;
			long range = 0;
			int levelCount = 0;

			while(levelCount < level){
				levelCount++;
				range = (range << 8) + 0xFF;
				if (key <= range){
					break;
				}
			}

			buf = new byte[levelCount];

			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) ((key >> (8*(--levelCount))) & 0xFF);
			}		
			return buf;

		} catch (Exception e){
			return null;
		}
	}
}
