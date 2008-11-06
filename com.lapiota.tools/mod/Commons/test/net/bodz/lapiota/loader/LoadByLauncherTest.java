package net.bodz.lapiota.loader;

import net.bodz.bas.loader.BundledLoader;
import net.bodz.bas.loader.LoadByLauncher;

public class LoadByLauncherTest {

    public void testBundledLoader() throws Throwable {
        String loader = BundledLoader.class.getName();
        String main = "net.bodz.lapiota.devhelpers.WindowsHack";

        LoadByLauncher.main(new String[] { loader, main });
    }

    public static void main(String[] args) throws Throwable {
        new LoadByLauncherTest().testBundledLoader();
    }

}
