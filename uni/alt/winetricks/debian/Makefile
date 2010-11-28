all: control
	../winetricks-packages -p
	../winetricks-packages -i

control: control.m4 packages.control
	m4 -P control.m4 >$@

packages.control:
	../winetricks-packages -c >$@
