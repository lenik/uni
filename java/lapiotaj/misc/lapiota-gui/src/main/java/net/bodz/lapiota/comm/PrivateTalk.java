package net.bodz.lapiota.comm;

import org.eclipse.swt.widgets.Display;

import net.bodz.swt.program.BasicGUI;

public class PrivateTalk
        extends BasicGUI {

    @Override
    protected void doMainManaged(String[] args)
            throws Exception {
        System.out.println(Display.getCurrent());
        L.mesg("Hello");
    }

    public static void main(String[] args)
            throws Exception {
        new PrivateTalk().execute(args);
    }

}

class PrivateTalkFile {

    byte version;

    static final int MAC_MD5 = 1;
    static final int MAC_SHA1 = 2;

    // static final int ENC
    void parse(byte[] data) {
        version = data[0];
        if (version != 1)
            throw new IllegalArgumentException("invalid version: " + version);
        // byte reserved = data[1];
        // byte crypto = data[2];
        // byte flags = data[3];

        /**
         * flags: <br>
         * <ul>
         * 7: gzip
         * <li>6-4: mac: md5/sha1
         * <li>3: reserved
         * <li>2-0: encoding
         * </ul>
         */
    }

}
