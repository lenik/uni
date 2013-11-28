function main() {
    start_url=
    srv_url=http://sd.shenduncn.com/sds.php

    if [ -n "$start_url" ]; then
        read srv_url_enc < <(wget -qO- "$start_url")

        if [ -z "$srv_url_enc" ]; then
            quit "Failed to query from $start_url"
        fi

        srv_url=$(echo "$srv_url_enc" | base64 -d | yangwei -d)
        _log2 "Server URL: $srv_url"
    fi

    if [ -z "$srv_url" ]; then
        quit "The server url is unknown."
    fi

    iface=
    if route0=$(/sbin/route -n | grep '^0\.0\.0\.0'); then
        _log2 "Detected default route rule: $route0"

        iface0=${route0##* }
        if [ -n "$iface0" ]; then
            _log2 "Detected the default iface: $iface0"
            iface="$iface0"
        fi
    fi

    mac_net=$(/sbin/ifconfig $iface | grep -o -m1 'HWaddr [0-9a-f:]\+') \
        || quit "Can't find network address.";
    mac_net=${mac_net##* }
    mac_net=${mac_net//:/}
    _log2 "Found network address: $mac_net."

    mac_hd=
    for dev in sda sdb sdc sdd hda hdb hdc hdd; do
        [ -b /dev/$dev ] || continue

        serial_line=$(udisks --show-info /dev/$dev | grep -m1 serial:)
        # quit "Can't access the hard drive $dev."
        if [ -z "$serial_line" ]; then
            continue
        fi

        read _serail mac_hd < <(echo "$serial_line")
        if [ -z "$mac_hd" ]; then
            continue
        fi

        _log2 "Found the serial code: $mac_hd"
        break
    done

    if [ -z "$mac_hd" ]; then
        quit "No hard disk found, or none of them has a serial code."
    fi

    _mac="$mac_net/$mac_hd"
    read hmac _ < <(echo -n "$_mac" | md5sum)
    mac="${hmac:0:5}-${hmac:5:5}-${hmac:10:5}-${hmac:15:5}"
    _log2 "MAC=$mac ($_mac)"

    read -a resp < <(send "register: $mac")
    _log2 "Register-Response: ${resp[@]}"
    case "${resp[0]}" in
        auth|trial)
            now_ref=${resp[1]}
            expire=${resp[2]}
            ;;
        config)
            name="${resp[1]}"
            text="${resp[2]}"
            ;;
    esac
}

function send() {
    local data

    local req=$(echo -ne "$@" | yangwei -e | base64)
    _log2 "POST: $req ($*)"

    read data < <(wget -qO- --post-data "$req" "$srv_url")
    echo -n "$data" | base64 -d | yangwei -d
}
