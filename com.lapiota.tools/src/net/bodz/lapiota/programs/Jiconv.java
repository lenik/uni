package net.bodz.lapiota.programs;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Set;

import net.bodz.bas.cli.BatchProcessCLI;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.util.Doc;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.cli.util.Version;
import net.bodz.bas.functors.lang.ControlBreak;
import net.bodz.bas.io.Files;

@Doc("batch iconv written in java, JUN 2004")
@Version( { 0, 1 })
@RcsKeywords(id = "$Id: Rcs.java 784 2008-01-15 10:53:24Z lenik $")
public class Jiconv extends BatchProcessCLI {

    @Option(alias = "b", vnam = "value", doc = "BOM detect")
    protected boolean bomDetect = true;

    @Option(alias = "l", doc = "list available charsets")
    protected void listCharsets() {
        Map<String, Charset> charsets = Charset.availableCharsets();
        for (Map.Entry<String, Charset> e : charsets.entrySet()) {
            Charset charset = e.getValue();
            _stdout.printf("%s: ", e.getKey());
            if (!charset.displayName().equals(e.getKey()))
                _stdout.print(charset.displayName());

            if (_verbose > 1) {
                CharsetDecoder dec = charset.newDecoder();
                float avgcpb = dec.averageCharsPerByte();
                float maxcpb = dec.maxCharsPerByte();
                _stdout.printf(" dec(%.2f/%.2f)", maxcpb, avgcpb);
                if (charset.canEncode()) {
                    CharsetEncoder enc = charset.newEncoder();
                    float avgbpc = enc.averageBytesPerChar();
                    float maxbpc = enc.maxBytesPerChar();
                    _stdout.printf(" enc(%.2f/%.2f)", maxbpc, avgbpc);
                }
            }

            _stdout.println();

            Set<String> aliases = charset.aliases();
            if (!aliases.isEmpty()) {
                _stdout.print("   ");
                for (String alias : charset.aliases()) {
                    _stdout.print(' ');
                    _stdout.print(alias);
                }
                _stdout.println();
            }
        }
        throw new ControlBreak();
    }

    // private static Charset CHARSET_L1 = Charset.forName("ISO-8859-1");
    private static Charset CHARSET_UTF8     = Charset.forName("UTF-8");
    private static Charset CHARSET_UTF16_LE = Charset.forName("UTF-16LE");
    private static Charset CHARSET_UTF16_BE = Charset.forName("UTF-16BE");

    @Override
    protected int process(File in, File out) throws Throwable {
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

        _sig1("iconv", in, "(", srcenc, ") -> ", out, "(", dstenc, ")");

        String decoded = new String(src, srcenc);
        byte[] dst = decoded.getBytes(dstenc);
        Files.write(out, dst);

        return PROCESS_EDIT;
    }

    @Override
    protected int _cliflags() {
        return super._cliflags() & ~CLI_AUTOSTDIN;
    }

    public static void main(String[] args) throws Throwable {
        new Jiconv().climain(args);
    }
}
