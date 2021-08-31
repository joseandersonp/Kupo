package kupo.script;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class BinaryScript extends ArrayList<byte[]>{

	public boolean add(ArrayList<Byte> bytes) {
		
		byte[] byteArray = new byte[bytes.size()];

		for (int i=0; i < bytes.size(); i++){
			byteArray[i] = bytes.get(i);
		}	
		
		return super.add(byteArray);
	}
	
}