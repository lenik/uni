package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MountableResourceProvider
        extends AbstractResourceProvider {

    private MountPoint root;
    private boolean unionAuto = true;
    private boolean unionSorted = false;

    public MountableResourceProvider() {
        root = new MountPoint(null);
    }

    public boolean isUnionAuto() {
        return unionAuto;
    }

    public void setUnionAuto(boolean unionAuto) {
        this.unionAuto = unionAuto;
    }

    public boolean isUnionSorted() {
        return unionSorted;
    }

    public void setUnionSorted(boolean unionSorted) {
        this.unionSorted = unionSorted;
    }

    public MountPoint getRoot() {
        return root;
    }

    public synchronized void mount(String path, IResourceProvider resourceProvider) {
        MountPoint mountPoint = root.resolve(path);

        IResourceProvider existing = mountPoint.getResourceProvider();

        if (existing != null && unionAuto) {
            UnionResourceProvider union;
            if (existing instanceof UnionResourceProvider)
                union = (UnionResourceProvider) existing;
            else {
                union = new UnionResourceProvider(unionSorted);
                union.add(existing);
            }
            union.add(resourceProvider);
            resourceProvider = union;
        }

        mountPoint.setResourceProvider(resourceProvider);
    }

    @Override
    public URL getResource(String path)
            throws IOException {

        MountPoint node = root;

        while (path != null && node != null) {
            IResourceProvider resourceProvider = node.getResourceProvider();
            if (resourceProvider != null) {
                URL resource = resourceProvider.getResource(path);
                if (resource != null)
                    return resource;
            }

            int slash = path.indexOf('/');
            String next;
            if (slash == -1) {
                next = path;
                path = null;
            } else {
                next = path.substring(0, slash);
                path = path.substring(slash + 1);
            }

            node = node.getChild(next);
        }
        return null;
    }

    @Override
    public void findResources(List<URL> resources, String path)
            throws IOException {

        MountPoint node = root;

        while (path != null && node != null) {
            IResourceProvider resourceProvider = node.getResourceProvider();
            if (resourceProvider != null)
                resourceProvider.findResources(resources, path);

            int slash = path.indexOf('/');
            String next;
            if (slash == -1) {
                next = path;
                path = null;
            } else {
                next = path.substring(0, slash);
                path = path.substring(slash + 1);
            }

            node = node.getChild(next);
        }
    }

}
