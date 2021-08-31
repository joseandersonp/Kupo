package kupo.io;

public class MemBuffer {

	private byte buffer[];
	private int offset;
	private ByteOrder order = ByteOrder.LITTLE_ENDIAN;

	public enum ByteOrder {
		LITTLE_ENDIAN, BIG_ENDIAN;
	}

	private MemBuffer(int size) {
		this(new byte[size]);
	}

	private MemBuffer(byte[] data) {
		this.buffer = data;
	}

	private MemBuffer(int size, ByteOrder order) {
		this(new byte[size], order);
	}

	private MemBuffer(byte[] data, ByteOrder order) {
		this.buffer = data;
		if (order != null)
			this.order = order;
	}

	public static MemBuffer allocate(int size) {
		return new MemBuffer(size);
	}

	public static MemBuffer wrap(byte[] data) {
		return new MemBuffer(data);
	}

	public static MemBuffer wrap(byte[] data, ByteOrder order) {
		return new MemBuffer(data);
	}

	public ByteOrder getOrder() {
		return order;
	}

	public void setOrder(ByteOrder order) {
		this.order = order;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return buffer.length;
	}
	
	public int get(byte[] dest) {
		return get(dest, 0, dest.length);
	}
	
	public int get(byte[] dest, int offs, int len) {
		int c = 0;
		for (int i = offs; i < len; i++) {
			if (i >= dest.length || offset >= buffer.length)
				break;
			dest[i] = buffer[offset++];
			c++;
		}
		return c;
	}

	public void put(byte[] src) {
		for (int i = 0; i < src.length; i++)
			buffer[offset++] = src[i];
	}

	public byte get() {
		return buffer[offset++];
	}

	public void put(int b) {
		buffer[offset++] = (byte)(b & 0xFF);
	}
	
	public byte get(int o) {
		offset = o;
		return buffer[offset++];
	}

	public void put(int b, int o) {
		offset = o;
		buffer[offset++] = (byte)(b & 0xFF);
	}	

	public short getShort() {
		int value;
		if (order == ByteOrder.LITTLE_ENDIAN)
			value = (buffer[offset + 1] & 0xFF) << 8 | (buffer[offset] & 0xFF);
		else
			value = (buffer[offset] & 0xFF) << 8 | (buffer[offset + 1] & 0xFF);
		offset += 2;
		return (short) value;
	}

	public void putShort(int s) {
		if (order == ByteOrder.LITTLE_ENDIAN) {
			buffer[offset] = (byte) (s & 0xFF);
			buffer[offset + 1] = (byte) ((s >> 8) & 0xFF);
		} else {
			buffer[offset] = (byte) ((s >> 8) & 0xFF);
			buffer[offset + 1] = (byte) (s & 0xFF);
		}
		offset += 2;
	}

	public int getInt() {
		int value;
		if (order == ByteOrder.LITTLE_ENDIAN) {
			value = (buffer[offset] & 0xFF) | ((buffer[offset + 1] & 0xFF) << 8) | ((buffer[offset + 2] & 0xFF) << 16)
					| ((buffer[offset + 3] & 0xFF) << 24);
		} else {
			value = ((buffer[offset] & 0xFF) << 24) | ((buffer[offset + 1] & 0xFF) << 16) | ((buffer[offset + 2] & 0xFF) << 8)
					| (buffer[offset + 3] & 0xFF);
		}
		offset += 4;
		return value;
	}

	public void putInt(int i) {
		if (order == ByteOrder.LITTLE_ENDIAN) {
			buffer[offset] = (byte) (i & 0xFF);
			buffer[offset + 1] = (byte) ((i >> 8) & 0xFF);
			buffer[offset + 2] = (byte) ((i >> 16) & 0xFF);
			buffer[offset + 3] = (byte) ((i >> 24) & 0xFF);

		} else {
			buffer[offset] = (byte) ((i >> 24) & 0xFF);
			buffer[offset + 1] = (byte) ((i >> 16) & 0xFF);
			buffer[offset + 2] = (byte) ((i >> 8) & 0xFF);
			buffer[offset + 3] = (byte) (i & 0xFF);
		}
		offset += 4;
	}

	public long getLong() {
		int value;
		if (order == ByteOrder.LITTLE_ENDIAN) {
			value = ((buffer[offset + 3] & 0xFF) << 24) | ((buffer[offset + 2] & 0xFF) << 16)
					| ((buffer[offset + 1] & 0xFF) << 8) | (buffer[offset] & 0xFF);
		} else {
			value = (buffer[offset] & 0xFF) | ((buffer[offset + 1] & 0xFF) << 8) | ((buffer[offset + 2] & 0xFF) << 16)
					| ((buffer[offset + 3] & 0xFF) << 24);
		}
		offset += 8;
		return value;
	}

	public void putLong(long l) {
		if (order == ByteOrder.LITTLE_ENDIAN) {
			buffer[offset] = (byte) (l & 0xFF);
			buffer[offset + 1] = (byte) ((l >> 8) & 0xFF);
			buffer[offset + 2] = (byte) ((l >> 16) & 0xFF);
			buffer[offset + 3] = (byte) ((l >> 24) & 0xFF);
			buffer[offset + 4] = (byte) ((l >> 32) & 0xFF);
			buffer[offset + 5] = (byte) ((l >> 40) & 0xFF);
			buffer[offset + 6] = (byte) ((l >> 48) & 0xFF);
			buffer[offset + 7] = (byte) ((l >> 56) & 0xFF);

		} else {
			buffer[offset] = (byte) ((l >> 56) & 0xFF);
			buffer[offset + 1] = (byte) ((l >> 48) & 0xFF);
			buffer[offset + 2] = (byte) ((l >> 40) & 0xFF);
			buffer[offset + 3] = (byte) ((l >> 32) & 0xFF);
			buffer[offset + 4] = (byte) ((l >> 16) & 0xFF);
			buffer[offset + 5] = (byte) ((l >> 24) & 0xFF);
			buffer[offset + 6] = (byte) ((l >> 8) & 0xFF);
			buffer[offset + 7] = (byte) (l & 0xFF);
		}
		offset += 8;
	}
	
	public int getU() {
		return get() & 0xFF;
	}

	public int getUShort() {
		return getShort() & 0xFFFF;
	}

	public void putUShort(int us) {
		putShort((short) (us & 0xFFFF));
	}

	public long getUInt() {
		return getInt() & 0xFFFFFFFF;
	}

	public void putUInt(long ui) {
		putInt((int) (ui & 0xFFFFFFFF));
	}
	
	public byte[] array() {
		return buffer;
	}
	
	public static void main(String[] args) {
				
		byte[] data = new byte[] {(byte)0x80, 0x70, 0x60, 0x50, 0x40, 0x30, 0x20, 0x10};
		MemBuffer buffer = MemBuffer.wrap(data);
		buffer.setOrder(ByteOrder.BIG_ENDIAN);
		
		System.out.printf("%d%n",buffer.getShort());
		System.out.printf("%d%n",buffer.getUShort());
		System.out.printf("%d%n",buffer.getShort());
		System.out.printf("%d%n",buffer.getUShort());
		
	}

	public void skip(int b) {
		offset += b;
	}
}
