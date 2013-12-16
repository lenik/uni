module lenik.bas.log;

import std.datetime;
import std.file : File;
import std.stdio : stdout, stderr;
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

class Logger {

    string ident;
    int maxLevel;
    int option;
    File dev;

    this(string ident = null,
         int maxLevel = 10,
         int option = LogOption.LevelName | LogOption.Color,
         File dev = stderr) {
        this.ident = ident;
        this.maxLevel = maxLevel;
        this.option = option;
        this.dev = dev;
    }
    
    /* format: "2000-01-01 01:12:34 [ident] ERROR ... strerror" */

    void _logf(int level, S...)(int option, string format, S args) {
        _logf!(level, S)(option, cast(Error) null, format, args);
    }
    
    void _logf(int level, S...)(int option, Error e, string format, S args) {
        if (level > maxLevel)
            return;
        
        option |= this.option;
        
        bool dirty = false;
        bool color = (option & LogOption.Color) != 0;
        
        if (option & LogOption.DateTime) {
            SysTime t = Clock.currTime;
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

        /* strerror */
        /*
          if (option & LogOption.StrError) {
          dev.write(' ');
          if (color) dev.write(CSI ~ UNDERLINE ~ FG_RED ~ SGR);
          dev.write(strerror(errnum));
          if (color) dev.write(CSI ~ RESET ~ SGR);
          }
        */
    
        if (option & LogOption.CR)
            dev.write('\r');
        else
            dev.writeln();
    }
    
    void emerg(S...)(Error e, string fmt, S args) { _logf!(-4, S)(0, e, fmt, args); }
    void alert(S...)(Error e, string fmt, S args) { _logf!(-3, S)(0, e, fmt, args); }
    void crit(S...)(Error e, string fmt, S args) { _logf!(-2, S)(0, e, fmt, args); }
    void err(S...)(Error e, string fmt, S args) { _logf!(-1, S)(0, e, fmt, args); }
    void warn(S...)(Error e, string fmt, S args) { _logf!(0, S)(0, e, fmt, args); }

    void emerg(S...)(string fmt, S args) { _logf!(-4, S)(0, fmt, args); }
    void alert(S...)(string fmt, S args) { _logf!(-3, S)(0, fmt, args); }
    void crit(S...)(string fmt, S args) { _logf!(-2, S)(0, fmt, args); }
    void err(S...)(string fmt, S args) { _logf!(-1, S)(0, fmt, args); }
    void warn(S...)(string fmt, S args) { _logf!(0, S)(0, fmt, args); }
    void notice(S...)(string fmt, S args) { _logf!(1, S)(0, fmt, args); }
    void info(S...)(string fmt, S args) { _logf!(2, S)(0, fmt, args); }
    void dbg(S...)(string fmt, S args) { _logf!(3, S)(0, fmt, args); }
    void trace(S...)(string fmt, S args) { _logf!(4, S)(0, fmt, args); }
    
}

debug const int defaultLevel = 10;
 else const int defaultLevel = 1;

template Log(string ident = null,
             int maxLevel = defaultLevel,
             int option = LogOption.LevelName | LogOption.Color) {
    Logger log;
    static this() {
        log = new Logger(ident, maxLevel, option);
    }
}
