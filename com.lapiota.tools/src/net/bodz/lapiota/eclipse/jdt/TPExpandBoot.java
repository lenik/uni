package net.bodz.lapiota.eclipse.jdt;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli._RunInfo;
import net.bodz.lapiota.ant.tasks.ProgramName;
import net.bodz.lapiota.util.Lapiota;

@ProgramName("jgremove")
@RunInfo(init = { Lapiota.class },

_load = { "findcp|eclipse*/plugins/org.eclipse.jdt.core_*",
        "findcp|eclipse*/plugins/org.eclipse.text_*", })
public class TPExpandBoot extends BasicCLI {

    public static void main(String[] args) throws Throwable {
        _RunInfo.parse(TPExpandBoot.class).loadBoot();
        new TPExpand().climain(args);
    }

}
