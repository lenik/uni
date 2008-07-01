package net.bodz.lapiota.util;

import net.bodz.bas.cli.util.Rcs;
import net.bodz.bas.cli.util.VersionInfo;

import org.junit.Test;

public class RcsTest {

    @Test
    public void test1() {
        VersionInfo ver = Rcs.parseId(Rcs.class);
        System.out.println("version = " + ver.getVersion());
        System.out.println("date = " + ver.getDate());
        System.out.println("time = " + ver.getTime());
        System.out.println("author = " + ver.author);
        System.out.println("state = " + ver.state);
    }

}
