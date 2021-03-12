package net.bodz.uni.shelj.c.builtin;

import static net.bodz.uni.shelj.CLINLS.CLINLS;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.c.java.nio.Charsets;
import net.bodz.bas.err.control.ControlBreak;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BatchEditCLI;
import net.bodz.bas.program.skel.FileHandler;
import net.bodz.bas.vfs.IFile;

/**
 * Batch iconv written in java, JUN 2004
 */
@MainVersion({ 0, 1 })
@ProgramName("jiconv")
@RcsKeywords(id = "$Id$")
public class ConvertEncoding
        extends BatchEditCLI {

    static final Logger logger = LoggerFactory.getLogger(ConvertEncoding.class);

    /**
     * Detect Unicode BOM chars.
     *
     * @option -b --detect-bom =value
     */
    boolean detectBom = true;

    /**
     * List available charset names.
     *
     * @option -l --list
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

    @Override
    public void processFile(FileHandler handler)
            throws Exception {

        IFile inputFile = handler.getInputFile();
        IFile outputFile = handler.getOutputFile();

        byte[] input = handler.read().read();

        Charset srcCharset = inputFile.getPreferredCharset();
        Charset destCharset = outputFile.getPreferredCharset();

        if (detectBom) {
            // UTF-8 EF BB BF
            // UTF-16LE FF FE "Last is FE"
            // UTF-16BE FE FF "Begin is FE"
            if (input[0] == 0xEF && input[1] == 0xBB && input[2] == 0xBF)
                srcCharset = Charsets.UTF8;
            else if (input[0] == 0xFF && input[1] == 0xFE)
                srcCharset = Charsets.UTF16_LE;
            else if (input[0] == 0xFE && input[1] == 0xFF) {
                srcCharset = Charsets.UTF16_BE;
            }
        }

        logger.status(CLINLS.format("ConvertEncoding.iconv_ssss", //
                handler.getInputFile(), srcCharset, handler.getOutputFile(), destCharset));

        String text = new String(input, srcCharset);
        byte[] output = text.getBytes(destCharset);

        handler.write().write(output);
        handler.save();
    }

    public static void main(String[] args)
            throws Exception {
        new ConvertEncoding().execute(args);
    }

}
