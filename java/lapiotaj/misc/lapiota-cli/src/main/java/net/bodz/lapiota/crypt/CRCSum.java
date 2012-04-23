package net.bodz.lapiota.crypt;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.zip.Checksum;

import net.bodz.bas.c.type.Types;
import net.bodz.bas.cli.BatchCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.err.NotImplementedException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.io.util.Checksums.IKey;
import net.bodz.bas.io.util.Checksums._Checksum;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.text.codec.builtin.HexCodec;
import net.bodz.lapiota.crypt.Hashes.CRC32_BE;
import net.bodz.lapiota.crypt.Hashes.CRC32_LE;
import net.bodz.lapiota.nls.CLINLS;

/**
 * Print or check CRC (32-bit) checksums
 */
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class CRCSum
        extends BatchCLI {

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
    @Option(name = "binary", alias = "b", doc = "")
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
            L.warn(CLINLS.getString("CRCSum.errModeOnlyVerify")); //$NON-NLS-1$
        L.setLevel(LogTerm.ERROR);
    }

    /**
     * Warn about improperly formatted checksum lines
     *
     * @option -w --warn
     */
    void errWarn() {
        if (mode != CHECK)
            L.warn(CLINLS.getString("CRCSum.errModeOnlyVerify")); //$NON-NLS-1$
        L.setLevel(LogTerm.WARN);
    }

    Class<? extends Checksum> _class = CRC32_LE.class;

    static NamedTypes<Checksum> types;
    static {
        types = new NamedTypes<Checksum>();
        types.put("le", CRC32_LE.class); //$NON-NLS-1$
        types.put("be", CRC32_BE.class); //$NON-NLS-1$
        types.put("pgp", CRC32pgp.class); //$NON-NLS-1$
    }

    /**
     * @option -a =FQCN
     */
    @SuppressWarnings("unchecked")
    void setAlgorithm(String name)
            throws ParseException {
        Class<? extends Checksum> clazz = types.get(name);
        if (clazz == null)
            clazz = (Class<? extends Checksum>) new ClassParser().parse(name);
        _class = clazz;
    }

    Checksum create() {
        Checksum inst = Types.newInstance(_class);
        if (key != 0)
            if (inst instanceof IKey)
                ((IKey) inst).setKey((int) key);
            else
                throw new UnsupportedOperationException(CLINLS.getString("CRCSum.5") //$NON-NLS-1$
                        + _class.getName() + CLINLS.getString("CRCSum.6")); //$NON-NLS-1$
        return inst;
    }

    @Override
    protected void _boot()
            throws Exception {
        if (mode == CHECK)
            throw new NotImplementedException("Check is not implemented"); //$NON-NLS-1$
    }

    static HexCodec HEX = new HexCodec(""); //$NON-NLS-1$

    @Override
    protected void doFile(File file, InputStream in)
            throws Exception {
        String name = file == null ? "-" : file.getName(); //$NON-NLS-1$
        byte[] data = Files.readBytes(in);
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
        System.out.println(hex + " *" + name); //$NON-NLS-1$
    }

    @Override
    protected InputStream _getDefaultIn() {
        return System.in;
    }

    @Override
    protected void _help(CharOut out)
            throws CLIException {
        super._help(out);
        out.println();

        out.println(CLINLS.getString("CRCSum.namedAlgs")); //$NON-NLS-1$
        for (String name : types.keySet())
            out.printf(CLINLS.getString("CRCSum.algInfo_ss"), name, types.get(name)); //$NON-NLS-1$

        out.flush();
    }

    public static void main(String[] args)
            throws Exception {
        new CRCSum().run(args);
    }

    public static class CRC32pgp
            extends _Checksum
            implements IKey {

        public static final int STD = 0x04c11db7;
        private static final int INIT = 0;

        private int key = STD;
        private int crc;

        public CRC32pgp() {
            this.crc = INIT;
        }

        public CRC32pgp(int crc) {
            this.crc = crc;
        }

        @Override
        public CRC32pgp clone()
                throws CloneNotSupportedException {
            CRC32pgp copy = new CRC32pgp(crc);
            copy.key = key;
            return copy;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public void setKey(int key) {
            this.key = key;
        }

        @Override
        public final void reset() {
            this.crc = INIT;
        }

        @Override
        public final void update(int b) {
            assert b >= 0;
            crc ^= b << 8;
            for (int bit = 0; bit < 8; bit++) {
                int check = crc;
                crc <<= 1;
                if (check < 0)
                    crc ^= key;
            }
        }

        @Override
        public long getValue() {
            int x = crc; // ^ 0xffffffff;
            return x & 0xffffffffl;
        }

        public byte[] getBytesLE() {
            byte[] buf = new byte[4];
            Int32LE.write(buf, (int) getValue());
            return buf;
        }

        public byte[] getBytesBE() {
            byte[] buf = new byte[4];
            Int32BE.write(buf, (int) getValue());
            return buf;
        }

    }

}
