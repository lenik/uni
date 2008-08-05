package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.RunInfo;
import net.bodz.lapiota.annotations.LoadBy;
import net.bodz.lapiota.loader.Lapiota;
import net.bodz.lapiota.loader.SWTClassLoader;

@RunInfo(lib = "bodz_lapiota")
@LoadBy(SWTClassLoader.class)
public class BasicGUI extends net.bodz.swt.gui.BasicGUI {

    static {
        Lapiota.load();
    }

}
