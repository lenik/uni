# -*- mode: sh -*-
# vim: set filetype=sh :

start_url=http://sd.shenduncn.com/sds.php
auth_file="$rundir/sdefauth"
mac_lot=
mac_norm=

function main() {
    if [ -f "$auth_file" ]; then
        while IFS=' =' read k v; do
            [ -z "$k" ] && continue         # empty lines
            [ "${k:0:1}" = '#' ] && continue # comment lines
            declare $k="$v"
            _log2 "$k=$v"
        done < <(yangwei -d "$auth_file")
    fi

    net_failure=
    srv_url=`get_srv_url` || net_failure=1
    [ -n "$srv_url" ] || net_failure=1

    if [ -z "$net_failure" ]; then
        _log2 "Server URL: $srv_url"
    else
        _error "Network failure: the server url is unknown."

        # record & count the failure
        (( auth_fails++ ))
        save_auth
        return 1
    fi

    get_hw_mac
    _log2 "MAC=$mac_norm ($mac_lot)"

    local req
    if [ -z "$auth_mac" ]; then
        req="register: $mac_norm"
    else
        # no need to re-validate the old mac if they are mismatch.
        # just validate the new one.
        req="validate: $mac_norm"
    fi

    CR=$'\r'
    while IFS=': ' read resp_cmd resp_data; do
        resp_data="${resp_data%$CR}"
        read -a resp < <(echo "$resp_data")
        _log2 "Response: $resp_cmd: ${resp[@]}"

        case "$resp_cmd" in
            auth|trial)
                resp_mac="${resp[0]}"
                if [ "$mac_norm" != "$resp_mac" ]; then
                    quit "Invalid response: MAC unmatched."
                fi

                resp_now=${resp[1]}
                resp_expire=${resp[2]}

                if [ -n "$auth_recent" ]; then
                    if [ "$resp_now" -le "$auth_recent" ]; then
                        quit "Invalid response: current time is illegal."
                    fi
                fi
                auth_mac="$resp_mac"
                auth_recent="$resp_now"
                auth_expire="$resp_expire"
                if [ -z "$auth_creation" ]; then
                    auth_creation="$resp_now"
                fi
                ;;

            config)
                name="${resp[0]}"
                text="${resp[1]}"
                _log2 "Update config $name.yw"
                echo -e "$text" >"$configdir/$name.yw"
                (( auth_configs++ ))
                ;;

            error)
                quit "Server Error: ${resp[@]}"
                ;;
        esac
    done < <(send "$srv_url" "$req")

    save_auth
}

function get_srv_url() {
    if [ -n "$start_url" ]; then
        read resp_enc < <(wget -qO- "$start_url")

        if [ -z "$resp_enc" ]; then
            quit "Failed to query from $start_url"
        fi

        IFS='|' read srv_ip _ srv_port _ srv_path \
            < <(echo "$resp_enc" | base64 -d | yangwei -d)

        echo "http://$srv_ip:$srv_port/$srv_path"
    fi
}

function send() {
    local url="$1"
    shift

    local req=$(echo -ne "$@" | yangwei -e | base64)
    _log2 "POST: $req ($*)"

    local data
    read data < <(wget -qO- --post-data "$req" "$url")
    echo -n "$data" | base64 -d | yangwei -d
}

function get_hw_mac() {
    local iface iface0
    local route0
    if route0=$(/sbin/route -n | grep '^0\.0\.0\.0'); then
        _log2 "Detected default route rule: $route0"

        iface0=${route0##* }
        if [ -n "$iface0" ]; then
            _log2 "Detected the default iface: $iface0"
            iface="$iface0"
        fi
    fi

    local mac_net
    local mac_hd

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

    mac_lot="$mac_net/$mac_hd"
    read hmac _ < <(echo -n "$mac_lot" | md5sum)
    mac_norm="${hmac:0:5}-${hmac:5:5}-${hmac:10:5}-${hmac:15:5}"
    # echo "$mac_norm"
}

function save_auth() {
    if [ ! -w "$auth_file" ]; then
        # assert false. cuz this script should be run by root user.
        if ! rm -f "$auth_file"; then
            _error "Auth file isn't writable."
            return 1
        fi
    fi

    (( auth_serial ++ ))
    _log2 "Updating $auth_file (serial $auth_serial)."

    cat <<EOF | yangwei -e >"$auth_file"
# shendun authentication file
# updated at `date`
auth_configs    = $auth_configs
auth_creation   = $auth_creation
auth_recent     = $auth_recent
auth_expire     = $auth_expire
auth_fails      = $auth_fails
auth_mac        = $auth_mac
auth_serial     = $auth_serial
EOF
}
