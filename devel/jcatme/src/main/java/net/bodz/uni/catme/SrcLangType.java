package net.bodz.uni.catme;

public enum SrcLangType {

    UNIX(null, null, "#"),
    C("/*", "*/", "//"),
    SQL(null, null, "--"),
    XML("<!--", "-->", null);

    String opener;
    String closer;
    String simpleOpener;

    private SrcLangType(String opener, String closer, String simpleOpener) {
        this.opener = opener;
        this.closer = closer;
        this.simpleOpener = simpleOpener;
    }

    public static SrcLangType forExtension(String extension) {
        switch (extension.toLowerCase()) {
        case "htm":
        case "html":
        case "xhtml":
        case "xml":
        case "xsl":
        case "xslt":
            return XML;

        case "p":
        case "pl":
        case "py":
        case "r":
        case "rb":
        case "sh":
            return UNIX;

        case "cql":
        case "ddl":
        case "sql":
            return SQL;

        case "css":
        case "c":
        case "cpp":
        case "cxx":
        case "c++":
        case "h":
        case "hpp":
        case "hxx":
        case "h++":
            return C;

        default:
            return UNIX;
        }
    }

}
