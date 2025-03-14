package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bodz.bas.c.java.io.FilePath;
import net.bodz.bas.err.DuplicatedKeyException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.meta.decl.NotNull;

public class DerivedResourceProvider
        extends DecoratedResourceProvider
        implements IResourceProvider {

    private static final long serialVersionUID = 1L;

    private DerivedResourceOverlapMode overlapMode = DerivedResourceOverlapMode.derivedOnly;
    private final Map<String, String> extensionMap;

    public DerivedResourceProvider(IResourceProvider _orig) {
        this(_orig, new HashMap<String, String>());
    }

    public DerivedResourceProvider(IResourceProvider _orig, Map<String, String> extensionMap) {
        super(_orig);
        this.extensionMap = extensionMap;
    }

    public DerivedResourceOverlapMode getOverlapMode() {
        return overlapMode;
    }

    public void setOverlapMode(DerivedResourceOverlapMode overlapMode) {
        if (overlapMode == null)
            throw new NullPointerException("overlapMode");
        this.overlapMode = overlapMode;
    }

    public Map<String, String> getExtensionMap() {
        return extensionMap;
    }

    /**
     * Add a derived extension mapping.
     *
     * @param derivedExtension
     *            The derived extension name include the dot(.). Can't be <code>null</code>.
     * @param srcExtension
     *            The original extension name include the dot(.). Can't be <code>null</code>.
     */
    public void addDerivedExtension(String derivedExtension, String srcExtension) {
        if (derivedExtension == null)
            throw new NullPointerException("derivedExtension");

        String oldSrc = extensionMap.get(derivedExtension);
        if (oldSrc != null)
            throw new DuplicatedKeyException(derivedExtension, oldSrc);

        extensionMap.put(derivedExtension, srcExtension);
    }

    /**
     * Remove a derived extensnion mapping.
     *
     * @param derivedExtension
     *            The derived extension name include the dot(.). Can't be <code>null</code>.
     */
    public void removeDerivedExtension(String derivedExtension) {
        extensionMap.remove(derivedExtension);
    }

    @Override
    public URL getResource(@NotNull String path) {
        String dotExtension = FilePath.dotExtensionOfPath(path);
        if (dotExtension.isEmpty())
            return super.getResource(path);

        String srcExtension = extensionMap.get(dotExtension);
        if (srcExtension == null)
            return super.getResource(path);

        if (overlapMode == DerivedResourceOverlapMode.originalFirst) {
            URL original = super.getResource(path);
            if (original != null)
                return original;
        }

        String srcPath = path.substring(0, path.length() - dotExtension.length()) + srcExtension;
        URL srcResource = super.getResource(srcPath);
        if (srcResource == null) {
            if (overlapMode == DerivedResourceOverlapMode.derivedFirst)
                return super.getResource(path);
            else
                return null;
        }

        String srcUrl = srcResource.toString();
        assert srcUrl.endsWith(srcExtension);

        String srcName = FilePath.baseName(srcUrl);
        String derivedName = srcName.substring(0, srcName.length() - srcExtension.length()) + dotExtension;

        URI context;
        try {
            context = srcResource.toURI();
        } catch (URISyntaxException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
        URI derivedResource = context.resolve(derivedName);
        try {
            return derivedResource.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("malformed: " + derivedResource, e);
        }
    }

    @Override
    public void findResources(@NotNull List<URL> resources, @NotNull String path)
            throws IOException {
        String extension = FilePath.dotExtensionOfPath(path);
        String srcExtension = extensionMap.get(extension);
        if (extension.isEmpty() || srcExtension == null) {
            super.findResources(resources, path);
            return;
        }

        if (overlapMode == DerivedResourceOverlapMode.originalFirst)
            super.findResources(resources, path);

        String srcPath = path.substring(0, path.length() - extension.length()) + srcExtension;
        for (URL srcResource : super.getResources(srcPath)) {

            String srcUrl = srcResource.toString();
            assert srcUrl.endsWith(srcExtension);

            String derivedUrl = srcUrl.substring(0, srcUrl.length() - srcExtension.length()) + extension;
            URL derivedResource;
            try {
                derivedResource = URI.create(derivedUrl).toURL();
            } catch (MalformedURLException e) {
                throw new UnexpectedException("URL subst should work for: " + derivedUrl, e);
            }

            resources.add(derivedResource);
        }

        if (overlapMode == DerivedResourceOverlapMode.derivedFirst)
            super.findResources(resources, path);
    }

    @Override
    public String toString() {
        return "derived:" + getWrapped().toString();
    }

}
