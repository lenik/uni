package net.bodz.uni.echo.client;

import net.bodz.mda.xjdoc.model.javadoc.XjdocObject;

public abstract class AbstractEchoClientCommand
        extends XjdocObject
        implements IEchoClientCommand {

    @Override
    public int getRequiredArgumentCount() {
        return 0;
    }

}
