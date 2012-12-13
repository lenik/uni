package net.bodz.bas.html;

import java.util.Map;

import net.bodz.bas.t.order.IPriority;

public interface IPageGenerator
        extends IPriority {

    String generate(Map<String, ?> args);

}
