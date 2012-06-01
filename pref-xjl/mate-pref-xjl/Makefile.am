include libauto/coolauto.am

SUBDIRS = mateconf

do_subst += \
    -e 's,[@]x11confdir[@],$(x11confdir),g' \
    -e 's,[@]xsessiondir[@],$(xsessiondir),g' \
    -e 's,[@]mateconf2dir[@],$(mateconf2dir),g'

x11confdir  = ${sysconfdir}/X11
xsessiondir = ${x11confdir}/Xsession.d

mateconf2dir   = ${sysconfdir}/mateconf/2

bgdir       = ${datadir}/backgrounds

x11conf_DATA = \
    Xmodmap

dist_bg_DATA = \
    wallpapers/Boheme.jpg \
    wallpapers/Pointy.jpg

dist_xsession_DATA = \
    40load-xmodmap

setup_SCRIPTS = \
    postinst \
    prerm

setup_DATA = \
    path.diff

path.diff: path.0 path
	-diff -u ${srcdir}/path.0 ${srcdir}/path >$@

#path.0: ${mateconf2dir}/path
#	cp -f "$<" "$@"

EXTRA_DIST = \
    postinst.in \
    prerm.in \
    load-xmodmap.in

CLEANFILES = ${setup_SCRIPTS} \
    load-xmodmap \
    mateconf-cleanbase/%mateconf-tree.xml
