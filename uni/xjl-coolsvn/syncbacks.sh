#!/bin/bash

# bash --login:
#   always use cygwin's svn tools.  but, --login will set CWD to HOME.
#   so just force to set to cygwin's

# fix: it seems not work when locale set to zh_CN.
export LANG=C

SVNADMIN=/usr/bin/svnadmin
SVNSYNC=/usr/bin/svnsync

function mkdir_p() {
    for dir in "$@"; do
        if [ -d "$dir" ]; then continue; fi
        local top="${dir//\\/\/}"
        while [ ! -d "$top" ]; do
            local parent="${top%/*}"
            if [ "$parent" = "$top" ]; then
                top=
            else
                top="$parent"
            fi
        done
        if [ -z "$top" ]; then
            echo "Unexpected: no existing root for $dir"
            exit 1
        fi
        mkdir -p "$dir"
        aclreset.bat `cygpath -m "$top"` >/dev/null
    done
}

master="`dirname "$BASH_SOURCE"`/.."

read master_root <"$master/.root"
if [ -z "$master_root" ]; then
    echo "$master/.root isn't set. "
    exit 1
fi

function syncrepo() {
    repo_name="${repo:${#master}+1}"
    echo "  svnsync $repo_name"
    slave_repo="$slave/$repo_name"
    if [ ! -d "$slave_repo" ]; then
        echo "    create new backup repo at $slave_repo"
        mkdir_p "$slave_repo"
        if "$SVNADMIN" create "$slave_repo"; then
            conf="$slave_repo/conf"
            hook="$slave_repo/hooks"

            echo "      enabling pre-revprop-change"
            echo "@echo off"             >"$hook/pre-revprop-change.bat"
            echo "exit /b 0"            >>"$hook/pre-revprop-change.bat"
            echo "#!/bin/sh"             >"$hook/pre-revprop-change"
            echo "exit 0"               >>"$hook/pre-revprop-change"
            chmod a+x "$hook/pre-revprop-change"

            echo "      create syncuser with password syncpass"
            echo "[general]"             >"$conf/svnserve.conf"
            echo "password-db = passwd" >>"$conf/svnserve.conf"
            echo "authz-db = authz"     >>"$conf/svnserve.conf"
            echo "[users]"               >"$conf/passwd"
            echo "syncuser = syncpass"  >>"$conf/passwd"
            echo "[groups]"              >"$conf/authz"
            echo "syncusers = syncuser" >>"$conf/authz"
            echo "[/]"                  >>"$conf/authz"
            echo "* = r"                >>"$conf/authz"
            echo "@syncusers = rw"      >>"$conf/authz"

            # cygwin permission fix:
            echo "      cygwin fix: reset the NTFS-ACL on $slave_repo"
            aclreset.bat `cygpath -m $slave_repo` >/dev/null
        else
            echo "      create fail"
            exit 1
        fi

        echo "    svnsync init, source=$master_root/$repo_name"
        "$SVNSYNC" init \
            --non-interactive --trust-server-cert \
            --source-username "" \
            --source-password "" \
            --sync-username "syncuser" \
            --sync-password "syncpass" \
            "$slave_root/$repo_name" "$master_root/$repo_name"
    fi
    "$SVNSYNC" sync \
        --non-interactive --trust-server-cert \
        --sync-username "syncuser" \
        --sync-password "syncpass" \
        "$slave_root/$repo_name"
}

while read backup_device; do
    if [ "$backup_device" = "" ]; then continue; fi
    if [ "${backup_device:0:1}" = "#" ]; then continue; fi
    slave="$backup_device/lam.svn.0"
    if [ ! -d "$slave" ]; then continue; fi
    if [ -f "$slave/.root" ]; then
        read slave_root <"$slave/.root"
        if [ -z "$slave_root" ]; then
            echo "$slave/.root isn't set. "
            exit 1
        fi
    else
        slave_root="file://$slave"
    fi
    echo "mirror to $slave($slave_root)"

    if [ "$#" = 0 ]; then
        for a in "$master"/*; do
            for repo in "$a"/*; do
                if [ ! -f "$repo/db/uuid" ]; then continue; fi
                syncrepo "$repo"
            done
        done
    else
        for repo in "$@"; do
            syncrepo "$repo"
        done
    fi

    echo
done <"$master/etc/backup.ms"

if [ $# = 0 ]; then
    read -n1 -p "Press any key to quit..."
fi
