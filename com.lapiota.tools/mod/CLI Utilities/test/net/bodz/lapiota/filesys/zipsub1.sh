#!/bin/bash

rm -f *.zip
cd zipsub
zip -r ../all.zip .
zip -r ../bc.zip b c
zip -r ../dir.zip dir
cd ..
zipsub.bat all.zip bc.zip
zipsub.bat all.zip dir.zip
