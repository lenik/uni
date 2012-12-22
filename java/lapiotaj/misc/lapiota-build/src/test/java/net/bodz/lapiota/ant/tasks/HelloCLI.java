package net.bodz.lapiota.ant.tasks;

import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;

public class HelloCLI
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(HelloCLI.class);

    /** @option */
    private String[] welcomes;

    /** @option */
    private String yourName = "Lucy";

    /** @option */
    private boolean hot;

    @Override
    protected void reconfigure()
            throws Exception {
        if (welcomes == null)
            welcomes = new String[] { "Hello" };
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        logger.mesg("good morning!");
        logger.info("you are ", yourName);
        logger.debug("now in debug level");
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
        app.run();
    }

}
