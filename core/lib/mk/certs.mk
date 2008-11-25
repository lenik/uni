
OPENSSL = openssl
CERT_OPTS = -days 3650
SELF = _self

.SECONDARY:

%.all: %.pem %.crt %.p7 %.p8 %.p12 %.log
	@#touch $@
	@echo Certificates have been created for \`$*\'.

%.log: %.post %.p7 %.p8 %.p12
	if [ -f "$<" ]; then \
		. $< 2>&1 >$@; \
	fi

%.rand:
	$(OPENSSL) rand -out $@ 4096

%.pem_raw: %.rand
	$(OPENSSL) genrsa -rand $< -out $@ 4096
	pvk -in $@ -out $*.pvk -nocrypt -topvk

%.pem: .%.pem_raw
	$(OPENSSL) rsa -aes256 -passout "file:.$*.passwd" -in $< -out $@

%.p8: .%.pem_raw
	$(OPENSSL) pkcs8 -topk8 -nocrypt -in $< -out $@

%.pvk: .%.pem_raw
	$(PVK) -topvk -nocrypt -strong -in $< -out $@

.%.csr: .%.pem_raw %.config
	$(OPENSSL) req -new -key $< -config $*.config -out $@
	cp -f $@ .$*.p10

# -CA ca.crt -CAkey ca.pem -CAcreateserial
%.crt: .%.csr .%.pem_raw
	$(OPENSSL) x509 -req $(CERT_OPTS) -in $< -signkey .$*.pem_raw -out $@

%.p7: %.crt
	$(OPENSSL) crl2pkcs7 -nocrl -certfile $< -outform DER -out $@
	cp -f $@ .$*.p7b
	cp -f $@ .$*.spc

%.p12_raw: %.crt .%.pem_raw
	$(OPENSSL) pkcs12 -export -name "$*" -in $< -inkey .$*.pem_raw -out $@ -passout "pass:."

%.p12: %.crt .%.pem_raw .%.passwd
	$(OPENSSL) pkcs12 -export -name "$*" -in $< -inkey .$*.pem_raw -out $@ -passout "file:.$*.passwd"
	cp -f $@ .$*.pfx

default:
	@echo makecert TARGET.all

clean:
	shopt -s dotglob; \
	rm -f \
		*.rand \
		*.pem *.pem_raw \
		*.csr *.crt \
		*.p7 *.spc *.p8 *.p12 *.p12_raw \
		*.pvk \
		*.log *.all
