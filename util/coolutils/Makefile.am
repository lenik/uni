include libauto/project.am

colldir = /var/lib

dist_bin_SCRIPTS = \
    collect-copy \
    htmldoc-man \
    kill-all \
    timestamp \
    valcr \
    which2 \
    whichdir \
    whichother

dist_man_MANS = \
    collect-copy.1 \
    htmldoc-man.1 \
    kill-all.1 \
    timestamp.1 \
    valcr.1 \
    whichdir.1

dist_completion_DATA = \
    bash_completion.d/coolutils.sh

dist_alias_DATA = \
    bash_aliases.d/coolutils.sh

install-data-hook:
	install -d -m 2775 $(DESTDIR)$(colldir)

