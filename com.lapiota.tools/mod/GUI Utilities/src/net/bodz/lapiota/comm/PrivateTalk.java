package net.bodz.lapiota.comm;

import org.eclipse.swt.widgets.Display;

import net.bodz.bas.cli.a.RunInfo;
import net.bodz.lapiota.a.LoadBy;
import net.bodz.lapiota.loader.SWTClassLoader;
import net.bodz.lapiota.wrappers.BasicCLI;
import net.bodz.lapiota.wrappers.JavaLauncher;

@RunInfo(lib = { "bodz_swt", "bodz_icons" },

load = { "findcp|eclipse*/plugins/org.eclipse.swt_*", })
@LoadBy(

value = SWTClassLoader.class,

launcher = JavaLauncher.class

)
public class PrivateTalk extends BasicCLI {

    @Override
    protected void doMain(String[] args) throws Throwable {
        System.out.println(Display.getCurrent());
        L.m.P("Hello");
    }

    public static void main(String[] args) throws Throwable {
        new PrivateTalk().run(args);
    }

}

class PrivateTalkFile {

    byte             version;

    static final int MAC_MD5  = 1;
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
