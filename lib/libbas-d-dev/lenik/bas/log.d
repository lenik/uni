module lenik.bas.log;

import std.algorithm : min;
import std.datetime;
import std.stdio;
import std.string;

import lenik.bas.esc.csisgr;

enum LogOption {
    /* Useful for each logging */
    StrError  = 0x00000001,             /* append the errno string */
    CR        = 0x00000020,             /* end by CR instead of NL */
    /* Useful as common defaults */
    Color     = 0x00010000,             /* enable ANSI color fx */
    LevelName = 0x00020000,             /* print the level name */
    Date      = 0x00100000,             /* print the logging date */
    Time      = 0x00200000,             /* print the logging time */
    DateTime  = Date | Time,
}

int maxLogLevel = 1;                    /* global max log level */

class Logger {

    private string ident;
    private int logLevel;
    private int option;
    private File dev;
    private int indent;
    private string tab = "  ";
    
    this(string ident,
         int logLevel,
         int option = LogOption.LevelName | LogOption.Color,
         File dev = stderr) {
        this.ident = ident;
        this.logLevel = logLevel;
        this.option = option;
        this.dev = dev;
    }

    void enter() {
        indent++;
    }

    void leave() {
        if (indent > 0)
            indent--;
        else
            err("Nothing to leave()!");
    }
    
    /* format: "2000-01-01 01:12:34 [ident] ERROR ... strerror" */

    void _logf(S...)(int level, int option, string format, S args) {
        _logf!(S)(level, option, cast(Throwable) null, format, args);
    }
    
    void _logf(S...)(int level, int option, Throwable e, string format, S args) {
        if (level > logLevel || level > maxLogLevel)
            return;
        
        option |= this.option;
        
        bool dirty = false;
        bool color = (option & LogOption.Color) != 0;
        
        if (option & LogOption.DateTime) {
            SysTime t = Clock.currTime();
            if (color) dev.write(CSI ~ FG_DARKGRAY ~ SGR);

            if (option & LogOption.Date) {
                dev.writef("%04d-%02d-%02d", t.year, t.month, t.day);
            }
            
            if (option & LogOption.Time) {
                if (option & LogOption.Date)
                    dev.write(' ');
                dev.writef("%02d:%02d:%02d", t.hour, t.minute, t.second);
            }

            if (color) dev.write(CSI ~ RESET ~ SGR);
            dirty = true;
        }

        /* ident */
        if (ident !is null && ident.length > 0) {
            if (dirty) dev.write(' ');

            if (color) dev.write(CSI ~ FG_WHITE ~ BG_BROWN ~ SGR);

            dev.write('[');
            dev.write(ident);
            dev.write(']');
            
            if (color) dev.write(CSI ~ RESET ~ SGR);
            dirty = true;
        }
    
        /* level */
        string level_name = "_Level";
        string level_color = "";

        if (color || option & LogOption.LevelName)
            switch (level) {
            case -4:
                level_name = "EMERG";
                level_color = CSI ~ BLINK ~ FG_WHITE ~ BG_RED ~ SGR;
                break;
            case -3:
                level_name = "ALERT";
                level_color = CSI ~ BLINK ~ FG_WHITE ~ BG_RED ~ SGR;
                break;
            case -2:
                level_name = "FATAL";   /* CRIT */
                level_color = CSI ~ FG_WHITE ~ BG_RED ~ SGR;
                break;
            case -1:
                level_name = "ERROR";
                level_color = CSI ~ FG_LIGHTRED ~ SGR;
                break;
            case 0:
                level_name = "WARN";
                level_color = CSI ~ FG_LIGHTMAGENTA ~ SGR;
                break;
            case 1:
                level_name = "NOTICE";
                level_color = CSI ~ BOLD ~ SGR;
                break;
            case 2:
                level_name = "INFO";
                level_color = CSI ~ FG_DARKGRAY ~ SGR;
                break;
            case 3:
                level_name = "DEBUG";
                level_color = CSI ~ FG_GRAY ~ _DARK ~ SGR;
                break;
            case 4:
                level_name = "TRACE";
                level_color = CSI ~ FG_GRAY ~ _DARK ~ SGR;
                break;
            default:
                if (level < -3)
                    level_name = "FATAL";
                if (level > 3)
                    level_name = "DEBUG";
            }

        if (dirty) dev.write(' ');
        for (int i = 0; i < indent; i++)
            dev.write(tab);
        
        if (color) dev.write(level_color);

        if (option & LogOption.LevelName) {
            dev.write(level_name);
            dev.write(':');
            dirty = true;
        }

        /* message */
        if (dirty) dev.write(' ');
        dev.writef(format, args);
    
        if (color) dev.write(CSI ~ RESET ~ SGR);

        if (e !is null) {
            dev.write(' ');
            if (color) dev.write(CSI ~ UNDERLINE ~ FG_RED ~ SGR);
            dev.write(e.msg);
            if (color) dev.write(CSI ~ RESET ~ SGR);
        }
        
        if (option & LogOption.CR)
            dev.write('\r');
        else
            dev.writeln();
    }
    
    void emerg(S...)(Throwable e, string fmt, S args) { _logf!(S)(-4, 0, e, fmt, args); }
    void alert(S...)(Throwable e, string fmt, S args) { _logf!(S)(-3, 0, e, fmt, args); }
    void  crit(S...)(Throwable e, string fmt, S args) { _logf!(S)(-2, 0, e, fmt, args); }
    void   err(S...)(Throwable e, string fmt, S args) { _logf!(S)(-1, 0, e, fmt, args); }
    void  warn(S...)(Throwable e, string fmt, S args) { _logf!(S)( 0, 0, e, fmt, args); }

    void  emerg(S...)(string fmt, S args) { _logf!(S)(-4, 0, fmt, args); }
    void  alert(S...)(string fmt, S args) { _logf!(S)(-3, 0, fmt, args); }
    void   crit(S...)(string fmt, S args) { _logf!(S)(-2, 0, fmt, args); }
    void    err(S...)(string fmt, S args) { _logf!(S)(-1, 0, fmt, args); }
    void   warn(S...)(string fmt, S args) { _logf!(S)( 0, 0, fmt, args); }
    void notice(S...)(string fmt, S args) { _logf!(S)( 1, 0, fmt, args); }
    void   info(S...)(string fmt, S args) { _logf!(S)( 2, 0, fmt, args); }
    void    log(S...)(string fmt, S args) { _logf!(S)( 3, 0, fmt, args); }
    void    dbg(S...)(string fmt, S args) { _logf!(S)( 4, 0, fmt, args); }
    void  trace(S...)(string fmt, S args) { _logf!(S)( 5, 0, fmt, args); }

}

debug const int defaultLogLevel = 10;
 else const int defaultLogLevel = 3;

template Log(string ident = null,
             int logLevel = defaultLogLevel,
             int option = LogOption.LevelName | LogOption.Color) {
    Logger log;
    static this() {
        log = new Logger(ident, logLevel, option);
    }
}
