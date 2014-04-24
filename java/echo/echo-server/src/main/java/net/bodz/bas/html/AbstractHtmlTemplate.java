package net.bodz.bas.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.http.ctx.CurrentHttpService;

import com.googlecode.jatl.Html;

/**
 * Features:
 *
 * <ul>
 * <li>Wrapped Html but exports the writer, so toString() would work.
 * <li>Add more html sugar constructors.
 * </ul>
 */
public abstract class AbstractHtmlTemplate
        extends Html {

    protected final Writer out;
    boolean instantiated;

    public AbstractHtmlTemplate() {
        this(new StringWriter());
    }

    public AbstractHtmlTemplate(Writer writer) {
        super(writer);
        this.out = writer;
    }

    protected HttpServletRequest getRequest() {
        return CurrentHttpService.getRequest();
    }

    public String make() {
        try {
            instantiateOnce();
        } catch (IOException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
        return toString();
    }

    public synchronized void instantiateOnce()
            throws IOException {
        if (!instantiated) {
            instantiate();
            instantiated = true;
        }
    }

    protected abstract void instantiate()
            throws IOException;

    @Override
    public String toString() {
        if (!instantiated)
            return "(not instantiated yet)";
        else
            return out.toString();
    }

}
