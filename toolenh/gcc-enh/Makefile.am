include libauto/coolauto.am

dist_bin_SCRIPTS = \
    gcc-eval

dist_aliases_DATA = \
    bash_aliases.d/gcc-enh.sh

dist_bashcmpl_DATA = \
    bash_completion.d/gcc-enh.sh

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${setup_SCRIPTS}
