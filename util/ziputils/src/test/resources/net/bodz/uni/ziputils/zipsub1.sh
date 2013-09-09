#!/bin/bash

rm -f *.zip
cd zipsub
find .   -name .svn -prune -o -print | xargs zip ../all.zip
zip ../bc.zip b c
find dir -name .svn -prune -o -print | xargs zip ../dir.zip
cd ..
zipsub.bat all.zip bc.zip
zipsub.bat all.zip dir.zip
