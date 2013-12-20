include libauto/project.am

driverdir=${pkgdatadir}/driver

do_subst += -e 's,@driverdir@,${driverdir},g'

dist_bin_SCRIPTS = \
    coolsql

dist_driver_DATA = \
    pg.csd

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
