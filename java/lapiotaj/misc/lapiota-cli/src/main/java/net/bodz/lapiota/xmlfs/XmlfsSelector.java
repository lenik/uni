package net.bodz.lapiota.xmlfs;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jaxen.XPath;

import net.bodz.bas.cli.meta.ProgramName;
import net.bodz.bas.cli.skel.BasicCLI;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.vfs.IFile;

/**
 * Xmlfs XPath Selector
 */
@ProgramName("xfss")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
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
    protected XPath select;

    /**
     * @option -O =pretty|compact
     */
    protected OutputFormat outputFormat = OutputFormat.createPrettyPrint();

    public void selectFromRoot(IFile root)
            throws Exception {
        XmlfsDocument doc = new XmlfsDocument(root);
        List<?> list = select.selectNodes(doc);
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
        new XmlfsSelector().execute(args);
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        for (IFile file : expandFiles(args))
            selectFromRoot(file);
    }

}
