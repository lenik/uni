package net.bodz.lapiota.gui;

import java.nio.charset.Charset;
import java.util.SortedMap;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.gui.GUIException;
import net.bodz.bas.gui.Interaction;
import net.bodz.bas.gui.a.PreferredSize;
import net.bodz.lapiota.wrappers.BasicGUI;
import net.bodz.swt.gui.SWTInteraction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@Doc("GUI iconv utility")
@PreferredSize(width = 400, height = 300)
@RcsKeywords(id = "$Id: GIconv.java 0 2008-11-17 下午11:59:23 Shecti $")
@Version( { 0, 0 })
public class GIconv extends BasicGUI {

    private Combo       fromCombo;
    private Combo       toCombo;
    private Text        sourceText;
    private Text        destText;

    private Interaction iact = new SWTInteraction();

    @Override
    protected void createInitialView(Composite comp) throws GUIException,
            SWTException {
        comp.setLayout(new FillLayout());
        comp = new Composite(comp, SWT.NONE);
        comp.setLayout(new FormLayout());

        Composite params = new Composite(comp, SWT.NONE);
        final FormData fd_params = new FormData();
        fd_params.right = new FormAttachment(100, -5);
        fd_params.bottom = new FormAttachment(0, 32);
        fd_params.top = new FormAttachment(0, 5);
        fd_params.left = new FormAttachment(0, 5);
        params.setLayoutData(fd_params);
        params.setLayout(new FormLayout());

        final Label fromLabel = new Label(params, SWT.NONE);
        final Label toLabel = new Label(params, SWT.NONE);

        final FormData fd_fromLabel = new FormData();
        fd_fromLabel.top = new FormAttachment(0, 5);
        fd_fromLabel.left = new FormAttachment(0, 5);
        fromLabel.setLayoutData(fd_fromLabel);
        fromLabel.setText("&From:");

        fromCombo = new Combo(params, SWT.READ_ONLY);
        final FormData fd_fromCombo = new FormData();
        fd_fromCombo.top = new FormAttachment(fromLabel, 0, SWT.TOP);
        fd_fromCombo.left = new FormAttachment(fromLabel, 5, SWT.RIGHT);
        fromCombo.setLayoutData(fd_fromCombo);

        fd_fromCombo.right = new FormAttachment(toLabel, -5, SWT.LEFT);
        final FormData fd_toLabel = new FormData();
        fd_toLabel.bottom = new FormAttachment(0, 27);
        fd_toLabel.left = new FormAttachment(50, 0);
        fd_toLabel.top = new FormAttachment(fromCombo, 0, SWT.TOP);
        toLabel.setLayoutData(fd_toLabel);
        toLabel.setText("&To: ");

        toCombo = new Combo(params, SWT.READ_ONLY);
        final FormData fd_toCombo = new FormData();
        fd_toCombo.top = new FormAttachment(0, 5);
        fd_toCombo.right = new FormAttachment(100, -5);
        toCombo.setLayoutData(fd_toCombo);
        fd_toCombo.left = new FormAttachment(toLabel, 5, SWT.RIGHT);

        final Label sourceLabel = new Label(comp, SWT.NONE);
        final FormData fd_sourceLabel = new FormData();
        fd_sourceLabel.top = new FormAttachment(params, 5, SWT.BOTTOM);
        fd_sourceLabel.left = new FormAttachment(params, 0, SWT.LEFT);
        sourceLabel.setLayoutData(fd_sourceLabel);
        sourceLabel.setText("&Source:");

        sourceText = new Text(comp, SWT.BORDER);
        sourceText.setText("source");
        final FormData fd_sourceText = new FormData();
        fd_sourceText.bottom = new FormAttachment(52, 0);
        fd_sourceText.right = new FormAttachment(params, 0, SWT.RIGHT);
        fd_sourceText.top = new FormAttachment(sourceLabel, 0, SWT.TOP);
        fd_sourceText.left = new FormAttachment(sourceLabel, 5, SWT.RIGHT);
        sourceText.setLayoutData(fd_sourceText);

        final Label destLabel = new Label(comp, SWT.NONE);
        final FormData fd_destLabel = new FormData();
        fd_destLabel.top = new FormAttachment(sourceText, 5, SWT.BOTTOM);
        fd_destLabel.left = new FormAttachment(sourceLabel, 0, SWT.LEFT);
        destLabel.setLayoutData(fd_destLabel);
        destLabel.setText("&Dest: ");

        destText = new Text(comp, SWT.BORDER);
        destText.setText("dest");
        final FormData fd_destText = new FormData();
        fd_destText.bottom = new FormAttachment(100, -5);
        fd_destText.right = new FormAttachment(sourceText, 0, SWT.RIGHT);
        fd_destText.top = new FormAttachment(sourceText, 5, SWT.BOTTOM);
        fd_destText.left = new FormAttachment(sourceText, 0, SWT.LEFT);
        destText.setLayoutData(fd_destText);

        setup();
    }

    void setup() {
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        Charset def = Charset.defaultCharset();
        String defName = def.name();
        for (String name : charsets.keySet()) {
            fromCombo.add(name);
            toCombo.add(name);
            if (defName.equals(name)) {
                int lastIndex = fromCombo.getItemCount() - 1;
                fromCombo.select(lastIndex);
                toCombo.select(lastIndex);
            }
        }

        fromCombo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                iconv();
            }
        });

        toCombo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                iconv();
            }
        });

        sourceText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                iconv();
            }
        });

        destText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
                iconv(true);
            }
        });
    }

    void iconv() {
        iconv(false);
    }

    void iconv(boolean reverse) {
        if (reverse) {
            String src = destText.getText();
            String from = toCombo.getText();
            String to = fromCombo.getText();
            String dst = iconv(src, from, to);
            if (dst != null)
                sourceText.setText(dst);
        } else {
            String src = sourceText.getText();
            String from = fromCombo.getText();
            String to = toCombo.getText();
            String dst = iconv(src, from, to);
            if (dst != null)
                destText.setText(dst);
        }
    }

    String iconv(String src, String fromEnc, String toEnv) {
        try {
            byte[] bytes = src.getBytes(fromEnc);
            String dst = new String(bytes, toEnv);
            return dst;
        } catch (Exception e) {
            iact.alert("Error", e);
            return null;
        }
    }

    public static void main(String[] args) throws Throwable {
        new GIconv().run(args);
    }

}
