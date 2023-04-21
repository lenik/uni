include ../libauto/common.am

do_subst += \
	-e 's,[@]bindconfdir[@],$(bindconfdir),g' \
	-e 's,[@]dbdir[@],$(dbdir),g' \
	-e 's,[@]incdir[@],$(incdir),g' \
	-e 's,[@]lenikdir[@],$(lenikdir),g' \
	-e 's,[@]netsdir[@],$(netsdir),g' \
	-e 's/IP__CN1/121.43.146.153/g' \
	-e 's/IP__SG1/13.215.235.166/g' \
	-e 's/IP__KR1/43.201.144.10/g' \
	-e 's/IP__US1/13.52.54.88/g' \
	-e 's/IP6_CN1/2408:4005:30e:e700::100/g' \
	-e 's/IP6_SG1/2406:da18:6a8:f200::100/g' \
	-e 's/IP6_KR1/2406:da12:1ac:1202::100/g' \
	-e 's/IP6_US1/2600:1f1c:834:9c03::100/g'

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
    bossnova.db \
    c1904.db \
    ebja.db \
    its-it.db \
    lenik.db \
    lilyfond.com.db \
    lilyfond.org.db \
    mia.db \
    mine.db \
    node-z.db \
    ooojjj.db \
    peegoo.db \
    pfpf.db \
    rainfield.db \
    selfbuy.db \
    space-x.db \
    topcroak.db \
    xor.db \
    youmute.db \
    国家地理.db \
    小睡莲.db \
    浅水湾.db \
    空山.db \
    童话.db \
    花骨朵.db \
    集英.db \
    集英社.db

nets_DATA = \
    lan.db \
    lo.db \
    zoo.db \
    zoo_j.db \
    zoo-i.db \
    zoo-pi.db \
    cn1.db \
    cn2.db \
    sg1.db

lan.db: lan.db.php
	php $< >$@

CLEANFILES = \
    cls.conf \
    $(inc_DATA) \
    $(lenik_DATA) \
    $(nets_DATA)

dist-hook:
	cp ${srcdir}/*.in ${distdir}

install-data-hook:

# Eval inst:*[ipw] using substcat
%: %.m4
	cat $< | m4 ip.conf - >$@

%: %.in Makefile
	$(do_subst) <$< >$@
