package com.lapiota.util;

import groovy.lang.GroovyShell;

import org.junit.Test;

import com.lapiota.util.RefBinding;

public class RefBindingTest {

    String      name = "tom"; //$NON-NLS-1$

    @SuppressWarnings("unused")
    private int age  = 13;

    String      _prop;

    public String getProp() {
        return _prop;
    }

    public void setProp(String newval) {
        _prop = newval;
    }

    @Test
    public void test1() throws Throwable {
        RefBinding b = new RefBinding();
        b.bindScriptFields(this, true);
        GroovyShell shell = new GroovyShell(b);
        shell.evaluate("println('hello')"); //$NON-NLS-1$
        shell.evaluate("println('name = ' + name)"); //$NON-NLS-1$
        shell.evaluate("name = 'jimmy'"); //$NON-NLS-1$
        System.out.println("name' = " + name); //$NON-NLS-1$
        shell.evaluate("println('prop = ' + prop)"); //$NON-NLS-1$
        shell.evaluate("prop = 'changed by script'"); //$NON-NLS-1$
        System.out.println("prop' = " + getProp()); //$NON-NLS-1$
        // shell.evaluate("println('hello')");
        // shell.evaluate("println('hello')");
        // shell.evaluate("println('hello')");
    }

    public static void main(String[] args) throws Throwable {
        RefBindingTest test = new RefBindingTest();
        test.test1();
    }

}
