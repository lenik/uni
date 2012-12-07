package net.bodz.bas.html;

import javax.servlet.http.HttpServletRequest;

import com.googlecode.jatl.Html;

public interface IFormChild {

    void render(Html out, HttpServletRequest req);

}
