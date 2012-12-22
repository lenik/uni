package net.bodz.lapiota.program.dev;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.compile.Win32Only;
import net.bodz.jna.win32.GDI32.RECT;
import net.bodz.jna.win32.*;
import net.bodz.jna.win32.User32.POINT;
import net.bodz.jna.win32.User32.POINTByValue;
import net.bodz.jna.win32.User32.WNDENUMPROC;
import net.bodz.jna.win32.W32API.HWND;
import net.bodz.swt.c3.misc.Timer;
import net.bodz.swt.program.BasicGUI;

import com.sun.jna.Pointer;

/**
 * Win32 Windows Editor
 *
 * @style width: 456; height: 375
 */
@ProgramName("winedit")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
@Win32Only
public class WinEdit
        extends BasicGUI
        implements IWin32 {

    /**
     * Capture interval, default 1000
     *
     * @option -i =ms
     */
    int interval = 100;

    /**
     * Follow the mouse cursor
     *
     * @option -f
     */
    boolean follow;

    HWND hwnd;
    ArrayList<HWND> againstList = new ArrayList<HWND>();

    private Timer updaterTimer;

    private Text text;
    private Tree tree;
    private Button enabledButton;
    private Button visibleButton;
    private Button childWindowButton;
    private Text textEdit;
    private Text fontText;
    private Combo againstCombo;
    private Text boundsText;
    private Text sizeText;
    private Text searchText;
    private List searchList;

    @Override
    protected void _start() {
        if (interval > 0) {
            updaterTimer = new Timer(interval, shell) {
                boolean noReentrant = false;

                @Override
                public void run() {
                    if (noReentrant)
                        return;
                    noReentrant = true;
                    try {
                        updater();
                    } finally {
                        noReentrant = false;
                    }
                }
            };
        }
    }

    static void dumpTree(final String prefix, HWND hwnd) {
        RECT rect = new RECT();
        user32.GetWindowRect(hwnd, rect);
        System.out.println(prefix + hwnd + " rect=" + rect);
        user32.EnumChildWindows(hwnd, new WNDENUMPROC() {
            @Override
            public boolean callback(HWND child, Pointer data) {
                dumpTree(prefix + "    ", child);
                return true;
            }
        }, null);
    }

    synchronized void updater() {
        POINT cursor = new POINT();
        user32.GetCursorPos(cursor);

        // boolean scroll = (OS.GetKeyState(OS.VK_SCROLL) & 0x1) != 0;
        boolean ctrlDown = (OS.GetKeyState(OS.VK_CONTROL) & 0x8000) != 0;
        if (!(follow ^ ctrlDown))
            return;
        // shell.setLocation(10 + cursor.x, 10 + cursor.y);

        POINTByValue point = new POINTByValue(cursor.x, cursor.y);
        setHwnd(User32Utils.GetWindowAt(null, point));
    }

    void setHwnd(HWND hwnd) {
        this.hwnd = hwnd;

        boolean valid = hwnd != null;
        enabledButton.setEnabled(valid);
        visibleButton.setEnabled(valid);
        childWindowButton.setEnabled(valid);
        textEdit.setEnabled(valid);

        String _text = "NULL";
        if (hwnd != null)
            _text = hwnd.getText();
        text.setText(_text);

        int style = user32.GetWindowLong(hwnd, User32.GWL_STYLE);
        enabledButton.setSelection(user32.IsWindowEnabled(hwnd));
        visibleButton.setSelection(user32.IsWindowVisible(hwnd));
        childWindowButton.setSelection((User32.WS_CHILDWINDOW & style) != 0);
        textEdit.setText(_text);

        RECT rect = new RECT();
        user32.GetWindowRect(hwnd, rect);
        boundsText.setText(rect.toString());
        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;
        sizeText.setText(width + "*" + height);

        againstCombo.removeAll();
        againstList.clear();
        HWND parent = user32.GetParent(hwnd);
        while (parent != null) {
            String text = parent.getText();
            againstCombo.add(text);
            againstList.add(parent);
            parent = user32.GetParent(parent);
        }
    }

    @Override
    protected void createInitialView(Composite holder) {
        final GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 2;
        gridLayout.verticalSpacing = 2;
        gridLayout.numColumns = 1;
        holder.setLayout(gridLayout);

        text = new Text(holder, SWT.READ_ONLY | SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        SashForm sash = new SashForm(holder, SWT.NONE);
        sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tree = new Tree(sash, SWT.BORDER);
        final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);
        gd_tree.widthHint = 100;
        tree.setLayoutData(gd_tree);

        final TabFolder tabFolder = new TabFolder(sash, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sash.setWeights(new int[] { 3, 7 });

        final TabItem propertiesTabItem = new TabItem(tabFolder, SWT.NONE);
        propertiesTabItem.setText("Properties");

        final TabItem layoutTabItem = new TabItem(tabFolder, SWT.NONE);
        layoutTabItem.setText("Layout");

        final Composite layouts = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.verticalSpacing = 2;
        gridLayout_3.numColumns = 1;
        layouts.setLayout(gridLayout_3);
        layoutTabItem.setControl(layouts);

        final Composite layouts_1 = new Composite(layouts, SWT.NONE);
        layouts_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.marginWidth = 0;
        gridLayout_1.marginHeight = 0;
        gridLayout_1.numColumns = 4;
        layouts_1.setLayout(gridLayout_1);

        final Label againstLabel = new Label(layouts_1, SWT.NONE);
        againstLabel.setText("Against:");

        againstCombo = new Combo(layouts_1, SWT.READ_ONLY);
        againstCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label autoFitLabel = new Label(layouts_1, SWT.NONE);
        autoFitLabel.setText("Auto Size:");

        final Slider slider = new Slider(layouts_1, SWT.NONE);
        slider.setLayoutData(new GridData(80, SWT.DEFAULT));
        slider.setToolTipText("Adjust margin");
        slider.setThumb(2);
        slider.setPageIncrement(5);
        slider.setMaximum(25);
        slider.setMinimum(-25);

        final Composite againstOuter = new Composite(layouts, SWT.BORDER);
        againstOuter.setLayout(new FormLayout());
        final GridData gd_againstOuter = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        againstOuter.setLayoutData(gd_againstOuter);

        final Button anchorButton = new Button(againstOuter, SWT.NONE);
        anchorButton.setEnabled(false);
        anchorButton.setToolTipText("Hold down <ALT> key to change size. ");
        final FormData fd_anchorButton = new FormData();
        fd_anchorButton.bottom = new FormAttachment(24, 0);
        fd_anchorButton.right = new FormAttachment(18, 0);
        fd_anchorButton.left = new FormAttachment(1, 0);
        fd_anchorButton.top = new FormAttachment(2, 0);
        anchorButton.setLayoutData(fd_anchorButton);
        anchorButton.setText("Anchor");

        final Composite layouts_2 = new Composite(layouts, SWT.NONE);
        layouts_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        final GridLayout gridLayout_5 = new GridLayout();
        gridLayout_5.numColumns = 4;
        gridLayout_5.marginWidth = 0;
        gridLayout_5.marginHeight = 0;
        layouts_2.setLayout(gridLayout_5);

        final Label boundsLabel = new Label(layouts_2, SWT.NONE);
        boundsLabel.setText("Bounds:");

        boundsText = new Text(layouts_2, SWT.READ_ONLY | SWT.BORDER);
        boundsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label sizeLabel = new Label(layouts_2, SWT.NONE);
        sizeLabel.setText("Size:");

        sizeText = new Text(layouts_2, SWT.READ_ONLY | SWT.BORDER);
        sizeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        final TabItem listComboTabItem = new TabItem(tabFolder, SWT.NONE);
        listComboTabItem.setText("List/Combo");

        final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.verticalSpacing = 2;
        gridLayout_4.numColumns = 2;
        composite_1.setLayout(gridLayout_4);
        listComboTabItem.setControl(composite_1);

        final Label dropDownSizeLabel = new Label(composite_1, SWT.NONE);
        dropDownSizeLabel.setText("Drop down size: ");

        final Slider slider_1 = new Slider(composite_1, SWT.NONE);
        slider_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        slider_1.setIncrement(10);
        slider_1.setPageIncrement(50);
        slider_1.setThumb(16);
        slider_1.setMaximum(1000);
        slider_1.setMinimum(1);

        final Label searchTextLabel = new Label(composite_1, SWT.NONE);
        searchTextLabel.setText("Search keywords:");

        searchText = new Text(composite_1, SWT.BORDER);
        searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button followButton = new Button(composite_1, SWT.CHECK);
        followButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        followButton.setText("Follow");

        searchList = new List(composite_1, SWT.BORDER);
        searchList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        new Label(composite_1, SWT.NONE);

        final Composite composite_2 = new Composite(composite_1, SWT.NONE);
        composite_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        final RowLayout rowLayout_1 = new RowLayout();
        rowLayout_1.marginRight = 0;
        rowLayout_1.marginTop = 0;
        rowLayout_1.marginLeft = 0;
        rowLayout_1.marginBottom = 0;
        composite_2.setLayout(rowLayout_1);

        final Button selectAllButton = new Button(composite_2, SWT.NONE);
        selectAllButton.setText("Select All");

        final Button copyButton = new Button(composite_2, SWT.NONE);
        copyButton.setText("Copy");

        final Button insertButton = new Button(composite_2, SWT.NONE);
        insertButton.setText("Insert");

        final Button deleteButton = new Button(composite_2, SWT.NONE);
        deleteButton.setText("Delete");

        final Composite properties = new Composite(tabFolder, SWT.NONE);
        propertiesTabItem.setControl(properties);

        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 3;
        properties.setLayout(gridLayout_2);

        new Label(properties, SWT.NONE);
        enabledButton = new Button(properties, SWT.CHECK);
        enabledButton.setText("Enable");
        enabledButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hwnd == null)
                    return;
                boolean selection = enabledButton.getSelection();
                user32.EnableWindow(hwnd, selection);
            }
        });
        new Label(properties, SWT.NONE);

        new Label(properties, SWT.NONE);
        visibleButton = new Button(properties, SWT.CHECK);
        visibleButton.setText("Visible");
        visibleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hwnd == null)
                    return;
                boolean selection = visibleButton.getSelection();
                user32.ShowWindow(hwnd, selection ? OS.SW_SHOW : OS.SW_HIDE);
            }
        });
        new Label(properties, SWT.NONE);

        new Label(properties, SWT.NONE);
        childWindowButton = new Button(properties, SWT.CHECK);
        childWindowButton.setText("Child Window");
        childWindowButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hwnd == null)
                    return;
                boolean selection = childWindowButton.getSelection();
                int style = user32.GetWindowLong(hwnd, User32.GWL_STYLE);
                style &= ~User32.WS_CHILDWINDOW;
                if (selection)
                    style |= User32.WS_CHILDWINDOW;
                user32.SetWindowLong(hwnd, User32.GWL_STYLE, style);
            }
        });
        new Label(properties, SWT.NONE);

        final Label textLabel = new Label(properties, SWT.NONE);
        textLabel.setText("Text:");
        textEdit = new Text(properties, SWT.BORDER);
        final GridData gd_textEdit = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textEdit.setLayoutData(gd_textEdit);
        final Button saveTextButton = new Button(properties, SWT.NONE);
        saveTextButton.setText("Save");
        saveTextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hwnd == null)
                    return;
                if (hwnd.equals(Pointer.createConstant(textEdit.handle)))
                    return;
                String text = textEdit.getText();
                user32.SetWindowText(hwnd, text);
            }
        });

        final Label fontLabel = new Label(properties, SWT.NONE);
        fontLabel.setText("Font:");

        fontText = new Text(properties, SWT.READ_ONLY | SWT.BORDER);
        fontText.setText("FONT");
        final GridData gd_fontText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fontText.setLayoutData(gd_fontText);

        final Button changeFontButton = new Button(properties, SWT.NONE);
        changeFontButton.setText("...");
        new Label(properties, SWT.NONE);

        final Button readOnlyButton = new Button(properties, SWT.CHECK);
        readOnlyButton.setText("Read Only");
        new Label(properties, SWT.NONE);

        final Composite buttons = new Composite(holder, SWT.NONE);
        final RowLayout rowLayout = new RowLayout();
        rowLayout.marginTop = 0;
        rowLayout.marginRight = 0;
        rowLayout.marginLeft = 0;
        rowLayout.marginBottom = 0;
        buttons.setLayout(rowLayout);
        final GridData gd_buttonAligner = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        buttons.setLayoutData(gd_buttonAligner);

        final Button dumpButton = new Button(buttons, SWT.NONE);
        dumpButton.setText("Dump");
        dumpButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (hwnd == null)
                    return;
                dumpTree("  ", hwnd);
            }
        });
    }

    public static void main(String[] args)
            throws Throwable {
        new WinEdit().execute(args);
    }

}
