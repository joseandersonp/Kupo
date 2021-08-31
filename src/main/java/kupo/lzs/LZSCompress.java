package kupo.lzs;

import java.util.ArrayList;

public class LZSCompress{
	
	private int minBufferLength;
	private int bufferLength;
	private int windowLength;
	private byte flagEndString;
	private byte flagCompress;
	
	private ArrayList<Byte> output = new ArrayList<Byte>();
	
	public LZSCompress(int minBufferLength, int bufferLength, int WindowLength, byte flagEndString, byte flagCompress){
		
		this.minBufferLength = minBufferLength;
		this.bufferLength = bufferLength;
		this.windowLength = WindowLength;
		this.flagCompress = flagCompress;
		this.flagEndString = flagEndString;
		
	}
	
	public void writeLiteral(byte literal){
		output.add(literal);
	}
	
	public void writeLiteral(byte[] literals){
		for (int i = 0; i < literals.length; i++) {
			output.add(literals[i]);
		}
	}

	public void write(byte[] input){
		
		int offsetIniWindow = 0;
		int offsetEndWindow = 0;

		int offsetInput = 0;
		int offsetOutput = output.size();
		
		int offsetIniBuffer = 0;
		int offsetEndBuffer = 0;
		
		while (offsetInput < input.length){

			offsetIniWindow = offsetOutput - windowLength;
			offsetEndWindow = offsetOutput;
			
			offsetIniBuffer = offsetInput;			 
			offsetEndBuffer = offsetInput + bufferLength;

			if (offsetIniWindow < 0){
				offsetIniWindow = 0;
			}		

			if (offsetEndBuffer > input.length){
				offsetEndBuffer = input.length;
			}
			
			int indexWindow = offsetIniWindow;
			int indexBuffer = offsetIniBuffer;
			
			int lengthOccur= 0;
			int offsetOccur= 0;
			
			while (indexWindow < offsetEndWindow){
				
				indexBuffer = offsetIniBuffer;
				
				while (output.get(indexWindow) == input[indexBuffer]){
					
					indexWindow++;
					indexBuffer++;
					
					if (indexWindow == offsetEndWindow || indexBuffer >= offsetEndBuffer){
						break;
					}
				}				
				
				int countBuffer = indexBuffer - offsetIniBuffer;
				
				if (countBuffer >=  + minBufferLength && countBuffer > lengthOccur){
					
					lengthOccur = countBuffer;
					offsetOccur = offsetEndWindow - (indexWindow-countBuffer) -1;
				}
				indexWindow += 1 - countBuffer;	
				
			}
			
			if(lengthOccur >= minBufferLength){
				
				output.add(flagCompress);
				 
				int tupple = offsetOccur;
				tupple	+= (((lengthOccur - minBufferLength) >> 1) << 6);
				 
				output.add((byte) tupple);
				
				//System.out.printf("[Encoding: %02X%02X ," ,flagCompress,tupple);
				//System.out.printf("Tuple:    %02X : %02X]\n" ,lengthOccur,offsetOccur);
				
				offsetOutput += 2;
				offsetInput += (tupple >> 6) * 2 + 4;
	
			} else {
				output.add(input[offsetIniBuffer]);
				offsetInput++;
				offsetOutput++;
			}
			lengthOccur = 0;
		}
		
		if (output.get(output.size()-1) != flagEndString){
			output.add(flagEndString);
		}
	}
	
	public ArrayList<Byte> toArrayList() {
		return output;
	}
	
	public byte[] toArray(){
		byte[] bytes = null;
		if (output != null){
			bytes = new byte[output.size()];
			int i = 0;
			for(byte b :output){
				bytes[i++] = b;
			}
		}
		return bytes;
	}
	
	public int size(){
		return output.size();
	}
}