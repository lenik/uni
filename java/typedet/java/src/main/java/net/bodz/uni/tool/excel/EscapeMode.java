package net.bodz.uni.tool.excel;

public enum EscapeMode {

    C,

    CSV,

    ;

    public String escape(String s) {
        if (s == null)
            return null;
        switch (this) {
        case C:
            s = s.replace("\\", "\\\\");
            s = s.replace("\"", "\\\"");
            s = s.replace("\n", "\\n");
            break;

        case CSV:
            s = s.replace("\"", "\"\"");
            break;
        }
        return s;
    }

}
