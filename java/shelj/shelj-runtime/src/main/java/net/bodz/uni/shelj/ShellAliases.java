package net.bodz.uni.shelj;

import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.c.java.util.TreeTextMap;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.i18n.nls.II18nCapable.nls;

public class ShellAliases
        extends TreeTextMap<String[]> {

    private static final long serialVersionUID = 1L;

    static int MAX_NEST = 32;

    TextMap<String[]> map;

    public String[] expandAliases(String alias) {
        List<String> rev = null;
        String[] exp;
        int nest = 0;
        while ((exp = map.get(alias)) != null) {
            nest++;
            if (nest > MAX_NEST)
                throw new IllegalUsageException(nls.tr("alias nest too much: ") + alias);
            assert exp.length != 0;
            alias = exp[0];
            if (exp.length == 1)
                continue;
            if (rev == null)
                rev = new ArrayList<String>();
            for (int i = exp.length - 1; i >= 1; i--)
                rev.add(exp[i]);
        }
        if (rev == null)
            return new String[] { alias };
        rev.add(alias);
        // Collections.reverse(rev);
        int n = rev.size();
        exp = new String[n];
        for (int i = 0; i < n; i++)
            exp[i] = rev.get(n - i - 1);
        return exp;
    }

}
