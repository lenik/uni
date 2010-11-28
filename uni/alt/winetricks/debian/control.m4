Source: winetricks
Section: otherosfs
Priority: extra
Maintainer: Lenik (谢继雷) <lenik@bodz.net>
Build-Depends: debhelper (>= 7.0.50~)
Standards-Version: 3.8.4
Homepage: http://www.kegel.com/wine/winetricks

m4_include(packages.control)

Package: winetricks
Architecture: all
Depends: wine, cabextract, xjl-coolutils
Description: winetricks is a quick and dirty script to download and
    install various redistributable runtime libraries sometimes needed
    to run programs in Wine.
