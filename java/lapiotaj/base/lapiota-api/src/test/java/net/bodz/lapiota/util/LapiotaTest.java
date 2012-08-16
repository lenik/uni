package net.bodz.lapiota.util;

import java.io.File;

import net.bodz.lapiota.Lapiota;

import org.junit.Test;


public class LapiotaTest {

    @Test
    public void testFindabc() {
        System.out.println(Lapiota.lapAbcd);

        File eclipse = Lapiota.findabc("ec");
        System.out.println(eclipse);
    }

}
