package net.bodz.lapiota.xmltools;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.io.CharOuts;
import net.bodz.bas.lang.ControlBreak;
import net.bodz.bas.types.util.Comparators;
import net.bodz.lapiota.util.StringUtil;
import net.bodz.lapiota.util.TypeExtensions.XPathParser;
import net.bodz.lapiota.wrappers.BasicCLI;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

@Doc("Convert XML document to plain table")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id$")
@RunInfo(lib = { "dom4j", "jaxen" })
public class XMLFlat extends BasicCLI {

    @Option(alias = "d", vnam = "LIST", doc = "reuse characters from LIST instead of TABs")
    protected char[]    delimiters     = ":".toCharArray();

    @Option(alias = "E", vnam = "FORMAT", doc = "input encoding")
    protected Charset   inputEncoding  = Charset.defaultCharset();

    @Option(alias = "e", vnam = "ENCODING", doc = "output encoding")
    protected Charset   outputEncoding = Charset.defaultCharset();

    @Option(alias = "f", vnam = "FILE", doc = "read from this file (default stdin)")
    protected File      inputFile;

    @Option(alias = "o", vnam = "FILE", doc = "output to this file (default stdout)")
    protected File      outputFile;

    @Option(alias = "s", vnam = "XPATH", parser = XPathParser.class, doc = "start from selected nodes (default root)")
    protected XPath     select;

    @Option(alias = "c", doc = "include caption row")
    protected boolean   caption;

    @Option(doc = "include nodes which doesn't match any field")
    protected boolean   includeEmpties;

    @Option(alias = "a", doc = "fill column with space to align at width")
    protected boolean   align;

    @Option(alias = "w", vnam = "LIST(,)", doc = "reuse widths from LIST instead of autosize(0)")
    protected int[]     widths;
    protected boolean[] autosize;

    @Option(alias = "t", vnam = "LIST(,)", doc = "sort by columns (-column for descending)")
    protected int[]     sortColumns;

    private boolean     escaping;

    @Option(alias = "x", doc = "switch escaping mode")
    protected void switchEscaping() {
        escaping = !escaping;
    }

    protected CharOut         out;
    protected DocumentFactory docfac;
    protected Document        doc;
    protected String[]        captions;
    protected XPath[]         fields;

    @Override
    protected void _boot() throws Throwable {
        if (delimiters == null)
            delimiters = ",".toCharArray();
        if (widths != null)
            align = true;

        docfac = DocumentFactory.getInstance();
        if (select == null)
            select = docfac.createXPath("//*");
    }

    public void convert(Document doc) throws DocumentException {
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
            Collections.sort(table, Comparators.array(sortColumns));
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
    protected void _help(CharOut out) throws CLIException {
        try {
            super._help(out);
        } catch (ControlBreak b) {
            out.println();
            StringUtil.helpEscapes(out);
            throw b;
        }
    }

    private static final Pattern CAPNAME;
    static {
        CAPNAME = Pattern.compile("([^\\[]+)=.*");
    }

    @Override
    protected void _main(String[] args) throws Throwable {
        SAXReader reader = new SAXReader();
        if (inputFile == null) {
            L.u.P("enter the xml document: ");
            doc = reader.read(System.in);
        } else {
            L.i.P("process ", inputFile);
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
            out = CharOuts.stdout;
        else
            out = CharOuts.get(new FileOutputStream(outputFile), //
                    outputEncoding.name());

        convert(doc);
    }

    public static void main(String[] args) throws Throwable {
        new XMLFlat().run(args);
    }

}
