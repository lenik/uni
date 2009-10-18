package com.lapiota.util;

import java.io.File;


import org.junit.Test;

import com.lapiota.Lapiota;

public class LapiotaTest {

    @Test
    public void testFindabc() {
        System.out.println(Lapiota.lapAbcd);

        File eclipse = Lapiota.findabc("ec"); //$NON-NLS-1$
        System.out.println(eclipse);
    }

}
