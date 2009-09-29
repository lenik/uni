package net.bodz.lapiota.xmlfs;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import net.bodz.bas.a.BootInfo;
import net.bodz.bas.a.Doc;
import net.bodz.bas.a.ProgramName;
import net.bodz.bas.a.RcsKeywords;
import net.bodz.bas.a.Version;
import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.a.Option;
import net.bodz.bas.cli.a.ParseBy;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;
import net.bodz.lapiota.util.TypeExtensions.XPathParser;

import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@BootInfo(syslibs = { "dom4j", "jaxen" })
@Doc("Xmlfs XPath Selector")
@ProgramName("xfss")
@RcsKeywords(id = "$Id$")
@Version( { 0, 1 })
public class XmlfsSelector extends BasicCLI {

    @Option(alias = "E", vnam = "FORMAT", doc = "input encoding")
    protected Charset      inputEncoding  = Charset.defaultCharset();

    @Option(alias = "e", vnam = "ENCODING", doc = "output encoding")
    protected Charset      outputEncoding = Charset.defaultCharset();

    @Option(alias = "s", vnam = "XPATH", required = true)
    @ParseBy(XPathParser.class)
    protected XPath        select;

    @Option(alias = "O", vnam = "pretty|compact")
    @ParseBy(OutputFormatParser.class)
    protected OutputFormat outputFormat   = OutputFormat.createPrettyPrint();

    @SuppressWarnings("unchecked")
    @Override
    protected void doFileArgument(File root) throws Exception {
        XmlfsDocument doc = new XmlfsDocument(root);
        List list = select.selectNodes(doc);
        for (Object o : list) {
            if (o instanceof Node) {
                OutputStream out = System.out;
                String xmlenc = doc.getXMLEncoding();
                if (xmlenc != null)
                    outputFormat.setEncoding(xmlenc);
                XMLWriter writer = new XMLWriter(out, outputFormat);
                writer.write(doc);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new XmlfsSelector().run(args);
    }

}
