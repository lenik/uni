package net.bodz.lapiota.util;

import net.bodz.bas.a.A_bas;
import net.bodz.bas.a.VersionInfo;

import org.junit.Test;

public class BasNsTest {

    @Test
    public void test1() {
        VersionInfo ver = A_bas.parseId(A_bas.class);
        System.out.println("version = " + ver.getVersion()); //$NON-NLS-1$
        System.out.println("date = " + ver.getDate()); //$NON-NLS-1$
        System.out.println("time = " + ver.getTime()); //$NON-NLS-1$
        System.out.println("author = " + ver.author); //$NON-NLS-1$
        System.out.println("state = " + ver.state); //$NON-NLS-1$
    }

}
