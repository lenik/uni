package net.bodz.lapiota.ant.tasks;

import net.bodz.bas.cli.Option;
import net.bodz.bas.lang.script.ScriptClass;
import net.bodz.bas.log.ALog;
import net.bodz.lapiota.wrappers.BasicCLI;

public class HelloCLI extends BasicCLI {

    @Option
    private String[] welcomes;

    @Option
    private String   yourName = "Lucy";

    @Option
    private boolean  hot;

    @Override
    protected void _boot() throws Throwable {
        if (welcomes == null)
            welcomes = new String[] { "Hello" };
    }

    @Override
    protected void _main(String[] args) throws Throwable {
        L.m.P("good morning!");
        L.d.P("you are ", yourName);
        L.x.P("now in debug level");
        for (String welcome : welcomes) {
            String s = welcome + ", " + yourName + "!";
            if (hot)
                s = s.toUpperCase();
            System.out.println(s);
        }
        // System.err.println("go to hell!");
    }

    public static void main(String[] args) throws Throwable {
        HelloCLI app = new HelloCLI();
        ScriptClass<?> sclass = app.getScriptClass();
        ALog L = (ALog) sclass.get(app, "logout");
        System.out.println("Level=" + L.getLevel());
        app.run();

    }

}
