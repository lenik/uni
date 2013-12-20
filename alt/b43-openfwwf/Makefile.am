include libauto/project.am

firmwaredir = /lib/firmware
b43dir      = ${firmwaredir}/b43

b43_DATA = \
    openfwwf-5.2/b0g0bsinitvals5.fw \
    openfwwf-5.2/b0g0initvals5.fw \
    openfwwf-5.2/ucode5.fw

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
