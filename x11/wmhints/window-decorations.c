/*
 * Copyright (C) 2017 Alberts Muktupāvels
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Compile with:
 * gcc toggle-decorations.c -Wall -o toggle-decorations `pkg-config --cflags --libs x11`
 *
 * Usage:
 * toggle-decorations 0x1234567
 *
 * Support me:
 * https://www.patreon.com/muktupavels
 */

#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <X11/Xlib.h>

#define MODE_GET 0
#define MODE_ADD 1
#define MODE_REMOVE 2
#define MODE_TOGGLE 3

#define MWM_HINTS_DECORATIONS   (1L << 1)
#define MWM_DECOR_ALL           (1L << 0)       /* [v] */

typedef struct
{
    unsigned long flags;
    unsigned long functions;
    unsigned long decorations;
    long input_mode;
    unsigned long status;
} MotifWmHints;

static MotifWmHints *
get_motif_wm_hints(Display *display,
                   Window window)
{
    Atom property;
    int result;
    Atom actual_type;
    int actual_format;
    unsigned long nitems;
    unsigned long bytes_after;
    unsigned char *data;

    property = XInternAtom(display, "_MOTIF_WM_HINTS", False);
    result = XGetWindowProperty(display, window, property,
                                0, LONG_MAX, False, AnyPropertyType,
                                &actual_type, &actual_format,
                                &nitems, &bytes_after, &data);

    if (result == Success && data != NULL)
    {
        size_t data_size;
        size_t max_size;
        MotifWmHints *hints;

        data_size = nitems * sizeof(long);
        max_size = sizeof(*hints);

        hints = calloc(1, max_size);

        memcpy(hints, data, data_size > max_size ? max_size : data_size);
        XFree(data);

        return hints;
    }

    return NULL;
}

static void
change_window_decorations(Display *display,
                          Window window,
                          int mode)
{
    MotifWmHints *hints;
    Atom property;
    int nelements;

    hints = get_motif_wm_hints(display, window);
    if (hints == NULL)
    {
        hints = calloc(1, sizeof(*hints));
        hints->decorations = MWM_DECOR_ALL;
    }

    if (mode == MODE_GET) {
        printf("%ld\n", hints->decorations);
        return;
    }

    hints->flags |= MWM_HINTS_DECORATIONS;

    switch (mode) {
        case MODE_ADD:
            hints->decorations = MWM_DECOR_ALL;
            break;
        case MODE_REMOVE:
            hints->decorations = 0;
            break;
        case MODE_TOGGLE:
            hints->decorations = hints->decorations == 0 ? MWM_DECOR_ALL : 0;
            break;
    }
    
    property = XInternAtom(display, "_MOTIF_WM_HINTS", False);
    nelements = sizeof(*hints) / sizeof(long);

    XChangeProperty(display, window, property, property, 32, PropModeReplace,
                    (unsigned char *)hints, nelements);

    free(hints);
}

int main(int argc,
         char *argv[])
{
    const char *bin = argv[0];
    Window window = 0;
    Display *display;
    int mode = MODE_GET;

    while (--argc > 0)
    {
        char *arg = *++argv;
        switch (*arg) {
            case 0x2d: // dash char
                if (strcmp(arg, "-a") == 0 || strcmp(arg, "--add") == 0)
                    mode = MODE_ADD;
                else if (strcmp(arg, "-s") == 0 || strcmp(arg, "--set") == 0)
                    mode = MODE_ADD;
                else if (strcmp(arg, "-r") == 0 || strcmp(arg, "--remove") == 0)
                    mode = MODE_REMOVE;
                else if (strcmp(arg, "-u") == 0 || strcmp(arg, "--unset") == 0)
                    mode = MODE_REMOVE;
                else if (strcmp(arg, "-t") == 0 || strcmp(arg, "--toggle") == 0)
                    mode = MODE_TOGGLE;
                else
                    printf("invalid option: %s\n", arg);
                break;
            default:
                sscanf(arg, "0x%lx", &window);
                if (window == 0)
                    sscanf(arg, "%lu", &window);
        }
    }

    if (window == 0)
    {
        printf("Usage: %s [-art] WINDOW-ID\n", bin);
        printf("\nExample:\n%s -t 0x1234567\n", bin);
        return 1;
    }

    display = XOpenDisplay(NULL);
    if (display == NULL)
        return 1;

    change_window_decorations(display, window, mode);
    XCloseDisplay(display);

    return 0;
}
