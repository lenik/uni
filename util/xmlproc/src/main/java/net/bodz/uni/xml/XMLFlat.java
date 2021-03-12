package net.bodz.uni.xml;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import net.bodz.bas.c.string.StringUtil;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.io.adapter.PrintStreamPrintOut;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.order.TableOrderComparator;

/**
 * Convert XML document to plain table
 */
@MainVersion({ 0, 1 })
@ProgramName("xmlflat")
@RcsKeywords(id = "$Id$")
public class XMLFlat
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(XMLFlat.class);

    /**
     * reuse characters from LIST instead of TABs
     *
     * @option -d =LIST
     */
    protected char[] delimiters = ":".toCharArray();

    /**
     * Input encoding
     *
     * @option -E =FORMAT
     */
    protected Charset inputEncoding = Charset.defaultCharset();

    /**
     * Output encoding
     *
     * @option -e =ENCODING
     */
    protected Charset outputEncoding = Charset.defaultCharset();

    /**
     * Read from this file (default stdin)
     *
     * @option -f =FILE
     */
    protected File inputFile;

    /**
     * Output to this file (default stdout)
     *
     * @option -o =FILE
     */
    protected File outputFile;

    /**
     * Start from selected nodes (default root)
     *
     * @option -s =XPATH
     */
    protected XPath select;

    /**
     * Include caption row
     *
     * @option -c
     */
    protected boolean caption;

    /**
     * Include nodes which doesn't match any field
     *
     * @option
     */
    protected boolean includeEmpties;

    /**
     * Fill column with space to align at width
     *
     * @option -a
     */
    protected boolean align;

    /**
     * Reuse widths from LIST instead of autosize(0)
     *
     * @option -w =LIST(,)
     */
    protected int[] widths;
    protected boolean[] autosize;

    /**
     * Sort by columns (-column for descending)
     *
     * @option -t =LIST(,)
     */
    protected int[] sortColumns;

    private boolean escaping;

    /**
     * Switch escaping mode
     *
     * @option -x
     */
    protected void switchEscaping() {
        escaping = !escaping;
    }

    protected IPrintOut out;
    protected DocumentFactory docfac;
    protected Document doc;
    protected String[] captions;
    protected XPath[] fields;

    @Override
    protected void reconfigure()
            throws Exception {
        if (delimiters == null)
            delimiters = ",".toCharArray();
        if (widths != null)
            align = true;

        docfac = DocumentFactory.getInstance();
        if (select == null)
            select = docfac.createXPath("//*");
    }

    public void convert(Document doc)
            throws DocumentException {
        if (align) {
            autosize = new boolean[fields.length];
            int[] _w = new int[fields.length];
            for (int i = 0; i < fields.length; i++)
                autosize[i] = 0 == (_w[i] = widths[i % widths.length]);
            widths = _w;
        }

        List<String[]> table = new ArrayList<String[]>();
        StringBuffer buf = new StringBuffer();

        for (Object _row : select.selectNodes(doc)) {
            Node row = (Node) _row;
            String[] vals = new String[fields.length];
            int validCells = 0;
            for (int i = 0; i < fields.length; i++) {
                List<?> cell = fields[i].selectNodes(row);
                if (cell.isEmpty()) {
                    vals[i] = "";
                } else {
                    buf.setLength(0);
                    for (Object _picoNode : cell) {
                        Node pico = (Node) _picoNode;
                        String picoText = pico.getText();
                        buf.append(picoText);
                    }
                    String val = buf.toString();
                    if (align && val.length() > widths[i])
                        widths[i] = val.length();
                    vals[i] = val;
                    validCells++;
                }
            }
            if (validCells == 0 && !includeEmpties)
                continue;
            table.add(vals);
        }

        if (sortColumns != null) {
            Collections.sort(table, new TableOrderComparator(sortColumns));
        }

        if (caption) {
            String[] vals = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                String val = captions[i];
                if (align && val.length() > widths[i])
                    widths[i] = val.length();
                vals[i] = val;
            }
            table.add(0, vals);
        }

        for (String[] vals : table) {
            String lastval = null;
            for (int i = 0; i < vals.length; i++) {
                if (i != 0) {
                    int last = i - 1;
                    if (align) {
                        int width = widths[last % widths.length];
                        int len = lastval.length();
                        while (len++ < width)
                            out.print(' ');
                    }
                    char delim = delimiters[last % delimiters.length];
                    out.print(delim);
                }
                lastval = vals[i]; // escaping...
                out.print(lastval);
            }
            out.println();
        }
    }

    @Override
    protected void showHelpPage(IPrintOut out) {
        super.showHelpPage(out);
        out.println();
        StringUtil.helpEscapes(out);
        out.flush();
    }

    private static final Pattern CAPNAME;
    static {
        CAPNAME = Pattern.compile("([^\\[]+)=.*");
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        SAXReader reader = new SAXReader();
        if (inputFile == null) {
            logger.stdout(tr._("enter the xml document: "));
            doc = reader.read(System.in);
        } else {
            logger.info(tr._("process "), inputFile);
            doc = reader.read(inputFile);
        }

        fields = new XPath[args.length];
        captions = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            Matcher matcher = CAPNAME.matcher(arg);
            if (matcher.matches()) {
                captions[i] = matcher.group(1);
                arg = arg.substring(matcher.end(1) + 1);
            } else
                captions[i] = arg.replaceAll("\\W", "_");
            fields[i] = doc.createXPath(arg);
        }

        if (outputFile == null)
            out = Stdio.cout;
        else
            out = new PrintStreamPrintOut(new PrintStream(outputFile, outputEncoding.name()));

        convert(doc);
    }

    public static void main(String[] args)
            throws Exception {
        new XMLFlat().execute(args);
    }

}
