include libauto/project.am

dcompiler = gdc \
    -O3 -frelease \
    -I/usr/include/d2/site \
    -fignore-unknown-pragmas \
    -fproperty

dist_alias_DATA = \
    netcaps.sh

dist_bin_SCRIPTS = \
    tcpflow0

bin_PROGRAMS = \
    nc-url$(EXEEXT) \
    nc-passwd$(EXEEXT)

dist_nc_url_SOURCES = nc-url.d
dist_nc_passwd_SOURCES = nc-passwd.d

nc-url$(EXEEXT): nc-url.d
	$(dcompiler) -o $@ $< -lbas-d
nc-passwd$(EXEEXT): nc-passwd.d
	$(dcompiler) -o $@ $< -lbas-d

dist_man_MANS = \
    nc-url.1 \
    nc-passwd.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
