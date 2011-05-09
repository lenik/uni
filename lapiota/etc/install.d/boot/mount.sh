#!/bin/bash

__DIR__="${BASH_SOURCE%/*}"
FSTAB="$__DIR__/fstab.tc"
PASSWD="$__DIR__/passwd.tc"
KEYFILE=`cygpath -m ~/db/keys/keyf`

for d in "$__DIR__/truecrypt"*; do
    if [ -x "$d/truecrypt" ]; then
        TRUECRYPT="$d/truecrypt"
        break
    fi
done
if [ ! -x "$TRUECRYPT" ]; then
    echo "No truecrypt found. "
    exit 1
fi

function mkdir_p() {
    # mkdir -p "$1"
    cmd /c mkdir "$1"
}

function getmountstatus() {
    local findvol="$1"
    local ctxvol
    mtab=()
    while read -r l; do
        if [ -z "$l" ]; then continue; fi
        if [ "${l:0:4}" = "\\\\?\\" ]; then
            ctxvol="$l"
            continue
        fi
        if [ -z "$ctxvol" ]; then continue; fi
        if [ "$ctxvol" = "$findvol" ]; then
            l_norm=`cygpath -w "$l"`
            l_norm="${l_norm%[/\\]}"
            mtab[${#mtab}]="$l_norm"
        fi
    done < <(mountvol)
}

function getpasswd() {
    local name="$1"
    local IFS=":"
    local nam pass
    PASSWORD=
    if [ -f "$PASSWD" ]; then
        while read -r nam pass; do
            if [ "$name" = "$nam" ]; then
                PASSWORD="$pass"
                return 0
            fi
        done <"$PASSWD"
    fi
    return 1
}

cat "$FSTAB" | while read -r LETTER DEVICE PASSWORD MOUNTPOINTS; do
    # ignore comment lines
    if [ -z "$LETTER" -o "${LETTER:0:1}" = '#' ]; then continue; fi
    echo -n "mount $DEVICE to $LETTER: "

    # parse password options
    TRUECRYPTOPTS="/a /s /q"
    if [ "${PASSWORD:0:1}" = '&' ]; then
        PASSWORD="${PASSWORD:1}"
        if [ "${PASSWORD:0:1}" != '&' ]; then
            TRUECRYPTOPTS="$TRUECRYPTOPTS -k $KEYFILE"
        fi
    fi
    if [ "${PASSWORD:0:1}" = '~' ]; then
        if ! getpasswd "${PASSWORD:1}"; then
            echo "no password, skip"
            continue
        fi
    fi

    # do mount
    if "$TRUECRYPT" $TRUECRYPTOPTS /l "$LETTER" /v "$DEVICE" /p "$PASSWORD"; then
        read -r VOL < <(mountvol $LETTER: /l)
        if [ -z "$VOL" ]; then
            echo "unknown win32 volume path, skip"
            continue
        fi
        # echo "  mounted at $VOL"

        if [ -z "$MOUNTPOINTS" ]; then
            echo "no mount-point specified."
            continue
        fi

        getmountstatus "$VOL"

        #for t in "${mtab[@]}"; do
        #    echo "    current linked to $t"
        #done

        for m in $MOUNTPOINTS; do
            m_norm=`cygpath -w "$m"`
            for t in "${mtab[@]}"; do
                if [ "$t" = "$m_norm" ]; then
                    echo "already linked to $m_norm"
                    continue 2
                fi
            done

            echo
            echo "  link to $m_norm"
            _try=3
            while true; do
                # rmdir "$m"; mkdir_p "$m"
                cmd /c rmdir "$m_norm" \& mkdir "$m_norm"
                if err=`mountvol "$m_norm" "$VOL"`; then
                    break
                else
                    echo "    mountvol: $err"
                    if [ $((_try--)) -le 0 ]; then
                        echo "    failed. "
                        break
                    else
                        echo "    clean and retry $_try"
                    fi
                fi
            done
        done
    fi
done
