package net.bodz.lapiota.filesys;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.cli.BatchEditCLI;
import net.bodz.bas.cli.EditResult;
import net.bodz.bas.lang.ControlBreak;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.meta.program.ProgramName;
import net.bodz.lapiota.nls.CLINLS;

/**
 * Batch iconv written in java, JUN 2004
 */
@ProgramName("jiconv")
@RcsKeywords(id = "$Id$")
@Version({ 0, 1 })
public class ConvertEncoding
        extends BatchEditCLI {

    Charset inputEncoding;
    Charset outputEncoding;

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
            L.fmesg("%s: ", e.getKey()); //$NON-NLS-1$
            if (!charset.displayName().equals(e.getKey()))
                L.nmesg(charset.displayName());

            if (L.showDetail()) {
                CharsetDecoder dec = charset.newDecoder();
                float avgcpb = dec.averageCharsPerByte();
                float maxcpb = dec.maxCharsPerByte();
                L.fdetail(CLINLS.getString("ConvertEncoding.decode_ff"), maxcpb, avgcpb); //$NON-NLS-1$
                if (charset.canEncode()) {
                    CharsetEncoder enc = charset.newEncoder();
                    float avgbpc = enc.averageBytesPerChar();
                    float maxbpc = enc.maxBytesPerChar();
                    L.fdetail(CLINLS.getString("ConvertEncoding.encode_ff"), maxbpc, avgbpc); //$NON-NLS-1$
                }
            }
            L.mesg().p();

            Set<String> aliases = charset.aliases();
            if (!aliases.isEmpty()) {
                L.nmesg("   "); //$NON-NLS-1$
                for (String alias : charset.aliases()) {
                    L.nmesg(' ');
                    L.nmesg(alias);
                }
                L.mesg().p();
            }
        }
        throw new ControlBreak();
    }

    @Override
    protected void _boot()
            throws Exception {
        inputEncoding = parameters().getInputEncoding();
        outputEncoding = parameters().getOutputEncoding();
    }

    // private static Charset CHARSET_L1 = Charset.forName("ISO-8859-1");
    private static Charset CHARSET_UTF8 = Charset.forName("UTF-8"); //$NON-NLS-1$
    private static Charset CHARSET_UTF16_LE = Charset.forName("UTF-16LE"); //$NON-NLS-1$
    private static Charset CHARSET_UTF16_BE = Charset.forName("UTF-16BE"); //$NON-NLS-1$

    @Override
    protected EditResult doEdit(File in, File out)
            throws Exception {
        byte[] src = Files.readBytes(in);
        Charset srcenc = inputEncoding;
        Charset dstenc = outputEncoding;
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

        L.tmesg(CLINLS.format("ConvertEncoding.iconv_ssss", in, srcenc, out, dstenc)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        String decoded = new String(src, srcenc);
        byte[] dst = decoded.getBytes(dstenc);
        Files.write(out, dst);

        return EditResult.compareAndSave();
    }

    public static void main(String[] args)
            throws Exception {
        new ConvertEncoding().run(args);
    }

}
