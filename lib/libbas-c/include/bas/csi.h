#ifndef __BAS_CSI_H
#define __BAS_CSI_H

/* see console_codes (4) */
#define CSI             "\x1b["         /* control seq introducer */
#define SGR             "m"             /* select graphic rendition */
#define RESET           ";0"            /* reset attributes to their defaults */
#define BOLD            ";1"            /* increased intensity */
#define FAINT           ";2"            /* decreased intensity, half-bright */
#define _DARK           FAINT
#define ITALIC          ";3"            /* sometimes treated as inverse */
#define UNDERLINE       ";4"            /* Use ESC ] to set the color*/
#define BLINK           ";5"            /* 150- per minute */
#define BLINK_RAPID     ";6"            /* (MSDOS) 150+ per minute */
#define IMG_NEGATIVE    ";7"            /* swap fg/bg */
#define CONCEAL         ";8"            /* not widely supported */
#define CROSSED_OUT     ";9"            /* marked for deletion */
#define FONT_PRIMARY    ";10"           /* FONT_ "0" ... "9" */
#define FONT_ALT1       ";11"           /* FONT_ "0" ... "9" */
#define FONT_ALT2       ";12"           /* FONT_ "0" ... "9" */
#define FONT_ALT3       ";13"           /* FONT_ "0" ... "9" */
#define FONT_ALT4       ";14"           /* FONT_ "0" ... "9" */
#define FONT_ALT5       ";15"           /* FONT_ "0" ... "9" */
#define FONT_ALT6       ";16"           /* FONT_ "0" ... "9" */
#define FONT_ALT7       ";17"           /* FONT_ "0" ... "9" */
#define FONT_ALT8       ";18"           /* FONT_ "0" ... "9" */
#define FONT_ALT9       ";19"           /* FONT_ "0" ... "9" */
#define FRAKTUR         ";20"           /* hardly ever supported */
#define NO_BOLD         ";21"           /* or: double underline */
#define NO_COLOR        ";22"           /* normal color or intensity */
#define NO_ITALIC       ";23"           /* not italic, not fraktur */
#define NO_UNERLINE     ";24"           /* not singly or doubly underlined */
#define NO_BLINK        ";25"
#define IMG_POSITIVE    ";27"
#define REVEAL          ";28"           /* conceal off */
#define NO_CROSSED_OUT  ";29"
#define FG_BLACK        ";30"
#define FG_RED          ";31"
#define FG_GREEN        ";32"
#define FG_BROWN        ";33"
#define FG_BLUE         ";34"
#define FG_MAGENTA      ";35"
#define FG_CYAN         ";36"
#define FG_GRAY         ";37"
#define FG_DARKGRAY     ";1;30"
#define FG_LIGHTRED     ";1;31"
#define FG_LIGHTGREEN   ";1;32"
#define FG_YELLOW       ";1;33"
#define FG_LIGHTBLUE    ";1;34"
#define FG_LIGHTMAGENTA ";1;35"
#define FG_LIGHTCYAN    ";1;36"
#define FG_WHITE        ";1;37"
#define FG_WITH_UL      ";38"           /* default fg w/i underscore */
#define FG_DEFAULT      ";39"           /* default fg w/o underscore */
#define BG_BLACK        ";40"
#define BG_RED          ";41"
#define BG_GREEN        ";42"
#define BG_BROWN        ";43"
#define BG_BLUE         ";44"
#define BG_MAGENTA      ";45"
#define BG_CYAN         ";46"
#define BG_GRAY         ";47"
#define BG_DEFAULT      ";49"
#define FRAMED          ";51"
#define ENCIRCLED       ";52"
#define OVERLINED       ";53"
#define NO_FRAME        ";54"           /* not framed or encircled */
#define NO_OVERLINE     ";55"           /* not overlined */

/* existence check: /usr/share/terminfo/x/xterm-256color */
#define X_FG_256        ";38;5"         /* set xterm-256 text color */
#define X_BG_256        ";48;5"         /* set xterm-256 background color */
#include <bas/xterm256.h>

#endif
