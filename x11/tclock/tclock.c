#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xft/Xft.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <limits.h>
#include <math.h>
#include <time.h>
#include <sys/time.h>

#define MWM_HINTS_DECORATIONS (1L << 1)
#define MWM_DECOR_ALL (1L << 0) /* [v] */

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

void removeBorder(Display *display,
                  Window window)
{
    MotifWmHints *hints;
    Atom property;
    int nelements;

    hints = get_motif_wm_hints(display, window);
    if (hints == NULL)
    {
        hints = calloc(1, sizeof(*hints));
        hints->decorations = 0;
    }

    hints->flags |= MWM_HINTS_DECORATIONS;
    hints->decorations = 0;
    property = XInternAtom(display, "_MOTIF_WM_HINTS", False);
    nelements = sizeof(*hints) / sizeof(long);

    XChangeProperty(display, window, property, property, 32, PropModeReplace,
                    (unsigned char *)hints, nelements);

    free(hints);
}

int clock_margin_h = 80;
int clock_margin_v = 80;
int clock_radius = 100;

Colormap colormap;
XColor blue_color;
XColor red_color;
XftDraw *draw = NULL;
XftFont *font = NULL;

void drawClock(Display *display, Window window, int hour, int minute, int second, int usec)
{
    int screen = DefaultScreen(display);

    int clock_center_x = clock_radius + clock_margin_h;
    int clock_center_y = clock_radius + clock_margin_v;

    float hour_len = 0.4;
    float minute_len = 0.6;
    float second_len = 0.8;

    XGCValues values;
    GC gc = XCreateGC(display, window, 0, &values);

    // Draw clock face
    XSetForeground(display, gc, blue_color.pixel);
    XFillArc(display, window, gc, clock_center_x - clock_radius, clock_center_y - clock_radius,
             2 * clock_radius, 2 * clock_radius, 0, 360 * 64);

    double hour_angle = (-(hour + minute / 60.0) / 12 + 1 / 4.0) * M_PI * 2;
    double minute_angle = (-minute / 60.0 + 1 / 4.0) * M_PI * 2;
    double second_angle = (-(second + usec / 1000000.0) / 60.0 + 1 / 4.0) * M_PI * 2;

    // Draw hour hand (white color)
    XSetForeground(display, gc, WhitePixel(display, screen));
    XDrawLine(display, window, gc, clock_center_x, clock_center_y,
              clock_center_x + hour_len * clock_radius * cos(hour_angle),
              clock_center_y - hour_len * clock_radius * sin(hour_angle));

    // Draw minute hand (white color)
    XDrawLine(display, window, gc, clock_center_x, clock_center_y,
              clock_center_x + minute_len * clock_radius * cos(minute_angle),
              clock_center_y - minute_len * clock_radius * sin(minute_angle));

    // Draw second hand (red color)
    XSetForeground(display, gc, red_color.pixel);
    XDrawLine(display, window, gc, clock_center_x, clock_center_y,
              clock_center_x + second_len * clock_radius * cos(second_angle),
              clock_center_y - second_len * clock_radius * sin(second_angle));

    // Draw numbers 1-12 around the clock face
    if (font)
    {
        // XSetFont(display, gc, font_info->fid);
        // XSetForeground(display, gc, WhitePixel(display, screen));
        XftDraw *draw = XftDrawCreate(display, window,
                                      DefaultVisual(display, screen),
                                      DefaultColormap(display, screen));

        XftColor color;
        color.color.red = 65535;
        color.color.green = 65535;
        color.color.blue = 65535;
        color.color.alpha = 65535;

        for (int i = 1; i <= 12; i++)
        {
            double angle = (-(i) / 12.0 + 1 / 4.0) * M_PI * 2;
            int x = clock_center_x + 1 * clock_radius * cos(angle) - 5;
            int y = clock_center_y - 1 * clock_radius * sin(angle) + 10;
            char number[10];
            sprintf(number, "%d", i);
            // XDrawString(display, window, gc, x, y, number, strlen(number));
            XftDrawStringUtf8(draw, &color, font, x, y, number, strlen(number));
        }
        XftDrawDestroy(draw);
    }
    else
    {
        fprintf(stderr, "Error: Unable to load font\n");
    }

    XFreeGC(display, gc);
}

void updateClock(Display *display, Window window)
{
    time_t now = time(NULL);
    struct tm *local_time = localtime(&now);

    struct timeval tvTime;
    gettimeofday(&tvTime, NULL);

    int hour = local_time->tm_hour % 12;
    int minute = local_time->tm_min;
    int second = local_time->tm_sec;

    drawClock(display, window, hour, minute, second, tvTime.tv_usec);
}

int error_logger(
    Display *display,
    XErrorEvent *event)
{
    char msg[200];
    XGetErrorText(display, event->error_code, msg, sizeof(msg));

    fprintf(stderr, "[serial %d] [type %d] error %d (%s): request %d.%d\n", //
            event->serial,
            event->type,
            event->error_code,
            msg,
            event->request_code,
            event->minor_code);
}

static int wait_fd(int fd, long usec)
{
    struct timeval tv;
    fd_set in_fds;
    FD_ZERO(&in_fds);
    FD_SET(fd, &in_fds);
    tv.tv_sec = usec / 1000000;
    tv.tv_usec = usec % 1000000;
    return select(fd + 1, &in_fds, 0, 0, &tv);
}

int XNextEventTimeout(Display *display, XEvent *event, double seconds)
{
    if (XPending(display) || wait_fd(ConnectionNumber(display), seconds))
    {
        XNextEvent(display, event);
        return 0;
    }
    else
    {
        // event->type = 0;
        return 1;
    }
}

int main()
{
    XSetErrorHandler(error_logger);

    Display *display = XOpenDisplay(NULL);
    if (display == NULL)
    {
        fprintf(stderr, "Error: Could not open display\n");
        return 1;
    }

    Window root = DefaultRootWindow(display);
    int screen = DefaultScreen(display);

    Window origFocusWindow;
    int origRevert;
    XGetInputFocus(display, &origFocusWindow, &origRevert);

    // Create a window with transparent background
    XVisualInfo vinfo;
    XMatchVisualInfo(display, screen, 32, TrueColor, &vinfo);
    XSetWindowAttributes attr;
    colormap = XCreateColormap(display, root, vinfo.visual, AllocNone);
    attr.colormap = colormap;
    attr.border_pixel = 0;
    attr.background_pixel = 0;
    // attr.override_redirect = False;

    int left = 100, top = 100;
    int width = (clock_radius + clock_margin_h) * 2;
    int height = (clock_radius + clock_margin_v) * 2;
    int border_width = 0;
    Window window = XCreateWindow(display, root, left, top, width, height, border_width,
                                  vinfo.depth, InputOutput, vinfo.visual,
                                  CWColormap | CWBorderPixel | CWBackPixel, &attr);
    // removeBorder(display, window);

    Colormap colormap = DefaultColormap(display, screen);
    XAllocNamedColor(display, colormap, "blue", &blue_color, &blue_color);
    XAllocNamedColor(display, colormap, "red", &red_color, &red_color);

    font = XftFontOpenName(display, screen, "Sans:size=10");

    // Set window background to transparent
    // XSetWindowBackground(display, window, 0);
    XClearWindow(display, window);

    XMapWindow(display, window);
    XSelectInput(display, window, ExposureMask | StructureNotifyMask);

    // XMapWindow(display, focusWindow);
    // XRaiseWindow(display, focusWindow);
    // XSelectInput(display, focusWindow, ExposureMask | KeyPressMask);
    // XSync(display, True);
    // int r = XSetInputFocus(display, focusWindow, RevertToParent, CurrentTime);

    while (1)
    {
        XEvent event;
        int timeout = XNextEventTimeout(display, &event, 100 * 1000);
        if (timeout)
        {
            updateClock(display, window);
            continue;
        }

        switch (event.type)
        {
        case ConfigureNotify:
            XConfigureEvent xce = event.xconfigure;
            if (xce.width < xce.height)
                clock_radius = xce.width / 2 - clock_margin_h;
            else
                clock_radius = xce.height / 2 - clock_margin_v;

            int font_height = clock_radius / 12;

            if (font)
            {
                XftFontClose(display, font);
                font = NULL;
            }

            char font_name[80];
            sprintf(font_name, "Sans:size=%d", font_height);
            font = XftFontOpenName(display, screen, font_name);

            updateClock(display, window);
            break;

        case Expose:
            updateClock(display, window);

            Window focusWindow;
            int revert;
            XGetInputFocus(display, &focusWindow, &revert);
            if (focusWindow == window)
            {
                XSetInputFocus(display, origFocusWindow, RevertToParent, CurrentTime);
            }
            break;

        case MapNotify:
        case ClientMessage:
            break;

        default:
            printf("other event: %d\n", event.type);
        }
    }

    XCloseDisplay(display);

    return 0;
}
