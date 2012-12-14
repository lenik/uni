#!/bin/bash

find . \( -iname "*.pl" -or -iname "*.pm" \) -exec dos2unix {} \;
find . -iname "*.pl" -exec chmod a+x {} \;
find . -type d -exec chmod a+x {} \;
find ./hellos -exec chmod a+x {} \;
chmod a+r -R *
