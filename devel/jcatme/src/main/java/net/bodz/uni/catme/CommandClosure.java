package net.bodz.uni.catme;

import java.io.IOException;

import net.bodz.bas.err.ParseException;

public interface CommandClosure {

    void prepare();

    void run()
            throws IOException, ParseException;

}
