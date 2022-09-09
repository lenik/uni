include libauto/project.am

driverdir=${pkgdatadir}/driver
incdir = ${includedir}/coolmake

do_subst += -e 's,@driverdir@,${driverdir},g'

dist_bin_SCRIPTS = \
    ksql \
    ksql-backup \
    ksql-restore \
    ksql-csv \
    pg_dump-insert \
    pg_dump-sort \
    pg_dump-split

dist_man_MANS = \
    ksql.1 \
    ksql-backup.1 \
    ksql-restore.1 \
    ksql-csv.1 \
    pg_dump-insert.1 \
    pg_dump-sort.1 \
    pg_dump-split.1

dist_driver_DATA = \
    pg.csd

dist_inc_DATA = \
    catsql.mf

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = ${dist_setup_SCRIPTS}
