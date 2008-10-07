package net.bodz.lapiota.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

import net.bodz.bas.types.util.Empty;

public class SWTClassLoader extends URLClassLoader {

    private LibInstaller installer = new LibInstaller();

    public SWTClassLoader(URL[] urls, ClassLoader parent,
            URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public SWTClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public SWTClassLoader(URL[] urls) {
        super(urls);
    }

    public SWTClassLoader() {
        super(Empty.URLs);
    }

    @Override
    protected String findLibrary(String libname) {
        return installer.findLibrary(this, libname);
    }

}
