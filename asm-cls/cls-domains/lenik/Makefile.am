include ../libauto/common.am

do_subst += -e 's,[@]datadir[@],$(datadir),g'

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
    lenik.db \
    zoo.db \
    p.db \
    lo.db

c_inc_DATA = \
    default.db \
    dns-usage.db \
    google.db \
    hotmail.db

c_hack_DATA = \
    pfpf.db \
    xor.db

c_org_DATA = \
    ebja.db

c_social_DATA = \
    99jsj.db \
    bela.db \
    ooojjj.db

c_utils_DATA = \
    1na.db \
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
	$(do_subst) <$< | substcat >$@
