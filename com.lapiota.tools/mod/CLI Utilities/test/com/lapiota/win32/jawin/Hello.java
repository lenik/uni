package com.lapiota.win32.jawin;

import java.io.UnsupportedEncodingException;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.lang.util.Classpath;

import org.jawin.COMException;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;
import org.jawin.io.LittleEndianOutputStream;
import org.jawin.io.NakedByteStream;
import org.jawin.win32.Ole32;

import com.lapiota.win32.JawinConfig;

@BootInfo(userlibs = "%project", configs = JawinConfig.class)
public class Hello extends BasicCLI {

    static boolean HELLO_DUMP = false;

    @Override
    protected void doMain(String[] args) throws Exception {
        if (HELLO_DUMP)
            Classpath.dumpURLs(CharOuts.stderr);
        f3();
    }

    void errIconv(COMException e) {
        try {
            String s = e.getMessage();
            byte[] b = s.getBytes("l1"); //$NON-NLS-1$
            s = new String(b, "gb2312"); //$NON-NLS-1$
            System.err.println(s);
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
        e.printStackTrace();
    }

    void f1() throws Exception {
        FuncPtr msgbox = new FuncPtr("user32", "MessageBoxW"); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            msgbox.invoke_I(0, "hello", "world", 0, ReturnFlags.CHECK_FALSE); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (COMException e) {
            errIconv(e);
        }
    }

    void f2() throws Exception {
        FuncPtr msgBox = null;
        try {
            msgBox = new FuncPtr("USER32.DLL", "MessageBoxW"); //$NON-NLS-1$ //$NON-NLS-2$
            msgBox.invoke_I(0, "Hello From a DLL", "From Jawin", 0, //$NON-NLS-1$ //$NON-NLS-2$
                    ReturnFlags.CHECK_FALSE);
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

    void f3() throws Exception {
        FuncPtr msgBox = null;
        try {
            msgBox = new FuncPtr("USER32.DLL", "MessageBoxW"); //$NON-NLS-1$ //$NON-NLS-2$

            // create a NakedByteStream for the serialization of Java variables
            NakedByteStream nbs = new NakedByteStream();

            // wrap it in a LittleEndianOutputStream
            LittleEndianOutputStream leos = new LittleEndianOutputStream(nbs);

            // and then write the Java arguments
            leos.writeInt(0);
            leos.writeStringUnicode("Generic Hello From a DLL"); //$NON-NLS-1$
            leos.writeStringUnicode("From Jawin"); //$NON-NLS-1$
            leos.writeInt(0);

            // call the generic invoke, with the NakedByteStream
            // and parameters describing how to deserialize the
            // NakedByteStream byte-array on the native side
            msgBox.invoke("IGGI:I:", 16, nbs, null, ReturnFlags.CHECK_FALSE); //$NON-NLS-1$
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

    public static void main(String[] args) throws Exception {
        Ole32.CoInitialize();
        new Hello().run(args);
        Ole32.CoUninitialize();
    }

}
