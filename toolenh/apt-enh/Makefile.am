include libauto/project.am

aptconfdir=${sysconfdir}/apt/apt.conf.d

dist_bin_SCRIPTS = \
    apt-f \
    apt-list-dep \
    dwhich \
    lsdeb \
    debsize

dist_sbin_SCRIPTS = \
    apt-get-reinstall2

dist_man_MANS = \
    apt-f.8 \
    debsize.8 \
    dwhich.8 \
    lsdeb.8

dwhich.8:

dist_alias_DATA = \
    aptutils.sh

dist_completion_DATA = \
    completions/apt-enh

dist_aptconf_DATA = \
    10allowunauth

dist_setup_SCRIPTS = \
    postinst \
    prerm

CLEANFILES = \
    ${dist_setup_SCRIPTS}

install-data-hook:
	cd $(DESTDIR)${completiondir}; \
        ln -snf apt-enh sagb; \
        ln -snf apt-enh sagi; \
        ln -snf apt-enh safi; \
        ln -snf apt-enh acp; \
        ln -snf apt-enh sagu; \
        ln -snf apt-enh sagp; \
        ln -snf apt-enh sagr; \
        ln -snf apt-enh dtree; \
        ln -snf apt-enh sdr

