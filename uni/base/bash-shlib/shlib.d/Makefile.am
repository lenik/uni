include ../libauto/coolauto.am

dist_shlib_DATA = \
    bashunit \
    cliboot \
    cmds \
    export-p \
    findabc \
    get_perm \
    import \
    initdir \
    lam \
    load \
    log \
    overrides \
    path \
    rcs \
    string

%: %.in Makefile
	$(do_subst) <$< >$@
