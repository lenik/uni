
set _homex=%_home%
set _homex=%_homex:\=/%

echo [erlang]>%1
echo Rootdir=%_homex%>>%1
echo Bindir=%_homex%/erts-5.5.5/bin>>%1
echo Progname=erl>>%1
