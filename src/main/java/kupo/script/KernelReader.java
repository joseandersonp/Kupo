package kupo.script;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import kupo.io.MemBuffer;

public class KernelReader {

	byte[][] sections;
	int indexSection;

	public KernelReader(File kernelFile) throws IOException {
		sections = new byte[27][];
		process(kernelFile);
	}

	private void process(File kernelFile) throws IOException {

		try (FileInputStream isKernel = new FileInputStream(kernelFile)) {
			
			byte[] kernelData = new byte[isKernel.available()];
			isKernel.read(kernelData);
			MemBuffer memBuffer = MemBuffer.wrap(kernelData);
			
			for (int count = 0; count < 27; count++) {

				int lengthComp = memBuffer.getUShort();
				int lengthDecomp = memBuffer.getUShort();
				memBuffer.getUShort();

				byte[] sectionComp = new byte[lengthComp];
				byte[] sectionDecomp = new byte[lengthDecomp];

				memBuffer.get(sectionComp);

				GZIPInputStream inputGzip = new GZIPInputStream(new ByteArrayInputStream(sectionComp));
				
				byte[] buf = new byte[2048];
				int countRead = 0;
				int countWrite = 0;
				while ((countRead = inputGzip.read(buf)) != -1) {

					System.arraycopy(buf, 0, sectionDecomp, countWrite, countRead);
					countWrite += countRead;
				}
				inputGzip.read(sectionDecomp);

				sections[count] = sectionDecomp;

			}

		}
	}

	public byte[] getSection(int index) throws IndexOutOfBoundsException {
		return sections[index];
	}

	public byte[] readSection() throws IndexOutOfBoundsException {
		return sections[indexSection++];
	}

	public void reset() {
		indexSection = 0;
	}

	public byte[][] getSections() {
		return sections;
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
