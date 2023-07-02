package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.io.IPrintOut;

public class SBCodegen {

    IPrintOut out;
    String var;
    ILiteralCodeFormatter literalCodeFormatter = JavaLiterals.INSTANCE;

    public SBCodegen(IPrintOut out, String var) {
        this.out = out;
        this.var = var;
    }

    public SBCodegen(IPrintOut out, String var, String initCapacityCode) {
        this(out, var, true, initCapacityCode);
    }

    public SBCodegen(IPrintOut out, String var, boolean declareVar, String initCapacityCode) {
        this(out, var);
        if (declareVar) {
            out.print("StringBuilder ");
            out.print(var);
            out.print(" = new StringBuilder(");
            if (initCapacityCode != null)
                out.print(initCapacityCode);
            out.print(");\n");
        }
    }

    public void appendCode(String format, Object... args) {
        String code = String.format(format, args);
        appendCode(code);
    }

    public void appendCode(String code) {
        out.print(var);
        out.print('.');
        out.print("append(");
        out.print(code);
        out.println(");");
    }

    public void appendCodeLine(String format, Object... args) {
        String code = String.format(format, args);
        appendCodeLine(code);
    }

    public void appendCodeLine(String code) {
        out.print(var);
        out.print('.');
        out.print("append(");
        out.print(code);
        out.println(");");

        out.print(var);
        out.println(".append('\\n');");
    }

    public void append(Object obj) {
        appendCode(literalCodeFormatter.compile(obj));
    }

    public void append(String str) {
        appendCode(literalCodeFormatter.compile(str));
    }

    public void append(String format, Object... args) {
        appendCode(literalCodeFormatter.compile(format, args));
    }

    public void append(StringBuffer sb) {
        appendCode(literalCodeFormatter.compile(sb));
    }

    public void append(CharSequence s) {
        appendCode(literalCodeFormatter.compile(s));
    }

    public void append(CharSequence s, int start, int end) {
        appendCode(literalCodeFormatter.compile(s, start, end));
    }

    public void append(char[] str) {
        appendCode(literalCodeFormatter.compile(str));
    }

    public void append(char[] str, int offset, int len) {
        appendCode(literalCodeFormatter.compile(str, offset, len));
    }

    public void append(boolean b) {
        appendCode(literalCodeFormatter.compile(b));
    }

    public void append(char c) {
        appendCode(literalCodeFormatter.compile(c));
    }

    public void append(int i) {
        appendCode(literalCodeFormatter.compile(i));
    }

    public void append(long lng) {
        appendCode(literalCodeFormatter.compile(lng));
    }

    public void append(float f) {
        appendCode(literalCodeFormatter.compile(f));
    }

    public void append(double d) {
        appendCode(literalCodeFormatter.compile(d));
    }

    public void appendLine(Object obj) {
        appendCodeLine(literalCodeFormatter.compile(obj));
    }

    public void appendLine(String str) {
        appendCodeLine(literalCodeFormatter.compile(str));
    }

    public void appendLine(String format, Object... args) {
        appendCodeLine(literalCodeFormatter.compile(format, args));
    }

    public void appendLine(StringBuffer sb) {
        appendCodeLine(literalCodeFormatter.compile(sb));
    }

    public void appendLine(CharSequence s) {
        appendCodeLine(literalCodeFormatter.compile(s));
    }

    public void appendLine(CharSequence s, int start, int end) {
        appendCodeLine(literalCodeFormatter.compile(s, start, end));
    }

    public void appendLine(char[] str) {
        appendCodeLine(literalCodeFormatter.compile(str));
    }

    public void appendLine(char[] str, int offset, int len) {
        appendCodeLine(literalCodeFormatter.compile(str, offset, len));
    }

    public void appendLine(boolean b) {
        appendCodeLine(literalCodeFormatter.compile(b));
    }

    public void appendLine(char c) {
        appendCodeLine(literalCodeFormatter.compile(c));
    }

    public void appendLine(int i) {
        appendCodeLine(literalCodeFormatter.compile(i));
    }

    public void appendLine(long lng) {
        appendCodeLine(literalCodeFormatter.compile(lng));
    }

    public void appendLine(float f) {
        appendCodeLine(literalCodeFormatter.compile(f));
    }

    public void appendLine(double d) {
        appendCodeLine(literalCodeFormatter.compile(d));
    }

}
