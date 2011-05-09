package com.lapiota.pooh.util;


import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import com.lapiota.pooh.util.SysShells;

public class SysShellsTest {

    @Test
    public void testDefault() throws Exception {
        SysShells.open(new Shell(), null);
    }

}
