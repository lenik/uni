package net.bodz.lapiota.xmlfs;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;
import net.bodz.lapiota.util.TypeExtensions.XPathParser;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jaxen.XPath;

/**
 * Xmlfs XPath Selector
 */
@BootInfo(syslibs = { "dom4j", "jaxen" })
@ProgramName("xfss")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class XmlfsSelector
        extends BasicCLI {

    /**
     * input encoding
     *
     * @option -E =FORMAT
     */
    protected Charset inputEncoding = Charset.defaultCharset();

    /**
     * output encoding
     *
     * @option -e =ENCODING
     */
    protected Charset outputEncoding = Charset.defaultCharset();

    /**
     * @option -s =XPATH required
     */
    @ParseBy(XPathParser.class)
    protected XPath select;

    /**
     * @option -O =pretty|compact
     */
    @ParseBy(OutputFormatParser.class)
    protected OutputFormat outputFormat = OutputFormat.createPrettyPrint();

    @SuppressWarnings("unchecked")
    @Override
    protected void doFileArgument(File root)
            throws Exception {
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

    public static void main(String[] args)
            throws Exception {
        new XmlfsSelector().run(args);
    }

}
