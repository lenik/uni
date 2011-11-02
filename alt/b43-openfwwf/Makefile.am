include libauto/coolauto.am

firmwaredir = /lib/firmware
b43dir      = ${firmwaredir}/b43

b43_DATA = \
    openfwwf-5.2/b0g0bsinitvals5.fw \
    openfwwf-5.2/b0g0initvals5.fw \
    openfwwf-5.2/ucode5.fw

setup_SCRIPTS = \
    postinst \
    prerm

EXTRA_DIST = \
    postinst.in \
    prerm.in

CLEANFILES = ${setup_SCRIPTS}
