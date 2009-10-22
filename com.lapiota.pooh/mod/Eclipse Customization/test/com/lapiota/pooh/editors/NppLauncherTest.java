package com.lapiota.pooh.editors;

import java.io.File;

import com.lapiota.pooh.editors.NppLauncher;

public class NppLauncherTest {

    public static void main(String[] args) throws Exception {
        NppLauncher launcher = new NppLauncher();
        File file = new File("c:/boot.ini");
        launcher.open(file);
    }

}
