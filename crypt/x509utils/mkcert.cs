# vim: set filetype=sh :

DN_C=CN
DN_ST=Zhejiang
DN_O="www.example.com"
# DN_OU="Your Organization"
DN_CN="Your Name"
DN_emailAddress="your-name@example.com"
# DAYS=36525 # 100 years.
DAYS=18262 # 50 years

function main() {
    if ! type -t "$1" >/dev/null; then echo "Bad config parameter: $1" >&2
        return 1
    fi

    n_domain="${#domains[@]}"

    "$@"
}

function rand-size() {
    echo 4096
}

function passphrase() {
    echo .
}

function config() {
    local private_key="$1"
    local reqexts=SAN

    echo "[ca]"
    echo "default_ca              = CA_default"
    echo
    echo "[CA_default]"
    echo "x509_extensions         = root_ca_extensions"
    echo
    echo "[req]"
    echo "default_bits            = 4096"
    echo "default_keyfile         = $private_key"
    echo "distinguished_name      = req_distinguished_name"
    echo "attributes              = req_attributes"
    echo "prompt                  = no"
    echo "x509_extensions         = v3_ca"
    echo "req_extensions          = req_extensions"
    echo
    echo "[req_distinguished_name]"
    [ -n "$DN_C" ] && echo "C     = $DN_C"
    [ -n "$DN_ST" ] && echo "ST   = $DN_ST"
    [ -n "$DN_O" ] && echo "O     = $DN_O"
    [ -n "$DN_OU" ] && echo "OU   = $DN_OU"
    [ -n "$DN_CN" ] && echo "CN   = $DN_CN"
    [ -n "$DN_emailAddress" ] &&
        echo "emailAddress        = $DN_emailAddress"
    echo
    echo "[req_attributes]"
    echo
    echo "[req_extensions]"
    echo "basicConstraints        = CA:false"
    if [ "$n_domain" != 0 ]; then
        echo "keyUsage                = nonRepudiation, digitalSignature, keyEncipherment"
        echo "subjectAltName          = @alt_names"
        echo
        echo "[alt_names]"
        index=1
        for domain in "${domains[@]}"; do
            echo "DNS.$index = $domain"
            (( index++ ))
        done
    fi
    echo
    echo "[root_ca_extensions]"
    echo "basicConstraints        = CA:true"
    echo
    echo "[v3_ca]"
    echo "basicConstraints        = CA:true"

}

function x509-opts() {
    config_file="$1"
    if [ -z "$config_file" ]; then
        quit "syntax: x509-opts <config-file>"
    fi

    opts=()
    nopt=0

    if [ "$n_domain" != 0 ]; then
        opts[nopt++]=-extfile
        opts[nopt++]="$1"
        opts[nopt++]=-extensions
        opts[nopt++]=v3_req
    fi

    if [ -n "$DAYS" ]; then
        opts[nopt++]=-days
        opts[nopt++]=$DAYS
    fi

    echo "${opts[@]}"
}
