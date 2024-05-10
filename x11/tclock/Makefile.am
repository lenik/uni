include libauto/project.am

bin_PROGRAMS = \
    tclock

AM_CPPFLAGS = ${X11_CFLAGS} ${XT_CFLAGS} ${XFT_CFLAGS}

dist_tclock_SOURCES = tclock.c
tclock_LDADD = -lm ${X11_LIBS} ${XT_LIBS} ${XFT_LIBS}

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

#tclock: tclock.c
#	gcc $< -o $@ -lm `pkg-config --cflags --libs x11,xt,xft,freetype2`

auto:
	onchange tclock.c make replace

replace: tclock
	-killall tclock
	./tclock &

