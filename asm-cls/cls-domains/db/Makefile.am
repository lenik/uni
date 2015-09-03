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
    instances.db \
    soa.db \
    mail-google.db \
    mail-hotmail.db \
    mail-qq.db

lenik_DATA = \
    1na.db \
    99jsj.db \
    bela.db \
    bodz.db \
    ebja.db \
    lenik.db \
    mia.db \
    ooojjj.db \
    pfpf.db \
    topcroak.db \
    xor.db \
    国家地理.db \
    花骨朵.db \
    集英.db \
    集英社.db \
    空山.db \
    童话.db

nets_DATA = \
    lo.db \
    p.db \
    zoo.db \
    zoo-i.db \
    zoo-pi.db

CLEANFILES = \
    ${bindconf_DATA} \
    ${lenik_DATA} \
    ${inc_DATA} \
    ${nets_DATA}

dist-hook:
	cp ${srcdir}/*.in ${distdir}

# Eval inst:*[ipw] using substcat
%: %.in Makefile
	$(do_subst) <$< | substcat >$@
