package net.bodz.lily.tool.daogen.dir.dao.test;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.bodz.bas.c.java.lang.StringTypers;
import net.bodz.bas.c.java.math.BigDecimalTypers;
import net.bodz.bas.c.java.math.BigIntegerTypers;

public class MySampleFactory
        extends SampleFactory {

    int maxStringLen = 1000;

    public MySampleFactory(long... seeds) {
        super(seeds);
    }

    {
        define(String.class, options -> {
            int maxLen = options.getInt(StringTypers.OPTION_MAX_LENGTH, maxStringLen);
            if (maxLen <= 0)
                throw new IllegalArgumentException("invalid varchar length: " + maxLen);
            if (maxLen > maxStringLen)
                maxLen = maxStringLen;
            int wordMaxLen = 10;
            String sample = enGen.makeText(maxLen, wordMaxLen);
            return sample;
        });

        setOptions(String.class, (options, column) -> {
            options.addOption(StringTypers.OPTION_MAX_LENGTH, column.getPrecision());
        });

        setOptions(BigInteger.class, (options, column) -> {
            options.addOption(BigIntegerTypers.OPTION_MAX_PRECISION, column.getPrecision());
        });

        setOptions(BigDecimal.class, (options, column) -> {
            options.addOption(BigDecimalTypers.OPTION_MAX_PRECISION, column.getPrecision());
            options.addOption(BigDecimalTypers.OPTION_MAX_SCALE, column.getScale());
        });
    }

}
