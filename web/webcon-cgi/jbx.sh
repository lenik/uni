#!/bin/bash

CLASSPATH=/home/groups/f/fr/freejava/cgi-bin:$CLASSPATH
java $* | cat
