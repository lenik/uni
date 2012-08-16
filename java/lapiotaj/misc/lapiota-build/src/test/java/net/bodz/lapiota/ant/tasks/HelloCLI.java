package net.bodz.lapiota.ant.tasks;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.log.Logger;
import net.bodz.bas.potato.traits.IType;

public class HelloCLI
        extends BasicCLI {

    /** @option */
    private String[] welcomes;

    /** @option */
    private String yourName = "Lucy";

    /** @option */
    private boolean hot;

    @Override
    protected void _boot()
            throws Exception {
        if (welcomes == null)
            welcomes = new String[] { "Hello" };
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        L.mesg("good morning!");
        L.info("you are ", yourName);
        L.debug("now in debug level");
        for (String welcome : welcomes) {
            String s = welcome + ", " + yourName + "!";
            if (hot)
                s = s.toUpperCase();
            System.out.println(s);
        }
        // System.err.println("go to hell!");
    }

    public static void main(String[] args)
            throws Exception {
        HelloCLI app = new HelloCLI();
        IType type = app.getScriptClass();
        Logger logger = app.L;
        System.out.println("Max Priority = " + logger.getMaxPriority());
        app.run();
    }

}
