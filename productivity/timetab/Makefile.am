include libauto/project.am

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

name = $(notdir $(PWD))

all: symlinks

symlinks: mail server web

mail: 
	[ -d ../../mail ] && mkdir -p ../../mail/$(name)
	ln -snf ../../mail/$(name) $@

server:
	ln -snf $(name)-server server

web:
	ln -snf $(name)-web web

dir-skel01:
	ln -snf /home/dev/uni/lib/libjs-skel01/ $@

dir-lily:
	ln -snf /home/dev/jazz/lily/@lily/ $@

