package net.bodz.bas.html;

import jakarta.servlet.http.HttpServletRequest;

import com.googlecode.jatl.Html;

public interface IFormChild {

    void render(Html out, HttpServletRequest req);

}
