package user.jawin;

import java.io.UnsupportedEncodingException;

import org.jawin.COMException;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;
import org.jawin.io.LittleEndianOutputStream;
import org.jawin.io.NakedByteStream;
import org.jawin.win32.Ole32;

import net.bodz.bas.c.java.net.URLClassLoaders;
import net.bodz.bas.c.loader.ClassLoaders;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.program.skel.BasicCLI;

public class Hello
        extends BasicCLI {

    static boolean HELLO_DUMP = false;

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        ClassLoader runtimeClassLoader = ClassLoaders.getRuntimeClassLoader();

        if (HELLO_DUMP)
            URLClassLoaders.dump(runtimeClassLoader, Stdio.cerr);

        f3();
    }

    void errIconv(COMException e) {
        try {
            String s = e.getMessage();
            byte[] b = s.getBytes("l1");
            s = new String(b, "gb2312");
            System.err.println(s);
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
        e.printStackTrace();
    }

    void f1()
            throws Exception {
        FuncPtr msgbox = new FuncPtr("user32", "MessageBoxW");
        try {
            msgbox.invoke_I(0, "hello", "world", 0, ReturnFlags.CHECK_FALSE);
        } catch (COMException e) {
            errIconv(e);
        }
    }

    void f2()
            throws Exception {
        FuncPtr msgBox = null;
        try {
            msgBox = new FuncPtr("USER32.DLL", "MessageBoxW");
            msgBox.invoke_I(0, "Hello From a DLL", "From Jawin", 0, ReturnFlags.CHECK_FALSE);
        } catch (COMException e) {
            errIconv(e);
        } finally {
            if (msgBox != null) {
                try {
                    msgBox.close();
                } catch (COMException e) {
                    errIconv(e);
                }
            }
        }
    }

    void f3()
            throws Exception {
        FuncPtr msgBox = null;
        try {
            msgBox = new FuncPtr("USER32.DLL", "MessageBoxW");

            // create a NakedByteStream for the serialization of Java variables
            NakedByteStream _out = new NakedByteStream();

            // wrap it in a LittleEndianOutputStream
            LittleEndianOutputStream leOut = new LittleEndianOutputStream(_out);

            // and then write the Java arguments
            leOut.writeInt(0);
            leOut.writeStringUnicode("Generic Hello From a DLL");
            leOut.writeStringUnicode("From Jawin");
            leOut.writeInt(0);

            // call the generic invoke, with the NakedByteStream
            // and parameters describing how to deserialize the
            // NakedByteStream byte-array on the native side
            msgBox.invoke("IGGI:I:", 16, _out, null, ReturnFlags.CHECK_FALSE);

            leOut.close();
        } catch (COMException e) {
            errIconv(e);
        } finally {
            if (msgBox != null) {
                try {
                    msgBox.close();
                } catch (COMException e) {
                    errIconv(e);
                }
            }
        }
    }

    public static void main(String[] args)
            throws Exception {
        Ole32.CoInitialize();
        new Hello().execute(args);
        Ole32.CoUninitialize();
    }

}
