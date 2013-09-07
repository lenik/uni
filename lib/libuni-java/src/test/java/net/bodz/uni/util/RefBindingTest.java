package net.bodz.uni.util;

import groovy.lang.GroovyShell;

import org.junit.Test;

import net.bodz.uni.util.RefBinding;

public class RefBindingTest {

    String name = "tom";

    @SuppressWarnings("unused")
    private int age = 13;

    String _prop;

    public String getProp() {
        return _prop;
    }

    public void setProp(String newval) {
        _prop = newval;
    }

    @Test
    public void test1()
            throws Throwable {
        RefBinding b = new RefBinding();
        b.bindScriptFields(this, true);
        GroovyShell shell = new GroovyShell(b);
        shell.evaluate("println('hello')");
        shell.evaluate("println('name = ' + name)");
        shell.evaluate("name = 'jimmy'");
        System.out.println("name' = " + name);
        shell.evaluate("println('prop = ' + prop)");
        shell.evaluate("prop = 'changed by script'");
        System.out.println("prop' = " + getProp());
        // shell.evaluate("println('hello')");
        // shell.evaluate("println('hello')");
        // shell.evaluate("println('hello')");
    }

    public static void main(String[] args)
            throws Throwable {
        RefBindingTest test = new RefBindingTest();
        test.test1();
    }

}
