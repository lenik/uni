package net.bodz.lapiota.ant.tasks;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.lang.script.ScriptClass;
import net.bodz.bas.util.LogTerm;

public class HelloCLI extends BasicCLI {

    @Option
    private String[] welcomes;

    @Option
    private String   yourName = "Lucy"; //$NON-NLS-1$

    @Option
    private boolean  hot;

    @Override
    protected void _boot() throws Exception {
        if (welcomes == null)
            welcomes = new String[] { "Hello" }; //$NON-NLS-1$
    }

    @Override
    protected void doMain(String[] args) throws Exception {
        L.mesg("good morning!"); //$NON-NLS-1$
        L.detail("you are ", yourName); //$NON-NLS-1$
        L.debug("now in debug level"); //$NON-NLS-1$
        for (String welcome : welcomes) {
            String s = welcome + ", " + yourName + "!"; //$NON-NLS-1$ //$NON-NLS-2$
            if (hot)
                s = s.toUpperCase();
            System.out.println(s);
        }
        // System.err.println("go to hell!");
    }

    public static void main(String[] args) throws Exception {
        HelloCLI app = new HelloCLI();
        ScriptClass<?> sclass = app.getScriptClass();
        LogTerm logger = (LogTerm) sclass.get(app, "logger"); //$NON-NLS-1$
        System.out.println("Level=" + logger.getLevel()); //$NON-NLS-1$
        app.run();
    }

}
