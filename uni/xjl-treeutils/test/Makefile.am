include ../libauto/coolauto.am

testdir = ${pkgdatadir}/test

test_DATA = \
    ar.7z \
    ar.rar \
    ar.tar \
    ar.tar.gz \
    ar.tar.bz2 \
    ar.tar.xz \
    ar.zip

ar.7z:
	rm -f $@
	cd $(srcdir)/root && 7z a ../ar.7z .

#ar.ar:
#	cd $(srcdir)/root && ar c ../ar.ar @<(find -type f)

ar.rar:
	rm -f $@
	cd $(srcdir)/root && rar a -ol -r -- ../ar.rar .

ar.tar:
	tar cf $@ -C $(srcdir)/root/ .

ar.tar.gz:
	tar acf $@ -C $(srcdir)/root/ .

ar.tar.bz2:
	tar acf $@ -C $(srcdir)/root/ .

ar.tar.xz:
	tar acf $@ -C root/ .

ar.zip:
	rm -f $@
	cd $(srcdir)/root && zip -r ../ar.zip .

CLEANFILES = ${test_DATA}
