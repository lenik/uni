package net.bodz.lapiota.devhelpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
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

import net.bodz.bas.c.loader.ClassResource;
import net.bodz.bas.gui.err.GUIException;
import net.bodz.bas.io.resource.builtin.URLResource;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.xml.XMLs;
import net.bodz.swt.c.text.TextAdapters;
import net.bodz.swt.program.BasicGUI;

/**
 * CSS Look&Feel
 *
 * @style width: 800; height: 600
 */
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class VisualCSS
        extends BasicGUI {

    private URLResourceListEditor pageList;
    private URLResourceListEditor cssList;
    private Browser browser;

    private Label browserTitle;
    private ProgressBar progressBar;
    private Label statusLabel;

    private String templateHtml;
    private int cssInsertion;
    private String cssFragment;

    static final Pattern headPattern;
    static {
        headPattern = Pattern.compile("<head\b.*?>", Pattern.CASE_INSENSITIVE);
    }

    private Preferences preferences;
    private List<URLResource> pagev = new ArrayList<URLResource>();
    private List<URLResource> cssv = new ArrayList<URLResource>();

    static final String KEY_PAGEV = "pagev";
    static final String KEY_CSSV = "cssv";

    @Override
    protected void _boot()
            throws Exception {
        Class<? extends VisualCSS> clazz = getClass();
        preferences = Preferences.userNodeForPackage(clazz);

        String pagevXml = preferences.get(KEY_PAGEV, null);
        if (pagevXml == null) {
            pagev.add(ClassResource.getData(clazz, "basic.html"));
            pagev.add(ClassResource.getData(clazz, "ntfs.html"));
            pagev.add(ClassResource.getData(clazz, "stat.html"));
        } else {
            pagev = (List<URLResource>) XMLs.decode(pagevXml);
        }

        String cssvXml = preferences.get(KEY_CSSV, null);
        if (cssvXml == null) {
            cssv.add(ClassResource.getData(clazz, "basic.css"));
            cssv.add(ClassResource.getData(clazz, "ntfs.css"));
        } else {
            cssv = (List<URLResource>) XMLs.decode(cssvXml);
        }
    }

    @Override
    protected void _exit()
            throws Exception {
        String pagevXml = XMLs.encode(pagev);
        String cssvXml = XMLs.encode(cssv);
        preferences.put(KEY_PAGEV, pagevXml);
        preferences.put(KEY_CSSV, cssvXml);
        super._exit();
    }

    @Override
    protected void createInitialView(Composite holder)
            throws GUIException {
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

        pageList = new URLResourceListEditor(leftPane, SWT.NONE);
        pageList.setText("Demo &Page");
        pageList.setList(pagev);
        pageList.setAllowMovingItems(true);
        pageList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                URLResource res = pageList.getSelection();
                res.setCharset("utf-8"); // xml auto decode??
                try {
                    String html = res.tooling()._for(StreamReading.class).readTextContents();
                    parseTemplate(html);
                    render();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        cssList = new URLResourceListEditor(leftPane, SWT.NONE);
        cssList.setText("Apply with &CSS");
        cssList.setList(cssv);
        cssList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                URLResource res = cssList.getSelection();
                res.setCharset("utf-8"); // css auto decode??

                try {
                    String css = res.tooling()._for(StreamReading.class).readTextContents();
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

    public static void main(String[] args)
            throws Exception {
        new VisualCSS().execute(args);
    }

}
