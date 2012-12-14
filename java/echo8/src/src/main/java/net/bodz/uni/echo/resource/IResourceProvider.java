package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.bodz.bas.t.order.IPriority;

public interface IResourceProvider
        extends IPriority {

    /**
     * Get a system-wide accessible resource.
     *
     * @param path
     *            Non-null path string, without the leading "/".
     * @return <code>null</code> if the resource isn't existed.
     */
    URL getResource(String path)
            throws IOException;

    /**
     * Get resources with same path.
     *
     * @param path
     *            Non-null path string, without the leading "/".
     * @return List of matched resources.
     */
    List<URL> getResources(String path)
            throws IOException;

    /**
     * Get resources with same path.
     *
     * @param path
     *            Non-null path string, without the leading "/".
     * @return List of matched resources.
     */
    void findResources(List<URL> resources, String path)
            throws IOException;

}
