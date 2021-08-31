package kupo.ui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.GZIPOutputStream;
import com.jcraft.jzlib.InflaterInputStream;

/**
 * Example program to demonstrate how to use zlib compression with
 * Java.
 * Inspired by http://stackoverflow.com/q/6173920/600500.
 */
public class ZlibCompression {

    /**
     * Compresses a file with zlib compression.
     */
    public static void compressFile(File raw, File compressed)
        throws IOException
    {
        InputStream in = new FileInputStream(raw);
        Deflater def = new Deflater();
        def.init(9);
        OutputStream out =
            new GZIPOutputStream(new FileOutputStream(compressed), def, 1024,true);
        
        
        shovelInToOut(in, out);
        in.close();
        out.close();
    }

    /**
     * Decompresses a zlib compressed file.
     */
    public static void decompressFile(File compressed, File raw)
        throws IOException
    {
        InputStream in =
            new InflaterInputStream(new FileInputStream(compressed));
        OutputStream out = new FileOutputStream(raw);
        shovelInToOut(in, out);
        in.close();
        out.close();
    }

    /**
     * Shovels all data from an input stream to an output stream.
     */
    private static void shovelInToOut(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;
        while((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }

    /**
     * Main method to test it all.
     */
    public static void main(String[] args) throws IOException, DataFormatException {
        File compressed = new File("teste/KERNEL.BIN7.INFLATE");
        compressFile(new File("teste/7"), compressed);
        //decompressFile(compressed, new File("decompressed.txt"));
    }
}