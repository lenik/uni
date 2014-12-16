include libauto/project.am

presetdir=${pkgdatadir}/preset.d

do_subst += \
    -e 's,@presetdir@,${presetdir},g'

dist_bin_SCRIPTS = \
    mencoder-preset

dist_preset_DATA = \
    preset.d/m6 \
    preset.d/mpeg4

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

