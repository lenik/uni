#!/bin/bash
# Cygwin/Lapiota post-install init
    echo "Current cygwin installation version: "
    uname -srv
    echo

    if [ -d /cygdrive/c -o ! -d /mnt/c ]; then
        echo "cygdrive path prefix should be set to /mnt"
        # show user/system cygdrive path prefix.
        mount -p
        exit 1
    fi

# helper functions
    function contains() {
        local files=("$@")
        # echo "Files: ${files[@]}"
        [ ${#files} != 0 ]
    }

# base
    shopt -s nullglob
    mkdir -p /mirror /opt /media
    mkdir -p /var/cron/tabs
    CYGROOT=`cygpath -am /`
    LAM_SYS=`cygpath -au "${CYGROOT%/*}"`

    mkgroup -l >/etc/group
    mkpasswd -l >/etc/passwd

    ln -nsf /mnt/c/lam          /lam
    ln -nsf $LAM_KALA           /lapiota
    ln -nsf $LAM_KALA/xt        /xt
    ln -nsf /xt/etc/profile.d   /etc/profile.d/xt

    ln -nsf $LAM_KALA/etc/cygwin/*      /etc
    ln -nsf $LAM_KALA/etc/profile.d     /etc/profile.d/cygwin

    ln -nsf /mnt/b              /test
    rm -fR /tmp
    ln -nsf "$TEMP"             /tmp

# using cache of updates
    if [ -d /usr/lib/perl5/site_perl/5.10 ]; then
        mv /usr/lib/perl5/site_perl/5.10 /usr/lib/perl5/site_perl/5.10.orig
    fi
    for site in $LAM_KALA/usr/lib/perl5/site $LAM_SYS/lib/perl5/site; do
        if [ -d "$site" ]; then
            echo "Redirect perl5-site -> $site"
            ln -nsf "$site" /usr/lib/perl5/site_perl/5.10
            break
        fi
    done

# per user configs
    while IFS=: read name pass uid gid info home shellcmd; do
        if contains "$home/etc/cygwin"/*; then
            echo "install etc/: " ~/etc/cygwin/*
            ln -nsf ~/etc/cygwin/* /etc
        fi
        if [ -f "$home/etc/crontab" ]; then
            echo "Install crontab /var/cron/tabs/$USERNAME"
            ln -nsf ~/etc/crontab /var/cron/tabs/$USERNAME
        fi
    done </etc/passwd

# patches
    xpatch -c4 /bin/bash.exe <(echo -e "56c56\n< 03000080\n---\n> 02000080") /bin/bashw.exe
    xpatch -c4 /bin/perl.exe <(echo -e "56c56\n< 03000000\n---\n> 02000000") /bin/perlw.exe

# system wide configs
    cygserver-config
    ssh-host-config
