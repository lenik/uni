include ../libauto/coolauto.am

do_subst2 = $(do_subst) \
    -e 's,[@]datadir[@],$(datadir),g' \
    -e 's,[@]xjl-1[@],$(xjl_1),g' \
    -e 's,[@]xjl-2[@],$(xjl_2),g' \
    -e 's,[@]xjl-3[@],$(xjl_3),g' \
    -e 's,[@]xjl-4[@],$(xjl_4),g'

xjl_1       = $(shell substecho inst:xjl-1[ipw])

bindconfdir = ${sysconfdir}/bind
datadir     = ${bindconfdir}/lenik
c_incdir    = ${datadir}/inc
c_hackdir   = ${datadir}/hack
c_orgdir    = ${datadir}/org
c_projectsdir = ${datadir}/projects
c_socialdir = ${datadir}/social
c_utilsdir  = ${datadir}/utils

data_DATA = \
    named.conf \
    bodz.db \
    zoo.db

c_inc_DATA = \
    default.db \
    google.db

c_hack_DATA = \
    pfpf.db \
    xor.db

c_org_DATA = \
    6sn.db \
    ebja.db

c_projects_DATA = \
    labja.db \
    lapiota.db \
    sixor.db

c_social_DATA = \
    0yy.db \
    99jsj.db \
    bela.db \
    ooojjj.db

c_utils_DATA = \
    1na.db \
    bia.db \
    mia.db

CLEANFILES = \
    ${data_DATA} \
    ${c_inc_DATA} \
    ${c_hack_DATA} \
    ${c_org_DATA} \
    ${c_projects_DATA} \
    ${c_social_DATA} \
    ${c_utils_DATA}

dist-hook:
	cp ${srcdir}/*.in ${distdir}

%: %.in Makefile
	$(do_subst2) <$< >$@
