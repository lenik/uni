package net.bodz.lapiota.util;

import java.io.File;

import net.bodz.bas.cli.CLIConfig;
import net.bodz.lapiota.loader.Lapiota;

import org.junit.Test;

public class LapiotaTest {

    @Test
    public void testFindabc() {
        System.out.println(Lapiota.lapAbcd);

        File eclipse = CLIConfig.findPath.findabc("ec");
        System.out.println(eclipse);
    }

}
