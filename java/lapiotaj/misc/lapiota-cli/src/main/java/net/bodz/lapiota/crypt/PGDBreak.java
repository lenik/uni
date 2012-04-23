package net.bodz.lapiota.crypt;

import java.io.File;
import java.nio.file.Files;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.lapiota.nls.CLINLS;

/**
 * PGP disk headers break up
 */
@RcsKeywords(id = "$Id$")
@Version({ 0, 0 })
public class PGDBreak
        extends BasicCLI {

    /**
     * where to put the splitted chunk files
     *
     * @option -O =DIR
     */
    File outputDirectory = new File("."); //$NON-NLS-1$

    /**
     * <pre>
     *     PGPUInt32   headerMagic;    // Always OnDiskHeaderInfo::kHeaderMagic
     *     PGPUInt32   headerType;     // One of the HeaderType enums
     *     PGPUInt32   headerSize;     // Total size of this header, in bytes
     *
     *     PGPUInt32   headerCRC;          // CRC of this header
     *     PGPUInt64   nextHeaderOffset;   // Offset to next header from file start
     *                                     // 0 = no additional headers
     *     PGPUInt32   reserved[2];
     * </pre>
     */
    @Override
    protected void doFileArgument(File file)
            throws Exception {
        Memory mem = new RandomAccessFileMemory(file, 0);
        Memory chunk = mem;
        long addr = 0;
        int chunkIndex = 0;
        while (true) {
            byte[] header = new byte[32];
            chunk.read(0, header);
            String magic = new String(header, 0, 4);
            String type = new String(header, 4, 4);
            int size = chunk.readInt32(8);
            int crc = chunk.readInt32(12);
            L.mesg(magic, //
                    " addr=", addr, // //$NON-NLS-1$
                    " type=", type, // //$NON-NLS-1$
                    " size=", size, // //$NON-NLS-1$
                    " crc=", Integer.toHexString(crc)); //$NON-NLS-1$

            header = new byte[size];
            chunk.read(0, header);
            File chunkFile = new File(outputDirectory, file.getName() //
                    + "." + type + "." + chunkIndex++); //$NON-NLS-1$ //$NON-NLS-2$
            L.mesg(CLINLS.getString("PGDBreak.writeTo"), chunkFile, " (", size, " bytes)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Files.write(chunkFile, header);

            addr = chunk.readInt64(16);
            if (addr == 0)
                break;
            chunk = mem.offset(addr);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new PGDBreak().run(args);
    }

}
