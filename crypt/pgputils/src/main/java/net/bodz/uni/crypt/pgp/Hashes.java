package net.bodz.uni.crypt.pgp;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.Provider;
import java.util.zip.Checksum;

import net.bodz.bas.data.mem.types.Int32BE;
import net.bodz.bas.data.mem.types.Int32LE;
import net.bodz.bas.err.UnexpectedException;

public class Hashes {

    interface PeekDigest {
        byte[] peekDigest();
    }

    public static class HashProvider
            extends Provider {

        private static final long serialVersionUID = -1577821675944126223L;

        public HashProvider() {
            super("Lapiota Hashes", 0.1, "For crypt.Hashes");
        }

    }

    public static abstract class CRC32
            extends MessageDigest
            implements Checksum, PeekDigest, Cloneable {

        protected java.util.zip.CRC32 crc32;

        static final Field CRC32_crc;
        static {
            try {
                CRC32_crc = java.util.zip.CRC32.class.getDeclaredField("crc");
            } catch (SecurityException e) {
                throw new Error(e);
            } catch (NoSuchFieldException e) {
                throw new UnexpectedException(e);
            }
            CRC32_crc.setAccessible(true);
        }

        protected static int getCrc(java.util.zip.CRC32 crc32) {
            try {
                return CRC32_crc.getInt(crc32);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        }

        public CRC32(String algorithm, int state) {
            super(algorithm);
            crc32 = new java.util.zip.CRC32();
            if (state != 0)
                try {
                    CRC32_crc.set(crc32, state);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
        }

        protected abstract byte[] encode(int int32);

        @Override
        public byte[] peekDigest() {
            return encode((int) crc32.getValue());
        }

        @Override
        protected byte[] engineDigest() {
            byte[] digest = encode((int) crc32.getValue());
            crc32.reset();
            return digest;
        }

        @Override
        protected void engineReset() {
            crc32.reset();
        }

        @Override
        protected void engineUpdate(byte input) {
            crc32.update(input);
        }

        @Override
        protected void engineUpdate(byte[] input, int offset, int len) {
            crc32.update(input, offset, len);
        }

        @Override
        public long getValue() {
            return crc32.getValue();
        }

        @Override
        public void update(int b) {
            crc32.update(b);
        }

    }

    public static class CRC32_LE
            extends CRC32 {

        public CRC32_LE() {
            this(0);
        }

        public CRC32_LE(int state) {
            super("CRC32-LE", state);
        }

        @Override
        protected byte[] encode(int int32) {
            byte[] buf = new byte[4];
            Int32LE.write(buf, int32);
            return buf;
        }

        @Override
        public Object clone()
                throws CloneNotSupportedException {
            return new CRC32_LE(getCrc(crc32));
        }

    }

    public static class CRC32_BE
            extends CRC32 {

        public CRC32_BE() {
            this(0);
        }

        public CRC32_BE(int state) {
            super("CRC32-BE", state);
        }

        @Override
        protected byte[] encode(int int32) {
            byte[] buf = new byte[4];
            Int32BE.write(buf, int32);
            return buf;
        }

        @Override
        public Object clone()
                throws CloneNotSupportedException {
            return new CRC32_BE(getCrc(crc32));
        }

    }

}
