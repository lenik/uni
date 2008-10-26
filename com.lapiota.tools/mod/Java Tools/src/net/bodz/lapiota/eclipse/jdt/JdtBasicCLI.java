package net.bodz.lapiota.eclipse.jdt;

import net.bodz.bas.cli.CLIConfig;
import net.bodz.bas.cli.a.RunInfo;
import net.bodz.lapiota.a.LoadBy;
import net.bodz.lapiota.wrappers.BasicCLI;
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
public class JdtBasicCLI extends BasicCLI {

    static void setAliases() {
        CLIConfig.conds.setAlias("AST", Object.class.getName());
        CLIConfig.conds.setAlias("TextEdit", Object.class.getName());
        // CLIConfig.conds.setAlias("AST", "org.eclipse.jdt.core.dom.AST");
        // CLIConfig.conds.setAlias("TextEdit",
        // "org.eclipse.text.edits.TextEdit");
    }

    static {
        setAliases();
    }

}
