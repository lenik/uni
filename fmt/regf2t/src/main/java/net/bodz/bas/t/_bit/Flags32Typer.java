package net.bodz.bas.t._bit;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.bodz.bas.c.string.StringArray;
import net.bodz.bas.c.string.StringNum;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractCommonTypers;
import net.bodz.bas.typer.std.IFormatter;
import net.bodz.bas.typer.std.IParser;

public class Flags32Typer
        extends AbstractCommonTypers<Integer> {

    Map<Integer, String> bit2tok;
    Map<String, Integer> tok2bit;

    public Flags32Typer() {
        super(int.class);
        bit2tok = new TreeMap<>();
        tok2bit = new TreeMap<>();
    }

    public void declare(int bitmask, String token) {
        bit2tok.put(bitmask, token);
        tok2bit.put(token, bitmask);
    }

    @Override
    protected Object queryInt(int typerIndex) {
        switch (typerIndex) {
        case IParser.typerIndex:
            return this;
        case IFormatter.typerIndex:
            return this;
        }
        return null;
    }

    @Override
    public String format(Integer object, IOptions options) {
        int value = object;
        int remaining = value;
        StringBuilder sb = new StringBuilder(0x100);
        int count = 0;

        for (Entry<Integer, String> entry : bit2tok.entrySet()) {
            int bitmask = entry.getKey();
            if ((value & bitmask) == bitmask) {
                if (count++ != 0)
                    sb.append(' ');
                sb.append(entry.getValue());
                remaining &= ~bitmask;
            }
        }

        if (remaining != 0) {
            StringBuilder hex = new StringBuilder(16);
            hex.append("0x");
            hex.append(Integer.toHexString(remaining));
            if (count != 0)
                hex.append(' ');
            sb.insert(0, hex);
        }
        return sb.toString();
    }

    @Override
    public Integer parse(String text, IOptions options)
            throws ParseException {
        int value = 0;
        for (String word : StringArray.extractWords(text, Integer.MAX_VALUE))
            if (word.startsWith("0x")) {
                String hex = word.substring(2);
                int num = StringNum.parseInt(hex, 16);
                value |= num;
            } else {
                Integer bitmask = tok2bit.get(word);
                if (bitmask == null)
                    throw new ParseException("Bad flag name: " + word);
                value |= bitmask;
            }
        return value;
    }

}
