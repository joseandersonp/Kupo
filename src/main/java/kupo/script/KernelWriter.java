package kupo.script;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.GZIPOutputStream;

import kupo.io.MemBuffer;

public class KernelWriter {

	private byte[][] sections;
	private int indexSection;

	public KernelWriter(byte[][] sections) throws IOException {
		this.sections = sections;
	}

	public void writeFile(File kernelFile) throws IOException {

		try (FileOutputStream os = new FileOutputStream(kernelFile)) {

			int idSection = 0;
			int lengthSectionDecomp = 0;

			for (int count = 0; count < sections.length; count++) {

				byte[] sectionComp = null;
				byte[] sectionDecomp = sections[count];

				ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
				Deflater def = new Deflater(9, 31);
				GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baOutputStream, def, 1024, true);

				gzipOutputStream.write(sectionDecomp);

				lengthSectionDecomp = sectionDecomp.length;

				if (sectionDecomp.length % 2 != 0) {
					gzipOutputStream.write(0xFF);
					lengthSectionDecomp++;
				}

				gzipOutputStream.close();

				sectionComp = baOutputStream.toByteArray();
				sectionComp[9] = (byte) 0x3;

				if (count < 10) {
					idSection = count;
				}

				MemBuffer memBuffer = MemBuffer.allocate(sectionComp.length + 6);

				memBuffer.putShort(sectionComp.length);
				memBuffer.putShort(lengthSectionDecomp);
				memBuffer.putShort(idSection);
				memBuffer.put(sectionComp);

				os.write(memBuffer.array());

			}

		}
	}

	public byte[] getSection(int index) throws IndexOutOfBoundsException {
		return sections[index];
	}

	public void setSection(int index, byte[] section) throws IndexOutOfBoundsException {
		sections[index] = section;
	}

	public int writeSection(byte[] section) throws IndexOutOfBoundsException {
		sections[indexSection++] = section;
		return section.length;
	}

	public void reset() {
		indexSection = 0;
	}

	public ArrayList<byte[]> readSections() {

		ArrayList<byte[]> sections = new ArrayList<byte[]>();
		for (int i = 0; i < this.sections.length; i++) {
			sections.add(this.sections[i]);
		}
		indexSection = this.sections.length;
		return sections;

	}

	public int avaliable() {
		return sections.length - indexSection;
	}
}
