package net.bodz.lapiota.filesys;

import static net.bodz.lapiota.nls.CLINLS.CLINLS;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.cli.skel.BatchEditCLI;
import net.bodz.bas.cli.skel.CLIAccessor;
import net.bodz.bas.cli.skel.EditResult;
import net.bodz.bas.io.resource.tools.StreamReading;
import net.bodz.bas.io.resource.tools.StreamWriting;
import net.bodz.bas.lang.ControlBreak;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.bas.vfs.IFile;

/**
 * Batch iconv written in java, JUN 2004
 */
@ProgramName("jiconv")
@RcsKeywords(id = "$Id$")
@MainVersion({ 0, 1 })
public class ConvertEncoding
        extends BatchEditCLI {

    /**
     * BOM detect
     *
     * @option -b =value
     */
    boolean bomDetect = true;

    /**
     * list available charsets
     *
     * @option -l
     */
    protected void listCharsets() {
        Map<String, Charset> charsets = Charset.availableCharsets();
        for (Map.Entry<String, Charset> e : charsets.entrySet()) {
            Charset charset = e.getValue();
            logger.mesgf("%s: ", e.getKey());
            if (!charset.displayName().equals(e.getKey()))
                logger.mesg(charset.displayName());

            if (logger.isInfoEnabled()) {
                CharsetDecoder dec = charset.newDecoder();
                float avgcpb = dec.averageCharsPerByte();
                float maxcpb = dec.maxCharsPerByte();
                logger.infof(tr._(" dec(%.2f/%.2f)"), maxcpb, avgcpb);
                if (charset.canEncode()) {
                    CharsetEncoder enc = charset.newEncoder();
                    float avgbpc = enc.averageBytesPerChar();
                    float maxbpc = enc.maxBytesPerChar();
                    logger.infof(tr._(" enc(%.2f/%.2f)"), maxbpc, avgbpc);
                }
            }
            logger.mesg("");

            Set<String> aliases = charset.aliases();
            if (!aliases.isEmpty()) {
                logger.mesg("   ");
                for (String alias : charset.aliases()) {
                    logger.mesg(' ');
                    logger.mesg(alias);
                }
                logger.mesg("");
            }
        }
        throw new ControlBreak();
    }

    // private static Charset CHARSET_L1 = Charset.forName("ISO-8859-1");
    private static Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    private static Charset CHARSET_UTF16_LE = Charset.forName("UTF-16LE");
    private static Charset CHARSET_UTF16_BE = Charset.forName("UTF-16BE");

    @Override
    protected EditResult doEdit(IFile in, IFile out)
            throws Exception {
        byte[] src = in.tooling()._for(StreamReading.class).readBinaryContents();
        Charset srcenc = CLIAccessor.getInputEncoding(this);
        Charset dstenc = CLIAccessor.getOutputEncoding(this);
        if (bomDetect) {
            // UTF-8 EF BB BF
            // UTF-16LE FF FE "Last is FE"
            // UTF-16BE FE FF "Begin is FE"
            if (src[0] == 0xEF && src[1] == 0xBB && src[2] == 0xBF)
                srcenc = CHARSET_UTF8;
            else if (src[0] == 0xFF && src[1] == 0xFE)
                srcenc = CHARSET_UTF16_LE;
            else if (src[0] == 0xFE && src[1] == 0xFF) {
                srcenc = CHARSET_UTF16_BE;
            }
        }

        logger.status(CLINLS.format("ConvertEncoding.iconv_ssss", in, srcenc, out, dstenc));

        String decoded = new String(src, srcenc);
        byte[] dst = decoded.getBytes(dstenc);
        out.tooling()._for(StreamWriting.class).writeBytes(dst);

        return EditResult.compareAndSave();
    }

    public static void main(String[] args)
            throws Exception {
        new ConvertEncoding().execute(args);
    }

}
