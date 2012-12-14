package net.bodz.uni.echo.resource;

import net.bodz.bas.err.CreateException;
import net.bodz.bas.t.tree.AbstractMapTreeNode;

public final class MountPoint
        extends AbstractMapTreeNode<MountPoint> {

    private static final long serialVersionUID = 1L;

    private IResourceProvider resourceProvider;

    public MountPoint() {
        this.resourceProvider = null;
    }

    public MountPoint(IResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public IResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    public void setResourceProvider(IResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public boolean isMounted() {
        return resourceProvider != null;
    }

    @Override
    protected MountPoint newChild()
            throws CreateException {
        return new MountPoint();
    }

}
