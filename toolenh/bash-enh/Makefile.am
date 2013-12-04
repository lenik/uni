include libauto/coolauto.am

SUBDIRS = aliases.d profile.d

do_subst += -e 's,[@]libbashdir[@],$(libbashdir),g'

shsrcdir = ${bindir}

bin_PROGRAMS = \
    echo-n \
    echoln \
    echoli

dist_bin_SCRIPTS = \
    orig \
    tree-less \
    history2csv.d

dist_shsrc_SCRIPTS = \
    cd-ls \
    cd-first \
    cd..n \
    cd.x \
    cd-alias \
    d-which \
    history-histgram

dist_sudoers_DATA = \
    sudoers.d/keep-hist

dist_completion_DATA = \
    bash_completion.d/nosvn \
    bash_completion.d/paths

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}

