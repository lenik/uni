package net.bodz.bas.html;

import com.googlecode.jatl.Html;

import jakarta.servlet.http.HttpServletRequest;

public interface IFormChild {

    void render(Html out, HttpServletRequest req);

}
