package net.bodz.uni.echo._default;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.util.resource.Resource;

import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.uni.echo.resource.IResourceProvider;

public class MultiLayer {

    static final Logger logger = LoggerFactory.getLogger(MultiLayer.class);

    boolean checkAbsolute;
    IResourceProvider resourceProvider;
    IResourceResolver fallback;
    IResourceConverter converter;

    MultiLayer(IResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    MultiLayer(IResourceProvider resourceProvider, IResourceResolver fallback, IResourceConverter converter) {
        this.resourceProvider = resourceProvider;
        this.fallback = fallback;
        this.converter = converter;
    }

    public static MultiLayer from(IResourceProvider resourceProvider) {
        return new MultiLayer(resourceProvider);
    }

    public static MultiLayer from(IResourceProvider resourceProvider, IResourceResolver fallback, IResourceConverter converter) {
        return new MultiLayer(resourceProvider, fallback, converter);
    }

    public MultiLayer fallback(IResourceResolver fallback) {
        this.fallback = fallback;
        return this;
    }

    public MultiLayer convert(IResourceConverter converter) {
        this.converter = converter;
        return this;
    }

    public URL resolveURL(String path) {
        if (checkAbsolute)
            if (path == null || !path.startsWith("/"))
                throw new IllegalArgumentException("not absolute: " + path);

        while (path.startsWith("/"))
            path = path.substring(1);

        URL url = resourceProvider.getResource(path);
        return url;
    }

    public Resource getResource(String path) {
        URL url = resolveURL(path);
        if (url == null)
            if (fallback == null)
                return null;
            else
                try {
                    return fallback.resolve(path);
                } catch (MalformedURLException e) {
                    logger.error(e);
                    return null;
                }
        try {
            return converter.convert(url);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
    }

}
