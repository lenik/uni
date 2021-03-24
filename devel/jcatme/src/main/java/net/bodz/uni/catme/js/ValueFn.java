package net.bodz.uni.catme.js;

import org.graalvm.polyglot.Value;

public class ValueFn {

    public static Object convert(Value value) {
        if (value == null)
            return null;
        if (value.isHostObject())
            return value.asHostObject();
        return value;
    }

}
