package net.bodz.lapiota.devhelpers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.a.Doc;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.io.Files;
import net.bodz.bas.ui.UIException;
import net.bodz.bas.ui.a.PreferredSize;
import net.bodz.bas.xml.XMLs;
import net.bodz.lapiota.wrappers.BasicGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@Doc("CSS Look&Feel")
@PreferredSize(width = 800, height = 600)
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class VisualCSS extends BasicGUI {

    private URLListEditor pageList;
    private URLListEditor cssList;
    private Browser       browser;

    private String        demoHtml;
    private int           cssInsertion;
    private String        cssFragment;

    static final Pattern  headPattern;
    static {
        headPattern = Pattern.compile("<head\b.*?>", Pattern.CASE_INSENSITIVE);
    }

    private Preferences   preferences;
    private List<URL>     pagev     = new ArrayList<URL>();
    private List<URL>     cssv      = new ArrayList<URL>();

    static final String   KEY_PAGEV = "pagev";
    static final String   KEY_CSSV  = "cssv";

    @SuppressWarnings("unchecked")
    @Override
    protected void _boot() throws Exception {
        Class<? extends VisualCSS> clazz = getClass();
        preferences = Preferences.userNodeForPackage(clazz);

        String pagevXml = preferences.get(KEY_PAGEV, null);
        if (pagevXml == null) {
            pagev.add(Files.classData(clazz, "basic.html"));
            pagev.add(Files.classData(clazz, "ntfs.html"));
            pagev.add(Files.classData(clazz, "stat.html"));
        } else {
            pagev = (List<URL>) XMLs.decode(pagevXml);
        }

        String cssvXml = preferences.get(KEY_CSSV, null);
        if (cssvXml == null) {
            cssv.add(Files.classData(clazz, "basic.css"));
            cssv.add(Files.classData(clazz, "ntfs.css"));
        } else {
            cssv = (List<URL>) XMLs.decode(cssvXml);
        }
    }

    @Override
    protected void _exit() throws Exception {
        String pagevXml = XMLs.encode(pagev);
        String cssvXml = XMLs.encode(cssv);
        preferences.put(KEY_PAGEV, pagevXml);
        preferences.put(KEY_CSSV, cssvXml);
        super._exit();
    }

    @Override
    protected void createInitialView(Composite holder) throws UIException {
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        holder.setLayout(gridLayout);

        final Composite topPane = new Composite(holder, SWT.NONE);
        topPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        topPane.setLayout(new GridLayout(3, false));
        final Label locationLabel = new Label(topPane, SWT.NONE);
        locationLabel.setText("&Location: ");
        final Text locationText = new Text(topPane, SWT.BORDER);
        locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        locationText.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                String location = locationText.getText();
                go(location);
            }
        });

        final SashForm mainSash = new SashForm(holder, SWT.HORIZONTAL | SWT.BORDER);
        mainSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final SashForm leftPane = new SashForm(mainSash, SWT.VERTICAL | SWT.BORDER);

        pageList = new URLListEditor(leftPane, SWT.NONE);
        pageList.setText("Demo &Page");
        pageList.setList(pagev);
        pageList.setAllowArrange(true);
        pageList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                URL url = pageList.getSelection();
                try {
                    demoHtml = Files.readAll(url, "utf-8"); // xml auto decode??
                    Matcher m = headPattern.matcher(demoHtml);
                    if (m.find()) {
                        cssInsertion = m.end();
                    } else {
                        cssInsertion = 0;
                    }
                    render();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        cssList = new URLListEditor(leftPane, SWT.NONE);
        cssList.setText("Apply with &CSS");
        cssList.setList(cssv);
        cssList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                URL url = cssList.getSelection();
                try {
                    String css = Files.readAll(url, "utf-8"); //
                    cssFragment = "<style><!--\n" + css + "\n--></style>\n";
                    render();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        leftPane.setWeights(new int[] { 1, 1 });

        browser = new Browser(mainSash, SWT.NONE);

        mainSash.setWeights(new int[] { 3, 7 });
    }

    public void go(String href) {
        browser.setUrl(href);
    }

    void render() {
        if (demoHtml == null)
            return;
        String html = demoHtml.substring(0, cssInsertion) //
                + cssFragment//
                + demoHtml.substring(cssInsertion);
        browser.setText(html);
    }

    public static void main(String[] args) throws Exception {
        new VisualCSS().run(args);
    }

}
