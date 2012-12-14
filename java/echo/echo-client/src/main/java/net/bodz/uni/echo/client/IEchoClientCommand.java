package net.bodz.uni.echo.client;

import net.bodz.bas.i18n.dom1.IElement;
import net.bodz.bas.meta.codegen.IndexedType;

@IndexedType
public interface IEchoClientCommand
        extends IElement {

    int getRequiredArgumentCount();

    void execute(String... args)
            throws Exception;

}
