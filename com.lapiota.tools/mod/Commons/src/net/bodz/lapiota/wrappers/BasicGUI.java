package net.bodz.lapiota.wrappers;

import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.a.LoadBy;
import net.bodz.lapiota.loader.Lapiota;
import net.bodz.lapiota.loader.SWTClassLoader;

@RunInfo(lib = "bodz_lapiota")
@LoadBy(

value = SWTClassLoader.class,

launcher = JavaLauncher.class

)
public class BasicGUI extends net.bodz.swt.gui.BasicGUI {

    static {
        Types.load(Lapiota.class);
    }

}
