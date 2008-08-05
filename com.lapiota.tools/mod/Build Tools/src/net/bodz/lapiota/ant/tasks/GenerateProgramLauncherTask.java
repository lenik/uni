package net.bodz.lapiota.ant.tasks;

import java.io.File;

import net.bodz.bas.cli.CLIConfig;
import net.bodz.lapiota.javatools.GenerateProgramLauncher;

import org.apache.tools.ant.BuildException;

public class GenerateProgramLauncherTask extends CLITask {

    public GenerateProgramLauncherTask() {
        super(new GenerateProgramLauncher());
        addArguments("-rq", "--");

        /*
         * load lapiota multiple times will cause problems. The same class (e.g.
         * ProgramName) will have different hashCodes loaded by different
         * loaders. And then, we can't get any annotation (ProgramName, RunInfo,
         * etc.).
         *
         * Set cli.lib_loaded to suppress load bodz_lapiota by BasicCLI, so all
         * annotation types are loaded only by AntClassLoader.
         */
        System.setProperty(CLIConfig.PROPERTY_LIB_LOADED, "1");
    }

    private File srcdir;

    public File getSrcdir() {
        return srcdir;
    }

    public void setSrcdir(File srcdir) {
        this.srcdir = srcdir;
    }

    public File getOutdir() {
        return (File) get("outputDirectory");
    }

    public void setOutdir(File outdir) {
        set("outputDirectory", outdir);
    }

    @Override
    public void execute() throws BuildException {
        if (srcdir != null)
            addArguments(srcdir.getPath());
        super.execute();
    }

}
