include libauto/project.am

dist_alias_DATA = \
    netcaps.sh

dist_bin_SCRIPTS = \
    tcpflow0

bin_SCRIPTS = \
    nc-url \
    nc-passwd

#dist_nc_url_SOURCES = nc-url.d
#dist_nc_passwd_SOURCES = nc-passwd.d

%: %.d
	gdc -O3 -frelease \
        -I/usr/include/d2/site \
        -fignore-unknown-pragmas \
        -fproperty \
        -o $@ $< \
        -lbas-d

dist_man_MANS = \
    nc-url.1 \
    nc-passwd.1

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_man_MANS} \
    ${dist_setup_SCRIPTS}
