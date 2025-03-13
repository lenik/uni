package net.bodz.uni.echo._default;

import java.net.MalformedURLException;

import org.eclipse.jetty.util.resource.Resource;

@FunctionalInterface
public interface IResourceResolver {

    Resource resolve(String path)
            throws MalformedURLException;

}
