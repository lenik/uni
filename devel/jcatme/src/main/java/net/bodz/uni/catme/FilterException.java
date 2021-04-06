package net.bodz.uni.catme;

import net.bodz.bas.err.ParseException;

public class FilterException
        extends ParseException {

    private static final long serialVersionUID = 1L;

    public FilterException() {
        super();
    }

    public FilterException(String name, String message, Throwable cause) {
        super(name, message, cause);
    }

    public FilterException(String name, String message) {
        super(name, message);
    }

    public FilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterException(String message) {
        super(message);
    }

    public FilterException(Throwable cause) {
        super(cause);
    }

}
