include libauto/project.am

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

dist_bashrc_DATA = \
    bashrc.d/prompt \
    bashrc.d/shlibs

dist_completion_DATA = \
    bash_completion.d/nosvn \
    bash_completion.d/paths

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

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

