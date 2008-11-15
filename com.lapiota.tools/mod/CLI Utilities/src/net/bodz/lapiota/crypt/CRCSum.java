package net.bodz.lapiota.crypt;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.Checksum;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.Files;
import net.bodz.bas.io.util.Checksums.IKey;
import net.bodz.bas.io.util.Checksums._Checksum;
import net.bodz.bas.lang.err.NotImplementedException;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.log.ALog;
import net.bodz.bas.mem.types.Int32BE;
import net.bodz.bas.mem.types.Int32LE;
import net.bodz.bas.text.encodings.HexEncoding;
import net.bodz.bas.types.NamedTypes;
import net.bodz.bas.types.TypeParsers.ClassParser;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.crypt.Hashes.CRC32_BE;
import net.bodz.lapiota.crypt.Hashes.CRC32_LE;
import net.bodz.lapiota.wrappers.BatchCLI;

@Doc("Print or check CRC (32-bit) checksums")
@RcsKeywords(id = "$Id: FileLister.java 29 2008-10-07 13:38:08Z lenik $")
@Version( { 0, 1 })
public class CRCSum extends BatchCLI {

    @Option(alias = "k", vnam = "CRC-MODULO")
    private long     key;

    static final int CHECK  = 0;
    static final int BINARY = 1;
    static final int TEXT   = 2;

    private int      mode   = TEXT;

    @Option(name = "binary", alias = "b", doc = "read in binary mode (default unless reading tty stdin)")
    void setBinaryMode() {
        mode = BINARY;
    }

    @Option(name = "text", alias = "t", doc = "read in text mode (default if reading tty stdin)")
    void setTextMode() {
        mode = BINARY;
    }

    @Option(name = "check", alias = "c", doc = "read in text mode (default if reading tty stdin)")
    void setCheckMode() {
        mode = CHECK;
    }

    @Option(name = "status", alias = "S", doc = "don't output anything, status code shows success")
    void errStatus() {
        if (mode != CHECK)
            L.w.P("err mode is meaningful only when verifying checksums");
        L.setLevel(ALog.ERROR);
    }

    @Option(name = "warn", alias = "w", doc = "warn about improperly formatted checksum lines")
    void errWarn() {
        if (mode != CHECK)
            L.w.P("err mode is meaningful only when verifying checksums");
        L.setLevel(ALog.WARN);
    }

    Class<? extends Checksum>   _class = CRC32_LE.class;

    static NamedTypes<Checksum> types;
    static {
        types = new NamedTypes<Checksum>();
        types.put("le", CRC32_LE.class);
        types.put("be", CRC32_BE.class);
        types.put("pgp", CRC32pgp.class);
    }

    @SuppressWarnings("unchecked")
    @Option(alias = "a", vnam = "CLASS")
    void setAlgorithm(String name) throws ParseException {
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
                throw new UnsupportedOperationException("algorithm "
                        + _class.getName() + " doesn't support key");
        return inst;
    }

    @Override
    protected void _boot() throws Throwable {
        if (mode == CHECK)
            throw new NotImplementedException("Check is not implemented");
    }

    static HexEncoding HEX = new HexEncoding("");

    @Override
    protected void doFile(File file, InputStream in) throws Throwable {
        String name = file == null ? "-" : file.getName();
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
        System.out.println(hex + " *" + name);
    }

    @Override
    protected InputStream _getDefaultIn() {
        return System.in;
    }

    @Override
    protected void _help(CharOut out) throws CLIException {
        super._help(out);
        out.println();

        out.println("Named Algorithms: ");
        for (String name : types.keySet())
            out.printf("    %8s = %s\n", name, types.get(name));
    }

    public static void main(String[] args) throws Throwable {
        new CRCSum().run(args);
    }

    public static class CRC32pgp extends _Checksum implements IKey {

        public static final int  STD  = 0x04c11db7;
        private static final int INIT = 0;

        private int              key  = STD;
        private int              crc;

        public CRC32pgp() {
            this.crc = INIT;
        }

        public CRC32pgp(int crc) {
            this.crc = crc;
        }

        @Override
        public CRC32pgp clone() throws CloneNotSupportedException {
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
