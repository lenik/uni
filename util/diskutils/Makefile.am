include libauto/project.am

dist_bin_SCRIPTS = \
    bin2iso \
    blockdev-info \
    mkhdd \
    partoffset \
    readmbr \
    rootfs \
    writembr

dist_pkgdata_DATA = \
    x86_boot_sector

dist_man_MANS = \
    bin2iso.1 \
    blockdev-info.8 \
    mkhdd.8 \
    partoffset.8 \
    readmbr.8 \
    rootfs.8 \
    writembr.8

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
