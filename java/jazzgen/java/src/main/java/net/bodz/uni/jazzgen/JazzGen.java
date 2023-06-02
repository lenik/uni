package net.bodz.uni.jazzgen;

import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;

/**
 * A sample application.
 */
@ProgramName("jazzgen")
public class JazzGen
        extends BasicCLI {

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        System.out.println("Hello");
    }

    public static void main(String[] args)
            throws Exception {
        new JazzGen().execute(args);
    }

}