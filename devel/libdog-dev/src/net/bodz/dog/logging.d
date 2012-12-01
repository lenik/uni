module net.bodz.dog.logging;

import std.stdio;
import std.string;

int loglevel = 1;

void _log(File dev, int level, string[] mesgs ...) {
    if (level <= loglevel) {
        for (int i = 0; i < mesgs.length; i++) {
            auto mesg = mesgs[i];
            if (i != 0)
                dev.write(' ');
            dev.write(mesg);
        }
        dev.writeln();
    }
}

void _log(int level, string[] mesgs ...) {
    _log(stderr, level, mesgs);
}

void _logx(int level, string[] mesgs ...) {
    _log(stderr, level, mesgs);
}

void _fatal(string[] mesgs ...) { _logx(-3, mesgs); }
void _error(string[] mesgs ...) { _logx(-2, mesgs); }
void _warn(string[] mesgs ...) { _logx(-1, mesgs); }
void _info(string[] mesgs ...) { _log(0, mesgs); }

void _log1(string[] mesgs ...) { _log(1, mesgs); }
void _log2(string[] mesgs ...) { _log(2, mesgs); }
void _log3(string[] mesgs ...) { _log(3, mesgs); }
