package kupo.script;

import kupo.lzs.LZSCompress;

public class BinaryStriptWriter {

	byte[] output;
	BinaryScript script;

	public BinaryStriptWriter(BinaryScript script) {
		this.script = script;
	}
	
	public byte[] generateUncompressed(){

		LZSCompress outputLZS = new LZSCompress(4, 10, 64,(byte) 0xFF, (byte)0xF9);
		byte[] output;

		int startPointer = script.size() * 2;
		int pointer = startPointer;
		int countPointer = 0 ;

		int[] pointers = new int[script.size()];
	

		for (byte[] bytes : script){

			pointers[countPointer] = pointer;

			if (bytes[0] == (byte)0xFF) {

				pointers[countPointer] = pointer - 1;

			} else {
				outputLZS.writeLiteral(bytes);
				pointer =  startPointer + outputLZS.size();
			}

			countPointer++;
		}

		output = new byte[startPointer + outputLZS.size()];

		int j = 0;
		for (int p : pointers){
			output[j++] =  (byte)(p & 0xFF);
			output[j++] =  (byte)((p >>> 8) & 0xFF);
		}
		
		byte[] source = outputLZS.toArray();

		System.arraycopy(source, 0, output, startPointer, source.length);
		
		this.output = output;
		
		return output;

	}
	
	public byte[] generate(){

		byte[] output;

		LZSCompress outputLZS = new LZSCompress(4, 10, 64,(byte) 0xFF, (byte)0xF9);

		int startPointer = script.size() * 2;
		int pointer = startPointer;
		int countPointer = 0 ;

		int[] pointers = new int[script.size()];


		for (byte[] bytes : script){

			pointers[countPointer] = pointer;

			if (bytes[0] == (byte)0xFF) {

				pointers[countPointer] = pointer - 1;

			} else {

				if (bytes[0] == (byte)0xF8){

					outputLZS.writeLiteral(bytes);
					pointer =  startPointer + outputLZS.size();

				} else {

					outputLZS.write(bytes);
					pointer =  startPointer + outputLZS.size();	
				}
			}

			countPointer++;
		}

		output = new byte[startPointer + outputLZS.size()];

		int j = 0;
		for (int p : pointers){
			output[j++] =  (byte)(p & 0xFF);
			output[j++] =  (byte)((p >>> 8) & 0xFF);		
		}
		
		byte[] source = outputLZS.toArray();

		System.arraycopy(source, 0, output, startPointer, source.length);

		this.output = output;
		return output;

	}
	
	public byte[] generate09(){

		byte[] output;

		LZSCompress outputLZS = new LZSCompress(4, 10, 64,(byte) 0xFF, (byte)0xF9);

		int startPointer = script.size() * 2;
		int pointer = startPointer;
		int countPointer = 0 ;

		int[] pointers = new int[script.size()];
		
		pointers[countPointer] = (byte)(0x3F);
		

		for (byte[] bytes : script){
			
			if (countPointer == 0){
				countPointer++;
				continue;
			}

			pointers[countPointer] = pointer;

			if (bytes[0] == (byte)0xFF) {

				pointers[countPointer] = pointer - 1;

			} else {

				if (bytes[0] == (byte)0xF8){

					outputLZS.writeLiteral(bytes);
					pointer =  startPointer + outputLZS.size();

				} else {

					outputLZS.write(bytes);
					pointer =  startPointer + outputLZS.size();	
				}
			}

			countPointer++;
		}

		output = new byte[startPointer + outputLZS.size()];

		int j = 0;
		for (int p : pointers){
			output[j++] =  (byte)(p & 0xFF);
			output[j++] =  (byte)((p >>> 8) & 0xFF);		
		}
		
		byte[] source = outputLZS.toArray();

		System.arraycopy(source, 0, output, startPointer, source.length);

		this.output = output;
		return output;

	}

	public byte[] getBytes(){
		if (output == null) {
			generate();
		}
		return output;
	}
}