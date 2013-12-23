include libauto/project.am

dist_bin_SCRIPTS = \
    gcc-eval \
    gcc-which

dist_aliases_DATA = \
    bash_aliases.d/gcc-enh.sh

dist_completion_DATA = \
    bash_completion.d/gcc-enh.sh

dist_man_MANS = \
    gcc-eval.1 \
    gcc-which.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_bin_SCRIPTS} \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
