include ../libauto/coolauto.am

bcdir = ${datadir}/doc/bc

do_subst += -e 's,@bcdir@,$(bcdir),g'

dist_aliases_DATA = \
    dirs.sh \
    envchk.sh \
    maths.sh \
    shorts.sh \
    stats.sh

dist_bc_DATA = \
    maths.b

CLEANFILES = \
    maths.sh
