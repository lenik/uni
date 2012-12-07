package net.bodz.uni.echo.client;

import net.bodz.mda.xjdoc.model1.ArtifactObject;

public abstract class AbstractEchoClientCommand
        extends ArtifactObject
        implements IEchoClientCommand {

    @Override
    public int getRequiredArgumentCount() {
        return 0;
    }

}
