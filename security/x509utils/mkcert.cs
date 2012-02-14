# vim: set filetype=sh :

DN_C=CN
DN_ST=Zhejiang
DN_O="www.example.com"
DN_OU="Your Organization"
DN_CN="Your Name"
DN_emailAddress="your-name@example.com"
DAYS=3650

function main() {
    if ! type -t "$1" >/dev/null; then echo "Bad config parameter: $1" >&2
        return 1
    else
        "$@"
    fi
}

function rand-size() {
    echo 4096
}

function passphrase() {
    echo .
}

function csr-config() {
    local private_key="$1"

    cat <<EOT
[ca]
default_ca              = CA_default

[CA_default]
x509_extensions         = root_ca_extensions

[req]
default_bits            = 4096
default_keyfile         = $private_key
distinguished_name      = req_distinguished_name
attributes              = req_attributes
prompt                  = no
x509_extensions         = v3_ca

[req_distinguished_name]
C                       = $DN_C
ST                      = $DN_ST
O                       = $DN_O
OU                      = $DN_OU
CN                      = $DN_CN
emailAddress            = $DN_emailAddress

[req_attributes]

[root_ca_extensions]
basicConstraints        = CA:true

[v3_ca]
basicConstraints        = CA:true
EOT
}

function x509-opts() {
    echo -days $DAYS
}
