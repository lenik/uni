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
    c1904.db \
    mia.db \
    ooojjj.db \
    pfpf.db \
    selfbuy.db \
    xor.db \
    国家地理.db \
    花骨朵.db \
    集英.db \
    集英社.db \
    空山.db \
    浅水湾.db \
    童话.db

nets_DATA = \
    lan.db \
    lo.db \
    zoo.db \
    zoo-i.db \
    zoo-pi.db \
    cn1.db \
    cn2.db \
    sg1.db

lan.db: lan.db.php
	php $< >$@

CLEANFILES = \
    ${bindconf_DATA} \
    ${lenik_DATA} \
    ${inc_DATA} \
    ${nets_DATA}

dist-hook:
	cp ${srcdir}/*.in ${distdir}

install-data-hook:
	ln -sn bodz.db  ${DESTDIR}${lenikdir}/ebja.db
	ln -sn bodz.db  ${DESTDIR}${lenikdir}/lenik.db
	ln -sn bodz.db  ${DESTDIR}${lenikdir}/topcroak.db
	ln -sn zoo.db   ${DESTDIR}${netsdir}/j.db

# Eval inst:*[ipw] using substcat
%: %.in Makefile
	$(do_subst) <$< | m4 ip.conf - >$@
