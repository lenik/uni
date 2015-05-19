include ../libauto/common.am

do_subst += \
	-e 's,[@]bindconfdir[@],$(bindconfdir),g' \
	-e 's,[@]dbdir[@],$(dbdir),g' \
	-e 's,[@]incdir[@],$(incdir),g' \
	-e 's,[@]lenikdir[@],$(lenikdir),g' \
	-e 's,[@]netsdir[@],$(netsdir),g'

bindconfdir = ${sysconfdir}/bind
dbdir       = ${bindconfdir}
incdir      = ${dbdir}/inc
lenikdir    = ${dbdir}/lenik
netsdir     = ${dbdir}/net

bindconf_DATA = \
    cls.conf

inc_DATA = \
	auto.db \
    default.db \
    dns-usage.db \
    google.db \
    hotmail.db

lenik_DATA = \
    1na.db \
    99jsj.db \
    bela.db \
    bodz.db \
    ebja.db \
    mia.db \
    ooojjj.db \
    pfpf.db \
    topcroak.db \
    xor.db \
    国家地理.db \
    空山.db \
    童话.db \
    集英.db \
    集英社.db

nets_DATA = \
    lo.db \
    p.db \
    zoo.db

CLEANFILES = \
    ${bindconf_DATA} \
    ${inc_DATA} \
    ${lenik_DATA} \
    ${nets_DATA}

dist-hook:
	cp ${srcdir}/*.in ${distdir}

%: %.in Makefile
	$(do_subst) <$< | substcat >$@
