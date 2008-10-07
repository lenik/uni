package net.bodz.lapiota.javatools;

import org.junit.Test;

public class ClassLauncherTest {

    @Test
    public void testSWTLoader() throws Throwable {
        String loader = "net.bodz.lapiota.loader.SWTClassLoader";
        String main = "net.bodz.lapiota.devhelpers.WindowsHack";
        ClassLauncher.main(new String[] { loader, main });
    }

    public static void main(String[] args) throws Throwable {
        new ClassLauncherTest().testSWTLoader();
    }

}
