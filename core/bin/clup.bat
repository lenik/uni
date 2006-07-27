@echo off

if not "%1"=="" cd %1

cvs log | cvs2cl --stdin -P --group-within-date --hide-filenames

cvs ci -m "" ChangeLog
