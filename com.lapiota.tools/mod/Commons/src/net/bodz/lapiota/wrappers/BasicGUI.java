package net.bodz.lapiota.wrappers;

import net.bodz.bas.a.LoadBy;
import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.loader.BundledLoader;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.loader.Lapiota;

@LoadBy(value = BundledLoader.class, launcher = LoadByLauncher.class)
@RunInfo(lib = "bodz_lapiota",

load = { "findcp eclipse*/plugins/org.eclipse.swt.win32.win32.x86_*", })
public class BasicGUI extends net.bodz.swt.gui.BasicGUI {

    static {
        Types.load(Lapiota.class);
    }

}
