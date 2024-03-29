# vim: set ft=make :

SHELL=bash

%.rand:
	openssl rand -out "$*.rand" 4096

%.pem: %.rand
	openssl genrsa -rand "$*.rand" -out "$*.pem" $(BITS)

%-x.pem: %.pem %.cs
	openssl rsa -in "$*.pem" -out "$*-x.pem" -aes256 -passout file:<(get-config "$*.cs" passphrase)

%.pk8: %.pem
	openssl pkcs8 -nocrypt -in "$*.pem" -topk8 -out "$*.pk8"

%.cfg: %.cs
	get-config "$*.cs" config >"$*.cfg"

%.csr: %.pem %.cfg
	openssl req -utf8 -new -out "$*.csr" -key "$*.pem" -config "$*.cfg"

%.crt: %.csr %.pem %.cfg
	@dirname="$${PWD##*/}"; echo "[$$dirname]"; \
	    if [ -f "../$$dirname.crt" ]; then \
	        echo "Generate child x509 certificate signed by $$dirname"; \
	        openssl x509 -req -in "$*.csr" -out "$*.crt" -signkey "$*.pem" \
	            -CA "../$$dirname.crt" -CAkey "../$$dirname.pem" -CAcreateserial \
                    -extfile "$*.cfg" -extensions req_extensions \
	            `get-config "$*.cs" x509-opts $*.cfg`; \
	    else \
	        echo "Generate self-signed x509 certificate"; \
	        openssl x509 -req -in "$*.csr" -out "$*.crt" -signkey "$*.pem" \
                    -extfile "$*.cfg" -extensions req_extensions \
	            `get-config "$*.cs" x509-opts $*.cfg`; \
	    fi

%.spc: %.crt
	openssl crl2pkcs7 -nocrl -certfile "$*.crt" -outform DER -out "$*.spc"

%.p12: %.crt %.pem
	openssl pkcs12 -export -in "$*.crt" -inkey "$*.pem" -out "$*.p12" \
	    -name "PKCS#12 of $*" -passout "pass:."

%-x.p12: %.crt %.pem
	openssl pkcs12 -export -in "$*.crt" -inkey "$*.pem" -out "$*-x.p12" \
	    -name "PKCS#12 of $*" -passout file:<(get-config "$*.cs" passphrase)

%.jks: %.p12
	keytool -importkeystore \
            -srcstoretype PKCS12 -srckeystore  "$<" -srcstorepass  `get-config "$*.cs" passphrase` \
            -deststoretype JKS   -destkeystore "$@" -deststorepass `get-config "$*.cs" passphrase`

%.jce: %.p12
	keytool -importkeystore \
            -srcstoretype PKCS12 -srckeystore "$<"  -srcstorepass  `get-config "$*.cs" passphrase` \
            -deststoretype JCEKS -destkeystore "$@" -deststorepass `get-config "$*.cs" passphrase`

%.pvk: %.pem
	pvk -in "$*.pem" -strong -out "$*.pvk" -topvk -nocrypt

%.pvkimprt.bat: %.spc
	echo "pvkimprt -import \"$*.spc\" \"$*.pvk\"" >$*.pvkimprt.bat

%.all: %-x.pem %.pk8 %.spc %.p12 %-x.p12
	touch "$*.all"

%.w32: %.all %.pvk %.pvkimprt.bat
	touch "$*.w32"
