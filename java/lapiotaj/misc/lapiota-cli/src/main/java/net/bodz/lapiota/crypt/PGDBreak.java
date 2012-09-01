package net.bodz.lapiota.crypt;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.io.resource.tools.StreamWriting;
import net.bodz.bas.mem.Memory;
import net.bodz.bas.mem.RandomAccessFileMemory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.vfs.IFile;

/**
 * PGP disk headers break up
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 0 })
public class PGDBreak
        extends BasicCLI {

    /**
     * where to put the splitted chunk files
     *
     * @option -O =DIR
     */
    IFile outputDirectory; // = CurrentDirectoryColo.getInstance().get();

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
    protected void doFileArgument(IFile file)
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
                    " addr=", addr, //
                    " type=", type, //
                    " size=", size, //
                    " crc=", Integer.toHexString(crc));

            header = new byte[size];
            chunk.read(0, header);

            String childName = file.getName() + "." + type + "." + chunkIndex++;
            IFile chunkFile = outputDirectory.getChild(childName);

            L.mesg(tr._("write to "), chunkFile, " (", size, " bytes)");
            chunkFile.tooling()._for(StreamWriting.class).writeBytes(header);

            addr = chunk.readInt64(16);
            if (addr == 0)
                break;
            chunk = mem.offset(addr);
        }
    }

    public static void main(String[] args)
            throws Exception {
        new PGDBreak().execute(args);
    }

}
