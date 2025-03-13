package net.bodz.uni.echo._default;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.util.resource.Resource;

@FunctionalInterface
public interface IResourceConverter {

    Resource convert(URL url)
            throws IOException;

}
