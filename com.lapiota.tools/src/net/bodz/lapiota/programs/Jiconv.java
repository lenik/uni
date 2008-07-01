package net.bodz.lapiota.programs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.freejava.err.IdentifiedException;
import net.sf.freejava.util.Files;

public class ReEncoding implements FilenameFilter {

    private String       inputEncoding        = "ISO-8859-1";
    private Charset      inputDecoder;

    private String       outputEncoding       = "UTF-8";
    private Charset      outputEncoder;

    private boolean      unicodeAutoRecognize = true;

    private boolean      caseSensitive        = false;
    private boolean      recursive            = false;

    private Pattern      inclusive            = null;
    private Pattern      exclusive            = null;

    private Set<Pattern> patterns             = new HashSet<Pattern>();

    public void process(File f) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File fo = new File(f.getName() + outputEncoding);
        byte[] inputBytes;
        String inputString;
        byte[] outputBytes;
        String inputEncoding = this.inputEncoding;

        try {
            fis = new FileInputStream(f);

            inputBytes = new byte[(int) f.length()];
            fis.read(inputBytes);
            fis.close();

            if (unicodeAutoRecognize) {
                // UTF-8 EF BB BF
                // UTF-16LE FF FE "Last is FE"
                // UTF-16BE FE FF "Begin is FE"
                if (inputBytes[0] == 0xEF && inputBytes[1] == 0xBB
                        && inputBytes[2] == 0xBF)
                    inputEncoding = "UTF-8";
                else if (inputBytes[0] == 0xFF && inputBytes[1] == 0xFE)
                    inputEncoding = "UTF-16LE";
                else if (inputBytes[0] == 0xFE && inputBytes[1] == 0xFF) {
                    inputEncoding = "UTF-16BE";
                }
            }

            System.out.println("[INFO] Translate from " + inputEncoding
                    + " to " + outputEncoding);
            inputString = new String(inputBytes, inputEncoding);
            outputBytes = inputString.getBytes(outputEncoding);

            fos = new FileOutputStream(fo);
            fos.write(outputBytes);
            fos.close();

            f.delete();
            fo.renameTo(f);
        } catch (IOException e) {
            System.err.println("[ERR] " + e.getMessage());
        } catch (UnsupportedCharsetException e) {
            System.err.println("[ERR] " + e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    /**
     * Not used.
     */
    public boolean accept(File dir, String name) {
        File sub = new File(dir, name);
        if (sub.isDirectory()) {
            if (!recursive)
                return false;
        }
        Iterator<Pattern> itPats = patterns.iterator();
        while (itPats.hasNext()) {
            Pattern pattern = itPats.next();
            if (!caseSensitive)
                name = name.toLowerCase();
            if (pattern.matcher(name).matches())
                return true;
        }
        return false;
    }

    public boolean accept(File file) {
        String name = file.getName();
        Iterator<Pattern> itPats = patterns.iterator();
        while (itPats.hasNext()) {
            Pattern pattern = itPats.next();
            if (pattern.matcher(name).matches())
                return true;
        }
        return false;
    }

    public void run(File containerFolder) {
        File[] files = containerFolder.listFiles();

        for (int iFile = 0; iFile < files.length; iFile++) {
            File f = files[iFile];

            if (f.isDirectory()) {
                if (recursive || accept(f)) {
                    // System.out.println("[DIR] " + f.getPath());
                    run(f);
                }
            }
            if (f.isFile()) {
                if (accept(f)) {
                    System.out.println("[FILE] " + f.getPath());
                    process(f);
                }
            }
            // otherwise, skip.
        }
    }

    public void run() {
        File currentFolder = new File(".");
        run(currentFolder);
    }

    public static boolean starts(String string, String full) {
        if (string == full)
            return true;
        if (string == null || full == null)
            return false;
        if (full.startsWith("--")) {
            if (string.length() < 2)
                return false;
            if (string.equals(full.substring(1, 3)))
                return true;
            if (string.length() < 3)
                return false;
            if (full.startsWith(string))
                return true;
            return false;
        }
        return full.startsWith(string);
    }

    public Pattern patternDosToJava(String dospat) {
        while (dospat.startsWith("\'"))
            dospat = dospat.substring(1);

        int len = dospat.length();

        StringBuffer regpat = new StringBuffer();

        for (int i = 0; i < len; i++) {
            char c = dospat.charAt(i);
            switch (c) {
            case '*':
            case '?':
                regpat.append("." + c);
                break;
            case '{':
            case '}':
            case '[':
            case ']':
            case '(':
            case ')':
            case '+':
            case '^':
            case '$':
                regpat.append("\\" + c);
                break;
            case '\\':
                regpat.append(dospat.charAt(++i));
                break;
            default:
                regpat.append(c);
            }
        }

        return Pattern.compile(regpat.toString(), caseSensitive ? 0
                : Pattern.CASE_INSENSITIVE);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            help();
            return;
        }

        ReEncoding program = new ReEncoding();

        for (int i = 0; i < args.length; i++) {
            // System.out.print("["+args[i]+"]");
            if (starts(args[i], "--help")) {
                help();
                return;
            }
            if (starts(args[i], "--version")) {
                version();
                return;
            }
            if (starts(args[i], "--recursive"))
                program.recursive = true;
            else if (starts(args[i], "--inclusive")) {
                if (program.exclusive != null) {
                    System.out
                            .println("Warning: option --exclusive are ignored");
                    program.exclusive = null;
                }
                program.inclusive = program.patternDosToJava(args[++i]);
            } else if (starts(args[i], "--exclusive")) {
                if (program.inclusive != null) {
                    System.out
                            .println("Warning: option --inclusive are ignored");
                    program.inclusive = null;
                }
                program.exclusive = program.patternDosToJava(args[++i]);
            } else if (starts(args[i], "--source"))
                program.inputEncoding = args[++i];
            else if (starts(args[i], "--target"))
                program.outputEncoding = args[++i];
            else if (starts(args[i], "--no-byteorder-detect"))
                program.unicodeAutoRecognize = false;
            else if (starts(args[i], "--case-sensitive"))
                program.caseSensitive = true;
            else
                program.patterns.add(program.patternDosToJava(args[i]));
        }

        program.run();
    }

    public static void help() {
        System.out.print("ReEncoding: Change encodings for text files\n"
                + "Available charsets: \n");
        Map<?, ?> charsets = Charset.availableCharsets();
        Iterator<?> charsetNames = charsets.keySet().iterator();
        while (charsetNames.hasNext()) {
            String charsetName = (String) charsetNames.next();
            System.out.print("	" + charsetName);
            Charset charset = (Charset) charsets.get(charsetName);

            Iterator<?> aliases = charset.aliases().iterator();
            while (aliases.hasNext()) {
                String aliasName = (String) aliases.next();
                System.out.print(", " + aliasName);
            }
            System.out.println();
        }

        String helpdoc;
        try {
            helpdoc = Files.readAll(ReEncoding.class, "help", "utf-8");
        } catch (IOException e) {
            throw new IdentifiedException(e.getMessage(), e);
        }
        System.out.print(helpdoc);
    }

    public static void version() {
        System.out.println("ReEncoding  version 1.1\n"
                + "Written by Danci.Z, JUN 2004\n"
                + "This program is distributed under GPL license. ");
    }

    static final Charset CHARSET_L1       = Charset.forName("ISO-8859-1");
    static final Charset CHARSET_UTF8     = Charset.forName("UTF-8");
    static final Charset CHARSET_UTF16_LE = Charset.forName("UTF-16LE");
    static final Charset CHARSET_UTF16_BE = Charset.forName("UTF-16BE");
}
