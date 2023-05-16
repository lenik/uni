package net.bodz.lily.tool.javagen.config;

import org.junit.Test;

import net.bodz.bas.fmt.json.JsonFn;
import net.bodz.bas.fmt.rst.RstFn;

public class KeyColumnSettingsTest {

    static String rstText = "key-columns {\n" //
            + "    format _dm {\n" //
            + "        pattern: (.*)_(dm)\n" //
            + "        alias: $1\n" //
            + "        component: $2\n" //
            + "    }\n" //
            + "}\n";

    @Test
    public void test() {
    }

    public static void main(String[] args)
            throws Throwable {
        KeyColumnSettings settings = new KeyColumnSettings();
        RstFn.loadFromRst(settings, rstText);
        String dump = RstFn.toString(settings);
        System.out.println(dump);

        KeyColumnNameInfo info = settings.parseColumnByAnyFormat("foo_bar_dm");
        if (info != null)
            System.out.println(JsonFn.toJson(info));
    }

}
