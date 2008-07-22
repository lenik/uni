package net.bodz.lapiota.loader;

public class LibInResourceLoader extends ClassLoader {

    LibInstaller installer = new LibInstaller();

    // public LibInResourceLoader(URL[] urls, ClassLoader parent,
    // URLStreamHandlerFactory factory) {
    // super(urls, parent, factory);
    // init();
    // }
    //
    // public LibInResourceLoader(URL[] urls, ClassLoader parent) {
    // super(urls, parent);
    // init();
    // }
    //
    // public LibInResourceLoader(URL[] urls) {
    // super(urls);
    // init();
    // }

    public LibInResourceLoader() {
        super();
    }

    public LibInResourceLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected String findLibrary(String libname) {
        return installer.findLibrary(this, libname);
    }

}
