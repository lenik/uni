package net.bodz.uni.echo.resource;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResourceProvider
        implements IResourceProvider {

    List<String> startPoints;

    public AbstractResourceProvider(String... startPoints) {
        this.startPoints = new ArrayList<String>();

        for (String startPoint : startPoints) {
            while (startPoint.startsWith("/"))
                startPoint = startPoint.substring(1);
            while (startPoint.endsWith("/"))
                startPoint = startPoint.substring(0, startPoint.length() - 1);
            this.startPoints.add(startPoint);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<String> getStartPoints() {
        return startPoints;
    }

}
