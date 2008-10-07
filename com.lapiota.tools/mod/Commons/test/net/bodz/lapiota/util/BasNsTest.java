package net.bodz.lapiota.util;

import net.bodz.bas.a.A_bas;
import net.bodz.bas.a.VersionInfo;

import org.junit.Test;

public class BasNsTest {

    @Test
    public void test1() {
        VersionInfo ver = A_bas.parseId(A_bas.class);
        System.out.println("version = " + ver.getVersion());
        System.out.println("date = " + ver.getDate());
        System.out.println("time = " + ver.getTime());
        System.out.println("author = " + ver.author);
        System.out.println("state = " + ver.state);
    }

}
