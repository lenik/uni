
HOME_bas    = "k:\\net.bodz\\net.bodz.bas"
HOME_dist   = "k:\\net.bodz\\net.bodz.dist"
HOME_lapiota= "k:\\net.bodz\\net.bodz.lapiota"
HOME_mda    = "k:\\net.bodz\\net.bodz.mda"
HOME_swt    = "k:\\net.bodz\\net.bodz.swt"
HOME_xml    = "k:\\net.bodz\\net.bodz.xml"
HOME_jaep   = "K:\\net.pfpf\\net.pfpf.jaep"

projenv: \
    bas.bat \
    dist.bat \
    lapiota.bat \
    mda.bat \
    swt.bat \
    xml.bat \
    jaep.bat \

%.bat:
	@echo "@echo off" >$@
	@echo "set PATH=$(HOME_$*)\\bin;%PATH%" >>$@
	@echo "if \"%~1\"==\"/\" cd /d $(HOME_$*)" >>$@
