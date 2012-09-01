package net.bodz.lapiota.util;

import java.io.File;

import org.junit.Test;

import net.bodz.lapiota.Lapiota;


public class LapiotaTest {

    @Test
    public void testFindabc() {
        System.out.println(Lapiota.lapAbcd);

        File eclipse = Lapiota.findabc("ec");
        System.out.println(eclipse);
    }

}
