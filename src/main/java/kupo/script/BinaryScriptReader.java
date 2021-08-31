package kupo.script;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import kupo.io.MemBuffer;
import kupo.lzs.LZSDecompress;

public class BinaryScriptReader{

	public static final byte END_STRING = (byte) 0xFF;
	public static final byte FLAG_COMP = (byte) 0xF9;
	public static final byte FLAG_VAR_MIN = (byte) 0xEA;
	public static final byte FLAG_VAR_MAX = (byte) 0xF0;

	private BinaryScript script;
	private byte[] bufferBin;

	public BinaryScriptReader(byte[] bufferBin) throws IOException{

		this.script = new BinaryScript();
		this.bufferBin = bufferBin;

	}

	public BinaryScript generateScript() throws IOException {
		
		
		MemBuffer memBuffer = MemBuffer.wrap(bufferBin);		
		BufferedInputStream streamStripts = new BufferedInputStream(new ByteArrayInputStream(bufferBin));

		int startPointer = memBuffer.getUShort();

		LinkedList<Integer> pointers = new LinkedList<Integer>();	
		pointers.add(startPointer);

		for(int i = 2; i < startPointer; i += 2) {
			pointers.add(memBuffer.getUShort());
		}

		byte[] buffer = new byte[streamStripts.available()];		
		streamStripts.read(buffer);

		BinaryScript script = new BinaryScript();

		for(int offset : pointers){

			ArrayList<Byte> bytes = new ArrayList<Byte>();

			while(offset < buffer.length){

				if (buffer[offset] >= FLAG_VAR_MIN && buffer[offset] <= FLAG_VAR_MAX){
					
					bytes.add(buffer[offset++]);
					bytes.add(buffer[offset++]);
					bytes.add(buffer[offset++]);
					
				}else if (buffer[offset] == FLAG_COMP){

					LZSDecompress.write(buffer, offset, bytes, END_STRING);
					offset+=2;

				} else if (buffer[offset] == END_STRING) { 

					//bytes.add(buffer[offset]);
					script.add(bytes);
					break;

				} else {

					bytes.add(buffer[offset]);
					offset++;
				}				
			}
		}

		this.script = script;
		return script;
	}

	public BinaryScript getScript() throws IOException{
		if (script == null){
			generateScript();
		}
		return script;
	}

}