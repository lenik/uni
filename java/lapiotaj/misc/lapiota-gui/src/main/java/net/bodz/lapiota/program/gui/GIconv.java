package net.bodz.lapiota.program.gui;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.SortedMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import net.bodz.bas.data.codec.builtin.HexCodec;
import net.bodz.bas.err.DecodeException;
import net.bodz.bas.gui.err.GUIException;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.swt.c.resources.SWTResources;
import net.bodz.swt.program.BasicGUI;

/**
 * GUI iconv utility
 *
 * @style width: 640; height: 480
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 1, 0 })
public class GIconv
        extends BasicGUI {

    private Button encode1Button;
    private Button decode2Button;
    private Button decode1Button;
    private Button encode2Button;
    private Button E1Hold;
    private Button D2Hold;
    private Button D1Hold;
    private Button E2Hold;
    private List charset1;
    private List charset2;
    private Text text1Text;
    private Text text2Text;
    private Text binText;

    private byte[] binary;

    @Override
    protected void createInitialView(Composite comp)
            throws GUIException, SWTException {
        comp.setLayout(new FillLayout());
        comp = new Composite(comp, SWT.NONE);
        comp.setLayout(new FormLayout());

        text1Text = new Text(comp, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
        text1Text.setText("text1");
        final FormData fd_text1Text = new FormData();
        fd_text1Text.bottom = new FormAttachment(30, 0);
        fd_text1Text.right = new FormAttachment(100, -5);
        fd_text1Text.top = new FormAttachment(0, 5);
        fd_text1Text.left = new FormAttachment(0, 5);
        text1Text.setLayoutData(fd_text1Text);

        text2Text = new Text(comp, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        text2Text.setText("text2");
        final FormData fd_text2Text = new FormData();
        fd_text2Text.top = new FormAttachment(70, 0);
        fd_text2Text.bottom = new FormAttachment(100, -5);
        fd_text2Text.left = new FormAttachment(0, 5);
        fd_text2Text.right = new FormAttachment(100, -5);
        text2Text.setLayoutData(fd_text2Text);

        charset1 = new List(comp, SWT.V_SCROLL | SWT.BORDER);
        final FormData fd_charset1 = new FormData();
        fd_charset1.left = new FormAttachment(0, 5);
        charset1.setLayoutData(fd_charset1);

        encode1Button = new Button(comp, SWT.NONE);
        fd_charset1.top = new FormAttachment(encode1Button, 5, SWT.BOTTOM);
        final FormData fd_encode1Button = new FormData();
        fd_encode1Button.right = new FormAttachment(charset1, 0, SWT.RIGHT);
        fd_encode1Button.top = new FormAttachment(text1Text, 5, SWT.BOTTOM);
        encode1Button.setLayoutData(fd_encode1Button);
        encode1Button.setText("&Encode↘");

        E1Hold = new Button(comp, SWT.TOGGLE);
        E1Hold.setImage(SWTResources.getImageRes("/icons/obj16/link_obj.gif"));
        final FormData fd_e1Hold = new FormData();
        fd_e1Hold.right = new FormAttachment(encode1Button, -5, SWT.LEFT);
        fd_e1Hold.top = new FormAttachment(text1Text, 5, SWT.BOTTOM);
        E1Hold.setLayoutData(fd_e1Hold);
        encode2Button = new Button(comp, SWT.NONE);
        fd_charset1.bottom = new FormAttachment(encode2Button, -5, SWT.TOP);
        final FormData fd_encode2Button = new FormData();
        fd_encode2Button.bottom = new FormAttachment(text2Text, -5, SWT.TOP);
        fd_encode2Button.right = new FormAttachment(charset1, 0, SWT.RIGHT);
        encode2Button.setLayoutData(fd_encode2Button);
        encode2Button.setText("&Encode↗");
        E2Hold = new Button(comp, SWT.TOGGLE);
        E2Hold.setImage(SWTResources.getImageRes("/icons/obj16/link_obj.gif"));
        final FormData fd_e2Hold = new FormData();
        fd_e2Hold.right = new FormAttachment(encode2Button, -5, SWT.LEFT);
        fd_e2Hold.bottom = new FormAttachment(text2Text, -5, SWT.TOP);
        E2Hold.setLayoutData(fd_e2Hold);

        charset2 = new List(comp, SWT.V_SCROLL | SWT.BORDER);
        final FormData fd_charset2 = new FormData();
        fd_charset2.right = new FormAttachment(100, -5);
        charset2.setLayoutData(fd_charset2);

        final RowData rd_decode1Button = new RowData();
        rd_decode1Button.height = 24;
        rd_decode1Button.width = 65;

        decode1Button = new Button(comp, SWT.NONE);
        fd_charset2.top = new FormAttachment(decode1Button, 5, SWT.BOTTOM);
        final FormData fd_decode1Button = new FormData();
        fd_decode1Button.top = new FormAttachment(text1Text, 5, SWT.BOTTOM);
        fd_decode1Button.left = new FormAttachment(charset2, 0, SWT.LEFT);
        decode1Button.setLayoutData(fd_decode1Button);
        decode1Button.setText("↗Decode");

        D1Hold = new Button(comp, SWT.TOGGLE);
        FormData fd_d1Hold;
        fd_d1Hold = new FormData();
        fd_d1Hold.top = new FormAttachment(text1Text, 5, SWT.BOTTOM);
        fd_d1Hold.left = new FormAttachment(decode1Button, 5, SWT.RIGHT);
        D1Hold.setLayoutData(fd_d1Hold);
        D1Hold.setImage(SWTResources.getImageRes("/icons/obj16/link_obj.gif"));
        decode2Button = new Button(comp, SWT.NONE);
        fd_charset2.bottom = new FormAttachment(decode2Button, -5, SWT.TOP);
        final FormData fd_decode2Button = new FormData();
        fd_decode2Button.bottom = new FormAttachment(text2Text, -5, SWT.TOP);
        fd_decode2Button.left = new FormAttachment(charset2, 0, SWT.LEFT);
        decode2Button.setLayoutData(fd_decode2Button);
        decode2Button.setText("↘Decode");

        D2Hold = new Button(comp, SWT.TOGGLE);
        final FormData fd_d2Hold = new FormData();
        fd_d2Hold.bottom = new FormAttachment(text2Text, -5, SWT.TOP);
        fd_d2Hold.left = new FormAttachment(decode2Button, 5, SWT.RIGHT);
        D2Hold.setLayoutData(fd_d2Hold);
        D2Hold.setImage(SWTResources.getImageRes("/icons/obj16/link_obj.gif"));

        binText = new Text(comp, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
        binText.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                binary = null;
            }
        });
        binText.setText("bin");
        final FormData fd_binText = new FormData();
        fd_binText.left = new FormAttachment(charset1, 5, SWT.RIGHT);
        fd_binText.bottom = new FormAttachment(text2Text, -5, SWT.TOP);
        fd_binText.top = new FormAttachment(text1Text, 5, SWT.BOTTOM);
        fd_binText.right = new FormAttachment(charset2, -5, SWT.LEFT);
        binText.setLayoutData(fd_binText);

        setup();
    }

    void setup() {
        encode1Button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                encode1();
            }
        });
        encode2Button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                encode2();
            }
        });
        decode1Button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                decode1();
            }
        });
        decode2Button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                decode2();
            }
        });
        E1Hold.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                encode1Button.setEnabled(!isE1Hold());
            }
        });
        E2Hold.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                encode2Button.setEnabled(!isE2Hold());
            }
        });
        D1Hold.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                decode1Button.setEnabled(!isD1Hold());
            }
        });
        D2Hold.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                decode2Button.setEnabled(!isD2Hold());
            }
        });

        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        Charset def = Charset.defaultCharset();
        String defName = def.name();
        for (String name : charsets.keySet()) {
            charset1.add(name);
            charset2.add(name);
            if (defName.equals(name)) {
                int lastIndex = charset1.getItemCount() - 1;
                charset1.select(lastIndex);
                charset1.showSelection();
                charset2.select(lastIndex);
                charset2.showSelection();
            }
        }
        charset1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (isE1Hold())
                    encode1();
                if (isE2Hold())
                    encode2();
            }
        });
        charset2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (isD1Hold())
                    decode1();
                if (isD2Hold())
                    decode2();
            }
        });
        text1Text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (isE1Hold())
                    encode1();
                if (isD2Hold())
                    decode2();
            }
        });
        text2Text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isE2Hold())
                    encode2();
                if (isD1Hold())
                    decode1();
            }
        });
    }

    boolean isE1Hold() {
        return E1Hold.getSelection();
    }

    boolean isE2Hold() {
        return E2Hold.getSelection();
    }

    boolean isD1Hold() {
        return D1Hold.getSelection();
    }

    boolean isD2Hold() {
        return D2Hold.getSelection();
    }

    String getCharset1() {
        int index = charset1.getSelectionIndex();
        if (index == -1)
            return null;
        return charset1.getItem(index);
    }

    String getCharset2() {
        int index = charset2.getSelectionIndex();
        if (index == -1)
            return null;
        return charset1.getItem(index);
    }

    byte[] getBinary() {
        String hex = binText.getText();
        byte[] bytes;
        try {
            bytes = HexCodec.getInstance().decode(hex);
        } catch (DecodeException e) {
            userDialogs.alert(e.getMessage(), e);
            return null;
        }
        return bytes;
    }

    static String newline = "\r\n";

    void setBinary(byte[] bytes) {
        String hex = HexCodec.getInstance().encode(bytes);
        hex = hex.replaceAll("\\b0[aA]\\s*", "0a" + newline);
        binText.setText(hex);
        binary = bytes;
    }

    void encode1() {
        String charset = getCharset1();
        String s = text1Text.getText();
        try {
            setBinary(s.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            userDialogs.alert(e.getMessage(), e);
        }
    }

    void encode2() {
        String charset = getCharset1();
        String s = text2Text.getText();
        try {
            setBinary(s.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            userDialogs.alert(e.getMessage(), e);
        }
    }

    void decode1() {
        if (binary == null) {
            binary = getBinary();
            if (binary == null)
                return;
        }
        String charset = getCharset2();
        String s;
        try {
            s = new String(binary, charset);
        } catch (UnsupportedEncodingException e) {
            userDialogs.alert(e.getMessage(), e);
            return;
        }
        text1Text.setText(s);
    }

    void decode2() {
        if (binary == null) {
            binary = getBinary();
            if (binary == null)
                return;
        }
        String charset = getCharset2();
        String s;
        try {
            s = new String(binary, charset);
        } catch (UnsupportedEncodingException e) {
            userDialogs.alert(e.getMessage(), e);
            return;
        }
        text2Text.setText(s);
    }

    public static void main(String[] args)
            throws Throwable {
        new GIconv().execute(args);
    }

}
