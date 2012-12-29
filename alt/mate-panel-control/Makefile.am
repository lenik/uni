include libauto/coolauto.am

bin_PROGRAMS = \
    mate-panel-control

mate_panel_control_LDADD = \
    ${X_LIBS}

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
