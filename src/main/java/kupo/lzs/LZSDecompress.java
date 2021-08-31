package kupo.lzs;

import java.util.ArrayList;

public class LZSDecompress {

	
	public static void write(byte[] input, int offset, ArrayList<Byte> bytes, byte flagEndString ){//	
	byte byteComp = input[offset+1];

	int offsetTail = offset - (byteComp & 0x3F) -1;
	int size = 4 + (2 * ((byteComp >> 6) & 0x3));

	for (int i = offsetTail; size > 0 ; --size, i++) {

		if (input[i] == flagEndString){
			break;
		}
		bytes.add(input[i]);
	}
}
	
}
