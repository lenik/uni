package net.bodz.uni.crypt.pgp;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Checksum;

import net.bodz.bas.c.java.lang.ClassTypers;
import net.bodz.bas.data.codec.builtin.HexCodec;
import net.bodz.bas.data.mem.types.Int32LE;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.res.tools.StreamReading;
import net.bodz.bas.log.LogLevel;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BatchCLI;
import net.bodz.bas.program.skel.FileHandler;
import net.bodz.bas.vfs.IFile;
import net.bodz.uni.crypt.pgp.Hashes.CRC32_BE;
import net.bodz.uni.crypt.pgp.Hashes.CRC32_LE;

/**
 * Print or check CRC (32-bit) checksums
 */
@MainVersion({ 0, 1 })
@ProgramName("crc32sum")
@RcsKeywords(id = "$Id$")
public class CRC32Sum
        extends BatchCLI {

    static final Logger logger = LoggerFactory.getLogger(CRC32Sum.class);

    /**
     * @option -k =MODULO
     */
    private long key;

    static final int CHECK = 0;
    static final int BINARY = 1;
    static final int TEXT = 2;

    private int mode = TEXT;

    /**
     * Read in binary mode (default unless reading tty stdin)
     *
     * @option -b --binary
     */
    void setBinaryMode() {
        mode = BINARY;
    }

    /**
     * Read in text mode (default if reading tty stdin)
     *
     * @option -t --text
     */
    void setTextMode() {
        mode = BINARY;
    }

    /**
     * Read in text mode (default if reading tty stdin)
     *
     * @option -c --check
     */
    void setCheckMode() {
        mode = CHECK;
    }

    /**
     * Don't output anything, status code shows success
     *
     * @option -S --status
     */
    void errStatus() {
        if (mode != CHECK)
            logger.warn(tr._("err mode is meaningful only when verifying checksums"));
        logger.setLevel(LogLevel.ERROR, 0);
    }

    /**
     * Warn about improperly formatted checksum lines
     *
     * @option -w --warn
     */
    void errWarn() {
        if (mode != CHECK)
            logger.warn(tr._("err mode is meaningful only when verifying checksums"));
        logger.setLevel(LogLevel.WARN, 0);
    }

    Class<? extends Checksum> _class = CRC32_LE.class;

    static Map<String, Class<? extends Checksum>> types;
    static {
        types = new HashMap<String, Class<? extends Checksum>>();
        types.put("le", CRC32_LE.class);
        types.put("be", CRC32_BE.class);
        types.put("pgp", PgpCrc32Checksum.class);
    }

    /**
     * @option -a =FQCN
     */
    void setAlgorithm(String name)
            throws ParseException {
        Class<? extends Checksum> clazz = types.get(name);
        if (clazz == null)
            clazz = new ClassTypers().parse(name);
        _class = clazz;
    }

    Checksum create()
            throws ReflectiveOperationException {
        Checksum inst = _class.newInstance();
        if (key != 0)
            if (inst instanceof IIntKey)
                ((IIntKey) inst).setKey((int) key);
            else
                throw new UnsupportedOperationException(tr._("algorithm ") + _class.getName()
                        + tr._(" doesn\'t support key"));
        return inst;
    }

    @Override
    protected void reconfigure()
            throws Exception {
        if (mode == CHECK)
            throw new NotImplementedException("Check is not implemented");
    }

    static HexCodec HEX = new HexCodec("");

    @Override
    public void processFile(FileHandler handler)
            throws Exception {
        IFile inputFile = handler.getInputFile();

        String name = inputFile == null ? "-" : inputFile.getName();
        byte[] data = inputFile.to(StreamReading.class).read();
        // algorithmClass.newInstance();
        Checksum csum;
        csum = create();
        csum.update(data, 0, data.length);
        byte[] buf;
        if (csum instanceof MessageDigest) {
            buf = ((MessageDigest) csum).digest();
        } else {
            int val = (int) csum.getValue();
            buf = new byte[4];
            Int32LE.write(buf, val);
        }
        String hex = HEX.encode(buf);
        System.out.println(hex + " *" + name);
    }

    @Override
    protected void showHelpPage(IPrintOut out) {
        super.showHelpPage(out);
        out.println();

        out.println(tr._("Named Algorithms: "));
        for (String name : types.keySet())
            out.printf(tr._("    %8s = %s\n"), name, types.get(name));

        out.flush();
    }

    public static void main(String[] args)
            throws Exception {
        new CRC32Sum().execute(args);
    }

}
