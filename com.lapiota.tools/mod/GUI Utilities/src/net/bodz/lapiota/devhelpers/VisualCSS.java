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
import net.bodz.swt.adapters.TextAdapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.AuthenticationEvent;
import org.eclipse.swt.browser.AuthenticationListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

@Doc("CSS Look&Feel")
@PreferredSize(width = 800, height = 600)
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class VisualCSS extends BasicGUI {

    private URLListEditor pageList;
    private URLListEditor cssList;
    private Browser       browser;

    private Label         browserTitle;
    private ProgressBar   progressBar;
    private Label         statusLabel;

    private String        templateHtml;
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
        final Display display = holder.getDisplay();

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
                browser.setUrl(location);
            }
        });
        TextAdapters.autoSelect(locationText);

        final SashForm mainSash = new SashForm(holder, SWT.HORIZONTAL | SWT.BORDER);
        mainSash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainSash.setSashWidth(1);

        final SashForm leftPane = new SashForm(mainSash, SWT.VERTICAL | SWT.BORDER);
        leftPane.setSashWidth(1);

        pageList = new URLListEditor(leftPane, SWT.NONE);
        pageList.setText("Demo &Page");
        pageList.setList(pagev);
        pageList.setAllowArrange(true);
        pageList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                URL url = pageList.getSelection();
                try {
                    String html = Files.readAll(url, "utf-8"); // xml auto decode??
                    parseTemplate(html);
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

        final Composite browserPane = new Composite(mainSash, SWT.NONE);
        GridLayout browserLayout = new GridLayout(2, false);
        browserPane.setLayout(browserLayout);
        browserLayout.marginWidth = browserLayout.marginHeight = 0;

        browserTitle = new Label(browserPane, SWT.NONE);
        browserTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        browserTitle.setFont(new Font(holder.getDisplay(), "Tahoma", 12, SWT.BOLD));

        browser = new Browser(browserPane, SWT.NONE);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        statusLabel = new Label(browserPane, SWT.BORDER);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        statusLabel.setForeground(display.getSystemColor(SWT.COLOR_GRAY));

        progressBar = new ProgressBar(browserPane, SWT.NONE);
        GridData progressData = new GridData();
        progressData.widthHint = 30;
        progressBar.setLayoutData(progressData);
        progressBar.setMinimum(0);

        browser.setJavascriptEnabled(true);
        browser.addAuthenticationListener(new AuthenticationListener() {
            @Override
            public void authenticate(AuthenticationEvent event) {
                // fill event.user, event.password
            }
        });
        browser.addLocationListener(new LocationListener() {
            @Override
            public void changing(LocationEvent event) {
            }

            @Override
            public void changed(LocationEvent event) {
                if ("about:blank".equals(event.location))
                    return;
                templateHtml = null;
            }
        });
        browser.addProgressListener(new ProgressListener() {
            @Override
            public void changed(ProgressEvent event) {
                progressBar.setMaximum(event.total);
                progressBar.setSelection(event.current);
            }

            @Override
            public void completed(ProgressEvent event) {
            }
        });
        browser.addStatusTextListener(new StatusTextListener() {
            @Override
            public void changed(StatusTextEvent event) {
                statusLabel.setText(event.text);
            }
        });
        browser.addTitleListener(new TitleListener() {
            @Override
            public void changed(TitleEvent event) {
                browserTitle.setText(event.title);
            }
        });

        mainSash.setWeights(new int[] { 3, 7 });
    }

    void parseTemplate(String templateHtml) {
        this.templateHtml = templateHtml;
        Matcher m = headPattern.matcher(templateHtml);
        if (m.find()) {
            cssInsertion = m.end();
        } else {
            cssInsertion = 0;
        }
    }

    void render() {
        if (templateHtml == null)
            parseTemplate(browser.getText());
        String mixedHtml = templateHtml.substring(0, cssInsertion) //
                + cssFragment//
                + templateHtml.substring(cssInsertion);
        browser.setText(mixedHtml);
    }

    public static void main(String[] args) throws Exception {
        new VisualCSS().run(args);
    }

}
