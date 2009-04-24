
. $LAM_KALA/lib/sh/findabc

findabc -p cyg
findabc -p au
findabc -p xxxfail

echo Before find: $PWD
findabc -p nsis/
echo After find: $PWD
echo

findabc -r /mnt/c/lam/nt.util/abc.d winamp
echo winamp: [$_home]
