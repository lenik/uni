package com.lapiota.pooh.shortcuts.editors;

import java.io.File;

import org.junit.Test;

public class NppLauncherTest {

    @Test
    public void test() throws Exception {
        NppLauncher launcher = new NppLauncher();
        File file = new File("c:/boot.ini");
        launcher.open(file);
    }

}
