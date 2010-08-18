
PREFIX=/usr/local
BINDIR=$(PREFIX)/bin
SBINDIR=$(PREFIX)/sbin
MANDIR=$(PREFIX)/share/man

all:

install:
	for sbin in mount.tc mountall.tc; do \
		install -m644 $$sbin $(SBINDIR); \
	done
	for bin in nextdevid; do \
		install -m644 $$bin $(BINDIR); \
	done
	install -m644 *.1 $(MANDIR)/man1
	install -m644 *.5 $(MANDIR)/man5
	install -m644 fstab.tc /etc
	install -m600 passwd.tc /etc
	install -m644 mount-tc.conf /etc/init
