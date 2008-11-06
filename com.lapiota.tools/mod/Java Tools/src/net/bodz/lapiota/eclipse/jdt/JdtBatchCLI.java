package net.bodz.lapiota.eclipse.jdt;

import net.bodz.bas.a.LoadBy;
import net.bodz.bas.cli.a.RunInfo;
import net.bodz.bas.types.util.Types;
import net.bodz.lapiota.wrappers.BatchProcessCLI;
import net.bodz.lapiota.wrappers.JavaLauncher;

@RunInfo(load = {
        "fortype %AST | findcp eclipse*/plugins/org.eclipse.jdt.core_*",
        "fortype %TextEdit | findcp eclipse*/plugins/org.eclipse.text_*", },

loadDelayed = { "findcp eclipse*/plugins/org.eclipse.equinox.common_*",
        "findcp eclipse*/plugins/org.eclipse.core.resources_*",
        "findcp eclipse*/plugins/org.eclipse.core.jobs_*",
        "findcp eclipse*/plugins/org.eclipse.core.runtime_*",
        "findcp eclipse*/plugins/org.eclipse.osgi_*",
        "findcp eclipse*/plugins/org.eclipse.core.contenttype_*",
        "findcp eclipse*/plugins/org.eclipse.equinox.preferences_*", })
@LoadBy(launcher = JavaLauncher.class)
public class JdtBatchCLI extends BatchProcessCLI {

    static {
        Types.load(JdtBasicCLI.class);
    }

}
