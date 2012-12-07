package net.bodz.uni.echo.resource;

import java.net.URL;
import java.util.List;

import net.bodz.bas.util.order.IPriority;

public interface IResourceProvider
        extends IPriority {

    /**
     * Get the common prefixes to the path.
     *
     * @return Non-null root paths, without the leading slash and the trailing slash.
     */
    List<String> getStartPoints();

    /**
     * Get a system-wide accessible resource.
     *
     * @param path
     *            Non-null path string, without the leading "/".
     */
    URL getResource(String path);

}
