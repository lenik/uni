package net.bodz.uni.util;

import java.io.File;

import org.junit.Test;

import net.bodz.uni.Lapiota;


public class LapiotaTest {

    @Test
    public void testFindabc() {
        System.out.println(Lapiota.lapAbcd);

        File eclipse = Lapiota.findabc("ec");
        System.out.println(eclipse);
    }

}
