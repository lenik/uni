package net.bodz.lily.tool.javagen;

import java.io.IOException;

import net.bodz.bas.err.FormatException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.json.IJsonForm;
import net.bodz.bas.fmt.json.IJsonOut;
import net.bodz.bas.fmt.json.JsonFormOptions;
import net.bodz.bas.fmt.rst.IRstForm;
import net.bodz.bas.fmt.rst.IRstHandler;
import net.bodz.bas.fmt.rst.IRstOutput;
import net.bodz.bas.json.JsonObject;

public class Rst2Json
        implements
            IRstForm,
            IJsonForm {

    @Override
    public void jsonIn(JsonObject o, JsonFormOptions opts)
            throws ParseException {
    }

    @Override
    public void jsonOut(IJsonOut out, JsonFormOptions opts)
            throws IOException, FormatException {
    }

    @Override
    public void writeObject(IRstOutput out)
            throws IOException, FormatException {
        IRstForm.super.writeObject(out);
    }

    @Override
    public IRstHandler getElementHandler() {
        return IRstForm.super.getElementHandler();
    }

}
