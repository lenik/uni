package net.bodz.lapiota.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

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

    @Override
    protected String findLibrary(String libname) {
        return installer.findLibrary(this, libname);
    }

}
