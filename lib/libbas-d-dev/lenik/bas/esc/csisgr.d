module lenik.bas.esc.csisgr;

/* see console_codes (4) */
string CSI             = "\x1b[";       /* control seq introducer */
string SGR             = "m";           /* select graphic rendition */
string RESET           = ";0";          /* reset attributes to their defaults */
string BOLD            = ";1";          /* increased intensity */
string FAINT           = ";2";          /* decreased intensity, half-bright */
string _DARK           = ";2"; // FAINT;
string ITALIC          = ";3";          /* sometimes treated as inverse */
string UNDERLINE       = ";4";          /* Use ESC ] to set the color*/
string BLINK           = ";5";          /* 150- per minute */
string BLINK_RAPID     = ";6";          /* (MSDOS) 150+ per minute */
string IMG_NEGATIVE    = ";7";          /* swap fg/bg */
string CONCEAL         = ";8";          /* not widely supported */
string CROSSED_OUT     = ";9";          /* marked for deletion */
string FONT_PRIMARY    = ";10";         /* FONT_ "0";... "9";*/
string FONT_ALT1       = ";11";         /* FONT_ "0";... "9";*/
string FONT_ALT2       = ";12";         /* FONT_ "0";... "9";*/
string FONT_ALT3       = ";13";         /* FONT_ "0";... "9";*/
string FONT_ALT4       = ";14";         /* FONT_ "0";... "9";*/
string FONT_ALT5       = ";15";         /* FONT_ "0";... "9";*/
string FONT_ALT6       = ";16";         /* FONT_ "0";... "9";*/
string FONT_ALT7       = ";17";         /* FONT_ "0";... "9";*/
string FONT_ALT8       = ";18";         /* FONT_ "0";... "9";*/
string FONT_ALT9       = ";19";         /* FONT_ "0";... "9";*/
string FRAKTUR         = ";20";         /* hardly ever supported */
string NO_BOLD         = ";21";         /* or: double underline */
string NO_COLOR        = ";22";         /* normal color or intensity */
string NO_ITALIC       = ";23";         /* not italic, not fraktur */
string NO_UNERLINE     = ";24";         /* not singly or doubly underlined */
string NO_BLINK        = ";25";
string IMG_POSITIVE    = ";27";
string REVEAL          = ";28";         /* conceal off */
string NO_CROSSED_OUT  = ";29";
string FG_BLACK        = ";30";
string FG_RED          = ";31";
string FG_GREEN        = ";32";
string FG_BROWN        = ";33";
string FG_BLUE         = ";34";
string FG_MAGENTA      = ";35";
string FG_CYAN         = ";36";
string FG_GRAY         = ";37";
string FG_DARKGRAY     = ";1;30";
string FG_LIGHTRED     = ";1;31";
string FG_LIGHTGREEN   = ";1;32";
string FG_YELLOW       = ";1;33";
string FG_LIGHTBLUE    = ";1;34";
string FG_LIGHTMAGENTA = ";1;35";
string FG_LIGHTCYAN    = ";1;36";
string FG_WHITE        = ";1;37";
string FG_WITH_UL      = ";38";         /* default fg w/i underscore */
string FG_DEFAULT      = ";39";         /* default fg w/o underscore */
string BG_BLACK        = ";40";
string BG_RED          = ";41";
string BG_GREEN        = ";42";
string BG_BROWN        = ";43";
string BG_BLUE         = ";44";
string BG_MAGENTA      = ";45";
string BG_CYAN         = ";46";
string BG_GRAY         = ";47";
string BG_DEFAULT      = ";49";
string FRAMED          = ";51";
string ENCIRCLED       = ";52";
string OVERLINED       = ";53";
string NO_FRAME        = ";54";         /* not framed or encircled */
string NO_OVERLINE     = ";55";         /* not overlined */

/* existence check: /usr/share/terminfo/x/xterm-256color */
string X_FG_256        = ";38;5";       /* set xterm-256 text color */
string X_BG_256        = ";48;5";       /* set xterm-256 background color */
